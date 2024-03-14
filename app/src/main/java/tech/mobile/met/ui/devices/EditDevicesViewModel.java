package tech.mobile.met.ui.devices;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.bson.types.ObjectId;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;
import tech.mobile.met.R;
import tech.mobile.met.models.realmentity.Client;
import tech.mobile.met.models.realmentity.Client_Address;
import tech.mobile.met.models.realmentity.Client_energy_provider;
import tech.mobile.met.models.realmentity.ElectricDevice;
import tech.mobile.met.ui.home.EditClientViewModel;

public class EditDevicesViewModel extends ViewModel {
    // Fields
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private MutableLiveData<String> textViewWelcomeName;
    private MutableLiveData<String> textViewEname;
    private MutableLiveData<String> textViewEHousUsed;
    private MutableLiveData<String> textViewCostWatt;
    private MutableLiveData<String> textViewCostDay;
    private MutableLiveData<String> textViewPowerRating;
    private MutableLiveData<String> textViewNumberDevices;
    //Realm Auth
    String appID = "app-energy-trends-iiaxj";
    private final App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
            .requestTimeout(30, TimeUnit.SECONDS).build());
    User user = app.currentUser();
    ObjectId syncConPartition = new ObjectId(user.getId());
    SyncConfiguration config = new SyncConfiguration.Builder(user, syncConPartition)
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build();
    private Realm backgroundThreadRealm;

    //Constructors
    public EditDevicesViewModel(){
        textViewWelcomeName = new MutableLiveData<>();
        textViewNumberDevices = new MutableLiveData<>();
        textViewEHousUsed = new MutableLiveData<>();
        textViewEname = new MutableLiveData<>();
        textViewPowerRating = new MutableLiveData<>();
        textViewCostDay = new MutableLiveData<>();
        textViewCostWatt = new MutableLiveData<>();
        //backgroundThreadRealm = Realm.getInstance(config);
    }

    // Getters and setters
    public LiveData<String> getTextViewEname() {
        return textViewEname;
    }

    public void setTextViewEname(MutableLiveData<String> textViewEname) {
        this.textViewEname = textViewEname;
    }

    public LiveData<String> getTextViewEHousUsed() {
        return textViewEHousUsed;
    }

    public void setTextViewEHousUsed(MutableLiveData<String> textViewEHousUsed) {
        this.textViewEHousUsed = textViewEHousUsed;
    }

    public LiveData<String> getTextViewCostWatt() {
        return textViewCostWatt;
    }

    public void setTextViewCostWatt(MutableLiveData<String> textViewCostWatt) {
        this.textViewCostWatt = textViewCostWatt;
    }

    public LiveData<String> getTextViewCostDay() {
        return textViewCostDay;
    }

    public void setTextViewCostDay(MutableLiveData<String> textViewCostDay) {
        this.textViewCostDay = textViewCostDay;
    }

    public LiveData<String> getTextViewPowerRating() {
        return textViewPowerRating;
    }

    public void setTextViewPowerRating(MutableLiveData<String> textViewPowerRating) {
        this.textViewPowerRating = textViewPowerRating;
    }

    public LiveData<String> getTextViewNumberDevices() {
        return textViewNumberDevices;
    }

    public void setTextViewNumberDevices(MutableLiveData<String> textViewNumberDevices) {
        this.textViewNumberDevices = textViewNumberDevices;
    }
    public LiveData<String> getTextViewWelcomeName() { return textViewWelcomeName; }
    public void setTextViewWelcomeName(MutableLiveData<String> textViewWelcomeName) {
        this.textViewWelcomeName = textViewWelcomeName;
    }

    //Methods

    //Delete device
    public void DeleteDeviceFromRealm(String deviceName){
        backgroundThreadRealm = Realm.getDefaultInstance();
        backgroundThreadRealm.executeTransaction( transactionRealm -> {
            ElectricDevice currentDevice = transactionRealm.where(ElectricDevice.class).equalTo("name", deviceName).findFirst();
            if(currentDevice != null){
                currentDevice.deleteFromRealm();
            }
            else {
                Log.v("REALMDELETE", "Did not find current device in realm for delete");
            }
        });
    }

    // Get Device info from realm
    public void GetSelectedDevice(String deviceName){
        backgroundThreadRealm = Realm.getDefaultInstance();
        ElectricDevice device = backgroundThreadRealm.where(ElectricDevice.class).equalTo("name", deviceName).findFirst();
        if(device != null){
            textViewWelcomeName.setValue(device.getName());
            textViewEname.setValue(device.getName());
            textViewEHousUsed.setValue(String.valueOf(device.getHours_used_per_day()));
            textViewPowerRating.setValue(String.valueOf(device.getPower_rating()));
            textViewNumberDevices.setValue(String.valueOf(device.getNumber_of_devices()));
            Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
            if(client != null){
                if(!client.getEnergy_provider().getInvoices().isEmpty()){
                    textViewCostDay.setValue(df.format(device.getCostPerDay()) + "€");
                    textViewCostWatt.setValue(df.format(device.getCostPerWatt()) + "€");
                }
                else {
                    Log.v("EDITDEVICES", "Landed here 1");
                    textViewCostDay.setValue("Please update e-provider settings");
                    textViewCostWatt.setValue("Please update e-provider settings");
                }
            }

        }
    }

    //Update device
    public void UpdateDevice(String deviceName, String[] deviceInfo){
        backgroundThreadRealm = Realm.getDefaultInstance();

        if (null != user){
            if(user.isLoggedIn()){
                backgroundThreadRealm.executeTransaction( transactionRealm -> {
                    ElectricDevice currentDevice = backgroundThreadRealm.where(ElectricDevice.class).equalTo("name", deviceName).findFirst();
                    if(currentDevice != null){
                        Log.v("REALMUPDATE", "Found current device in realm");
                        currentDevice.setName(deviceInfo[0]);
                        currentDevice.setHours_used_per_day(Integer.parseInt(deviceInfo[1]));
                        currentDevice.setPower_rating(Integer.parseInt(deviceInfo[2]));
                        currentDevice.setNumber_of_devices(Integer.parseInt(deviceInfo[3]));
                        Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                        if(client != null){
                            if(!client.getEnergy_provider().getInvoices().isEmpty()){
                                Double cost_watt = (Objects.requireNonNull(client.getEnergy_provider().getInvoices().last()).getCostPerKwh() / 1000.0) * currentDevice.getPower_rating() * currentDevice.getHours_used_per_day();
                                Double cost_day = (Objects.requireNonNull(client.getEnergy_provider().getInvoices().last()).getCostPerKwh() / 1000.0) * currentDevice.getPower_rating() * 24;
                                currentDevice.setCostPerWatt(cost_watt);
                                currentDevice.setCostPerDay(cost_day);
                            }
                            else{
                                currentDevice.setCostPerWatt(0.0);
                                currentDevice.setCostPerDay(0.0);
                            }
                        }
                        Log.v("REALMUPDATE", "Updated device");
                    }
                });
                //backgroundThreadRealm.close();
            }
        }

    }

    public void CloseRealm(){
        backgroundThreadRealm.close();
    }

}