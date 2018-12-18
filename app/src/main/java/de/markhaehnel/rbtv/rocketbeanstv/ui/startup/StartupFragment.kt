package de.markhaehnel.rbtv.rocketbeanstv.ui.startup

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import de.markhaehnel.rbtv.rocketbeanstv.R
import de.markhaehnel.rbtv.rocketbeanstv.binding.FragmentDataBindingComponent
import de.markhaehnel.rbtv.rocketbeanstv.databinding.FragmentStartupBinding
import de.markhaehnel.rbtv.rocketbeanstv.di.Injectable
import de.markhaehnel.rbtv.rocketbeanstv.util.autoCleared
import kotlinx.android.synthetic.main.fragment_startup.*
import javax.inject.Inject

class StartupFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // mutable for testing
    var binding by autoCleared<FragmentStartupBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var startupViewModel: StartupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentStartupBinding>(
            inflater,
            R.layout.fragment_startup,
            container,
            false,
            dataBindingComponent
        )
        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startupViewModel = ViewModelProviders.of(this, viewModelFactory).get(StartupViewModel::class.java)
        binding.setLifecycleOwner(viewLifecycleOwner)
    }

    override fun onStart() {
        super.onStart()
        imageLogo.startAnimation(AnimationUtils.loadAnimation(context, R.anim.infinite_rotate_zoom_in_out_anim))
    }

    override fun onResume() {
        super.onResume()
        NavHostFragment.findNavController(this).navigate(StartupFragmentDirections.actionStartupFragmentToPlayerFragment())
    }
}
