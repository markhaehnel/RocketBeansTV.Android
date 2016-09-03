
package de.markhaehnel.rbtv.rocketbeanstv;

import android.annotation.SuppressLint;
import butterknife.BindView;
import org.greenrobot.eventbus.Subscribe;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.devbrackets.android.exomedia.EMVideoView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;
import java.util.Arrays;
import java.util.List;
import butterknife.ButterKnife;
import de.markhaehnel.rbtv.rocketbeanstv.events.ChannelInfoUpdateEvent;
import de.markhaehnel.rbtv.rocketbeanstv.events.InternetCheckEvent;
import de.markhaehnel.rbtv.rocketbeanstv.events.ScheduleLoadEvent;
import de.markhaehnel.rbtv.rocketbeanstv.events.StreamUrlChangeEvent;
import de.markhaehnel.rbtv.rocketbeanstv.events.TogglePlayStateEvent;
import de.markhaehnel.rbtv.rocketbeanstv.loader.ChannelInfoLoader;
import de.markhaehnel.rbtv.rocketbeanstv.loader.ScheduleLoader;
import de.markhaehnel.rbtv.rocketbeanstv.loader.StreamUrlLoader;
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.ScheduleItem;
import de.markhaehnel.rbtv.rocketbeanstv.utils.*;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.*;

