package tech.mobile.met;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        String appID = "app-energy-trends-iiaxj"; // replace this with your Application ID
        App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
                .requestTimeout(30, TimeUnit.SECONDS).build());

        TextView btnAlreadyHaveAccount = findViewById(R.id.back2login);
        btnAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            }
        });

        Button btnResetPass = findViewById(R.id.btnRegister);
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id. inputEmail);
                EditText password = findViewById(R.id. inputPassword);
                EditText confirmpass = findViewById(R.id. inputConfirmPassword);
                String[] args = {"security answer 1"};

                if(password.getText().equals(confirmpass.getText())){

                    app.getEmailPassword().callResetPasswordFunctionAsync(email.getText().toString(), password.getText().toString(), args, it -> {
                        if (it.isSuccess()) {
                            Log.i("EXAMPLE", "Successfully reset the password for" + email.getText().toString());
                        } else {
                            Log.e("EXAMPLE", "Failed to reset the password for" + email.getText().toString() + ": " + it.getError().getErrorMessage());
                        }
                    });
                }
            }
        });

    }
}