package project;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;

public class G extends Application {
    public static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String HOME_DIR = SDCARD + "/test";
    public static final String APP_DIR = HOME_DIR + "/dot_n_boxes";


    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static DisplayMetrics displayMetrics;
    public static Resources resources;
    public static Handler handler;
    public static boolean hasWriteAccess;


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        resources = context.getResources();
        displayMetrics = resources.getDisplayMetrics();
        handler = new Handler();
    }

    public static void createDirectory() {
        if (hasWriteAccess) {
            File file = new File(APP_DIR);
            file.mkdirs();
        }
    }


}