import static de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper.hasInternet;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {

    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.exomediaplayer) EMVideoView mVideoView;
    @BindView(R.id.textCurrentShow) TextView textCurrentShow;
    @BindView(R.id.textViewerCount) TextView textViewerCount;
    @BindView(R.id.pauseImage) ImageView pauseView;
    @BindView(R.id.containerSchedule) ViewGroup containerSchedule;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.scheduleProgress) ProgressBar scheduleProgress;
    @BindView(R.id.webViewChat) WebView webViewChat;

    private final String RESOLUTION = "resolution";
    private final long ANIMATION_DURATION_NORMAL = 250;
    private final long ANIMATION_DURATION_SHORT = 100;

    private ChatState mChatState = ChatState.HIDDEN;
    private String mCurrentResolution;
    private String mVideoId = "";

    private String[] mAvailableResolutions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        EventBus.getDefault().register(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(hasInternet()) {
                    EventBus.getDefault().post(new InternetCheckEvent(EventStatus.OK));
                } else {
                    EventBus.getDefault().post(new InternetCheckEvent(EventStatus.FAILED));
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        System.exit(0);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetChecked(InternetCheckEvent event) {
        switch (event.getStatus()) {
            case OK:
                setupListeners();
                MediaSessionHandler.setupMediaSession(MainActivity.this);
                preparePlayer();
                new ChannelInfoLoader().start();
                break;
            case FAILED:
                showMessage(R.string.error_noInternet);
                break;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupChat(String videoId) {
        if (webViewChat != null) {
            webViewChat.setAlpha(0.75f);
            webViewChat.getSettings().setJavaScriptEnabled(true);
            webViewChat.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            view.loadUrl("javascript:(function() { document.getElementById('live-comments-controls').remove(); })()");
                        }
                });
            webViewChat.loadUrl("https://www.youtube.com/live_chat?dark_theme=1&is_popout=1&v=" + videoId);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                EventBus.getDefault().post(new TogglePlayStateEvent());
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
                changeStreamResolution();
                return true;
        }
        return false;
    }

    private void changeStreamResolution() {
        if (mAvailableResolutions != null && mAvailableResolutions.length > 0) {
            final String options[] = mAvailableResolutions;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.menuTitle_chooseQuality));

            int pos = Arrays.asList(options).indexOf(mCurrentResolution);
            builder.setSingleChoiceItems(options, pos, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showProgressBar();
                    new StreamUrlLoader(options[which]).start();

                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate().setDuration(ANIMATION_DURATION_NORMAL).alpha(1.0f);
    }

    private void toggleSchedule() {
        if (containerSchedule.getVisibility() == View.INVISIBLE) {
            scheduleProgress.setVisibility(View.VISIBLE);
            scheduleProgress.setAlpha(1.0f);
            containerSchedule.setAlpha(0.0f);
            containerSchedule.setVisibility(View.VISIBLE);
            containerSchedule.animate()
                    .setDuration(ANIMATION_DURATION_NORMAL)
                    .alpha(1.0f);
            new ScheduleLoader(getString(R.string.RBTVKEY), getString(R.string.RBTVSECRET)).start();
        } else {
            containerSchedule.animate()
                    .setDuration(ANIMATION_DURATION_NORMAL)
                    .alpha(0.0f)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            containerSchedule.setVisibility(View.INVISIBLE);
                            if (containerSchedule.getChildCount() > 1) {
                                containerSchedule.removeViews(1, containerSchedule.getChildCount() - 1);
                            }
                        }
                    });
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        showProgressBar();
        new StreamUrlLoader(mCurrentResolution).start();
        Log.d("onErrorMediaPlayer", String.valueOf(what) + " // " + String.valueOf(extra));
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                progressBar.animate()
                        .setDuration(ANIMATION_DURATION_NORMAL)
                        .alpha(0.0f)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                break;
        }

        return false;
    }

    @Override
    protected void onPause() {
        mVideoView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mVideoView.start();
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChannelInfoUpdate(ChannelInfoUpdateEvent event) {
        switch (event.getStatus())
        {
            case OK:
                textViewerCount.setText(String.format("%s %s", String.valueOf(event.getViewerCount()), getString(R.string.viewers)));
                if (!event.getCurrentShow().equals(textCurrentShow.getText())) {
                    textCurrentShow.setText(event.getCurrentShow());
                    toggleInfoOverlay(true);
                }
                break;

            case FAILED:
                textViewerCount.setText(R.string.empty);
                textCurrentShow.setText(R.string.no_info_available);
                new ChannelInfoLoader().start();
                break;
        }
        Heartbeat.doHeartbeat(mFirebaseAnalytics);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTogglePlayState(TogglePlayStateEvent event) {
        if (mVideoView.isPlaying()) {
            pauseView.setVisibility(View.VISIBLE);
            pauseView.animate()
                    .setDuration(ANIMATION_DURATION_SHORT)
                    .alpha(1.0f);
            mVideoView.pause();
        } else {
            mVideoView.start();
            pauseView.animate()
                    .setDuration(ANIMATION_DURATION_SHORT)
                    .alpha(0.0f)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            pauseView.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }

    private void toggleChat() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mVideoView.getLayoutParams());

        if (webViewChat != null) {
            switch (mChatState) {
                case HIDDEN:
                    //to fixed
                    int dpiMargin = 300 * Math.round(this.getResources().getDisplayMetrics().density);
                    lp.setMargins(0, 0, dpiMargin, 0);
                    webViewChat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
                    webViewChat.setVisibility(View.VISIBLE);
                    mChatState = ChatState.FIXED;
                    break;
                case FIXED:
                    //to overlay
                    webViewChat.setBackgroundColor(ContextCompat.getColor(this, R.color.overlayBackground));
                    webViewChat.setVisibility(View.VISIBLE);
                    mChatState = ChatState.OVERLAY;
                    break;
                case OVERLAY:
                    //to hidden
                    lp.setMargins(0, 0, 0, 0);
                    webViewChat.setVisibility(View.INVISIBLE);
                    mChatState = ChatState.HIDDEN;
                    break;
            }

            mVideoView.setLayoutParams(lp);
        }
    }

    private void setupListeners() {
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnInfoListener(this);
    }

    private void preparePlayer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentResolution = prefs.getString(RESOLUTION, "1x1");
        new StreamUrlLoader(mCurrentResolution).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStreamUrlChanged(StreamUrlChangeEvent event) {
        if (event.getStatus() == EventStatus.OK) {

            if (mCurrentResolution.compareToIgnoreCase(event.getStream().getResolution()) != 0) {
                mCurrentResolution = event.getStream().getResolution();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(RESOLUTION, mCurrentResolution);
                editor.apply();
            }

            mAvailableResolutions = event.getStream().getAvailableResolutions();

            mVideoView.stopPlayback();
            mVideoView.seekTo(0);
            mVideoView.setVideoURI(Uri.parse(event.getStream().getUrl()));

            if (mVideoId.trim().isEmpty() || event.getVideoId().compareTo(mVideoId) != 0) {
                setupChat(event.getVideoId());
                mVideoId = event.getVideoId();
            }
        } else {
            showMessage(R.string.error_unknown);
        }
    }

    public void showMessage(int resourceId) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setCancelable(false);
        ad.setMessage(getString(resourceId));
        ad.setTitle(getString(R.string.error));
        ad.setNeutralButton(getString(R.string.okay), new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        ad.create().show();
    }

    private void toggleInfoOverlay(boolean autoHide) {
        final LinearLayout infoOverlay = (LinearLayout) findViewById(R.id.containerCurrentShow);
        if (infoOverlay != null) {
            if (infoOverlay.getVisibility() == View.INVISIBLE) {
                infoOverlay.setAlpha(0.0f);
                infoOverlay.setVisibility(View.VISIBLE);
                infoOverlay.animate()
                        .setDuration(ANIMATION_DURATION_NORMAL)
                        .alpha(1.0f);
                if (autoHide) {
                    infoOverlay.startAnimation(AnimationBuilder.getDelayedFadeOutAnimation());
                    infoOverlay.setVisibility(View.INVISIBLE);
                }
            } else {
                infoOverlay.animate()
                        .setDuration(ANIMATION_DURATION_NORMAL)
                        .alpha(0.0f)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                infoOverlay.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScheduleLoaded(ScheduleLoadEvent event) {
        switch(event.getStatus()) {
            case OK:
                fillSchedule(event.getShows());
                break;
            case FAILED:
                Toast.makeText(this, R.string.error_getSchedule, Toast.LENGTH_SHORT).show();
                containerSchedule.startAnimation(AnimationBuilder.getFadeOutAnimation());
                containerSchedule.animate()
                        .setDuration(ANIMATION_DURATION_NORMAL)
                        .alpha(0.0f)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                containerSchedule.setVisibility(View.INVISIBLE);
                            }
                        });
                break;
        }
    }

    private void fillSchedule(final List<ScheduleItem> shows) {
        final LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scheduleProgress.animate()
                .setDuration(ANIMATION_DURATION_SHORT)
                .alpha(0.0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        scheduleProgress.setVisibility(View.GONE);

                        for (int i = 0; i < shows.size(); i++) {
                            @SuppressLint("InflateParams")
                            View v = vi.inflate(R.layout.component_scheduleitem, null);
                            v.setAlpha(0.0f);

                            TextView timeStart = (TextView) v.findViewById(R.id.textTimeStart);
                            timeStart.setText(shows.get(i).getTimeStartShort());

                            TextView type = (TextView) v.findViewById(R.id.textType);
                            type.setText(shows.get(i).getType());

                            TextView title = (TextView) v.findViewById(R.id.textTitle);
                            title.setText(shows.get(i).getTitle());

                            TextView topic = (TextView) v.findViewById(R.id.textTopic);
                            topic.setText(shows.get(i).getTopic());

                            v.animate().setDuration(ANIMATION_DURATION_SHORT).alpha(1.0f);

                            containerSchedule.addView(v, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                    }
                });
    }
}


