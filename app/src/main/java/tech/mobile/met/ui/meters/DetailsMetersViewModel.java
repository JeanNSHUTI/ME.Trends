package tech.mobile.met.ui.meters;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import tech.mobile.met.models.realmentity.ElectricMeter;
import tech.mobile.met.models.realmentity.ElectricMeter_records;
import tech.mobile.met.models.realmentity.GasMeter;
import tech.mobile.met.models.realmentity.GasMeter_records;
import tech.mobile.met.models.realmentity.WaterMeter;
import tech.mobile.met.models.realmentity.WaterMeter_records;
import tech.mobile.met.models.viewmodels.RecordViewModel;

public class DetailsMetersViewModel extends ViewModel {
    //Fields
    private MutableLiveData<String> eTextViewMeterName;
    private String meterType;
    private String meterName;
    //Realm Auth
    String appID = "app-energy-trends-iiaxj";
    private final App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
            .requestTimeout(30, TimeUnit.SECONDS).build());
    User user = app.currentUser();
    private Realm backgroundThreadRealm;

    public DetailsMetersViewModel(){
        eTextViewMeterName = new MutableLiveData<>();
        meterType = "";
        meterName = "";

    }

    //Getters and setters
    public String getMeterType() {
        return meterType;
    }
    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }
    public String getMeterName() {
        return meterName;
    }
    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }
    public LiveData<String> geteTextViewMeterName() {
        return eTextViewMeterName;
    }
    public void seteTextViewMeterName(String eTextViewMeterName) {
        this.eTextViewMeterName.setValue(eTextViewMeterName);
    }

    //Methods
    //Records in Realm for Gas|Electric|Water
    public RealmList<GasMeter_records> GetAllGasMeterRecords(){
        RealmList<GasMeter_records> records = new RealmList<GasMeter_records>();
        backgroundThreadRealm = Realm.getDefaultInstance();

        GasMeter selectedGasMeter = backgroundThreadRealm.where(GasMeter.class).equalTo("name", meterName).findFirst();

        if(selectedGasMeter != null){
            records = selectedGasMeter.getRecords();
        }
        return records;
    }

    public RealmList<WaterMeter_records> GetAllWaterMeterRecords(){
        RealmList<WaterMeter_records> records = new RealmList<WaterMeter_records>();
        backgroundThreadRealm = Realm.getDefaultInstance();

        WaterMeter selectedWaterMeter = backgroundThreadRealm.where(WaterMeter.class).equalTo("name", meterName).findFirst();

        if(selectedWaterMeter != null){
            records = selectedWaterMeter.getRecords();
        }
        return records;
    }

    public RealmList<ElectricMeter_records> GetAllElectricMeterRecords(){
        RealmList<ElectricMeter_records> records = new RealmList<ElectricMeter_records>();
        backgroundThreadRealm = Realm.getDefaultInstance();

        ElectricMeter selectedElectricMeter = backgroundThreadRealm.where(ElectricMeter.class).equalTo("name", meterName).findFirst();

        if(selectedElectricMeter != null){
            records = selectedElectricMeter.getMy_meter_records();
        }
        return records;
    }

    //Load records to Record view model for ui
    public RealmList<RecordViewModel> LoadRecordsForDisplay(){
        RealmList<RecordViewModel> viewModels = new RealmList<RecordViewModel>();
        switch (meterType){
            case "Electric":
                for(ElectricMeter_records rec : GetAllElectricMeterRecords()){
                    RecordViewModel mRecord= new RecordViewModel(rec);
                    mRecord.setType("Electric");
                    viewModels.add(mRecord);
                }
                break;
            case "Gas":
                for(GasMeter_records rec : GetAllGasMeterRecords()){
                    RecordViewModel mRecord= new RecordViewModel(rec);
                    mRecord.setType("Gas");
                    viewModels.add(mRecord);
                }
                break;
            case "Water":
                for(WaterMeter_records rec : GetAllWaterMeterRecords()){
                    RecordViewModel mRecord= new RecordViewModel(rec);
                    mRecord.setType("Water");
                    viewModels.add(mRecord);
                }
                break;

        }
        return viewModels;
    }

    private boolean CheckCurrentReadingString(String input){
        String _REGEX = "[0-9]+";
        //String newstr = "";
        //int ind = 0;
        Pattern p = Pattern.compile(_REGEX);

        // If the string is empty return false
        if (input == null) {
            return false;
        }
        Matcher m = p.matcher(input);

        /*if(m.matches()){
            //Check if there are leading zeroes, if so, remove them
            if (input.startsWith("0")){
                for (int i = 0; i < input.length(); i++) {
                    char digit = input.charAt(i);
                    if (digit != '0') {
                        ind = i;
                        break;
                    }
                }
            }
        }*/
        return m.matches();
    }

    //Create Record
    public boolean SaveNewRecord(String currentReading){
        AtomicBoolean result = new AtomicBoolean(false);
        backgroundThreadRealm = Realm.getDefaultInstance();
        if(CheckCurrentReadingString(currentReading)){
            if (null != user) {
                if (user.isLoggedIn()) {
                    switch (meterType){
                        case "Electric":
                            backgroundThreadRealm.executeTransaction( transactionRealm -> {
                                ElectricMeter selectedEMeter = backgroundThreadRealm.where(ElectricMeter.class).equalTo("name", meterName).findFirst();
                                if(selectedEMeter != null){
                                    Log.v("REALMUPDATE", "Found current electric meter in realm");
                                    ElectricMeter_records newEmRecord = new ElectricMeter_records(Long.parseUnsignedLong(currentReading));
                                    if(!selectedEMeter.getMy_meter_records().isEmpty()){
                                        //Use last record in order to new compute consumption value
                                        newEmRecord.ComputeConsumption(selectedEMeter.getMy_meter_records().last());
                                    }
                                    else{
                                        //Adding first record to meter
                                        newEmRecord.setConsumption(Long.parseUnsignedLong(currentReading));
                                    }
                                    //Add new to record to selected meter
                                    selectedEMeter.getMy_meter_records().add(newEmRecord);

                                    result.set(true);
                                    Log.v("REALMUPDATE", "Updated provider");
                                }
                            });
                            break;
                        case "Gas":
                            backgroundThreadRealm.executeTransaction( transactionRealm -> {
                                GasMeter selectedGMeter = backgroundThreadRealm.where(GasMeter.class).equalTo("name", meterName).findFirst();
                                if(selectedGMeter != null){
                                    Log.v("REALMUPDATE", "Found current electric meter in realm");
                                    GasMeter_records newGsRecord = new GasMeter_records(Long.parseUnsignedLong(currentReading));
                                    if(!selectedGMeter.getRecords().isEmpty()){
                                        //Use last record in order to new compute consumption value
                                        newGsRecord.ComputeConsumption(selectedGMeter.getRecords().last());
                                    }
                                    else{
                                        newGsRecord.setConsumption(Long.parseUnsignedLong(currentReading));
                                    }
                                    //Add new to record to selected meter
                                    selectedGMeter.getRecords().add(newGsRecord);

                                    result.set(true);
                                    Log.v("REALMUPDATE", "Updated provider");
                                }
                            });
                            break;
                        case "Water":
                            backgroundThreadRealm.executeTransaction( transactionRealm -> {
                                WaterMeter selectedWMeter = backgroundThreadRealm.where(WaterMeter.class).equalTo("name", meterName).findFirst();
                                if(selectedWMeter != null){
                                    Log.v("REALMUPDATE", "Found current electric meter in realm");
                                    WaterMeter_records newWmRecord = new WaterMeter_records(Long.parseUnsignedLong(currentReading));
                                    if(!selectedWMeter.getRecords().isEmpty()){
                                        //Use last record in order to new compute consumption value
                                        newWmRecord.ComputeConsumption(selectedWMeter.getRecords().last());
                                    }
                                    else{
                                        newWmRecord.setConsumption(Long.parseUnsignedLong(currentReading));
                                    }
                                    //Add new to record to selected meter
                                    selectedWMeter.getRecords().add(newWmRecord);

                                    result.set(true);
                                    Log.v("REALMUPDATE", "Updated provider");
                                }
                            });
                            break;
                        default:
                            result.set(false);
                            break;
                    }
                }
            }
        }
        return result.get();
    }

    //Update Meter name
    public boolean UpdateMeterName(String newMeterName){
        AtomicBoolean result = new AtomicBoolean(false);
        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {

                switch (meterType){
                    case "Electric":
                        backgroundThreadRealm.executeTransaction( transactionRealm -> {
                            ElectricMeter selectedEMeter = backgroundThreadRealm.where(ElectricMeter.class).equalTo("name", meterName).findFirst();
                            if(selectedEMeter != null){
                                if(CheckIfMeterNameDoesntExist(newMeterName)){
                                    Log.v("REALMUPDATE", "Found current electric meter in realm");
                                    selectedEMeter.setName(newMeterName);
                                    result.set(true);
                                    Log.v("REALMUPDATE", "Updated provider");
                                }
                            }
                        });
                        break;
                    case "Gas":
                        backgroundThreadRealm.executeTransaction( transactionRealm -> {
                            GasMeter selectedGMeter = backgroundThreadRealm.where(GasMeter.class).equalTo("name", meterName).findFirst();
                            if(selectedGMeter != null){
                                if(CheckIfMeterNameDoesntExist(newMeterName)){
                                    Log.v("REALMUPDATE", "Found current electric meter in realm");
                                    selectedGMeter.setName(newMeterName);
                                    result.set(true);
                                    Log.v("REALMUPDATE", "Updated provider");
                                }
                            }
                        });
                        break;
                    case "Water":
                        backgroundThreadRealm.executeTransaction( transactionRealm -> {
                            WaterMeter selectedWMeter = backgroundThreadRealm.where(WaterMeter.class).equalTo("name", meterName).findFirst();
                            if(selectedWMeter != null){
                                if(CheckIfMeterNameDoesntExist(newMeterName)){
                                    Log.v("REALMUPDATE", "Found current electric meter in realm");
                                    selectedWMeter.setName(newMeterName);
                                    result.set(true);
                                    Log.v("REALMUPDATE", "Updated provider");
                                }
                            }
                        });
                        break;
                    default:
                        result.set(false);
                        break;
                }
            }
        }
        return result.get();
    }

    //Delete record
    public boolean RemoveRecordFromSelectedMeter(Date record_date){
        //Convert dates
        AtomicBoolean success = new AtomicBoolean(false);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        //Date recordDate = new Date();

        //try {
        //    recordDate = dateFormat.parse(record_date);
        //} catch (ParseException e) {
        //    e.printStackTrace();
        //}

        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {
                c1.setTime(record_date);

                switch (meterType){
                    case "Electric":
                        backgroundThreadRealm.executeTransaction (transactionRealm -> {
                            ElectricMeter selectedMeter = backgroundThreadRealm.where(ElectricMeter.class).equalTo("name", meterName).findFirst();
                            if(selectedMeter != null){
                                if(!selectedMeter.getMy_meter_records().isEmpty()){
                                    for(ElectricMeter_records rec : selectedMeter.getMy_meter_records()){
                                        c2.setTime(rec.getRecord_date());
                                        if( c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR) == 0 && c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH) == 0 &&
                                                c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH) == 0){
                                            //Delete record
                                            success.set(selectedMeter.DeleteRecord(rec));
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                        break;
                    case "Gas":
                        backgroundThreadRealm.executeTransaction (transactionRealm -> {
                            GasMeter selectedMeter = backgroundThreadRealm.where(GasMeter.class).equalTo("name", meterName).findFirst();
                            if(selectedMeter != null){
                                if(!selectedMeter.getRecords().isEmpty()){
                                    for(GasMeter_records rec : selectedMeter.getRecords()){
                                        c2.setTime(rec.getRecordDate());
                                        if( c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR) == 0 && c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH) == 0 &&
                                                c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH) == 0){
                                            //Delete record
                                            success.set(selectedMeter.DeleteRecord(rec));
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                        break;
                    case "Water":
                        backgroundThreadRealm.executeTransaction (transactionRealm -> {
                            WaterMeter selectedMeter = backgroundThreadRealm.where(WaterMeter.class).equalTo("name", meterName).findFirst();
                            if(selectedMeter != null){
                                if(!selectedMeter.getRecords().isEmpty()){
                                    for(WaterMeter_records rec : selectedMeter.getRecords()){
                                        c2.setTime(rec.getRecordDate());
                                        if( c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR) == 0 && c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH) == 0 &&
                                                c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH) == 0){
                                            //Delete record
                                            success.set(selectedMeter.DeleteRecord(rec));
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                        break;
                }

                if(success.get()){
                    Log.v("DELETE", "Electric meter record successfully deleted");
                }
            }
        }


        return success.get();
    }


    //check if meter already exists
    public boolean CheckIfMeterNameDoesntExist(String newMetername){
        backgroundThreadRealm = Realm.getDefaultInstance();

        if (null != user) {
            if (user.isLoggedIn()) {
                switch (meterType){
                    case "Electric":
                        if(backgroundThreadRealm.where(ElectricMeter.class).equalTo("name", newMetername).findFirst() == null){
                            return true;
                        }
                        break;
                    case "Gas":
                        if(backgroundThreadRealm.where(GasMeter.class).equalTo("name", newMetername).findFirst() == null){
                            return true;
                        }
                        break;
                    case "Water":
                        if(backgroundThreadRealm.where(WaterMeter.class).equalTo("name", newMetername).findFirst() == null){
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    //Check if record exist for selected meter
    public boolean CheckIfRecordsExist(){
        backgroundThreadRealm = Realm.getDefaultInstance();

        if (null != user) {
            if (user.isLoggedIn()) {
                switch (meterType){
                    case "Electric":
                        if(backgroundThreadRealm.where(ElectricMeter.class).equalTo("name", meterName).findFirst() != null){
                            if(!backgroundThreadRealm.where(ElectricMeter.class).equalTo("name", meterName).findFirst().getMy_meter_records().isEmpty()){
                                return true;
                            }
                        }

                        break;
                    case "Gas":
                        if(backgroundThreadRealm.where(GasMeter.class).equalTo("name", meterName).findFirst() != null){
                            if(!backgroundThreadRealm.where(GasMeter.class).equalTo("name", meterName).findFirst().getRecords().isEmpty()){
                                return true;
                            }
                        }

                        break;
                    case "Water":
                        if(backgroundThreadRealm.where(WaterMeter.class).equalTo("name", meterName).findFirst() != null){
                            if(!backgroundThreadRealm.where(WaterMeter.class).equalTo("name", meterName).findFirst().getRecords().isEmpty()){
                                return true;
                            }
                        }
                        break;
                    default:
                        return false;

                }

            }
        }
        return false;
    }

    //Close realm
    public void CloseRealm(){
        backgroundThreadRealm.close();
    }



}