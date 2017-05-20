package de.markhaehnel.rbtv.rocketbeanstv.utils

import java.util.ArrayList
import java.util.Collections
import java.util.regex.Pattern

import de.markhaehnel.rbtv.rocketbeanstv.objects.Stream

object PlaylistHelper {
    fun getStreamsFromM3U(content: String): List<Stream> {
        val streamList = ArrayList<Stream>()

        val lines = ArrayList<String>()
        Collections.addAll(lines, *content.split("\n|\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())

        val usedLines = ArrayList<String>()

        for (line in lines) {
            if (line.toUpperCase().startsWith("HTTP") || line.toUpperCase().startsWith("#EXT-X-STREAM-INF:")) {
                usedLines.add(line)
            }
        }

        val availableResolutions = ArrayList<String>()

        var i = 0
        while (i < usedLines.size) {
            val url = usedLines[i + 1]
            val info = usedLines[i]
            streamList.add(getStreamFromUrlAndInfo(url, info))
            availableResolutions.add(getInfoPropertyValue(info, "RESOLUTION"))
            i += 2
        }

        setAvailableResolutionsOnStreams(streamList, availableResolutions.toTypedArray())

        return streamList
    }

    private fun setAvailableResolutionsOnStreams(streamList: List<Stream>, availableResolutions: Array<String>) {
        for (stream in streamList) {
            stream.availableResolutions = availableResolutions
        }
    }

    fun getStreamByResolution(streams: List<Stream>, resolution: String): Stream {
        for (stream in streams) {
            if (stream.resolution.compareTo(resolution, ignoreCase = true) == 0) {
                return stream
            }
        }

        return getBestResolutionStream(streams)
    }

    private fun getBestResolutionStream(streams: List<Stream>): Stream {
        var result: Stream? = null

        for (stream in streams) {
            if (result != null) {
                val streamResX = Integer.parseInt(stream.resolution.split(Pattern.quote("x").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                val resultResX = Integer.parseInt(result.resolution.split(Pattern.quote("x").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                if (streamResX > resultResX) {
                    result = stream
                }
            } else {
                result = stream
            }
        }

        return result!!
    }

    private fun getStreamFromUrlAndInfo(url: String, info: String): Stream {
        return Stream(url, getInfoPropertyValue(info, "BANDWIDTH"), getInfoPropertyValue(info, "RESOLUTION"))
    }

    private fun getInfoPropertyValue(info: String, property: String): String {
        val propertyEnd = info.indexOf(property + "=") + property.length + 1
        return info.substring(propertyEnd, info.indexOf(",", propertyEnd))
    }
}
