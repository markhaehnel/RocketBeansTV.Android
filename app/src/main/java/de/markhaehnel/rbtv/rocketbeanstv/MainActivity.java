
package de.markhaehnel.rbtv.rocketbeanstv;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.devbrackets.android.exomedia.EMVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EMVideoView emVideoView = (EMVideoView)findViewById(R.id.exomediaplayer);

        emVideoView.setOnPreparedListener(this);

        try {
            JSONObject json = new GetAccesToken().execute().get();
            String token = json.getString("token");
            String sig = json.getString("sig");
            String url = "http://usher.twitch.tv/api/channel/hls/rocketbeanstv.m3u8?player=twitchweb&token=" + token + "&sig=" + sig + "&allow_audio_only=true&allow_source=true&type=any&p=" + Math.round(Math.random()*10000);
            Uri theUri = Uri.parse(url);
            emVideoView.setVideoURI(Uri.parse(url));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        EMVideoView emVideoView = (EMVideoView)findViewById(R.id.exomediaplayer);
        emVideoView.start();
    }

    @Override
    public void onPause() {
        EMVideoView emVideoView = (EMVideoView)findViewById(R.id.exomediaplayer);
        emVideoView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        EMVideoView emVideoView = (EMVideoView)findViewById(R.id.exomediaplayer);
        emVideoView.start();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        boolean handled = false;

        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                togglePlayState();
                handled = true;
                break;
            case KeyEvent.KEYCODE_MENU:
                break;
        }
        return handled || super.onKeyDown(keyCode, event);
    }

    private void togglePlayState() {
        EMVideoView emVideoView = (EMVideoView)findViewById(R.id.exomediaplayer);
        ImageView pauseView = (ImageView)findViewById(R.id.pauseImage);
        if (emVideoView.isPlaying()) {
            emVideoView.pause();
            pauseView.setVisibility(View.VISIBLE);
        } else {
            emVideoView.start();
            pauseView.setVisibility(View.INVISIBLE);
        };
    }
}

