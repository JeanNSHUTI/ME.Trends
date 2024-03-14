package tech.mobile.met.models.viewmodels;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Random;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class MeterViewModel extends RealmObject{
    //Fields
    @PrimaryKey private Long _id;
    private String name;
    private String type;
    private Long consumption;
    private Long current_reading;
    private Date record_date;

    public MeterViewModel(){
        this.record_date = new Date();
        this._id = new Random().nextLong();
    }

    public MeterViewModel(String name, Long consumption, Long current_reading, String type){
        this._id = new Random().nextLong();
        this.record_date = new Date();
        this.name = name;
        this.consumption = consumption;
        this.current_reading = current_reading;
        this.type = type;
    }

    public MeterViewModel(String name){
        this._id = new Random().nextLong();
        this.record_date = new Date();
        this.name = name;
    }

    public MeterViewModel(Date date, String name, Long consumption, Long current_reading){
        this._id = new Random().nextLong();
        this.record_date = date;
        this.name = name;
        this.consumption = consumption;
        this.current_reading = current_reading;
    }

    //Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getConsumption() {
        return consumption;
    }

    public void setConsumption(Long consumption) {
        this.consumption = consumption;
    }

    public Long getCurrent_reading() {
        return current_reading;
    }

    public void setCurrent_reading(Long current_reading) {
        this.current_reading = current_reading;
    }

    public Date getRecord_date() {
        return record_date;
    }

    public void setRecord_date(Date record_date) {
        this.record_date = record_date;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
