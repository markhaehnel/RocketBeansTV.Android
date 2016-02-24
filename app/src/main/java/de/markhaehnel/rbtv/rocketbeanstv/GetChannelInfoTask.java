package de.markhaehnel.rbtv.rocketbeanstv;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

public class GetChannelInfoTask extends AsyncTask<Void, ChannelInfo, String> {
    protected String doInBackground(Void... voids) {
        while (true) {
            try {
                try {
                    String response = HttpRequest.get("https://api.twitch.tv/kraken/streams/rocketbeanstv").body();
                    JSONObject json = new JSONObject(response).getJSONObject("stream");
                    JSONObject channel = json.getJSONObject("channel");
                    publishProgress(new ChannelInfo(channel.getString("status"), json.getString("viewers") + " Zuschauer"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Thread.sleep(15000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onProgressUpdate(ChannelInfo... infos) {
        ChannelInfo info = infos[0];
        if (info.currentShow.length() != 0) {
            MainActivity.getInstance().setInfoOverlay(info);
        }
    }
}
