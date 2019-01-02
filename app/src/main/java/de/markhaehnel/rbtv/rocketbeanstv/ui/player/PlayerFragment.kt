package de.markhaehnel.rbtv.rocketbeanstv.ui.player

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
import de.markhaehnel.rbtv.rocketbeanstv.R
import de.markhaehnel.rbtv.rocketbeanstv.binding.FragmentDataBindingComponent
import de.markhaehnel.rbtv.rocketbeanstv.databinding.FragmentPlayerBinding
import de.markhaehnel.rbtv.rocketbeanstv.di.Injectable
import de.markhaehnel.rbtv.rocketbeanstv.ui.common.RetryCallback
import de.markhaehnel.rbtv.rocketbeanstv.ui.schedule.ScheduleFragment
import de.markhaehnel.rbtv.rocketbeanstv.ui.serviceinfo.ServiceInfoFragment
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentPlayerBinding>(
            inflater,
            de.markhaehnel.rbtv.rocketbeanstv.R.layout.fragment_player,
            container,
            false,
            dataBindingComponent
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                playerViewModel.retry()
            }
        }

        inflateFragments()

        binding = dataBinding
        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlayerViewModel::class.java)
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.serviceInfo = playerViewModel.rbtvServiceInfo
        binding.isServiceInfoVisible = playerViewModel.isServiceInfoVisible
        binding.isScheduleVisible = playerViewModel.isScheduleVisible

        initStreamData()
        initPlayer()
    }
    override fun onResume() {
        super.onResume()
        videoView.start()
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    private fun inflateFragments() {
        val scheduleFragment = ScheduleFragment()
        val serviceInfoFragment = ServiceInfoFragment()
        childFragmentManager.beginTransaction().apply {
            replace(R.id.scheduleContainer, scheduleFragment)
            replace(R.id.serviceInfoContainer, serviceInfoFragment)
            commit()
        }
    }

    private fun initStreamData() {
        playerViewModel.streamPlaylist.observe(viewLifecycleOwner, Observer { streamPlaylist ->
            if (streamPlaylist?.data != null) {
                videoView.setVideoURI(streamPlaylist.data.highestBandwith().uri().toUri())
            }
        })
    }

    private fun initPlayer() {
        videoView.setOnPreparedListener {
            videoView.start()
        }
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_UP -> {
                playerViewModel.isServiceInfoVisible.postValue(playerViewModel.isServiceInfoVisible.value == false)
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                playerViewModel.isScheduleVisible.postValue(playerViewModel.isScheduleVisible.value == false)
                return true
            }
        }
        return false
    }
}
