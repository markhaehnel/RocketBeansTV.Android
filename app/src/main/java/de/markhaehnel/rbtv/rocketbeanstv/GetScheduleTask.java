package de.markhaehnel.rbtv.rocketbeanstv;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GetScheduleTask extends AsyncTask<Void, String, String> {
    protected String doInBackground(Void... voids) {
        String key = "";
        String secret = "";
        String id = "00000000-0000-0000-0000-000000000000";
        String created = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").format(new Date());
        created = created.substring(0, created.length()-2) + ":" + created.substring(created.length()-2, created.length());

        String nonce = id + created + getRandomString(10);

        String sha1 = SHA1(nonce + created + secret);

        String url = "https://api.rocketmgmt.de/schedule";

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "WSSE profile=\"UsernameToken\"");
        headers.put("X-WSSE", "UsernameToken Username=\"" + key + "\", PasswordDigest=\"" + HttpRequest.Base64.encode(sha1) + "\", Nonce=\"" + HttpRequest.Base64.encode(nonce) + "\", Created=\"" + created + "\"");

        HttpRequest response = HttpRequest.get(url).headers(headers);
        String result = response.body();
        int code = response.code();

        return "";
    }

    protected void onProgressUpdate(String... strings) {

    }

    private String getRandomString(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                }
                else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();


    }


    public static String SHA1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
