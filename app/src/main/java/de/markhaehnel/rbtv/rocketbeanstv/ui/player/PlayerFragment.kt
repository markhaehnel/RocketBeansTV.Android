package de.markhaehnel.rbtv.rocketbeanstv.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.binding.FragmentDataBindingComponent
import de.markhaehnel.rbtv.rocketbeanstv.databinding.FragmentPlayerBinding
import de.markhaehnel.rbtv.rocketbeanstv.di.Injectable
import de.markhaehnel.rbtv.rocketbeanstv.util.autoCleared
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<FragmentPlayerBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentPlayerBinding>(
            inflater,
            de.markhaehnel.rbtv.rocketbeanstv.R.layout.fragment_player,
            container,
            false,
            dataBindingComponent
        )
        /*dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                playerViewModel.retry()
            }
        }*/

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

    private fun initStreamData() {
        playerViewModel.stream.observe(viewLifecycleOwner, Observer { stream ->
            if (stream?.data != null) {
                Toast.makeText(context, stream.data.videoId, Toast.LENGTH_SHORT).show()
            }
        })

        playerViewModel.streamData.observe(viewLifecycleOwner, Observer { streamData ->
            if (streamData?.data != null) {
                Toast.makeText(context, streamData.data.videoUri.toString(), Toast.LENGTH_SHORT).show()
                videoView.setVideoURI(streamData.data.videoUri)
            }
        })
    }

    private fun initPlayer() {
        videoView.setOnPreparedListener {
            videoView.start()
        }
    }
}
