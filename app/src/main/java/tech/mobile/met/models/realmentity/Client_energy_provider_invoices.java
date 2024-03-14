package tech.mobile.met.models.realmentity;

import org.bson.types.Decimal128;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass(embedded = true)
public class Client_energy_provider_invoices extends RealmObject {
    //Fields
    @Required
    private Date invoice_start_date;
    @Required
    private Date invoice_end_date;
    private Integer gas_yearly_consumption;
    private Integer elec_yearly_consumption;
    private Integer water_yearly_consumption;
    private Double consumption_charge;
    //@Required
    private Double cost_per_kwh;

    //Constructor
    public Client_energy_provider_invoices(){}

    public Client_energy_provider_invoices(Date start, Date end, Integer gas_consumption, Integer elec_consumption,
                                           Integer water_consumption, Double charge, Double cost_per_kwh){
        this.invoice_start_date = start;
        this.invoice_end_date = end;
        this.gas_yearly_consumption = gas_consumption;
        this.elec_yearly_consumption = elec_consumption;
        this.water_yearly_consumption = water_consumption;
        this.consumption_charge = charge;
        this.cost_per_kwh = cost_per_kwh;
    }

    //Getters and Setters
    public Date getInvoice_start_date() {
        return invoice_start_date;
    }
    public void setInvoice_start_date(Date invoice_start_date) {
        this.invoice_start_date = invoice_start_date;
    }

    public Date getInvoice_end_date() {
        return invoice_end_date;
    }
    public void setInvoice_end_date(Date invoice_end_date) {
        this.invoice_end_date = invoice_end_date;
    }

    public Integer getGas_yearly_consumption() {
        return gas_yearly_consumption;
    }
    public void setGas_yearly_consumption(Integer gas_yearly_consumption) {
        this.gas_yearly_consumption = gas_yearly_consumption;
    }

    public Integer getElec_yearly_consumption() {
        return elec_yearly_consumption;
    }
    public void setElec_yearly_consumption(Integer elec_yearly_consumption) {
        this.elec_yearly_consumption = elec_yearly_consumption;
    }

    public Integer getWater_yearly_consumption() {
        return water_yearly_consumption;
    }
    public void setWater_yearly_consumption(Integer water_yearly_consumption) {
        this.water_yearly_consumption = water_yearly_consumption;
    }

    public Double getConsumptionCharge() { return consumption_charge; }
    public void setConsumptionCharge(Double consumption_charge) { this.consumption_charge = consumption_charge; }
    public Double getCostPerKwh() { return cost_per_kwh; }
    public void setCostPerKwh(Double cost_per_kwh) { this.cost_per_kwh = cost_per_kwh; }
}
