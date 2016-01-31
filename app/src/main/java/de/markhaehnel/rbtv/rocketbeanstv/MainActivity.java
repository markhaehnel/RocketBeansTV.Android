
package de.markhaehnel.rbtv.rocketbeanstv;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.devbrackets.android.exomedia.EMVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {


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

        if (isOnline()) {
            emVideoView.setOnPreparedListener(this);
            emVideoView.setOnErrorListener(this);
            emVideoView.setOnInfoListener(this);

            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mbr = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());

            int result = am.requestAudioFocus(focusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                am.registerMediaButtonEventReceiver(mbr);
                try {
                    JSONObject json = new GetAccesToken().execute(this).get();
                    if (json.length() != 0) {
                        String token = json.getString("token");
                        String sig = json.getString("sig");
                        String url = "http://usher.twitch.tv/api/channel/hls/rocketbeanstv.m3u8?player=twitchweb&token=" + token + "&sig=" + sig + "&allow_audio_only=true&allow_source=true&type=any&p=" + Math.round(Math.random() * 10000);
                        Uri theUri = Uri.parse(url);
                        emVideoView.setVideoURI(Uri.parse(url));
                    } else {
                        showMessage(R.string.error_accessToken);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    showMessage(R.string.error_accessToken);
                }
            }
        } else {
            showMessage(R.string.error_noInternet);
        }
    }

    public static MainActivity getInstance() {
        return ins;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                togglePlayState();
                return true;
        }
        return false;
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

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        showMessage(R.string.error_unknown);
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);

        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                pb.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                pb.setVisibility(View.INVISIBLE);
                break;
            default:
                pb.setVisibility(View.INVISIBLE);
                break;
        }
        return false;
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

    private void showMessage(int resourceId) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setCancelable(false);
        ad.setMessage(getString(resourceId));
        ad.setTitle("Fehler");
        ad.setNeutralButton("Okay", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        ad.create().show();
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
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


