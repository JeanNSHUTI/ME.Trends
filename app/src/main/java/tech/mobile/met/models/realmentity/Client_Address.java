package tech.mobile.met.models.realmentity;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class Client_Address extends RealmObject {
    //Fields
    private String housenumber;
    private String streetname;
    private String city;
    private String postalcode;
    private String state_province;
    private String country;

    //Constructors
    public Client_Address(){}

    public Client_Address(String street, String hnumber, String city, String code, String state, String country){
        this.housenumber = hnumber;
        this.streetname = street;
        this.city = city;
        this.postalcode = code;
        this.state_province = state;
        this.country = country;
    }

    //Getters and setters
    public String getHousenumber() {
        return housenumber;
    }
    public void setHousenumber(String housenumber) {
        this.housenumber = housenumber;
    }

    public String getStreetname() {
        return streetname;
    }
    public void setStreetname(String streetname) {
        this.streetname = streetname;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalcode() {
        return postalcode;
    }
    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getState_province() {
        return state_province;
    }
    public void setState_province(String state_province) {
        this.state_province = state_province;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

}
