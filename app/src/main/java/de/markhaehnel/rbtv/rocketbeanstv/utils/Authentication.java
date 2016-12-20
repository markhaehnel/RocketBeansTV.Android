package de.markhaehnel.rbtv.rocketbeanstv.utils;

import android.util.Base64;

import com.google.firebase.crash.FirebaseCrash;

import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Authentication {
    public static Map<String, String> getAuthenticationHeaders(String key, String secret) {
        final String id = "00000000-0000-0000-0000-000000000000";
        final String created = DateTime.now().toDateTimeISO().toString();
        final String nonce = id + created + RandomString.generate(10);
        final String sha1 = SHA1(nonce + created + secret);
        final String b64sha1 = Base64.encodeToString(sha1.getBytes(), Base64.NO_WRAP);
        final String b64nonce = Base64.encodeToString(nonce.getBytes(), Base64.NO_WRAP);

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "WSSE profile=\"UsernameToken\"");
        headers.put("X-WSSE", "UsernameToken Username=\"" + key + "\", PasswordDigest=\"" + b64sha1 + "\", Nonce=\"" + b64nonce + "\", Created=\"" + created + "\"");

        return headers;
    }

    private static String SHA1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
        return "";
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte aData : data) {
            int halfbyte = (aData >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = aData & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
