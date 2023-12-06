package project;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

public class G extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.i("LOG", "onCreate From G");
    }
}
