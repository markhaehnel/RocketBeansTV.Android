
package de.markhaehnel.rbtv.rocketbeanstv;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import butterknife.BindView;
import org.greenrobot.eventbus.Subscribe;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import java.util.Arrays;
import java.util.List;
import butterknife.ButterKnife;
import de.markhaehnel.rbtv.rocketbeanstv.events.BufferUpdateEvent;
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

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.exomediaplayer) EMVideoView mVideoView;
    @BindView(R.id.textCurrentShow) TextView textCurrentShow;
    @BindView(R.id.textCurrentTopic) TextView textCurrentTopic;
    @BindView(R.id.textViewerCount) TextView textViewerCount;
    @BindView(R.id.pauseImage) ImageView pauseView;
    @BindView(R.id.containerSchedule) ViewGroup containerSchedule;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.scheduleProgress) ProgressBar scheduleProgress;
    @BindView(R.id.progressCurrentShow) ProgressBar progressCurrentShow;
    @BindView(R.id.webViewChat) WebView webViewChat;

    private final String RESOLUTION = "resolution";
    private final long ANIMATION_DURATION_NORMAL = 250;
    private final long ANIMATION_DURATION_SHORT = 100;

    private ChatState mChatState = ChatState.HIDDEN;
    private String mCurrentResolution;
    private String mVideoId = "";

    private String[] mAvailableResolutions;
    private boolean mPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JodaTimeAndroid.init(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                new ChannelInfoLoader(getString(R.string.RBTVKEY), getString(R.string.RBTVSECRET)).start();
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
            webViewChat.setFocusable(false);
            webViewChat.setFocusableInTouchMode(false);
            webViewChat.setClickable(false);
            webViewChat.getSettings().setJavaScriptEnabled(true);
            webViewChat.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            view.loadUrl("javascript:(function() { " +
                                        "document.getElementsByTagName('yt-live-chat-header-renderer')[0].remove();" +
                                        "document.getElementsByTagName('yt-live-chat-message-input-renderer')[0].remove();" +
                                    "})()");
                        }
                });
            webViewChat.loadUrl("https://gaming.youtube.com/live_chat?is_popout=1&v=" + videoId);
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
        return super.onKeyDown(keyCode, event);
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
                    new StreamUrlLoader(options[which]).start();

                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    private void showProgressBar() {
        if (progressBar.getVisibility() == View.INVISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.animate().setDuration(ANIMATION_DURATION_NORMAL).alpha(1.0f);
        }
    }

    private void hideProgressBar() {
        progressBar.animate()
                .setDuration(ANIMATION_DURATION_NORMAL)
                .alpha(0.0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
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
    protected void onPause() {
        mPaused = true;
        mVideoView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mVideoView.start();
        mPaused = false;
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChannelInfoUpdate(ChannelInfoUpdateEvent event) {
        switch (event.getStatus())
        {
            case OK:
                if (!event.getScheduleItem().getTitle().equals(textCurrentShow.getText())) {
                    textCurrentShow.setText(event.getScheduleItem().getTitle());
                    textCurrentTopic.setText(event.getScheduleItem().getTopic());
                    toggleInfoOverlay(true);
                }

                textViewerCount.setText(event.getRbtv().getViewerCount());

                DateTime startTime = new DateTime(event.getScheduleItem().getTimeStart());
                DateTime now = DateTime.now();
                Duration duration = new Duration(startTime, now);
                progressCurrentShow.setMax(event.getScheduleItem().getLength().intValue());

                if (duration.getStandardSeconds() < progressCurrentShow.getMax()) {
                    ObjectAnimator animation = ObjectAnimator.ofInt(progressCurrentShow, "progress", (int) duration.getStandardSeconds());
                    animation.setDuration(1000);
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                }

                break;

            case FAILED:
                textCurrentShow.setText(R.string.no_info_available);
                new ChannelInfoLoader(getString(R.string.RBTVKEY), getString(R.string.RBTVSECRET)).start();
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
            mPaused = true;
            mVideoView.pause();

        } else {
            mVideoView.start();
            mPaused = false;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBufferUpdate(BufferUpdateEvent event) {
        if (event.getStatus() == BufferUpdateEvent.BufferState.BUFFERING_END) {
            hideProgressBar();
        } else {
            if(!mPaused) {
                showProgressBar();
            }
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
        mVideoView.setMeasureBasedOnAspectRatioEnabled(true);
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                mVideoView.start();
            }
        });
        mVideoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError() {
                new StreamUrlLoader(mCurrentResolution).start();
                return true;
            }
        });
        new PlayStateListener(mVideoView).start();
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
            mPaused = false;
            pauseView.setVisibility(View.INVISIBLE);
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
                            switch (shows.get(i).getType().toUpperCase()) {
                                case "LIVE":
                                    type.setBackgroundColor(ContextCompat.getColor(v.getContext(), android.R.color.holo_red_dark));
                                    break;
                                case "PREMIERE":
                                    type.setBackgroundColor(ContextCompat.getColor(v.getContext(), android.R.color.holo_green_dark));
                                    break;
                                default:
                                    type.setBackgroundColor(Color.TRANSPARENT);
                            }

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


