package tech.mobile.met.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.bson.types.ObjectId;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;
import tech.mobile.met.models.realmentity.Client;
import tech.mobile.met.models.realmentity.Client_Address;
import tech.mobile.met.models.realmentity.Client_energy_provider;

public class EditClientViewModel extends ViewModel {
    // Fields
    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> textViewWelcome;
    private MutableLiveData<String> textViewFullname;
    private MutableLiveData<String> textViewEmail;
    private MutableLiveData<String> textViewEnergyProvider;
    private MutableLiveData<String> textViewStreetNo;
    private MutableLiveData<String> textViewCityPC;
    private MutableLiveData<String> textViewProvinceCountry;
    //Realm Auth
    final String appID = "app-energy-trends-iiaxj";
    private App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
                .requestTimeout(30, TimeUnit.SECONDS).build());
    User user = app.currentUser();
    ObjectId syncConPartition = new ObjectId(user.getId());
    private Realm backgroundThreadRealm;

    SyncConfiguration config = new SyncConfiguration.Builder(user, syncConPartition)
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build();
    /*SyncConfiguration config = new SyncConfiguration.Builder(user, syncConPartition).build();*/

    //Constructor
    public EditClientViewModel(){
        mText = new MutableLiveData<>();
        textViewWelcome = new MutableLiveData<>();
        textViewFullname = new MutableLiveData<>();
        textViewEmail = new MutableLiveData<>();
        textViewEnergyProvider = new MutableLiveData<>();
        textViewStreetNo = new MutableLiveData<>();
        textViewCityPC = new MutableLiveData<>();
        textViewProvinceCountry = new MutableLiveData<>();
        mText.setValue("This is edit client fragment");
        //backgroundThreadRealm = Realm.getDefaultInstance();
    }

    //Getters and setters
    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getWelcomeText() {
        return textViewWelcome;
    }
    public LiveData<String> getEmailText() {
        return textViewEmail;
    }
    public LiveData<String> getFullnameText(){
        return textViewFullname;
    }
    public LiveData<String> getEnergyProviderText(){
        return textViewEnergyProvider;
    }
    public LiveData<String> getStreetNoText(){
        return textViewStreetNo;
    }
    public LiveData<String> getViewCityPCText(){
        return textViewCityPC;
    }
    public LiveData<String> getViewProvinceCountryText(){
        return textViewProvinceCountry;
    }

    //Methods
    public void UpdateClientInfo(String[] client_details) {
        backgroundThreadRealm = Realm.getDefaultInstance();

        if (null != user) {
            if (user.isLoggedIn()) {
                // all modifications to a realm must happen inside of a write block
                // Update current user
                backgroundThreadRealm.executeTransaction( transactionRealm -> {
                    Client currentClient = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                    if(currentClient != null){
                        Log.v("REALMUPDATE", "Found current saved client in realm");
                        currentClient.setFirstname(client_details[0]);
                        currentClient.setLastname(client_details[1]);
                        if(currentClient.getEnergy_provider() == null){
                            Client_energy_provider client_Eprovider = new Client_energy_provider(client_details[2]);
                            currentClient.setEnergy_provider(client_Eprovider);
                        }
                        else {
                            currentClient.getEnergy_provider().setEp_name(client_details[2]);
                        }
                        Client_Address client_address = new Client_Address(client_details[3],
                                client_details[4], client_details[5], client_details[6],
                                client_details[7], client_details[8]);
                        currentClient.setAddress(client_address);
                    }
                });
                //backgroundThreadRealm.close();
            }
        }
    }

    public void LoadViewData(){
        backgroundThreadRealm = Realm.getDefaultInstance();

        if (null != user) {
            if (user.isLoggedIn()) {
                RealmQuery<Client> realmClient = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail());
                if(realmClient != null){
                    Log.v("Success", "Fetched object by email key query after creation : " + realmClient.findFirst().getEmail());
                    Log.v("Success", "Fetched object by email key query after creation COID : " + realmClient.findFirst().getClientoid());
                    textViewWelcome.setValue("Welcome " + realmClient.findFirst().getLastname());
                    textViewEmail.setValue(realmClient.findFirst().getEmail());
                    if(!realmClient.findFirst().getLastname().isEmpty()){
                        textViewFullname.setValue(realmClient.findFirst().getFirstname() + " " + realmClient.findFirst().getLastname());
                    }
                    if(realmClient.findFirst().getEnergy_provider() != null){
                        textViewEnergyProvider.setValue(realmClient.findFirst().getEnergy_provider().getEp_name());
                    }
                    if(realmClient.findFirst().getAddress() != null){
                        textViewStreetNo.setValue(realmClient.findFirst().getAddress().getStreetname() + ", " + realmClient.findFirst().getAddress().getHousenumber());
                    }
                    if(realmClient.findFirst().getAddress() != null){
                        textViewCityPC.setValue(realmClient.findFirst().getAddress().getCity() + ", " + realmClient.findFirst().getAddress().getPostalcode());
                    }
                    if(realmClient.findFirst().getAddress() != null){
                        textViewProvinceCountry.setValue(realmClient.findFirst().getAddress().getState_province() + ", " + realmClient.findFirst().getAddress().getCountry());
                    }

                }

            }
        }
    }

    public void CloseRealm(){
        //backgroundThreadRealm = Realm.getDefaultInstance();
        if(backgroundThreadRealm != null){
            backgroundThreadRealm.close();
        }

    }

}