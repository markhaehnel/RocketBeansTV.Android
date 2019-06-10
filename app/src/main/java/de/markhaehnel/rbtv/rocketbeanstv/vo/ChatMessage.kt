package de.markhaehnel.rbtv.rocketbeanstv.vo

/*
 * {
 *   "dateFrom": "2019-01-29T13:05:11.970Z",
 *   "message": "FortOne Kreygasm",
 *   "source": 0,
 *   "specialPayload": {
 *     "emotes": "822112:0-6/41:8-15",
 *     "mod": false,
 *     "subscriber": false,
 *     "uuid": "aef98ee5-0980-4fbb-951c-f9516b30a0e5"
 *   },
 *   "user": "EZTEQ",
 *   "userIdentifier": "ezteq",
 *   "uuid": "aef98ee5-0980-4fbb-951c-f9516b30a0e5"
 *  }
 */

private const val DELIMITER_EMOTELIST = "/"
private const val DELIMITER_EMOTEINFO = ":"
private const val DELIMITER_EMOTESPAN = ","
private const val DELIMITER_EMOTESPANINFO = ","

data class ChatMessage(
    val dateFrom: String,
    val message: String,
    val source: Int,
    val user: String,
    val userIdentifier: String,
    val uuid: String,
    val specialPayload: SpecialPayload
) {
    fun getEmotes() : List<Emote> {
        val emoteList = mutableListOf<Emote>()

        if (!specialPayload.emotes.isNullOrEmpty()) {
            val emotes = specialPayload.emotes.split(DELIMITER_EMOTELIST)

            emotes.forEach {
                val (emoteId, emoteSpans) = it.split(DELIMITER_EMOTEINFO)
                val emote = Emote(emoteId.toInt())

                emoteSpans.split(DELIMITER_EMOTESPAN).forEach { emoteSpan ->
                    val (spanStart, spanEnd) = emoteSpan.split(DELIMITER_EMOTESPANINFO)
                    emote.spans.add(EmoteSpan(spanStart.toInt(), spanEnd.toInt()))
                }

                emoteList.add(emote)
            }
        }

        return emoteList
    }

    data class Emote(val id: Int, val spans: MutableList<EmoteSpan> = mutableListOf())
    data class EmoteSpan(val start: Int, val end: Int)
}

data class SpecialPayload(
    val emotes: String?,
    val mod: Boolean?,
    val subscriber: Boolean?,
    val uuid: String?
)