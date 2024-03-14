package tech.mobile.met.ui.devices;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.bson.types.ObjectId;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;
import tech.mobile.met.models.realmentity.Client;
import tech.mobile.met.models.realmentity.ElectricDevice;

public class DevicesViewModel extends ViewModel {
    //Fields
    //Realm Auth
    String appID = "app-energy-trends-iiaxj";
    private final App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
            .requestTimeout(30, TimeUnit.SECONDS).build());
    User user = app.currentUser();
    private Realm backgroundThreadRealm;

    //Constructors
    public DevicesViewModel() {}

    //Methods
    public boolean CheckIfDevicesExist(RealmResults<ElectricDevice> devices){
        boolean result = devices.size() > 0;
        return result;
    }

    public void CreateDevice(){
        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {
                backgroundThreadRealm.executeTransaction (transactionRealm -> {
                    //ElectricDevice newDevice = transactionRealm.createObject(ElectricDevice.class, new ObjectId());
                    ElectricDevice newDevice = new ElectricDevice(user.getId());
                    newDevice.setName("New Device");
                    Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                    if(client != null){
                        newDevice.setClientoid(client);
                    }
                    newDevice.setNumber_of_devices(1);
                    newDevice.setCostPerDay(0.0);
                    newDevice.setCostPerWatt(0.0);
                    //newDevice.set_partition(user.getId());
                    transactionRealm.insert(newDevice);
                    Log.v("CREATEDEVICE", "Device successfully created");
                });
                //backgroundThreadRealm.close();
            }
        }

    }

    //Update all device cost per day and and cost per watt
    public void UpdateDeviceCosts(){
        //Double cost_watt;
        RealmResults<ElectricDevice> allDevices = GetAllDevices();
        if(!allDevices.isEmpty()){
            backgroundThreadRealm.executeTransaction(transactionRealm -> {
                Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                for(ElectricDevice device : allDevices.subList(0, allDevices.size())){
                    Double cost_watt = (Objects.requireNonNull(client.getEnergy_provider().getInvoices().last()).getCostPerKwh() / 1000.0) * device.getPower_rating() * device.getHours_used_per_day();
                    Double cost_day = (Objects.requireNonNull(client.getEnergy_provider().getInvoices().last()).getCostPerKwh() / 1000.0) * device.getPower_rating() * 24;
                    device.setCostPerWatt(cost_watt);
                    device.setCostPerDay(cost_day);

                }

            });

        }
    }

    //Check if an invoice has been set
    public boolean CheckIfInvoiceExists(){
        if (null != user){
            if(user.isLoggedIn()){
                Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                if(client != null){
                    if(!client.getEnergy_provider().getInvoices().isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Get all devices
    public RealmResults<ElectricDevice> GetAllDevices(){
        backgroundThreadRealm = Realm.getDefaultInstance();

        return backgroundThreadRealm.where(ElectricDevice.class).findAll();
    }

    public void CloseRealm(){
        backgroundThreadRealm.close();
    }
}