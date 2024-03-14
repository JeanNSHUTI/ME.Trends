package tech.mobile.met.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.bson.types.ObjectId;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;
import tech.mobile.met.models.realmentity.Client;
import tech.mobile.met.models.realmentity.Client_energy_provider;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    public Client newClient;
    private final MutableLiveData<String> textViewWelcome;
    private MutableLiveData<String> textViewFullname;
    private MutableLiveData<String> textViewEmail;
    private MutableLiveData<String> textViewEnergyProvider;
    private MutableLiveData<String> textViewStreetNo;
    private MutableLiveData<String> textViewCityPC;
    private MutableLiveData<String> textViewProvinceCountry;
    //Realm Auth
    private final static String appID = "app-energy-trends-iiaxj";
    private static final App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
                .requestTimeout(30, TimeUnit.SECONDS).build());
    static User user = app.currentUser();
    private Realm backgroundThreadRealm;

    //Create default sync configuration for current user using email as partition !
    private static final SyncConfiguration config = new SyncConfiguration.Builder(user, user.getId())
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build();

    //final SyncConfiguration config = new SyncConfiguration.Builder(user, user.getId()).build();

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        textViewWelcome = new MutableLiveData<>();
        textViewFullname = new MutableLiveData<>();
        textViewEmail = new MutableLiveData<>();
        textViewEnergyProvider = new MutableLiveData<>();
        textViewStreetNo = new MutableLiveData<>();
        textViewCityPC = new MutableLiveData<>();
        textViewProvinceCountry = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        Realm.setDefaultConfiguration(config);


        //backgroundThreadRealm = Realm.getInstance(config);

        /*if (null != user) {
            if(user.isLoggedIn()){
                Realm.getInstanceAsync(config, new Realm.Callback() {
                    @Override
                    public void onSuccess(Realm realm) {
                        Log.i("Realm Path :", realm.getPath());
                        Log.v("EXAMPLE", "Successfully opened a realm with reads and writes allowed on the UI thread.");
                        // later, you can look up this subscription by name
                        //subscriptionClient = realm.getSubscriptions().find("subscription_client");
                        //Log.v("EXAMPLE", "Found subscription: " + subscriptionClient.getObjectType());
                        RealmQuery<Client> client = realm.where(Client.class).equalTo("email", user.getProfile().getEmail());
                        //If user is newly registered, instantiate new object to save to realm
                        if(null == client.findFirst()){
                            realm.executeTransaction(r -> {
                                // Instantiate the class using the factory function.
                                newClient = r.createObject(Client.class, new ObjectId(user.getId()));
                                //newClient = r.createObject(Client.class, new ObjectId(user.getId()));
                                // Configure the instance.
                                newClient.setEmail(user.getProfile().getEmail());
                                Client_energy_provider default_ep = new Client_energy_provider("Default Name");
                                newClient.setEnergy_provider(default_ep);
                                newClient.setClientoid(new ObjectId(user.getId()), user.getId());
                                Log.v("EXAMPLE", "Created client object in realm: " + newClient.getEmail());
                                Log.v("EXAMPLE", "Created client object in realm id: " + newClient.get_id());
                                r.insert(newClient);
                            });
                        }
                        else {
                            Log.v("EXAMPLE", "Fetched object by email key query id : " + client.findFirst().get_id());
                            //client.findFirst().setEmail(userCred.getEmail());
                            Log.v("EXAMPLE", "Fetched object by email- User email: " + client.findFirst().getEmail());
                        }

                        RealmQuery<Client> realmClient = realm.where(Client.class).equalTo("email", user.getProfile().getEmail());
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
                        else{
                            textViewWelcome.setValue("Welcome");
                        }
                        //realm.close();
                    }
                });
            }
        }*/
    }

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

    //Check if client is connecting for the first time, if so
    //create user and save to realm if not -> collect data for view
    public void UpdateClientData(){
        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {

                backgroundThreadRealm.executeTransaction (r -> {

                    RealmQuery<Client> client = r.where(Client.class).equalTo("email", user.getProfile().getEmail());

                    if(null == client.findFirst()){
                        // Instantiate the class using the factory function.
                        newClient = r.createObject(Client.class, new ObjectId(user.getId()));
                        //newClient = r.createObject(Client.class, new ObjectId(user.getId()));
                        // Configure the instance.
                        newClient.setEmail(user.getProfile().getEmail());
                        Client_energy_provider default_ep = new Client_energy_provider("Default Name");
                        newClient.setEnergy_provider(default_ep);
                        newClient.setClientoid(new ObjectId(user.getId()), user.getId());
                        Log.v("EXAMPLE", "Created client object in realm: " + newClient.getEmail());
                        Log.v("EXAMPLE", "Created client object in realm id: " + newClient.get_id());
                        r.insert(newClient);
                    }
                    else {
                        Log.v("EXAMPLE", "Fetched object by email key query id : " + client.findFirst().get_id());
                        //client.findFirst().setEmail(userCred.getEmail());
                        Log.v("EXAMPLE", "Fetched object by email- User email: " + client.findFirst().getEmail());
                    }
                });
                //backgroundThreadRealm.close();
            }
        }
    }

    public boolean CheckIfClientHasBeenCreated(){
        backgroundThreadRealm = Realm.getInstance(config);
        if(backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst() != null){
            return true;
        }
        return false;
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