package tech.mobile.met.models.realmentity;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class ClientModelDevice_electric_devices extends RealmObject {
    //Fields
    private Double cost_per_day;
    private Double cost_per_watt;
    private Integer hours_used_per_day;
    private String name;
    private Integer number_of_devices;
    private Integer power_rating;

    //Constructor
    public ClientModelDevice_electric_devices(){}

    // Standard getters & setters
    public Double getCostPerDay() { return cost_per_day; }
    public void setCostPerDay(Double cost_per_day) { this.cost_per_day = cost_per_day; }
    public Double getCostPerWatt() { return cost_per_watt; }
    public void setCostPerWatt(Double cost_per_watt) { this.cost_per_watt = cost_per_watt; }
    public Integer getHoursUsedPerDay() { return hours_used_per_day; }
    public void setHoursUsedPerDay(Integer hours_used_per_day) { this.hours_used_per_day = hours_used_per_day; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getNumberOfDevices() { return number_of_devices; }
    public void setNumberOfDevices(Integer number_of_devices) { this.number_of_devices = number_of_devices; }
    public Integer getPowerRating() { return power_rating; }
    public void setPowerRating(Integer power_rating) { this.power_rating = power_rating; }
}
