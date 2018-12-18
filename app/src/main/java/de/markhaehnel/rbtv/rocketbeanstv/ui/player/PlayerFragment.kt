package de.markhaehnel.rbtv.rocketbeanstv.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import de.markhaehnel.rbtv.rocketbeanstv.binding.FragmentDataBindingComponent
import de.markhaehnel.rbtv.rocketbeanstv.databinding.FragmentPlayerBinding
import de.markhaehnel.rbtv.rocketbeanstv.di.Injectable
import de.markhaehnel.rbtv.rocketbeanstv.ui.common.RetryCallback
import de.markhaehnel.rbtv.rocketbeanstv.util.autoCleared
import kotlinx.android.synthetic.main.fragment_player.*
import javax.inject.Inject

class PlayerFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FragmentPlayerBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

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

        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(PlayerViewModel::class.java)
        binding.setLifecycleOwner(viewLifecycleOwner)

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

    private fun initStreamData() {
        playerViewModel.streamInfo.observe(viewLifecycleOwner, Observer { stream ->
            //TODO: implement stream info panel
        })

        playerViewModel.streamManifest.observe(viewLifecycleOwner, Observer { streamData ->
            if (streamData?.data != null) {
                videoView.setVideoURI(streamData.data.hlsUri)
            }
        })

        playerViewModel.currentShow.observe(viewLifecycleOwner, Observer { currentShow ->
            //TODO: implement stream info panel
        })

        playerViewModel.upcomingShows.observe(viewLifecycleOwner, Observer { upcomingShows ->
            //TODO: implement schedule
        })
    }

    private fun initPlayer() {
        videoView.setOnPreparedListener {
            videoView.start()
        }
    }
}
