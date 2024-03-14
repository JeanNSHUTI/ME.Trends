package tech.mobile.met.ui.meters;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import tech.mobile.met.models.realmentity.Client;
import tech.mobile.met.models.realmentity.ElectricMeter;
import tech.mobile.met.models.realmentity.GasMeter;
import tech.mobile.met.models.realmentity.WaterMeter;
import tech.mobile.met.models.viewmodels.MeterViewModel;

public class MetersViewModel extends ViewModel {
    //Fields
    //Realm Auth
    String appID = "app-energy-trends-iiaxj";
    private final App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
            .requestTimeout(30, TimeUnit.SECONDS).build());
    User user = app.currentUser();
    private Realm backgroundThreadRealm;

    //Constructor
    public MetersViewModel(){

    }


    //Methods
    public boolean CheckIfGasMeterExists(RealmResults<GasMeter> gasMeters){
        boolean result = gasMeters.size() > 0;
        return result;
    }
    public boolean CheckIfWatersMeterExists(RealmResults<WaterMeter> waterMeters){
        boolean result = waterMeters.size() > 0;
        return result;
    }
    public boolean CheckIfElecMeterExists(RealmResults<ElectricMeter> elecMeters){
        boolean result = elecMeters.size() > 0;
        return result;
    }


    //Create meters in realm
    public boolean CreateMeter(String meterType){
        AtomicBoolean result = new AtomicBoolean(false);
        backgroundThreadRealm = Realm.getDefaultInstance();

        if (null != user) {
            if (user.isLoggedIn()) {
                //Create electric meter if gas and water meters are null
                switch (meterType) {
                    case "Electric Meter":
                        //create electric meter
                        backgroundThreadRealm.executeTransaction(transactionRealm -> {
                            Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                            ElectricMeter newElecMeter = new ElectricMeter("New Electric Meter", client, user.getId());
                            newElecMeter.setType("Electric");
                            //Check if meter already exists with this name
                            if(backgroundThreadRealm.where(ElectricMeter.class).equalTo("name", newElecMeter.getName()).findFirst() == null){
                                transactionRealm.insert(newElecMeter);
                                Log.v("CREATE_METER", "Electric meter successfully created");
                                result.set(true);
                            }


                        });

                        break;
                    case "Gas Meter":
                        // Create gas meter
                        backgroundThreadRealm.executeTransaction(transactionRealm -> {
                            Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                            GasMeter newGasMeter = new GasMeter("New Gas Meter", client, user.getId());
                            newGasMeter.setType("Gas");
                            //Check if meter already exists with this name
                            if(backgroundThreadRealm.where(GasMeter.class).equalTo("name", newGasMeter.getName()).findFirst() == null){
                                transactionRealm.insert(newGasMeter);
                                Log.v("CREATE_METER", "Electric meter successfully created");
                                result.set(true);
                            }
                        });
                        break;
                    case "Water Meter":
                        //Create water meter
                        backgroundThreadRealm.executeTransaction(transactionRealm -> {
                            Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                            WaterMeter newWaterMeter = new WaterMeter("New Water Meter", client, user.getId());
                            newWaterMeter.setType("Water");
                            if(backgroundThreadRealm.where(WaterMeter.class).equalTo("name", newWaterMeter.getName()).findFirst() == null){
                                transactionRealm.insert(newWaterMeter);
                                Log.v("CREATE_METER", "Electric meter successfully created");
                                result.set(true);
                            }
                        });
                        break;
                }
            }
        }
        return result.get();
    }

    //Check empty after collecting all meters
    public boolean CheckIfAnyMetersExist(RealmList<MeterViewModel> myMeters){
        if(!myMeters.isEmpty()){
            return true;
        }
        return false;
    }

    //Get all Meters
    public RealmList<MeterViewModel>CollectAllMeters(){
        RealmList<MeterViewModel> meterViewModels = new RealmList<MeterViewModel>();
        RealmResults<GasMeter> gasMeters = GetAllGasMeters();
        RealmResults<WaterMeter> waterMeters = GetAllWaterMeters();
        RealmResults<ElectricMeter> electricMeters = GetAllElecMeters();

        //Gas meters
        if(CheckIfGasMeterExists(gasMeters)){
            //Affect meters to view models
            int i = 0;
            for(GasMeter gs : gasMeters.subList(0, gasMeters.size())){
                //If meter already has a record saved
                if(!gs.getRecords().isEmpty()){
                    MeterViewModel mviewModel = new MeterViewModel(gs.getRecords().get(i).getRecordDate(),
                            gs.getName(), gs.getRecords().get(i).getConsumption(), gs.getRecords().get(i).getCurrentReading());
                    mviewModel.setType(gs.getType());
                    meterViewModels.add(mviewModel);
                }
                else{
                    MeterViewModel meterViewModel = new MeterViewModel(gs.getName());
                    meterViewModel.setType(gs.getType());
                    meterViewModels.add(meterViewModel);
                }
                i++;
            }
        }

        //Electric meter
        if(CheckIfElecMeterExists(electricMeters)){
            //Affect meters to view models
            int i = 0;
            for(ElectricMeter em : electricMeters.subList(0, electricMeters.size())){
                //If meter already has a record saved
                if(!em.getMy_meter_records().isEmpty()){
                    MeterViewModel mviewModel = new MeterViewModel(em.getMy_meter_records().get(i).getRecord_date(),
                            em.getName(), em.getMy_meter_records().get(i).getConsumption(), em.getMy_meter_records().get(i).getCurrent_reading());
                    mviewModel.setType(em.getType());
                    meterViewModels.add(mviewModel);
                }
                else{
                    MeterViewModel meterViewModel = new MeterViewModel(em.getName());
                    meterViewModel.setType(em.getType());
                    meterViewModels.add(meterViewModel);
                }
                i++;
            }
        }

        //Water
        if(CheckIfWatersMeterExists(waterMeters)){
            //Affect meters to view models
            int i = 0;
            for(WaterMeter wm : waterMeters.subList(0, waterMeters.size())){
                //If meter already has a record saved
                if(!wm.getRecords().isEmpty()){
                    MeterViewModel mviewModel = new MeterViewModel(wm.getRecords().get(i).getRecordDate(),
                            wm.getName(), wm.getRecords().get(i).getConsumption(), wm.getRecords().get(i).getCurrentReading());
                    mviewModel.setType(wm.getType());
                    meterViewModels.add(mviewModel);
                }
                else{
                    MeterViewModel meterViewModel = new MeterViewModel(wm.getName());
                    meterViewModel.setType(wm.getType());
                    meterViewModels.add(meterViewModel);
                }
                i++;
            }
        }

        return meterViewModels;
    }

    //Meters in Realm
    public RealmResults<GasMeter> GetAllGasMeters(){
        backgroundThreadRealm = Realm.getDefaultInstance();

        return backgroundThreadRealm.where(GasMeter.class).findAll();
    }
    public RealmResults<WaterMeter> GetAllWaterMeters(){
        backgroundThreadRealm = Realm.getDefaultInstance();

        return backgroundThreadRealm.where(WaterMeter.class).findAll();
    }
    public RealmResults<ElectricMeter> GetAllElecMeters(){
        backgroundThreadRealm = Realm.getDefaultInstance();

        return backgroundThreadRealm.where(ElectricMeter.class).findAll();
    }

    //Delete Meter
    public boolean DeleteMeterFromRealm(String meterNameType){
        AtomicBoolean result = new AtomicBoolean(false);
        if(meterNameType.contains("-")){
            String[] meterNameNType = meterNameType.split("-");
            String type = meterNameNType[0].trim(); //Type;
            String name = meterNameNType[1].trim(); //name

            backgroundThreadRealm = Realm.getDefaultInstance();
            backgroundThreadRealm.executeTransaction( transactionRealm -> {
                switch (type){
                    case "Gas":
                        GasMeter currentGMeter = transactionRealm.where(GasMeter.class).equalTo("name", name).findFirst();
                        if(currentGMeter != null){
                            currentGMeter.deleteFromRealm();
                            result.set(true);
                        }
                        else {
                            Log.v("REALMDELETE", "Did not find current device in realm for delete");
                        }
                        break;
                    case "Electric":
                        ElectricMeter currentEMeter = transactionRealm.where(ElectricMeter.class).equalTo("name", name).findFirst();
                        if(currentEMeter != null){
                            currentEMeter.deleteFromRealm();
                            result.set(true);
                        }
                        else {
                            Log.v("REALMDELETE", "Did not find current device in realm for delete");
                        }
                        break;
                    case "Water":
                        WaterMeter currentWMeter = transactionRealm.where(WaterMeter.class).equalTo("name", name).findFirst();
                        if(currentWMeter != null){
                            currentWMeter.deleteFromRealm();
                            result.set(true);
                        }
                        else {
                            Log.v("REALMDELETE", "Did not find current device in realm for delete");
                        }
                        break;
                }

            });

        }

        return result.get();
    }

    //Close realm
    public void CloseRealm(){
        backgroundThreadRealm.close();
    }
}