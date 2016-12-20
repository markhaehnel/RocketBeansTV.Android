package de.markhaehnel.rbtv.rocketbeanstv.utils;

import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static de.markhaehnel.rbtv.rocketbeanstv.utils.AuthHelper.SHA1;

public class Authentication {
    public static Map<String, String> getAuthenticationHeaders(String key, String secret) {
        final String id = "00000000-0000-0000-0000-000000000000";
        String created = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").format(new Date());
        created = created.substring(0, created.length()-2) + ":" + created.substring(created.length()-2, created.length());

        String nonce = id + created + RandomString.generate(10);
        String sha1 = SHA1(nonce + created + secret);
        String b64sha1 = Base64.encodeToString(sha1.getBytes(), Base64.NO_WRAP);
        String b64nonce = Base64.encodeToString(nonce.getBytes(), Base64.NO_WRAP);

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "WSSE profile=\"UsernameToken\"");
        headers.put("X-WSSE", "UsernameToken Username=\"" + key + "\", PasswordDigest=\"" + b64sha1 + "\", Nonce=\"" + b64nonce + "\", Created=\"" + created + "\"");

        return headers;
    }
}
