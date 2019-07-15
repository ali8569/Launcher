package ir.markazandroid.launcher.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Coded by Ali on 09/01/2018.
 */

public class PreferencesManager {


    private static final String ARDUINO_TIME="ir.markazandroid.advertiser.util.PreferencesManager.ARDUINO_TIME";
    private static final String APPlIST="ir.markazandroid.launcher.util.PreferencesManager.APPLIST";
    private static final String PHONE="ir.markazandroid.advertiser.util.PreferencesManager.PHONE";

    private SharedPreferences privateSharedPreferences;

    public PreferencesManager(Context context){
        this.privateSharedPreferences =context.getSharedPreferences("ir.markazandroid.advertiser.util.privateSharedPreferences",Context.MODE_PRIVATE);
    }


    public Set<String> getAppList(){
        return privateSharedPreferences.getStringSet(APPlIST,new HashSet<>());
    }
    public synchronized void addAppToList(String appId){
        Set<String> cmds = new HashSet<>(getAppList());
        cmds.add(appId);
        privateSharedPreferences.edit().putStringSet(APPlIST,cmds).apply();
    }

    public synchronized void removeAppFromList(String appId){
        Set<String> cmds = new HashSet<>(getAppList());
        cmds.remove(appId);
        privateSharedPreferences.edit().putStringSet(APPlIST,cmds).apply();
    }

    public SharedPreferences getPrivateSharedPreferences() {
        return privateSharedPreferences;
    }

}
