
package de.markhaehnel.rbtv.rocketbeanstv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devbrackets.android.exomedia.EMVideoView;

import java.util.ArrayList;

import de.markhaehnel.rbtv.rocketbeanstv.utility.*;
import de.markhaehnel.rbtv.rocketbeanstv.utility.Enums.*;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {

    private EMVideoView videoView;

    boolean showGetterIsRunning = false;
    ChannelInfo channelInfo = new ChannelInfo("Keine Informationen", "-");

    ChatState chatState = ChatState.Hidden;
    Quality currentQuality;

    private static MainActivity ins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;

        setContentView(R.layout.activity_main);
        videoView = (EMVideoView)findViewById(R.id.exomediaplayer);

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return NetworkHelper.hasInternet();
            }
            @Override
            protected void onPostExecute(Boolean hasInternet) {
                super.onPostExecute(hasInternet);

                if (hasInternet) {
                    setupListeners();
                    MediaSessionHandler.setupMediaSession(MainActivity.this);
                    preparePlayer();
                    setupChat();
                } else {
                    showMessage(R.string.error_noInternet);
                }
            }
        }.execute();
    }

    private void setupChat() {
        WebView chat = (WebView)findViewById(R.id.webViewChat);
        chat.setAlpha(0.75f);
        chat.getSettings().setJavaScriptEnabled(true);
        chat.loadUrl("https://ezteq.github.io/rbtv-firetv/");
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
            case KeyEvent.KEYCODE_DPAD_LEFT:
                toggleSchedule();
                return true;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
                System.exit(0);
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                toggleInfoOverlay(false);
                return true;
            case KeyEvent.KEYCODE_MENU:
                changeStreamQuality();
                return true;
        }
        return false;
    }

    private void changeStreamQuality() {
        CharSequence options[] = new CharSequence[] {"Quelle", "Hoch", "Mittel", "Niedrig", "Mobil"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.menuTitle_chooseQuality));

        builder.setSingleChoiceItems(options, currentQuality.ordinal(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
                pb.setVisibility(View.VISIBLE);
                new PlayStreamTask().execute(Quality.values()[which]);
                currentQuality = Quality.values()[which];

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.getInstance());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("quality", currentQuality.ordinal());
                editor.commit();

                dialog.cancel();
            }
        });
        builder.show();
    }

    private void toggleSchedule() {
        LinearLayout schedule = (LinearLayout)findViewById(R.id.containerSchedule);
        if (schedule.getVisibility() == View.INVISIBLE) {
            new GetScheduleTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            schedule.setAnimation(AnimationBuilder.getFadeInAnimation());
        } else {
            schedule.setVisibility(View.INVISIBLE);
            schedule.removeAllViews();
        }
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

                if (!showGetterIsRunning) {
                    new GetChannelInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    showGetterIsRunning = true;
                }
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

    public static MainActivity getInstance() {
        return ins;
    }

    public void setInfoOverlay(ChannelInfo info) {
        if (!info.currentShow.equals(channelInfo.currentShow)) {
            toggleInfoOverlay(true);
        }
        channelInfo = info;
        setInfoOverlayInformation();
    }

    public void togglePlayState() {
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
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(videoView.getLayoutParams());
        WebView chat = (WebView)findViewById(R.id.webViewChat);

        switch (chatState) {
            case Hidden:
                //to fixed
                int dpiMargin = 300 * Math.round(this.getResources().getDisplayMetrics().density);
                lp.setMargins(0, 0, dpiMargin, 0);
                chat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
                chat.setVisibility(View.VISIBLE);
                chatState = ChatState.Fixed;
                break;
            case Fixed:
                //to overlay
                chat.setBackgroundColor(ContextCompat.getColor(this, R.color.overlayBackground));
                chat.setVisibility(View.VISIBLE);
                chatState = ChatState.Overlay;
                break;
            case Overlay:
            //to hidden
            lp.setMargins(0, 0, 0, 0);
            chat.setVisibility(View.INVISIBLE);
            chatState = ChatState.Hidden;
            break;
        }

        videoView.setLayoutParams(lp);
    }

    private void setupListeners() {
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnInfoListener(this);
    }

    private void preparePlayer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentQuality = Quality.values()[prefs.getInt("quality", Quality.Chunked.ordinal())];
        new PlayStreamTask().execute(currentQuality);
    }

    public void playURL(String url) {
        if (url != null && url.length() > 0) {
            videoView.stopPlayback();
            videoView.seekTo(0);
            videoView.setVideoURI(Uri.parse(url));
        } else {
            showMessage(R.string.error_unknown);
        }
    }

    public void showMessage(int resourceId) {
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

    private void setInfoOverlayInformation() {
        TextView textCurrentShow = (TextView)findViewById(R.id.textCurrentShow);
        textCurrentShow.setText(channelInfo.currentShow);
        TextView textViewerCount = (TextView)findViewById(R.id.textViewerCount);
        textViewerCount.setText(channelInfo.viewerCount);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(AnimationBuilder.getFadeInAnimation());
        animation.addAnimation(AnimationBuilder.getDelayedFadeOutAnimation());
    }

    private void toggleInfoOverlay(boolean autoHide) {
        LinearLayout infoOverlay = (LinearLayout) findViewById(R.id.containerCurrentShow);
        if (infoOverlay.getVisibility() == View.INVISIBLE) {
            if (autoHide) {
                AnimationSet animation = new AnimationSet(true);
                animation.addAnimation(AnimationBuilder.getFadeInAnimation());
                animation.addAnimation(AnimationBuilder.getDelayedFadeOutAnimation());
                infoOverlay.startAnimation(animation);
            } else {
                infoOverlay.setAnimation(AnimationBuilder.getFadeInAnimation());
                infoOverlay.setVisibility(View.VISIBLE);
            }
        } else {
            infoOverlay.setAnimation(AnimationBuilder.getFadeOutAnimation());
            infoOverlay.setVisibility(View.INVISIBLE);
        }
    }

    public void showSchedule(ArrayList<ScheduleShow> shows) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.containerSchedule);
        insertPoint.removeAllViews();
        insertPoint.setVisibility(View.VISIBLE);

        int animMulitplier = 250;

        for (int i = 0; i < shows.size(); i++) {
            View v = vi.inflate(R.layout.component_scheduleitem, null);

            TextView timeStart = (TextView)v.findViewById(R.id.textTimeStart);
            timeStart.setText(shows.get(i).getTimeStart());

            TextView type = (TextView)v.findViewById(R.id.textType);
            type.setText(shows.get(i).getType());

            TextView title = (TextView)v.findViewById(R.id.textTitle);
            title.setText(shows.get(i).getTitle());

            TextView topic = (TextView)v.findViewById(R.id.textTopic);
            topic.setText(shows.get(i).getTopic());

            v.startAnimation(AnimationBuilder.createDelayedFadeInAnimation(i * animMulitplier));

            insertPoint.addView(v, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}


