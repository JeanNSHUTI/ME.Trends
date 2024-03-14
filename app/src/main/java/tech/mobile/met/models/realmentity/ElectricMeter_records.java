package tech.mobile.met.models.realmentity;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass(embedded = true)
public class ElectricMeter_records extends RealmObject{
    //Fields
    @Required
    private Date record_date;
    private Long current_reading;
    private Long consumption;
    private String comment;

    //Constructors
    public ElectricMeter_records(){}

    public ElectricMeter_records(Long currentreading, String comment ){
        this.current_reading = currentreading;
        this.comment = comment;
        this.record_date = new Date();
    }

    public ElectricMeter_records(Long currentreading){
        this.current_reading = currentreading;
        this.comment = "";
        this.record_date = new Date();
    }


    //Getters and setters
    public Date getRecord_date() {
        return record_date;
    }
    public void setRecord_date(Date record_date) {
        this.record_date = record_date;
    }

    public Long getCurrent_reading() {
        return current_reading;
    }
    public void setCurrent_reading(Long current_reading) {
        this.current_reading = current_reading;
    }

    public Long getConsumption() {
        return consumption;
    }
    public void setConsumption(Long consumption) {
        this.consumption = consumption;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    //Methods
    public void ComputeConsumption(ElectricMeter_records oldrecord){
        this.consumption = this.current_reading - oldrecord.getCurrent_reading();
    }
}
