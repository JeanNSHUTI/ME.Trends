package tech.mobile.met;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import tech.mobile.met.databinding.ActivityHomeBinding;
import tech.mobile.met.models.realmentity.UserCredentials;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private Button editbutton;
    //private String strWelcome, strFirstname, strLastname, strEmail, strEnergyProv, strStreet, strNo,
    //        strProvince, strCountry;
    private ImageView imageView;
    public UserCredentials userCred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_energy_provider, R.id.nav_mydevices, R.id.nav_meters, R.id.nav_dashboard)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(intent != null){
            userCred = (UserCredentials) intent.getSerializableExtra("userCred");
            Log.v("HA","Home Act. credentials: " + userCred.getEmail());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Logout(){
        String appID = "app-energy-trends-iiaxj";
        final App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
                .requestTimeout(30, TimeUnit.SECONDS).build());
        User user = app.currentUser();
        if (null != user){
            if(user.isLoggedIn()){
                user.logOutAsync( result -> {
                    if (result.isSuccess()) {
                        Log.v("AUTH", "Successfully logged out.");
                        //if(!Realm.getDefaultInstance().isClosed()){
                        //    Realm.getDefaultInstance().close();

                        //}
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("AUTH", result.getError().toString());
                    }
                });
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}