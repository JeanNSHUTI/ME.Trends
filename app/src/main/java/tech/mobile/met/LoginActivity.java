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
import java.util.concurrent.atomic.AtomicReference;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;
import tech.mobile.met.models.realmentity.UserCredentials;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar pgsBar;
    private UserCredentials userCreds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pgsBar = findViewById(R.id.loginProgressBar);

        Context context = getApplicationContext();
        CharSequence failtext = "Failed to login! Verify credentials";
        CharSequence failtext2 = "No credentials configured.";
        int duration = Toast.LENGTH_SHORT;

        String appID = "app-energy-trends-iiaxj"; // replace this with your Application ID
        App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
                .requestTimeout(30, TimeUnit.SECONDS).build());


        TextView btnSignUp = findViewById(R.id. signUpText);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        TextView btnForgotPassword = findViewById(R.id.forgotPasswordText);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });


        Button btnLogin = findViewById(R.id. btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pgsBar.setVisibility(View.VISIBLE);
                //Authenticate user
                EditText emailInput = findViewById(R.id. inputEmail);
                EditText passwordInput = findViewById(R.id. inputPassword);
                Credentials emailPasswordCredentials = Credentials.emailPassword(emailInput.getText().toString(),
                        passwordInput.getText().toString());
                userCreds = new UserCredentials(emailInput.getText().toString(),
                        passwordInput.getText().toString());

                if(userCreds.getEmail().isEmpty() || userCreds.getPassword().isEmpty()){
                    Toast toast = Toast.makeText(context, failtext2, duration);
                    toast.show();
                    Log.e("REGISTRATION", "Pressed registration with empty fields.");
                }
                else{
                    AtomicReference<User> user = new AtomicReference<User>();
                    app.loginAsync(emailPasswordCredentials, it -> {
                        if (it.isSuccess()) {
                            Log.v("AUTH", "Successfully authenticated using an email and password.");
                            user.set(app.currentUser());
                            pgsBar.setVisibility(View.GONE);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("userCred", userCreds);
                            startActivity(intent);
                        } else {
                            pgsBar.setVisibility(View.GONE);
                            Toast toast = Toast.makeText(context, failtext, duration);
                            Log.e("AUTH", it.getError().toString());
                            toast.show();
                        }
                    });

                }
            }
        });
    }
}