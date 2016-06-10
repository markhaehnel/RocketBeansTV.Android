package de.markhaehnel.rbtv.rocketbeanstv.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

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
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
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
