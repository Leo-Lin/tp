package taoyuan.taipower;

import android.app.Application;
import com.parse.Parse;

/**
 * @author leolin
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this);
    }
}
