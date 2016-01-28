
package de.markhaehnel.rbtv.rocketbeanstv;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.devbrackets.android.exomedia.EMVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {


    AudioManager am;
    ComponentName mbr;
    EMVideoView emVideoView;

    public static MainActivity ins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;

        setContentView(R.layout.activity_main);
        emVideoView = (EMVideoView)findViewById(R.id.exomediaplayer);

        emVideoView.setOnPreparedListener(this);

        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mbr = new ComponentName(getPackageName(),
                MediaButtonReceiver.class.getName());

        int result = am.requestAudioFocus(focusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            am.registerMediaButtonEventReceiver(mbr);
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

    }

    public static MainActivity getInstance() {
        return ins;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        EMVideoView emVideoView = (EMVideoView)findViewById(R.id.exomediaplayer);
        emVideoView.start();
    }

    @Override
    public void onPause() {
        emVideoView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {

        emVideoView.start();
        super.onResume();
    }

    public void togglePlayState() {
        ImageView pauseView = (ImageView)findViewById(R.id.pauseImage);
        if (emVideoView.isPlaying()) {
            emVideoView.pause();
            pauseView.setVisibility(View.VISIBLE);
        } else {
            emVideoView.start();
            pauseView.setVisibility(View.INVISIBLE);
        };
    }


    AudioManager.OnAudioFocusChangeListener focusListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focus) {
            switch (focus) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    emVideoView.start();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    emVideoView.stopPlayback();
                    am.unregisterMediaButtonEventReceiver(mbr);
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    emVideoView.pause();
                    break;
            }
        }
    };
}


