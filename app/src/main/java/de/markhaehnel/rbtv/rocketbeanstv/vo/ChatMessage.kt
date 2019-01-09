package de.markhaehnel.rbtv.rocketbeanstv.vo

data class ChatMessage(
    val dateFrom: String,
    val message: String,
    val source: Int,
    val user: String,
    val userIdentifier: String,
    val uuid: String
)