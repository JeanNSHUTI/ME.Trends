package tech.mobile.met;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class Application extends android.app.Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here
        Realm.init(this);

    }
}
