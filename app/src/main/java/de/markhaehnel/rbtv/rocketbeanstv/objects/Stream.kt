package de.markhaehnel.rbtv.rocketbeanstv.objects

data class Stream(var url: String, var bandwidth: String, var resolution: String) {
    var availableResolutions: Array<String> = arrayOf<String>()
    var videoId: String = ""
}