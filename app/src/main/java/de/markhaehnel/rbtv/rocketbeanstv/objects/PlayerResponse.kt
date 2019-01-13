package de.markhaehnel.rbtv.rocketbeanstv.objects

data class PlayerResponse(
    val streamingData: StreamingData
)

data class StreamingData(
    val hlsManifestUrl: String
)