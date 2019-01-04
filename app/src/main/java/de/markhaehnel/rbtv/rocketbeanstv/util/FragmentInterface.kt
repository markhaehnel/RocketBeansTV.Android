package de.markhaehnel.rbtv.rocketbeanstv.util

import android.view.KeyEvent

interface FragmentInterface {
    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean { return false }
}