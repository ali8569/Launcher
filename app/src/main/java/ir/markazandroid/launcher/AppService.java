package ir.markazandroid.launcher;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.markazandroid.launcher.adapter.ApplicationAdapter;
import ir.markazandroid.launcher.event.BaseEvent;
import ir.markazandroid.launcher.signal.Signal;
import ir.markazandroid.launcher.signal.SignalManager;
import ir.markazandroid.launcher.signal.SignalReceiver;
import ir.markazandroid.launcher.util.PreferencesManager;

public class AppService extends Service implements View.OnTouchListener, SignalReceiver {
    public static final String advertisementApplicationId = "ir.markazandroid.advertiser";
    private String currentApplicationId = "ir.markazandroid.advertiser";

    private Timer getBackToAdvertisementTimer;
    private RecyclerView appList;
    private ApplicationAdapter applicationAdapter;
    private ArrayList<String> applicationIds;
    private Handler handler;
    private PreferencesManager preferencesManager;

    public AppService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler=new Handler(getMainLooper());

        getSignalManager().addReceiver(this);

        preferencesManager= ((LauncherApplication)getApplication()).getPreferencesManager();
        applicationIds=new ArrayList<>();
        applicationIds.addAll(preferencesManager.getAppList());

        Log.e("Service", "created");
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);;
        FrameLayout ll;
        WindowManager.LayoutParams ll_lp;

//Just a sample layout parameters.
        ll_lp = new WindowManager.LayoutParams();
        ll_lp.format = PixelFormat.TRANSLUCENT;
        ll_lp.gravity = Gravity.CLIP_VERTICAL | Gravity.CENTER_VERTICAL | Gravity.START;
        ll_lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ll_lp.width = WindowManager.LayoutParams.WRAP_CONTENT;

//This one is necessary.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ll_lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else
            ll_lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

//Play around with these two.
        ll_lp.flags = WindowManager.LayoutParams.FLAG_SPLIT_TOUCH |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        ll_lp.flags = ll_lp.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

//This is our main layout.
        ll = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.app_service, null);

        ll.setOnTouchListener(this);

        ll.setBackgroundColor(Color.argb(0, 0, 0, 0));
        ll.setHapticFeedbackEnabled(true);

//And finally we add what we created to the screen.
        wm.addView(ll, ll_lp);

        //List
        appList= ll.findViewById(R.id.appList);
        appList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        applicationAdapter=new ApplicationAdapter(this, applicationId -> {
            switchToApp(applicationId,true);
            resetBackToAdvertisementTimer();
        });

        applicationAdapter.setApplicationIds(applicationIds);

        appList.setAdapter(applicationAdapter);


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e("AppService", "Screen Touched");
        resetBackToAdvertisementTimer();
        return false;
    }

    private CountDownTimer backToAdvertisementCountDownTimer;


    private void resetBackToAdvertisementTimer() {
        if (backToAdvertisementCountDownTimer!=null)
            backToAdvertisementCountDownTimer.cancel();

        backToAdvertisementCountDownTimer=new CountDownTimer(30_000,900) {

            private int counter=30;

            @Override
            public void onTick(long millisUntilFinished) {
                handler.post(() ->{
                    applicationAdapter.setCounterTimer(counter+"");
                    counter--;
                    if (counter<=0) counter=0;

                });
            }

            @Override
            public void onFinish() {
                handler.post(() ->{
                    switchToApp(advertisementApplicationId,true);
                });
            }
        };
        backToAdvertisementCountDownTimer.start();
    }


    private void switchToApp(String applicationId, boolean killCurrent) {
        if (applicationId.equals(currentApplicationId))
            return;

        Intent intent = getPackageManager().getLaunchIntentForPackage(applicationId);
        startActivity(intent);

        if (killCurrent && !advertisementApplicationId.equals(currentApplicationId))
            ((LauncherApplication) getApplication().getApplicationContext()).getConsole().write("pkill " + currentApplicationId);
           /* ActivityManager activityManager =
                    (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses("ir.markazandroid.advertiser");*/

        //((LauncherApplication)getApplication().getApplicationContext()).getConsole().write("pkill ir.markazandroid.advertiser");

        if (!applicationId.startsWith("ir.markazandroid."))
            sendBroadcast(BaseEvent.getLaunching3PartyIntent());

        if (!advertisementApplicationId.equals(currentApplicationId)) {
            applicationIds.add(currentApplicationId);
            //applicationAdapter.notifyItemInserted(applicationIds.size()-1);
            applicationAdapter.notifyDataSetChanged();
        }


        int appIndex = applicationId.indexOf(applicationId);
        if (appIndex!=-1) {
            applicationIds.remove(applicationId);
            //applicationAdapter.notifyItemRemoved(appIndex);
            applicationAdapter.notifyDataSetChanged();
        }

        currentApplicationId = applicationId;

        if (!advertisementApplicationId.equals(currentApplicationId)){
            applicationAdapter.showAdvertiserCounter(true);
            //applicationAdapter.setCounterTimer(30+"");
        }
        else
            applicationAdapter.showAdvertiserCounter(false);

        applicationAdapter.notifyDataSetChanged();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (backToAdvertisementCountDownTimer!=null)
            backToAdvertisementCountDownTimer.cancel();

        getSignalManager().removeReceiver(this);

    }

    @Override
    public boolean onSignal(Signal signal) {
        switch (signal.getType()){
            case Signal.SIGNAL_3PARTY_APP_ADDED:
                String appId =(String) signal.getExtras();
                preferencesManager.addAppToList(appId);
                if (!applicationIds.contains(appId)){
                    applicationIds.add(appId);
                    applicationAdapter.notifyDataSetChanged();
                }
                break;

            case Signal.SIGNAL_3PARTY_APP_REMOVED:
                appId =(String) signal.getExtras();
                preferencesManager.removeAppFromList(appId);
                if (applicationIds.contains(appId)){
                    applicationIds.remove(appId);
                    applicationAdapter.notifyDataSetChanged();
                }
                break;
        }
        return false;
    }

    private SignalManager getSignalManager(){
        return ((LauncherApplication)getApplication()).getSignalManager();
    }
}
