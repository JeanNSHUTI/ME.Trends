package tech.mobile.met.models.realmentity;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

import java.util.Date;
@RealmClass(embedded = true)
public class WaterMeter_records extends RealmObject {
    //Fields
    private String comment;
    private Long consumption;
    private Long current_reading;
    private Date record_date;

    //Constructors
    public WaterMeter_records(){}

    public WaterMeter_records(Long currentreading, String comment ){
        this.current_reading = currentreading;
        this.comment = comment;
        this.record_date = new Date();
    }

    public WaterMeter_records(Long currentreading){
        this.current_reading = currentreading;
        this.comment = "";
        this.record_date = new Date();
    }

    // Standard getters & setters
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Long getConsumption() { return consumption; }
    public void setConsumption(Long consumption) { this.consumption = consumption; }
    public Long getCurrentReading() { return current_reading; }
    public void setCurrentReading(Long current_reading) { this.current_reading = current_reading; }
    public Date getRecordDate() { return record_date; }
    public void setRecordDate(Date record_date) { this.record_date = record_date; }

    //Methods
    public void ComputeConsumption(WaterMeter_records oldrecord){
        this.consumption = this.current_reading - oldrecord.getCurrentReading();
    }
}
