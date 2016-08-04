
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import de.markhaehnel.rbtv.rocketbeanstv.events.ChannelInfoUpdateEvent;
import de.markhaehnel.rbtv.rocketbeanstv.events.InternetCheckEvent;
import de.markhaehnel.rbtv.rocketbeanstv.events.ScheduleLoadEvent;
import de.markhaehnel.rbtv.rocketbeanstv.events.StreamUrlChangeEvent;
import de.markhaehnel.rbtv.rocketbeanstv.events.TogglePlayStateEvent;
import de.markhaehnel.rbtv.rocketbeanstv.loader.ScheduleLoader;
import de.markhaehnel.rbtv.rocketbeanstv.loader.StreamUrlLoader;
import de.markhaehnel.rbtv.rocketbeanstv.utils.*;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.*;
import static de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper.hasInternet;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {

    @BindView(R.id.exomediaplayer) EMVideoView mVideoView;
    @BindView(R.id.textCurrentShow) TextView textCurrentShow;
    @BindView(R.id.textViewerCount) TextView textViewerCount;
    @BindView(R.id.pauseImage) ImageView pauseView;

    private ChatState mChatState = ChatState.HIDDEN;
    private Quality mCurrentQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
                setupChat();
                //preparePlayer();
                break;
            case FAILED:
                showMessage(R.string.error_noInternet);
                break;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupChat() {
        WebView chat = (WebView)findViewById(R.id.webViewChat);
        if (chat != null) {
            chat.setAlpha(0.75f);
            chat.getSettings().setJavaScriptEnabled(true);
            chat.loadUrl("https://ezteq.github.io/rbtv-firetv/");
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
                changeStreamQuality();
                return true;
        }
        return false;
    }

    private void changeStreamQuality() {
        CharSequence options[] = new CharSequence[] {"Quelle", "Hoch", "Mittel", "Niedrig", "Mobil"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.menuTitle_chooseQuality));

        builder.setSingleChoiceItems(options, mCurrentQuality.ordinal(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
                if (pb != null) {
                    pb.setVisibility(View.VISIBLE);
                }
                new StreamUrlLoader(Quality.values()[which]).start();
                mCurrentQuality = Quality.values()[which];

                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void toggleSchedule() {
        LinearLayout schedule = (LinearLayout)findViewById(R.id.containerSchedule);
        if (schedule != null) {
            if (schedule.getVisibility() == View.INVISIBLE) {
                new ScheduleLoader().start();
            } else {
                schedule.setVisibility(View.INVISIBLE);
                schedule.removeAllViews();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.start();
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
                if (pb != null) {
                    pb.setVisibility(View.INVISIBLE);
                }
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
                textViewerCount.setText(String.valueOf(event.getViewerCount()));
                if (!event.getCurrentShow().equals(textCurrentShow.getText())) {
                    textCurrentShow.setText(event.getCurrentShow());
                    toggleInfoOverlay(true);
                }
                break;

            case FAILED:
                textViewerCount.setText(R.string.empty);
                textCurrentShow.setText(R.string.no_info_available);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTogglePlayState(TogglePlayStateEvent event) {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            pauseView.startAnimation(AnimationBuilder.getFadeInAnimation());
            pauseView.setVisibility(View.VISIBLE);
        } else {
            mVideoView.start();
            pauseView.startAnimation(AnimationBuilder.getFadeOutAnimation());
            pauseView.setVisibility(View.INVISIBLE);
        }
    }

    private void toggleChat() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mVideoView.getLayoutParams());
        WebView chat = (WebView) findViewById(R.id.webViewChat);

        if (chat != null) {
            switch (mChatState) {
                case HIDDEN:
                    //to fixed
                    int dpiMargin = 300 * Math.round(this.getResources().getDisplayMetrics().density);
                    lp.setMargins(0, 0, dpiMargin, 0);
                    chat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
                    chat.setVisibility(View.VISIBLE);
                    mChatState = ChatState.FIXED;
                    break;
                case FIXED:
                    //to overlay
                    chat.setBackgroundColor(ContextCompat.getColor(this, R.color.overlayBackground));
                    chat.setVisibility(View.VISIBLE);
                    mChatState = ChatState.OVERLAY;
                    break;
                case OVERLAY:
                    //to hidden
                    lp.setMargins(0, 0, 0, 0);
                    chat.setVisibility(View.INVISIBLE);
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
        mCurrentQuality = Quality.values()[prefs.getInt("quality", Quality.CHUNKED.ordinal())];
        new StreamUrlLoader(mCurrentQuality).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStreamUrlChanged(StreamUrlChangeEvent event) {
        if (event.getStatus() == EventStatus.OK) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("quality", mCurrentQuality.ordinal());
            editor.apply();

            mVideoView.stopPlayback();
            mVideoView.seekTo(0);
            mVideoView.setVideoURI(Uri.parse(event.getUrl()));
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
        LinearLayout infoOverlay = (LinearLayout) findViewById(R.id.containerCurrentShow);
        if (infoOverlay != null) {
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScheduleLoaded(ScheduleLoadEvent event) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.containerSchedule);
        if (insertPoint != null) {
            insertPoint.removeAllViews();
            insertPoint.setVisibility(View.VISIBLE);

            int animMultiplier = 150;

            for (int i = 0; i < event.getShows().size(); i++) {
                //TODO: check if set insertPoint back to null
                View v = vi.inflate(R.layout.component_scheduleitem, insertPoint);

                TextView timeStart = (TextView) v.findViewById(R.id.textTimeStart);
                timeStart.setText(event.getShows().get(i).getTimeStart());

                TextView type = (TextView) v.findViewById(R.id.textType);
                type.setText(event.getShows().get(i).getType());

                TextView title = (TextView) v.findViewById(R.id.textTitle);
                title.setText(event.getShows().get(i).getTitle());

                TextView topic = (TextView) v.findViewById(R.id.textTopic);
                topic.setText(event.getShows().get(i).getTopic());

                v.startAnimation(AnimationBuilder.createDelayedFadeInAnimation(i * animMultiplier));

                //TODO: check if this is needed
                insertPoint.addView(v, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }
}


