package de.markhaehnel.rbtv.rocketbeanstv.vo

data class PlayerResponse(
    val streamingData: StreamingData
)

data class StreamingData(
    val hlsManifestUrl: String
)