package tech.mobile.met.models.viewmodels;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Random;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import tech.mobile.met.models.realmentity.ElectricMeter_records;
import tech.mobile.met.models.realmentity.GasMeter_records;
import tech.mobile.met.models.realmentity.WaterMeter;
import tech.mobile.met.models.realmentity.WaterMeter_records;

public class RecordViewModel extends RealmObject {
    //Fields
    @PrimaryKey private Long _id;
    private String comment;
    private Long consumption;
    private Long current_reading;
    private Date record_date;
    private String type;

    public RecordViewModel(){this._id = new Random().nextLong();}

    public RecordViewModel(Date date, Long current_reading){
        this._id = new Random().nextLong();
        this.record_date = date;
        this.current_reading = current_reading;
    }

    public RecordViewModel(ElectricMeter_records record){
        this._id = new Random().nextLong();
        this.record_date = record.getRecord_date();
        this.current_reading = record.getCurrent_reading();
        this.consumption = record.getConsumption();
        this.comment = record.getComment();

    }
    public RecordViewModel(GasMeter_records record){
        this._id = new Random().nextLong();
        this.record_date = record.getRecordDate();
        this.current_reading = record.getCurrentReading();
        this.consumption = record.getConsumption();
        this.comment = record.getComment();
    }
    public RecordViewModel(WaterMeter_records record){
        this._id = new Random().nextLong();
        this.record_date = record.getRecordDate();
        this.current_reading = record.getCurrentReading();
        this.consumption = record.getConsumption();
        this.comment = record.getComment();
    }


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
