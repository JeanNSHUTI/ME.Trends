package tech.mobile.met.models.realmentity;

import org.bson.types.ObjectId;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ClientModelDevice extends RealmObject {
    //Fields
    @PrimaryKey private ObjectId _id;
    @Required private String _partition;
    @Required private String name;
    @Required private Date date_created;
    private Client clientoid;
    private RealmList<ClientModelDevice_electric_devices> electric_devices;
    @LinkingObjects("my_model_devices")
    private final RealmResults<Client> user = null;

    //Constructors
    public ClientModelDevice(){this._id = new ObjectId();}

    public ClientModelDevice(String name, Client clientid,
                             RealmList<ClientModelDevice_electric_devices> devices, String PARTITION){
        this._id = new ObjectId();
        this.name = name;
        this.date_created = new Date();
        this.clientoid = clientid;
        this.electric_devices = devices;
        this._partition = PARTITION;
    }

    // Getters and setters
    public Date getDate_created() {
        return date_created;
    }
    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public Client getClientoid() { return clientoid; }
    public void setClientoid(Client clientoid) { this.clientoid = clientoid; }


    public RealmList<ClientModelDevice_electric_devices> getElectricDevices() { return electric_devices; }
    public void setElectricDevices(RealmList<ClientModelDevice_electric_devices> electric_devices) { this.electric_devices = electric_devices; }

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

    public String get_partition() {
        return _partition;
    }
}
