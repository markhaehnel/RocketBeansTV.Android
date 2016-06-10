package de.markhaehnel.rbtv.rocketbeanstv.utility;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import de.markhaehnel.rbtv.rocketbeanstv.MainActivity;
import de.markhaehnel.rbtv.rocketbeanstv.R;
import de.markhaehnel.rbtv.rocketbeanstv.utility.Enums.Quality;

public class PlayStreamTask extends AsyncTask<Quality, Void, String> {
    protected String doInBackground(Quality... qualities) {

        String qualityString = "chunked";

        switch (qualities[0]) {
            case Chunked: qualityString = "chunked"; break;
            case High: qualityString = "high"; break;
            case Medium: qualityString = "medium"; break;
            case Low: qualityString = "low"; break;
            case Mobile: qualityString = "mobile"; break;
        }

        String streamUrl = null;

        try {
            String data = NetworkHelper.getContentFromUrl("http://api.twitch.tv/api/channels/rocketbeanstv/access_token");

            JSONObject json = new JSONObject(data);

            if (json != null && json.length() != 0) {
                String token = json.getString("token");
                String sig = json.getString("sig");

                if (token.length() != 0 && sig.length() != 0) {

                    String playlistUrl = "http://usher.twitch.tv/api/channel/hls/rocketbeanstv.m3u8?player=twitchweb&token=" + token + "&sig=" + sig + "&allow_audio_only=true&allow_source=true&type=any&p=" + Math.round(Math.random() * 10000);
                    InputStream is = new URL(playlistUrl).openStream();

                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(is));
                        if (is != null) {
                            String line = "";
                            while ((line = in.readLine()) != null) {
                                 if (line.contains("VIDEO=\"" + qualityString + "\"")) {
                                    return in.readLine();
                                }
                            }
                        }
                    } finally {
                        is.close();
                    }


                } else {
                    MainActivity.getInstance().showMessage(R.string.error_twitchError);
                }
            } else {
                MainActivity.getInstance().showMessage(R.string.error_twitchError);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return streamUrl;
    }

    protected void onPostExecute(String streamUrl) {
        MainActivity.getInstance().playURL(streamUrl);
    }
}
