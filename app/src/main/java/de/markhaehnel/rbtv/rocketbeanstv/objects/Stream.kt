package de.markhaehnel.rbtv.rocketbeanstv.objects

class Stream(var url: String, var bandwidth: String, var resolution: String) {
    var availableResolutions: Array<String>? = null
}