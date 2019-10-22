package de.markhaehnel.rbtv.rocketbeanstv

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import de.markhaehnel.rbtv.rocketbeanstv.util.Constants
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val intent = Intent(Constants.BROADCAST_KEYDOWN).apply { putExtra(Constants.BROADCAST_KEYDOWN_KEY_CODE, keyCode) }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        return super.onKeyDown(keyCode, event)
    }

    override fun onStop() {
        super.onStop()

        System.exit(0)
    }

    override fun androidInjector() = dispatchingAndroidInjector
}
