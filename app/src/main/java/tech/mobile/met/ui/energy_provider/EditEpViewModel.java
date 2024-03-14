package tech.mobile.met.ui.energy_provider;

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

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import tech.mobile.met.models.realmentity.Client;
import tech.mobile.met.models.realmentity.Client_energy_provider_invoices;

public class EditEpViewModel extends ViewModel {
    // Fields
    private MutableLiveData<String> textViewGasCon;
    private MutableLiveData<String> textViewElecCon;
    private MutableLiveData<String> textViewWaterCon;
    private MutableLiveData<String> etextViewGasCon;
    private MutableLiveData<String> etextViewElecCon;
    private MutableLiveData<String> etextViewWaterCon;
    private MutableLiveData<String> etextViewStarDate;
    private MutableLiveData<String> etextViewEndDate;
    private MutableLiveData<String> etextViewConCharge;
    private MutableLiveData<String> etextViewCostKwh;
    //Realm Auth
    String appID = "app-energy-trends-iiaxj";
    private final App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
            .requestTimeout(30, TimeUnit.SECONDS).build());
    User user = app.currentUser();
    private Realm backgroundThreadRealm;

    public EditEpViewModel(){
        textViewElecCon = new MutableLiveData<>();
        textViewWaterCon = new MutableLiveData<>();
        textViewGasCon = new MutableLiveData<>();
        etextViewConCharge = new MutableLiveData<>();
        etextViewCostKwh = new MutableLiveData<>();
        etextViewStarDate = new MutableLiveData<>();
        etextViewEndDate = new MutableLiveData<>();
        etextViewWaterCon = new MutableLiveData<>();
        etextViewElecCon = new MutableLiveData<>();
        etextViewGasCon = new MutableLiveData<>();
    }

    // Getters and setters

    public LiveData<String> getTextViewGasCon() {
        return textViewGasCon;
    }
    public void setTextViewGasCon(MutableLiveData<String> textViewGasCon) {
        this.textViewGasCon = textViewGasCon;
    }
    public LiveData<String> getTextViewWaterCon() {
        return textViewWaterCon;
    }
    public void setTextViewWaterCon(MutableLiveData<String> textViewWaterCon) {
        this.textViewWaterCon = textViewWaterCon;
    }
    public LiveData<String> getTextViewElecCon() {
        return textViewElecCon;
    }
    public void setTextViewElecCon(MutableLiveData<String> textViewElecCon) {
        this.textViewElecCon = textViewElecCon;
    }

    // Methods

    //Update yearly consumptions
    public void UpdateConsumptions(){
        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {
                backgroundThreadRealm.executeTransaction( transactionRealm -> {
                    Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                    if(client != null){
                        Log.v("REALMUPDATE", "Found current client in realm");
                        if(!client.getEnergy_provider().getInvoices().isEmpty()){
                            client.setElectric_consumption_per_annum(client.getEnergy_provider().getInvoices().last().getElec_yearly_consumption());
                            client.setGas_consumption_per_annum(client.getEnergy_provider().getInvoices().last().getGas_yearly_consumption());
                            textViewGasCon.setValue(String.valueOf(client.getGas_consumption_per_annum()));
                            textViewElecCon.setValue(String.valueOf(client.getElectric_consumption_per_annum()));
                            textViewWaterCon.setValue(String.valueOf(client.getEnergy_provider().getInvoices().last().getWater_yearly_consumption()));
                        }
                        else {
                            Log.v("REALM_GET", "Updated provider");
                            textViewGasCon.setValue("Please add an invoice");
                            textViewWaterCon.setValue("Please add an invoice");
                            textViewElecCon.setValue("Please add an invoice");
                        }
                    }
                });
            }
        }

    }

    //Add new invoice
    public void Addinvoice(String[] invoiceInfo){
        //Convert dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {
                backgroundThreadRealm.executeTransaction (transactionRealm -> {
                    Client_energy_provider_invoices invoice = new Client_energy_provider_invoices();
                    try {
                        invoice.setInvoice_start_date(dateFormat.parse(invoiceInfo[0]));
                        invoice.setInvoice_end_date(dateFormat.parse(invoiceInfo[1]));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    invoice.setGas_yearly_consumption(Integer.parseInt(invoiceInfo[2]));
                    invoice.setElec_yearly_consumption(Integer.parseInt(invoiceInfo[3]));
                    invoice.setWater_yearly_consumption(Integer.parseInt(invoiceInfo[4]));
                    invoice.setConsumptionCharge(Double.parseDouble(invoiceInfo[5]));
                    invoice.setCostPerKwh(Double.parseDouble(invoiceInfo[6]));
                    Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                    if(client != null){
                        client.getEnergy_provider().Addinvoice(invoice);
                    }
                    Log.v("CREATEDEVICE", "Device successfully created");
                });
            }
        }
    }



    //Get view data if updating invoice
    public void LoadEditInvoice(String invoiceEDate){
        //Convert dates
        Log.v("RECEIPTS", "Received date: " + invoiceEDate);
        String FORMAT = "yyyy/MM/dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = new Date();
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        try {
            endDate = dateFormat.parse(invoiceEDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {
                c1.setTime(endDate);
                backgroundThreadRealm.executeTransaction (transactionRealm -> {
                    Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();
                    if(client != null){
                        if(!client.getEnergy_provider().getInvoices().isEmpty()){
                            for(Client_energy_provider_invoices invoice : client.getEnergy_provider().getInvoices()){
                                c2.setTime(invoice.getInvoice_end_date());
                                if( c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR) == 0 && c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH) == 0 &&
                                        c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH) == 0){
                                    String sdate = android.text.format.DateFormat.format(FORMAT, invoice.getInvoice_start_date()).toString();
                                    String edate = android.text.format.DateFormat.format(FORMAT, invoice.getInvoice_end_date()).toString();
                                    etextViewStarDate.setValue(sdate);
                                    etextViewEndDate.setValue(edate);
                                    etextViewGasCon.setValue(String.valueOf(invoice.getGas_yearly_consumption()));
                                    etextViewElecCon.setValue(String.valueOf(invoice.getElec_yearly_consumption()));
                                    etextViewWaterCon.setValue(String.valueOf(invoice.getWater_yearly_consumption()));
                                    etextViewConCharge.setValue(String.valueOf(invoice.getConsumptionCharge()) + " €");
                                    etextViewCostKwh.setValue(String.valueOf(invoice.getCostPerKwh()) + " c€/KWh");
                                }
                            }
                        }
                    }
                    //Log.v("LOADRECEIPT", "Failed to load receipt");
                });
            }
        }
    }




    //Remove invoice
    public void DeleteInvoice(String invoiceEDate){
        //Convert dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date endDate = new Date();
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        AtomicBoolean success = new AtomicBoolean(false);

        try {
            endDate = dateFormat.parse(invoiceEDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        backgroundThreadRealm = Realm.getDefaultInstance();
        if (null != user) {
            if (user.isLoggedIn()) {
                c1.setTime(endDate);
                backgroundThreadRealm.executeTransaction (transactionRealm -> {
                    Client client = backgroundThreadRealm.where(Client.class).equalTo("email", user.getProfile().getEmail()).findFirst();

                    if(client != null){
                        if(!client.getEnergy_provider().getInvoices().isEmpty()){
                            for(Client_energy_provider_invoices invoice : client.getEnergy_provider().getInvoices()){
                                c2.setTime(invoice.getInvoice_end_date());
                                if( c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR) == 0 && c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH) == 0 &&
                                        c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH) == 0){
                                    success.set(client.getEnergy_provider().DeleteInvoice(invoice));
                                }
                            }
                        }
                    }
                    if(success.get()){
                        Log.v("DELETE", "Invoice successfully deleted");
                    }
                });
            }
        }

    }


    //Close realm
    public void CloseRealm(){
        backgroundThreadRealm.close();
    }

    public LiveData<String> getEtextViewGasCon() {
        return etextViewGasCon;
    }
    public void setEtextViewGasCon(MutableLiveData<String> etextViewGasCon) {
        this.etextViewGasCon = etextViewGasCon;
    }
    public LiveData<String> getEtextViewElecCon() {
        return etextViewElecCon;
    }
    public void setEtextViewElecCon(MutableLiveData<String> etextViewElecCon) {
        this.etextViewElecCon = etextViewElecCon;
    }
    public LiveData<String> getEtextViewWaterCon() {
        return etextViewWaterCon;
    }
    public void setEtextViewWaterCon(MutableLiveData<String> etextViewWaterCon) {
        this.etextViewWaterCon = etextViewWaterCon;
    }
    public LiveData<String> getEtextViewStarDate() {
        return etextViewStarDate;
    }
    public void setEtextViewStarDate(MutableLiveData<String> etextViewStarDate) {
        this.etextViewStarDate = etextViewStarDate;
    }
    public LiveData<String> getEtextViewEndDate() {
        return etextViewEndDate;
    }
    public void setEtextViewEndDate(MutableLiveData<String> etextViewEndDate) {
        this.etextViewEndDate = etextViewEndDate;
    }
    public LiveData<String> getEtextViewConCharge() {
        return etextViewConCharge;
    }
    public void setEtextViewConCharge(MutableLiveData<String> etextViewConCharge) {
        this.etextViewConCharge = etextViewConCharge;
    }
    public LiveData<String> getEtextViewCostKwh() {
        return etextViewCostKwh;
    }
    public void setEtextViewCostKwh(MutableLiveData<String> etextViewCostKwh) {
        this.etextViewCostKwh = etextViewCostKwh;
    }
}