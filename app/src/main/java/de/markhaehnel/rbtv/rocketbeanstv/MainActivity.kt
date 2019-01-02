package de.markhaehnel.rbtv.rocketbeanstv

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import de.markhaehnel.rbtv.rocketbeanstv.ui.player.PlayerFragment
import javax.inject.Inject



class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navContainer)
        val currentFragment : PlayerFragment = navHostFragment?.getChildFragmentManager()?.fragments?.get(0) as PlayerFragment
        currentFragment.onKeyDown(keyCode, event)

        return super.onKeyDown(keyCode, event)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}
