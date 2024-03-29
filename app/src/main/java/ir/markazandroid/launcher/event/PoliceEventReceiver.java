package ir.markazandroid.launcher.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ir.markazandroid.launcher.LauncherApplication;
import ir.markazandroid.launcher.signal.Signal;
import ir.markazandroid.launcher.signal.SignalManager;

public class PoliceEventReceiver extends BroadcastReceiver {

    private Context context;

    public static final String EVENT_TYPE_NAME = "ir.markazandroid.police.event.BaseEvent.EVENT_TYPE_NAME";

    public static final String EVENT_TYPE_MIRROR_BLOCK = "EVENT_TYPE_MIRROR_BLOCK";
    public static final String EVENT_TYPE_MIRROR_UNBLOCK = "EVENT_TYPE_MIRROR_UNBLOCK";
    public static final String EVENT_TYPE_3PARTY_APPLICATION_ADDED="EVENT_TYPE_3PARTY_APPLICATION_ADDED";
    public static final String EVENT_TYPE_3PARTY_APPLICATION_REMOVED="EVENT_TYPE_3PARTY_APPLICATION_REMOVED";

    public static final String PARAMETER_APP_ID="PARAMETER_APP_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.e("Event", intent.toString());
        switch (intent.getStringExtra(EVENT_TYPE_NAME)) {

            case EVENT_TYPE_MIRROR_BLOCK:
                onMirrorBlockEvent();
                break;

            case EVENT_TYPE_MIRROR_UNBLOCK:
                onMirrorUnBlockEvent();
                break;

            case EVENT_TYPE_3PARTY_APPLICATION_ADDED:
                on3PartyAppAddedEvent(intent.getStringExtra(PARAMETER_APP_ID));
                break;

            case EVENT_TYPE_3PARTY_APPLICATION_REMOVED:
                on3PartyAppRemovedEvent(intent.getStringExtra(PARAMETER_APP_ID));
                break;
        }
    }

    private void onDeviceAuthenticatedEvent() {
        getSignalManager().sendMainSignal(new Signal(Signal.SIGNAL_DEVICE_AUTHENTICATED));
    }

    private void onMirrorBlockEvent() {
        Signal signal = new Signal("screen block", Signal.SIGNAL_SCREEN_BLOCK);
        getSignalManager().sendMainSignal(signal);
    }

    private void onMirrorUnBlockEvent() {
        Signal signal = new Signal("screen unblock", Signal.SIGNAL_SCREEN_UNBLOCK);
        getSignalManager().sendMainSignal(signal);
    }

    private void on3PartyAppAddedEvent(String appId) {
        Signal signal = new Signal("app added", Signal.SIGNAL_3PARTY_APP_ADDED,appId);
        getSignalManager().sendMainSignal(signal);
    }
    private void on3PartyAppRemovedEvent(String appId) {
        Signal signal = new Signal("app removed", Signal.SIGNAL_3PARTY_APP_REMOVED,appId);
        getSignalManager().sendMainSignal(signal);
    }

    private SignalManager getSignalManager() {
        return ((LauncherApplication) context.getApplicationContext()).getSignalManager();
    }

}
