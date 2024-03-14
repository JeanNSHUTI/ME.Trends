package tech.mobile.met.models.realmentity;

import org.bson.types.ObjectId;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class Client_energy_provider extends RealmObject {
    //Fields
    private String ep_name;
    private RealmList<Client_energy_provider_invoices> invoices;

    //Constructors
    public Client_energy_provider(){}

    public Client_energy_provider(String name, RealmList<Client_energy_provider_invoices> invoices){
        this.ep_name = name;
        this.invoices = invoices;
    }

    public Client_energy_provider(String name){
        this.ep_name = name;
    }

    //Getters and Setters
    public String getEp_name() {
        return ep_name;
    }
    public void setEp_name(String ep_name) {
        this.ep_name = ep_name;
    }

    public RealmList<Client_energy_provider_invoices> getInvoices() {
        return invoices;
    }
    public void setInvoices(RealmList<Client_energy_provider_invoices> invoices) {
        this.invoices = invoices;
    }

    //Methods
    public void Addinvoice(Client_energy_provider_invoices newInvoice){
        this.invoices.add(newInvoice);
    }

    public boolean DeleteInvoice(Client_energy_provider_invoices invoice){
        return this.invoices.remove(invoice);
    }

}
