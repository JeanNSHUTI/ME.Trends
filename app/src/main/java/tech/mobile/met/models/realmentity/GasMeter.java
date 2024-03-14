package tech.mobile.met.models.realmentity;

import org.bson.types.ObjectId;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class GasMeter extends RealmObject {
    //Fields
    @PrimaryKey private ObjectId _id;
    @Required private String _partition;
    @Required private String name;
    private RealmList<GasMeter_records> records;
    @Required private String type;
    private Client clientoid;
    @LinkingObjects("my_gas_meter")
    private final RealmResults<Client> user = null;

    //Constructors
    public GasMeter(){/*this._id = new ObjectId();*/}

    public GasMeter(String name, Client clientoid, RealmList<GasMeter_records> meter_records, String type, String PARTITION){
        //this._id = new ObjectId();
        this.name = name;
        this.clientoid = clientoid;
        this.records = meter_records;
        this.type = type;
        this._partition = PARTITION;
    }

    public GasMeter(String name, Client clientoid, String PARTITION){
        this._id = new ObjectId();
        this.name = name;
        this.clientoid = clientoid;
        this.records = new RealmList<GasMeter_records>();
        this._partition = PARTITION;
    }

    //Getters and setter
    public RealmList<GasMeter_records> getRecords() { return records; }
    public void setRecords(RealmList<GasMeter_records> records) { this.records = records; }

    public Client getClientoid() { return clientoid; }
    public void setClientoid(Client clientoid) { this.clientoid = clientoid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    public String get_partition() {
        return _partition;
    }

    //Methods
    public boolean DeleteRecord(GasMeter_records record){
        return this.records.remove(record);
    }


}
