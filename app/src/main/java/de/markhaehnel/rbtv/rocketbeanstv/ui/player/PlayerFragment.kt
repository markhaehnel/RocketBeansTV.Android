package de.markhaehnel.rbtv.rocketbeanstv.ui.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.markhaehnel.rbtv.rocketbeanstv.R
import de.markhaehnel.rbtv.rocketbeanstv.binding.FragmentDataBindingComponent
import de.markhaehnel.rbtv.rocketbeanstv.databinding.FragmentPlayerBinding
import de.markhaehnel.rbtv.rocketbeanstv.di.Injectable
import de.markhaehnel.rbtv.rocketbeanstv.ui.chat.ChatFragment
import de.markhaehnel.rbtv.rocketbeanstv.ui.common.RetryCallback
import de.markhaehnel.rbtv.rocketbeanstv.ui.common.SharedViewModel
import de.markhaehnel.rbtv.rocketbeanstv.ui.schedule.ScheduleFragment
import de.markhaehnel.rbtv.rocketbeanstv.ui.serviceinfo.ServiceInfoFragment
import de.markhaehnel.rbtv.rocketbeanstv.util.Constants
import de.markhaehnel.rbtv.rocketbeanstv.util.IntervalTask
import de.markhaehnel.rbtv.rocketbeanstv.util.autoCleared
import de.markhaehnel.rbtv.rocketbeanstv.util.highestBandwith
import kotlinx.android.synthetic.main.fragment_player.*
import javax.inject.Inject

class PlayerFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentPlayerBinding>()

    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var playStateObserver: IntervalTask

    private val keyDownBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.getIntExtra(Constants.BROADCAST_KEYDOWN_KEY_CODE, 0)) {
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_DPAD_RIGHT,
                KeyEvent.KEYCODE_DPAD_DOWN,
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    sharedViewModel.showServiceInfo()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentPlayerBinding>(
            inflater,
            R.layout.fragment_player,
            container,
            false,
            dataBindingComponent
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                playerViewModel.retry()
            }
        }

        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlayerViewModel::class.java)
        binding.setLifecycleOwner(viewLifecycleOwner)

        sharedViewModel = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        binding.isChatVisible = sharedViewModel.chatVisible
        binding.isBuffering = playerViewModel.isBuffering

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(keyDownBroadcastReceiver, IntentFilter(Constants.BROADCAST_KEYDOWN))

        initObservers()
        initPlayer()
        inflateChat()
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    private fun showSchedule() {
        val fragmentTag = "fragment_schedule"
        val scheduleFragment = ScheduleFragment()
        scheduleFragment.show(childFragmentManager, fragmentTag)
    }

    private fun inflateChat() {
        val fragmentTag = "tagFragmentChat"

        if (childFragmentManager.findFragmentByTag(fragmentTag) == null) {
            val chatFragment = ChatFragment()
            childFragmentManager.beginTransaction().apply {
                replace(R.id.chatContainer, chatFragment, fragmentTag)
                commit()
            }
        }
    }

    /**
     * Inflates [ServiceInfoFragment] into it's container if not already inflated
     * @return wether the fragment was inflated
     */
    private fun inflateServiceInfoFragment() : Boolean {
        val fragmentTag = "tagFragmentServiceInfo"

        if (childFragmentManager.findFragmentByTag(fragmentTag) == null) {
            childFragmentManager.beginTransaction().apply {
                replace(R.id.serviceInfoContainer, ServiceInfoFragment(), fragmentTag)
                addToBackStack(null)
                commit()
            }
            return true
        }

        return false
    }

    private fun initObservers() {
        playerViewModel.streamPlaylist.observe(viewLifecycleOwner, Observer { streamPlaylist ->
            if (streamPlaylist?.data != null) {
                videoView.setVideoURI(streamPlaylist.data.highestBandwith().uri().toUri())
            }
        })

        sharedViewModel.scheduleTrigger.observe(viewLifecycleOwner, Observer { scheduleTrigger ->
            if (scheduleTrigger == true) {
                showSchedule()
                sharedViewModel.resetSchedule()
            }
        })

        sharedViewModel.serviceInfoTrigger.observe(viewLifecycleOwner, Observer { serviceInfoTrigger ->
            if (serviceInfoTrigger == true) {
                inflateServiceInfoFragment()
                sharedViewModel.resetServiceInfo()
            }
        })
    }

    private fun initPlayer() {
        videoView.setOnPreparedListener {
            videoView.start()
            playStateObserver = IntervalTask(lifecycle, 2000, Runnable {
                playerViewModel.isBuffering.postValue(!videoView.isPlaying)
            })
        }
    }
}
