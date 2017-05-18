package de.markhaehnel.rbtv.rocketbeanstv

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import org.greenrobot.eventbus.Subscribe
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import co.metalab.asyncawait.async

import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.firebase.analytics.FirebaseAnalytics

import net.danlew.android.joda.JodaTimeAndroid

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.Arrays
import de.markhaehnel.rbtv.rocketbeanstv.events.BufferUpdateEvent
import de.markhaehnel.rbtv.rocketbeanstv.events.ChannelInfoUpdateEvent
import de.markhaehnel.rbtv.rocketbeanstv.events.ScheduleLoadEvent
import de.markhaehnel.rbtv.rocketbeanstv.events.StreamUrlChangeEvent
import de.markhaehnel.rbtv.rocketbeanstv.loader.ChannelInfoLoader
import de.markhaehnel.rbtv.rocketbeanstv.loader.ScheduleLoader
import de.markhaehnel.rbtv.rocketbeanstv.loader.StreamUrlLoader
import de.markhaehnel.rbtv.rocketbeanstv.objects.schedule.ScheduleItem
import de.markhaehnel.rbtv.rocketbeanstv.utils.*
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.*

import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper.hasInternet
import lib.bindView

class MainActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    val mVideoView: VideoView by bindView(R.id.exomediaplayer)
    val textCurrentShow: TextView by bindView(R.id.textCurrentShow)
    val textCurrentTopic: TextView by bindView(R.id.textCurrentTopic)
    val textViewerCount: TextView by bindView(R.id.textViewerCount)
    val pauseView: ImageView by bindView(R.id.pauseImage)
    val containerSchedule: ViewGroup by bindView(R.id.containerSchedule)
    val progressBar: ProgressBar by bindView(R.id.progressBar)
    val scheduleProgress: ProgressBar by bindView(R.id.scheduleProgress)
    val progressCurrentShow: ProgressBar by bindView(R.id.progressCurrentShow)
    val webViewChat: WebView by bindView(R.id.webViewChat)

    private val RESOLUTION = "resolution"
    private val ANIMATION_DURATION_NORMAL: Long = 250
    private val ANIMATION_DURATION_SHORT: Long = 100

    private var mChatState = ChatState.HIDDEN
    private var mCurrentResolution: String? = null
    private var mVideoId = ""

    private var mAvailableResolutions: Array<String>? = null
    private var mPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        JodaTimeAndroid.init(this)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

        async {
            val connected = await { hasInternet() }
            if (connected) {
                setupListeners()
                MediaSessionHandler.setupMediaSession(this@MainActivity)
                preparePlayer()
                ChannelInfoLoader().start()
            } else {
                showMessage(R.string.error_noInternet)
            }
        }
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        System.exit(0)
        super.onStop()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupChat(videoId: String) {
        webViewChat.alpha = 0.75f
        webViewChat.isFocusable = false
        webViewChat.isFocusableInTouchMode = false
        webViewChat.isClickable = false
        webViewChat.settings.javaScriptEnabled = true
        webViewChat.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                view.loadUrl("javascript:(function() { " +
                        "document.getElementsByTagName('yt-live-chat-header-renderer')[0].remove();" +
                        "document.getElementsByTagName('yt-live-chat-message-input-renderer')[0].remove();" +
                        "})()")
            }
        })
        webViewChat.loadUrl("https://gaming.youtube.com/live_chat?is_popout=1&v=" + videoId)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                toggleChat()
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                toggleSchedule()
                return true
            }
            KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_HOME -> {
                System.exit(0)
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                toggleInfoOverlay(false)
                return true
            }
            KeyEvent.KEYCODE_MENU -> {
                changeStreamResolution()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun changeStreamResolution() {
        if ((mAvailableResolutions as Array<String>).isNotEmpty()) {
            val options = mAvailableResolutions

            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.menuTitle_chooseQuality))

            val pos = Arrays.asList(*options!!).indexOf(mCurrentResolution)
            builder.setSingleChoiceItems(options, pos) { dialog, which ->
                StreamUrlLoader(options[which]).start()

                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun showProgressBar() {
        if (progressBar.visibility == View.INVISIBLE) {
            progressBar.visibility = View.VISIBLE
            progressBar.animate().setDuration(ANIMATION_DURATION_NORMAL).alpha(1.0f)
        }
    }

    private fun hideProgressBar() {
        progressBar.animate()
                .setDuration(ANIMATION_DURATION_NORMAL)
                .alpha(0.0f)
                .withEndAction { progressBar.visibility = View.INVISIBLE }
    }

    private fun toggleSchedule() {
        if (containerSchedule.visibility == View.INVISIBLE) {
            scheduleProgress.visibility = View.VISIBLE
            scheduleProgress.alpha = 1.0f
            containerSchedule.alpha = 0.0f
            containerSchedule.visibility = View.VISIBLE
            containerSchedule.animate()
                    .setDuration(ANIMATION_DURATION_NORMAL)
                    .alpha(1.0f)
            ScheduleLoader().start()
        } else {
            containerSchedule.animate()
                    .setDuration(ANIMATION_DURATION_NORMAL)
                    .alpha(0.0f)
                    .withEndAction {
                        containerSchedule.visibility = View.INVISIBLE
                        if (containerSchedule.childCount > 1) {
                            containerSchedule.removeViews(1, containerSchedule.childCount - 1)
                        }
                    }
        }
    }

    override fun onPause() {
        mPaused = true
        mVideoView.pause()
        super.onPause()
    }

    override fun onResume() {
        mVideoView.start()
        mPaused = false
        super.onResume()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChannelInfoUpdate(event: ChannelInfoUpdateEvent) {
        when (event.status) {
            Enums.EventStatus.OK -> {
                if (event.scheduleItem.title != textCurrentShow.text) {
                    textCurrentShow.text = event.scheduleItem.title
                    textCurrentTopic.text = event.scheduleItem.topic
                    toggleInfoOverlay(true)
                }

                textViewerCount.text = event.rbtv.viewerCount

                val startTime = DateTime(event.scheduleItem.timeStart)
                val now = DateTime.now()
                val duration = Duration(startTime, now)
                progressCurrentShow.max = event.scheduleItem.length!!.toInt()

                if (duration.standardSeconds < progressCurrentShow.max) {
                    val animation = ObjectAnimator.ofInt(progressCurrentShow, "progress", duration.standardSeconds.toInt())
                    animation.duration = 1000
                    animation.interpolator = DecelerateInterpolator()
                    animation.start()
                }
            }

            Enums.EventStatus.FAILED -> {
                textCurrentShow.setText(R.string.no_info_available)
                ChannelInfoLoader().start()
            }
        }
        Heartbeat.doHeartbeat(mFirebaseAnalytics as FirebaseAnalytics)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBufferUpdate(event: BufferUpdateEvent) {
        if (event.status === BufferUpdateEvent.BufferState.BUFFERING_END) {
            hideProgressBar()
        } else if (!mPaused) {
            showProgressBar()
        }
    }

    private fun toggleChat() {
        val lp = FrameLayout.LayoutParams(mVideoView.layoutParams)

        when (mChatState) {
            ChatState.HIDDEN -> {
                //to fixed
                val dpiMargin = 300 * Math.round(this.resources.displayMetrics.density)
                lp.setMargins(0, 0, dpiMargin, 0)
                webViewChat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
                webViewChat.visibility = View.VISIBLE
                mChatState = ChatState.FIXED
            }
            ChatState.FIXED -> {
                //to overlay
                webViewChat.setBackgroundColor(ContextCompat.getColor(this, R.color.overlayBackground))
                webViewChat.visibility = View.VISIBLE
                mChatState = ChatState.OVERLAY
            }
            ChatState.OVERLAY -> {
                //to hidden
                lp.setMargins(0, 0, 0, 0)
                webViewChat.visibility = View.INVISIBLE
                mChatState = ChatState.HIDDEN
            }
        }

        mVideoView.layoutParams = lp
    }

    private fun setupListeners() {
        mVideoView.setMeasureBasedOnAspectRatioEnabled(true)
        mVideoView.setOnPreparedListener { mVideoView.start() }
        mVideoView.setOnErrorListener {
            StreamUrlLoader(mCurrentResolution!!).start()
            true
        }
        PlayStateListener(mVideoView).start()
    }

    private fun preparePlayer() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        mCurrentResolution = prefs.getString(RESOLUTION, "1x1")
        StreamUrlLoader(mCurrentResolution!!).start()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStreamUrlChanged(event: StreamUrlChangeEvent) {
        if (event.status == EventStatus.OK) {

            if (mCurrentResolution!!.compareTo(event.stream!!.resolution, ignoreCase = true) != 0) {
                mCurrentResolution = event.stream!!.resolution
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val editor = prefs.edit()
                editor.putString(RESOLUTION, mCurrentResolution)
                editor.apply()
            }

            mAvailableResolutions = event.stream!!.availableResolutions
            mPaused = false
            pauseView.visibility = View.INVISIBLE
            mVideoView.stopPlayback()
            mVideoView.seekTo(0)
            mVideoView.setVideoURI(Uri.parse(event.stream!!.url))

            if (mVideoId.trim { it <= ' ' }.isEmpty() || event.videoId!!.compareTo(mVideoId) != 0) {
                setupChat(event.videoId!!)
                mVideoId = event.videoId!!
            }
        } else {
            showMessage(R.string.error_unknown)
        }
    }

    fun showMessage(resourceId: Int) {
        val ad = AlertDialog.Builder(this)
        ad.setCancelable(false)
        ad.setMessage(getString(resourceId))
        ad.setTitle(getString(R.string.error))
        ad.setNeutralButton(getString(R.string.okay), { _, _ -> System.exit(0) })
        ad.create().show()
    }

    private fun toggleInfoOverlay(autoHide: Boolean) {
        val infoOverlay = findViewById(R.id.containerCurrentShow) as LinearLayout
        if (infoOverlay.visibility == View.INVISIBLE) {
            infoOverlay.alpha = 0.0f
            infoOverlay.visibility = View.VISIBLE
            infoOverlay.animate()
                    .setDuration(ANIMATION_DURATION_NORMAL)
                    .alpha(1.0f)
            if (autoHide) {
                infoOverlay.startAnimation(AnimationBuilder.delayedFadeOutAnimation)
                infoOverlay.visibility = View.INVISIBLE
            }
        } else {
            infoOverlay.animate()
                    .setDuration(ANIMATION_DURATION_NORMAL)
                    .alpha(0.0f)
                    .withEndAction { infoOverlay.visibility = View.INVISIBLE }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScheduleLoaded(event: ScheduleLoadEvent) {
        when (event.status) {
            Enums.EventStatus.OK -> fillSchedule(event.shows as List<ScheduleItem>)
            Enums.EventStatus.FAILED -> {
                Toast.makeText(this, R.string.error_getSchedule, Toast.LENGTH_SHORT).show()
                containerSchedule.startAnimation(AnimationBuilder.fadeOutAnimation)
                containerSchedule.animate()
                        .setDuration(ANIMATION_DURATION_NORMAL)
                        .alpha(0.0f)
                        .withEndAction { containerSchedule.visibility = View.INVISIBLE }
            }
        }
    }

    private fun fillSchedule(shows: List<ScheduleItem>) {
        val vi = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        scheduleProgress.animate()
                .setDuration(ANIMATION_DURATION_SHORT)
                .alpha(0.0f)
                .withEndAction {
                    scheduleProgress.visibility = View.GONE

                    for (i in shows.indices) {
                        @SuppressLint("InflateParams")
                        val v = vi.inflate(R.layout.component_scheduleitem, containerSchedule, false)
                        v.alpha = 0.0f

                        val timeStart = v.findViewById(R.id.textTimeStart) as TextView
                        timeStart.text = shows[i].timeStartShort

                        val type = v.findViewById(R.id.textType) as TextView
                        type.text = shows[i].type
                        when (shows[i].type!!.toUpperCase()) {
                            "LIVE" -> type.setBackgroundColor(ContextCompat.getColor(v.context, android.R.color.holo_red_dark))
                            "PREMIERE" -> type.setBackgroundColor(ContextCompat.getColor(v.context, android.R.color.holo_green_dark))
                            else -> type.setBackgroundColor(Color.TRANSPARENT)
                        }

                        val title = v.findViewById(R.id.textTitle) as TextView
                        title.text = shows[i].title

                        val topic = v.findViewById(R.id.textTopic) as TextView
                        topic.text = shows[i].topic

                        v.animate().setDuration(ANIMATION_DURATION_SHORT).alpha(1.0f)

                        containerSchedule.addView(v, -1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                    }
                }
    }
}


