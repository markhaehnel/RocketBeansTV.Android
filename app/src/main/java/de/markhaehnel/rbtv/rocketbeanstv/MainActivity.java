package de.markhaehnel.rbtv.rocketbeanstv;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final Button button = (Button) findViewById(R.id.button);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=tv.twitch.android.viewer"));
                startActivity(i);
            }
        });

        if(twitchInstalled()) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitch.tv/rocketbeanstv"));
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
        System.exit(0);
    }

    private boolean twitchInstalled() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("tv.twitch.android.viewer", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
