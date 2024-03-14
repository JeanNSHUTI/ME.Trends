package tech.mobile.met.models.realmentity;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.RealmList;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Client extends RealmObject{
    //Fields
    @PrimaryKey private ObjectId _id;
    // Clientoid is a mirror of _id necessary for relationships in MongoDb ATLAS as it is not allowed
    // to create relationships with Object _id field directly!
    private ObjectId clientoid;
    @Required private String _partition; // Realm sync partition key !
    @Required private String email;
    @Required private String password;
    @Required private String firstname;
    @Required private String lastname;
    private Client_energy_provider energy_provider;
    private Client_Address address;
    private int electric_consumption_per_annum;
    private int gas_consumption_per_annum;
    //Linked Fields - Not embedded in doc
    private ElectricMeter my_electric_meter;
    private GasMeter my_gas_meter;
    private WaterMeter my_water_meter;
    private RealmList<ElectricDevice> my_devices;
    private RealmList<ClientModelDevice> my_model_devices;

    //Constructors
    public Client(){
        /*this._id = new ObjectId();*/
        //this.clientoid = get_id();
        //this._partition = get_id().toString();
    }

    public Client(String email, String pw, String fname, String lname, Client_Address address,
                  int econsumption, int gconsumption){
        //this._id = new ObjectId();
        this.email = email;
        this._partition = email;
        this.firstname = fname;
        this.lastname = lname;
        this.password = pw;
        this.electric_consumption_per_annum = econsumption;
        this.gas_consumption_per_annum = gconsumption;
        this.address = address;

    }

    public Client(String email, String pw, String fname, String lname, String PARTITION){
        this.email = email;
        this._partition = PARTITION;
        this.firstname = fname;
        this.lastname = lname;
        this.password = pw;
    }

    public Client(String email, String pw){
        //this._partition = PARTITION;
        this.email = email;
        this.password = pw;
    }

    //Getters and setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {this.email = email;}

    public Client_energy_provider getEnergy_provider() {
        return energy_provider;
    }
    public void setEnergy_provider(Client_energy_provider energy_provider) {
        this.energy_provider = energy_provider;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Client_Address getAddress() {
        return address;
    }
    public void setAddress(Client_Address address) {
        this.address = address;
    }

    public int getElectric_consumption_per_annum() {
        return electric_consumption_per_annum;
    }
    public void setElectric_consumption_per_annum(int electric_consumption_per_annum) {
        this.electric_consumption_per_annum = electric_consumption_per_annum;
    }

    public int getGas_consumption_per_annum() {
        return gas_consumption_per_annum;
    }
    public void setGas_consumption_per_annum(int gas_consumption_per_annum) {
        this.gas_consumption_per_annum = gas_consumption_per_annum;
    }

    public ElectricMeter getMy_electric_meter() {
        return my_electric_meter;
    }
    public void setMy_electric_meter(ElectricMeter my_electric_meter) {
        this.my_electric_meter = my_electric_meter;
    }

    public GasMeter getMy_gas_meter() {
        return my_gas_meter;
    }
    public void setMy_gas_meter(GasMeter my_gas_meter) {
        this.my_gas_meter = my_gas_meter;
    }

    public WaterMeter getMy_water_meter() {
        return my_water_meter;
    }
    public void setMy_water_meter(WaterMeter my_water_meter) {
        this.my_water_meter = my_water_meter;
    }

    public RealmList<ElectricDevice> getMy_devices() {
        return my_devices;
    }
    public void setMy_devices(RealmList<ElectricDevice> my_devices) {
        this.my_devices = my_devices;
    }

    public RealmList<ClientModelDevice> getMy_model_devices() {
        return my_model_devices;
    }
    public void setMy_model_devices(RealmList<ClientModelDevice> my_model_devices) {
        this.my_model_devices = my_model_devices;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public ObjectId getClientoid() {
        return clientoid;
    }

    public void setClientoid(ObjectId clientoid, String PARTITION) {

        this.clientoid = clientoid;
        this._partition = PARTITION;
    }

    public String get_partition() {
        return _partition;
    }
}
