package de.markhaehnel.rbtv.rocketbeanstv.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class NetworkHelper {
    public static boolean hasInternet() {
        try {
            InetAddress inetAddress = InetAddress.getByName("google.com");
            return !inetAddress.equals("");
        } catch (Exception e) {
            return false;
        }
    }
    public static String getContentFromUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public static String getContentFromUrl(String url, Map<String, String> headers) throws IOException {
        Headers.Builder headerBuilder = new Headers.Builder();
        for (Map.Entry<String, String> header: headers.entrySet()) {
            headerBuilder.add(header.getKey(), header.getValue());
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).headers(headerBuilder.build()).build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}
