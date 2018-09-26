package cn.dankal.downloadlib;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

/**
 * Created by fred
 * Date: 2018/9/18.
 * Time: 11:27
 * classDescription:
 */
public class MyApp extends Application {
    public static String FILES_DIR= Environment.getExternalStorageDirectory().getPath() + "/" + "Android/data/";
    private static Context context;

    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        FILES_DIR+= this.getPackageName() + "/files/";
        context=this;
    }

}
