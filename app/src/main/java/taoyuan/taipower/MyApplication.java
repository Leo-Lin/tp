package taoyuan.taipower;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * @author leolin
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this);

//        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", "leo");
        installation.saveInBackground();
    }
}
