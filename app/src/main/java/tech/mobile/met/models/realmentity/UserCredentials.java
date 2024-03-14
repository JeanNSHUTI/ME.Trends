package tech.mobile.met.models.realmentity;

import java.io.Serializable;

public class UserCredentials implements Serializable {

    private String email;
    private String password;

    public UserCredentials(){}

    public UserCredentials(String mail, String pass){
        this.email = mail;
        this.password = pass;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
