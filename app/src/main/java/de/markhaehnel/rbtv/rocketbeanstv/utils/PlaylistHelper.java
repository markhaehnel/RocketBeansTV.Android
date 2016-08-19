package de.markhaehnel.rbtv.rocketbeanstv.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import de.markhaehnel.rbtv.rocketbeanstv.objects.Stream;

public final class PlaylistHelper {
    public static List<Stream> getStreamsFromM3U(String content) {
        List<Stream> streamList = new ArrayList<>();

        List<String> lines = new ArrayList<>();
        Collections.addAll(lines, content.split("\n|\r\n"));

        List<String> usedLines = new ArrayList<>();

        for (String line: lines) {
            if (line.toUpperCase().startsWith("HTTP") || line.toUpperCase().startsWith("#EXT-X-STREAM-INF:")) {
                usedLines.add(line);
            }
        }

        List<String> availableResolutions = new ArrayList<>();

        for (int i = 0; i < usedLines.size(); i+=2) {
            String url = usedLines.get(i+1);
            String info = usedLines.get(i);
            streamList.add(getStreamFromUrlAndInfo(url, info));
            availableResolutions.add(getInfoPropertyValue(info, "RESOLUTION"));
        }

        setAvailableResolutionsOnStreams(streamList, availableResolutions.toArray(new String[0]));

        return streamList;
    }

    private static void setAvailableResolutionsOnStreams(List<Stream> streamList, String[] availableResolutions) {
        for (Stream stream: streamList) {
            stream.setAvailableResolutions(availableResolutions);
        }
    }

    public static Stream getStreamByResolution(List<Stream> streams, String resolution) {
        for (Stream stream : streams) {
            if (stream.getResolution().compareToIgnoreCase(resolution) == 0) {
                return stream;
            }
        }

        return getBestResolutionStream(streams);
    }

    private static Stream getBestResolutionStream(List<Stream> streams) {
        Stream result = null;

        for (Stream stream: streams) {
            if (result != null) {
                int streamResX = Integer.parseInt(stream.getResolution().split(Pattern.quote("x"))[0]);
                int resultResX = Integer.parseInt(result.getResolution().split(Pattern.quote("x"))[0]);
                if (streamResX > resultResX) {
                    result = stream;
                }
            } else {
                result = stream;
            }
        }

        return result;
    }

    private static Stream getStreamFromUrlAndInfo(String url, String info) {
        return new Stream(url, getInfoPropertyValue(info, "BANDWIDTH"), getInfoPropertyValue(info, "RESOLUTION"));
    }

    private static String getInfoPropertyValue(String info, String property) {
        int propertyEnd = info.indexOf(property + "=") + property.length() + 1;
        return info.substring(propertyEnd, info.indexOf(",", propertyEnd));
    }
}
