package tech.mobile.met.models.realmentity;

import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ElectricDevice extends RealmObject{
    //Fields
    @PrimaryKey private ObjectId _id;
    @Required private String _partition;
    @Required private String name;
    @Required private Integer hours_used_per_day;
    @Required private Integer power_rating;
    private Double cost_per_day;
    private Double cost_per_watt;
    private Client clientoid;
    private int number_of_devices;
    @LinkingObjects("my_devices")
    private final RealmResults<Client> user = null;

    //Constructors
    public ElectricDevice(){}

    public ElectricDevice(String PARTITION){
        this._id = new ObjectId();
        this._partition = PARTITION;
    }

    public ElectricDevice(String name, int hours_per_day, int pow_rating, Double cost_p_watt,
                          Double cost_p_day, Client clientid, int no_devices, String PARTITION){
        this._id = new ObjectId();
        this.name = name;
        this.hours_used_per_day = hours_per_day;
        this.power_rating = pow_rating;
        this.cost_per_watt = cost_p_watt;
        this.cost_per_day = cost_p_day;
        this.clientoid = clientid;
        this.number_of_devices = no_devices;
        this._partition = PARTITION;
    }

    //Getters and Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getHours_used_per_day() {
        return hours_used_per_day;
    }
    public void setHours_used_per_day(int hours_used_per_day) {
        this.hours_used_per_day = hours_used_per_day;
    }

    public int getPower_rating() {
        return power_rating;
    }
    public void setPower_rating(int power_rating) {
        this.power_rating = power_rating;
    }

    public Client getClientoid() { return clientoid; }
    public void setClientoid(Client clientoid) { this.clientoid = clientoid; }

    public Double getCostPerDay() { return cost_per_day; }
    public void setCostPerDay(Double cost_per_day) { this.cost_per_day = cost_per_day; }

    public Double getCostPerWatt() { return cost_per_watt; }
    public void setCostPerWatt(Double cost_per_watt) { this.cost_per_watt = cost_per_watt; }

    public int getNumber_of_devices() {
        return number_of_devices;
    }
    public void setNumber_of_devices(int number_of_devices) {
        this.number_of_devices = number_of_devices;
    }

    public ObjectId get_id() {
        return _id;
    }
    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String get_partition() {
        return _partition;
    }

    public void set_partition(String _partition) {
        this._partition = _partition;
    }
}
