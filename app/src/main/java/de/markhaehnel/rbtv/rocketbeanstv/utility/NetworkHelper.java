package de.markhaehnel.rbtv.rocketbeanstv.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Map;

public final class NetworkHelper {
    public static boolean hasInternet() {
        try {
            InetAddress inetAddress = InetAddress.getByName("google.com");
            return !inetAddress.equals("");
        } catch (Exception e) {
            return false;
        }
    }
    static String getContentFromUrl(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setUseCaches(false);
        con.setRequestMethod("GET");
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    static String getContentFromUrl(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setUseCaches(false);
        con.setRequestMethod("GET");
        for (Map.Entry<String, String> header: headers.entrySet()) {
            con.setRequestProperty(header.getKey(), header.getValue());
        }
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();
        } else {
            return null;
        }
    }
}
