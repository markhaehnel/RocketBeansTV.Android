package de.markhaehnel.rbtv.rocketbeanstv;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GetScheduleTask extends AsyncTask<Void, String, ArrayList<ScheduleShow>> {
    protected ArrayList<ScheduleShow> doInBackground(Void... voids) {

        final String key = "";
        final String secret = "";
        final String id = "00000000-0000-0000-0000-000000000000";

        try {
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

            if (response.code() == 200) {
                JSONObject result = new JSONObject(response.body());
                return getNextShows(result, 6);
            }

            return new ArrayList<ScheduleShow>();

        } catch(Exception e) {
            e.printStackTrace();
            return new ArrayList<ScheduleShow>();
        }
    }

    protected void onPostExecute(ArrayList<ScheduleShow> shows) {
        MainActivity.getInstance().showSchedule(shows);
    }

    private ArrayList<ScheduleShow> getNextShows(JSONObject result, int count) {
        try {
            JSONArray json = result.getJSONArray("schedule");
            ArrayList<ScheduleShow> shows = new ArrayList<ScheduleShow>();
            int howMuch  = (count > json.length()) ? count : json.length();
            for (int i = 0; i < howMuch-1; i++) {
                JSONObject s = json.getJSONObject(i);
                shows.add(new ScheduleShow(
                                s.getString("title"),
                                s.getString("topic"),
                                s.getString("show"),
                                s.getString("timeStart"),
                                s.getString("timeEnd"),
                                s.getInt("length"),
                                s.getString("type")
                ));
            }
            return shows;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<ScheduleShow>();
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
