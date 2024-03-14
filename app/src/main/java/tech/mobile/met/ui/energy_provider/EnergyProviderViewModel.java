package tech.mobile.met.ui.energy_provider;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.bson.types.ObjectId;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import tech.mobile.met.models.realmentity.Client;
import tech.mobile.met.models.realmentity.Client_energy_provider;
import tech.mobile.met.models.realmentity.Client_energy_provider_invoices;
import tech.mobile.met.models.realmentity.ElectricDevice;

public class EnergyProviderViewModel extends ViewModel {

    //Fields
    private MutableLiveData<String> eTextViewEPName;
    //private Client_energy_provider energy_provider;
    //Realm Auth
    String appID = "app-energy-trends-iiaxj";
    private final App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
            .requestTimeout(30, TimeUnit.SECONDS).build());
    User user = app.currentUser();
    private Realm backgroundThreadRealm;

    //Constructors
    public EnergyProviderViewModel() {
        eTextViewEPName = new MutableLiveData<>();
        //energy_provider = new Client_energy_provider();
    }

    //Getters and setters

    public LiveData<String>  geteTextViewEPName() {
        return eTextViewEPName;
    }
    public void seteTextViewEPName(MutableLiveData<String> eTextViewEPName) {
        this.eTextViewEPName = eTextViewEPName;
    }

    //Methods

    // Get EP name
    public void LoadEPName(){
        backgroundThreadRealm = Realm.getDefaultInstance();

        if (null != user) {
            if (user.isLoggedIn()) {
                Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();

                if(client != null){
                    eTextViewEPName.setValue(client.getEnergy_provider().getEp_name());
                }

            }
        }
    }

    // Check if an invoice has already been added
    public boolean CheckIfInvoicesExist(){
        boolean result = false;
        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {
                Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                if(client != null){
                    if(!client.getEnergy_provider().getInvoices().isEmpty()){
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    // Get all invoices
    public RealmList<Client_energy_provider_invoices> GetAllClientInvoice(){
        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {
                Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                if(client != null){
                    return client.getEnergy_provider().getInvoices();
                }
            }
        }
        return null;
    }

    // Update Energy provider name
    public void UpdateEnergyProviderName(String epName){
        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {
                backgroundThreadRealm.executeTransaction( transactionRealm -> {
                    Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                    if(client != null){
                        Log.v("REALMUPDATE", "Found current client in realm");
                        client.getEnergy_provider().setEp_name(epName);
                        Log.v("REALMUPDATE", "Updated provider");
                    }
                });
            }
        }
    }

    public void CloseRealm(){
        backgroundThreadRealm.close();
    }

}