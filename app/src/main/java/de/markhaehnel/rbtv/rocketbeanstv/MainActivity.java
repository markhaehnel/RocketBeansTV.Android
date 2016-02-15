
package de.markhaehnel.rbtv.rocketbeanstv;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.devbrackets.android.exomedia.EMVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {

    private EMVideoView videoView;

    boolean showGetterIsRunning = false;
    String currentShow = "Keine Informationen";

    private static MainActivity ins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;

        setContentView(R.layout.activity_main);
        videoView = (EMVideoView)findViewById(R.id.exomediaplayer);

        if (isOnline()) {

            setupListeners();
            MediaSessionHandler.setupMediaSession(this);
            preparePlayer();

            new GetLatestVersionTask().execute();

            setupChat();

        } else {
            showMessage(R.string.error_noInternet);
        }
    }

    private void setupChat() {
        WebView chat = (WebView)findViewById(R.id.webViewChat);
        chat.setAlpha(0.85f);
        chat.getSettings().setJavaScriptEnabled(true);
        chat.loadUrl("http://ezteq.github.io/rbtv-firetv/");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                togglePlayState();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                toggleChat();
                return true;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
                System.exit(0);
                return true;
            case KeyEvent.KEYCODE_MENU:
                this.showCurrentShow();
                return true;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.start();
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
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                pb.setVisibility(View.INVISIBLE);

                if (!showGetterIsRunning) new GetCurrentShowTask().execute();
                break;
        }

        return false;
    }

    @Override
    protected void onPause() {
        videoView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        videoView.start();
        super.onResume();
    }

    @Override
    protected void onStop() {
        System.exit(0);
        super.onStop();
    }

    protected static MainActivity getInstance() {
        return ins;
    }

    protected void setCurrentShow(String showName) {
        if (!showName.equals(currentShow)) {
            currentShow = showName;
            showCurrentShow();
        }
    }

    protected void togglePlayState() {
        ImageView pauseView = (ImageView)findViewById(R.id.pauseImage);
        if (videoView.isPlaying()) {
            videoView.pause();
            pauseView.startAnimation(AnimationBuilder.getFadeInAnimation());
            pauseView.setVisibility(View.VISIBLE);
        } else {
            videoView.start();
            pauseView.startAnimation(AnimationBuilder.getFadeOutAnimation());
            pauseView.setVisibility(View.INVISIBLE);
        }
    }

    private void toggleChat() {
        WebView chat = (WebView)findViewById(R.id.webViewChat);
        if (chat.getVisibility() == View.VISIBLE) {
            chat.setVisibility(View.INVISIBLE);
            chat.setAnimation(AnimationBuilder.getFadeOutAnimation());
        } else {
            chat.setVisibility(View.VISIBLE);
            chat.setAnimation(AnimationBuilder.getTransparentFadeInAnimation());
        }
    }

    private void setupListeners() {
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnInfoListener(this);
    }

    private void preparePlayer() {
        try {
            JSONObject json = new GetAccesTokenTask().execute(this).get();
            if (json.length() != 0) {
                String token = json.getString("token");
                String sig = json.getString("sig");
                if (token.length() != 0 && sig.length() != 0) {
                    String url = "http://usher.twitch.tv/api/channel/hls/rocketbeanstv.m3u8?player=twitchweb&token=" + token + "&sig=" + sig + "&allow_audio_only=true&allow_source=true&type=any&p=" + Math.round(Math.random() * 10000);
                    videoView.setVideoURI(Uri.parse(url));
                } else {
                    showMessage(R.string.error_twitchError);
                }
            } else {
                showMessage(R.string.error_twitchError);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            showMessage(R.string.error_twitchError);
        }
    }

    private void showMessage(int resourceId) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setCancelable(false);
        ad.setMessage(getString(resourceId));
        ad.setTitle("Fehler");
        ad.setNeutralButton("Okay", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        ad.create().show();
    }

    private void showCurrentShow() {
        TableLayout containerCurrentShow = (TableLayout)findViewById(R.id.containerCurrentShow);
        TextView textCurrentShow = (TextView)findViewById(R.id.textCurrentShow);
        textCurrentShow.setText(currentShow);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(AnimationBuilder.getFadeInAnimation());
        animation.addAnimation(AnimationBuilder.getDelayedFadeOutAnimation());

        containerCurrentShow.startAnimation(animation);
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    protected void showNewVersionAvailable() {
        TextView textNewVersionAvailable = (TextView)findViewById(R.id.textNewVersionAvailable);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(AnimationBuilder.getFadeInAnimation());
        animation.addAnimation(AnimationBuilder.getDelayedFadeOutAnimation());

        textNewVersionAvailable.startAnimation(animation);
    }
}


