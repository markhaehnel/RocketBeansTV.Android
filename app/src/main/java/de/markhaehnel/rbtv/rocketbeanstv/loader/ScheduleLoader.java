package de.markhaehnel.rbtv.rocketbeanstv.loader;

import android.annotation.SuppressLint;
import android.util.Base64;
import org.greenrobot.eventbus.EventBus;
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

import de.markhaehnel.rbtv.rocketbeanstv.events.ScheduleLoadEvent;
import de.markhaehnel.rbtv.rocketbeanstv.utils.Enums.EventStatus;
import de.markhaehnel.rbtv.rocketbeanstv.utils.NetworkHelper;
import de.markhaehnel.rbtv.rocketbeanstv.utils.RandomString;
import de.markhaehnel.rbtv.rocketbeanstv.utils.ScheduleShow;

public class ScheduleLoader extends Thread {
    public void run() {

        final String key = "";
        final String secret = "";
        final String id = "00000000-0000-0000-0000-000000000000";

        try {
            @SuppressLint("SimpleDateFormat")
            String created = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").format(new Date());
            created = created.substring(0, created.length()-2) + ":" + created.substring(created.length()-2, created.length());

            String nonce = id + created + RandomString.generate(10);
            String sha1 = SHA1(nonce + created + secret);
            String url = "https://api.rocketmgmt.de/schedule";
            String b64sha1 = Base64.encodeToString(sha1.getBytes(), Base64.NO_WRAP);
            String b64nonce = Base64.encodeToString(nonce.getBytes(), Base64.NO_WRAP);

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Authorization", "WSSE profile=\"UsernameToken\"");
            headers.put("X-WSSE", "UsernameToken Username=\"" + key + "\", PasswordDigest=\"" + b64sha1 + "\", Nonce=\"" + b64nonce + "\", Created=\"" + created + "\"");

            String data = NetworkHelper.getContentFromUrl(url, headers);
            JSONObject result = new JSONObject(data);
            EventBus.getDefault().post(new ScheduleLoadEvent(getNextShows(result, 6), EventStatus.OK));
        } catch(Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new ScheduleLoadEvent(EventStatus.FAILED));
        }
    }

    private ArrayList<ScheduleShow> getNextShows(JSONObject result, int count) {
        try {
            JSONArray json = result.getJSONArray("schedule");
            ArrayList<ScheduleShow> shows = new ArrayList<>();
            int howMuch  = (count < json.length()) ? count : json.length();
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
        return new ArrayList<>();
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


    private static String SHA1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
