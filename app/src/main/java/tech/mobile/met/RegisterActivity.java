package tech.mobile.met;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import tech.mobile.met.models.realmentity.Client;

public class RegisterActivity extends AppCompatActivity {
    private ProgressBar pgsBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pgsBar = findViewById(R.id.registerProgressBar1);

        Context context = getApplicationContext();
        CharSequence successtext = "Successfully Registered!";
        CharSequence failtext = "Failed to register!";
        CharSequence failtext2 = "No credentials configured.";
        int duration = Toast.LENGTH_SHORT;

        String appID = "app-energy-trends-iiaxj"; // replace this with your Application ID
        App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
                .requestTimeout(30, TimeUnit.SECONDS).build());

        TextView btnAlreadyHaveAccount = findViewById(R.id.back2login);
        btnAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        Button btnSignUp = findViewById(R.id.btnRegister);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pgsBar.setVisibility(View.VISIBLE);
                EditText email = findViewById(R.id. inputEmail);
                EditText password = findViewById(R.id. inputPassword);
                EditText confirmpass = findViewById(R.id. inputConfirmPassword);

                if(password.getText().toString().equals(confirmpass.getText().toString())){
                    Client newclient = new Client(email.getText().toString(), password.getText().toString());
                    app.getEmailPassword().registerUserAsync(newclient.getEmail(), newclient.getPassword(), it -> {
                        if (it.isSuccess()) {
                            Toast toast = Toast.makeText(context, successtext, duration);
                            pgsBar.setVisibility(View.GONE);
                            toast.show();
                            Log.i("REGISTRATION", "Successfully registered user.");
                        } else {
                            Toast toast = Toast.makeText(context, failtext, duration);
                            pgsBar.setVisibility(View.GONE);
                            toast.show();
                            Log.e("REGISTRATION", "Failed to register user: " + it.getError().getErrorMessage());
                        }
                    });
                }
                else{
                    Toast toast = Toast.makeText(context, failtext2, duration);
                    toast.show();
                    Log.e("REGISTRATION", "Pressed registration with empty fields.");
                }

            }
        });
    }
}