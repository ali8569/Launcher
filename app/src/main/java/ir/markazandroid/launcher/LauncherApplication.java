package ir.markazandroid.launcher;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.PowerManager;

/**
 * Coded by Ali on 5/4/2019.
 */
public class LauncherApplication extends Application {

    public static final String DEFAULT_LAUNCHER_PACKAGE=/*"com.softwinner.launcher"*/"com.android.launcher3";
    public static final String DEFAULT_SYSTEMUI_PACKAGE=/*"com.softwinner.launcher"*/"com.android.systemui";
    private Console console;

    @Override
    public void onCreate() {
        super.onCreate();
        //initSystemUi();
    }

    public void initSystemUi() {
       disablePackage(DEFAULT_LAUNCHER_PACKAGE);
       hidePackage(DEFAULT_SYSTEMUI_PACKAGE);
    }


    public Console getConsole() {
        if (console==null) console=new Console();
        return console;
    }


    public void hidePackage(String packageId){
        getConsole().write("pm hide "+packageId);
    }

    public void disablePackage(String packageId){
        getConsole().write("pm disable "+packageId);
    }
}
