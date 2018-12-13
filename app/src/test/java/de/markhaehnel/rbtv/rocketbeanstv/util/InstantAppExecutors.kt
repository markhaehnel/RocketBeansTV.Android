package de.markhaehnel.rbtv.rocketbeanstv.util

import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import java.util.concurrent.Executor

class InstantAppExecutors : AppExecutors(instant, instant, instant) {
    companion object {
        private val instant = Executor { it.run() }
    }
}