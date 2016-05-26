package company.zzw.john.zwchat.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by john on 2016/5/16.
 */
public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
