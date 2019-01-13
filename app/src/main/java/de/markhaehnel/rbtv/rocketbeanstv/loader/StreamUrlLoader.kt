package de.markhaehnel.rbtv.rocketbeanstv.loader


import com.google.gson.Gson

import org.greenrobot.eventbus.EventBus

import java.net.URLDecoder
import java.util.HashMap
import java.util.regex.Pattern

import de.markhaehnel.rbtv.rocketbeanstv.events.StreamUrlChangeEvent
import de.markhaehnel.rbtv.rocketbeanstv.objects.PlayerResponse
import de.markhaehnel.rbtv.rocketbeanstv.objects.RBTV
import de.markhaehnel.rbtv.rocketbeanstv.objects.Stream

import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper
import de.markhaehnel.rbtv.rocketbeanstv.utils.PlaylistHelper


class StreamUrlLoader(private val mResolution: String) : Thread() {

    override fun run() {

        try {
            val response = NetworkHelper.getContentFromUrl("https://rbtvapi.markhaehnel.de/stream")

            val gson = Gson()
            val data: RBTV = gson.fromJson(response, RBTV::class.java)

            if (data.error == null) {

                val streamList: MutableList<Stream> = mutableListOf<Stream>();

                for (videoId in data.cameras) {
                    val url = "https://www.youtube.com/get_video_info?&video_id=${videoId}"
                    val ytResponse = NetworkHelper.getContentFromUrl(url)

                    val parameters = HashMap<String, String>()
                    for (param in ytResponse.split(Pattern.quote("&").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                        val line = param.split(Pattern.quote("=").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (line.size == 2)
                            parameters.put(line[0], URLDecoder.decode(line[1], "UTF-8"))
                    }

                    val playerResponse = gson.fromJson(parameters["player_response"], PlayerResponse::class.java)

                    val streams = PlaylistHelper.getStreamsFromM3U(NetworkHelper.getContentFromUrl(playerResponse.streamingData.hlsManifestUrl))
                    val stream = PlaylistHelper.getStreamByResolution(streams, mResolution)

                    stream.videoId = videoId

                    streamList.add(stream)
                }

                EventBus.getDefault().post(StreamUrlChangeEvent(streamList, EventStatus.OK))
            } else {
                EventBus.getDefault().post(StreamUrlChangeEvent(EventStatus.FAILED))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            EventBus.getDefault().post(StreamUrlChangeEvent(EventStatus.FAILED))
        }


    }
}
