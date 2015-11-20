package de.markhaehnel.rbtv.rocketbeanstv;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitch.tv/rocketbeanstv"));
		startActivity(i);
    }
}
