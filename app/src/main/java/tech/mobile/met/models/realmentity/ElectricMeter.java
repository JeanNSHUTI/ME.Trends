package tech.mobile.met.models.realmentity;


import org.bson.types.ObjectId;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ElectricMeter extends RealmObject {
    //Fields
    @PrimaryKey private ObjectId _id;
    @Required private String _partition;
    @Required private String name;
    private RealmList<ElectricMeter_records> records;
    @Required private String type;
    private Client clientoid;
    @LinkingObjects("my_electric_meter")
    private final RealmResults<Client> user = null;

    //Constructors
    public ElectricMeter(){/*this._id = new ObjectId();*/}

    public ElectricMeter(String name, Client clientid, RealmList<ElectricMeter_records> meter_records,
                         String type, String PARTITION){
        //this._id = new ObjectId();
        this.name = name;
        this.clientoid = clientid;
        this.records = meter_records;
        this.type = type;
        this._partition = PARTITION;
    }

    public ElectricMeter(String name, Client clientid, String PARTITION){
        this._id = new ObjectId();
        this.name = name;
        this.clientoid = clientid;
        this.records = new RealmList<ElectricMeter_records>();
        this._partition = PARTITION;
    }

    //Getters and setters
    public Client getClientoid() { return clientoid; }
    public void setClientoid(Client clientoid) { this.clientoid = clientoid; }

    public RealmList<ElectricMeter_records> getMy_meter_records() {
        return records;
    }
    public void setMy_meter_records(RealmList<ElectricMeter_records> my_meter_records) {
        this.records = my_meter_records;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ObjectId get_id() {
        return _id;
    }
    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String get_partition() {return _partition;}

    //Methods
    public boolean DeleteRecord(ElectricMeter_records record){
        return this.records.remove(record);
    }


}
