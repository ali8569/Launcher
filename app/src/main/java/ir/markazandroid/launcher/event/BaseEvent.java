package ir.markazandroid.launcher.event;

import android.content.Intent;

/**
 * Coded by Ali on 4/27/2019.
 */
public final class BaseEvent {
    public static final String ACTION_EVENT="ir.markazandroid.launcher.event.BaseEvent.ACTION_EVENT";
    public static final String EVENT_TYPE_NAME="ir.markazandroid.launcher.event.BaseEvent.EVENT_TYPE_NAME";

    public static final String EVENT_TYPE_LAUNCHING_3PARTY_APP="EVENT_TYPE_LAUNCHING_3PARTY_APP";

    public static Intent getLaunching3PartyIntent(){
        Intent intent = new Intent(ACTION_EVENT);
        intent.putExtra(EVENT_TYPE_NAME,EVENT_TYPE_LAUNCHING_3PARTY_APP);
        return intent;
    }
}
