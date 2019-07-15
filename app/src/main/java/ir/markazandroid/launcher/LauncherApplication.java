package ir.markazandroid.launcher;

import android.app.Application;
import android.content.Intent;

import ir.markazandroid.launcher.signal.SignalManager;
import ir.markazandroid.launcher.util.Console;
import ir.markazandroid.launcher.util.PreferencesManager;

/**
 * Coded by Ali on 5/4/2019.
 */
public class LauncherApplication extends Application {

    public static final String DEFAULT_LAUNCHER_PACKAGE=/*"com.softwinner.launcher"*/"com.android.launcher3";
    public static final String DEFAULT_SYSTEMUI_PACKAGE=/*"com.softwinner.launcher"*/"com.android.systemui";
    private Console console;
    private PreferencesManager preferencesManager;
    private SignalManager signalManager;


    @Override
    public void onCreate() {
        super.onCreate();
        //initSystemUi();

        Intent intent = new Intent(this,AppService.class);
        startService(intent);
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

    public SignalManager getSignalManager() {
        if (signalManager == null) signalManager = new SignalManager(this);
        return signalManager;
    }
    public PreferencesManager getPreferencesManager() {
        if (preferencesManager==null) preferencesManager = new PreferencesManager(this);
        return preferencesManager;
    }
}
