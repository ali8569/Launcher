package ir.markazandroid.launcher.activity;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.Toast;

import ir.markazandroid.launcher.Console;
import ir.markazandroid.launcher.LauncherApplication;
import ir.markazandroid.launcher.R;

public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_BACKGROUND_ID = "CURRENT_BACKGROUND_ID";
    private boolean userModeEnabled;
    private Console console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console=((LauncherApplication)getApplication()).getConsole();
        ((LauncherApplication)getApplication()).initSystemUi();

        //SharedPreferences pref  = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        //int backgroundId = pref.getInt(CURRENT_BACKGROUND_ID,-1);

        ViewGroup container = findViewById(R.id.container);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int width = size.x;
        int height = size.y;

        if (width > height)
            container.setBackgroundResource(R.drawable.logotype_bg_h);
        else
            container.setBackgroundResource(R.drawable.logotype_bg_v);


        findViewById(R.id.switchMode).setOnLongClickListener(v -> {
            switchToUserMode();
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (userModeEnabled){
            launchDefaultLauncher();
            userModeEnabled=false;
        }
    }

    private void switchToUserMode() {
        console.write("pm unhide "+LauncherApplication.DEFAULT_SYSTEMUI_PACKAGE);
        console.write("am startservice -n com.android.systemui/.SystemUIService");
        userModeEnabled = true;
        Toast.makeText(this, " ", Toast.LENGTH_LONG).show();
    }


    private void launchDefaultLauncher() {
        console.write("pm enable "+LauncherApplication.DEFAULT_LAUNCHER_PACKAGE);
        console.write("am start -n com.android.launcher3/.Launcher");
    }



}
