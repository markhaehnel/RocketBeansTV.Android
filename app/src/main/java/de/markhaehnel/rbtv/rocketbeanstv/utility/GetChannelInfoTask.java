package de.markhaehnel.rbtv.rocketbeanstv.utility;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import de.markhaehnel.rbtv.rocketbeanstv.ChannelInfo;
import de.markhaehnel.rbtv.rocketbeanstv.MainActivity;

public class GetChannelInfoTask extends AsyncTask<Void, ChannelInfo, String> {
    protected String doInBackground(Void... voids) {
        while (true) {
            try {
                try {
                    String data = NetworkHelper.getContentFromUrl("https://api.twitch.tv/kraken/streams/rocketbeanstv");

                    if (data != null && !data.isEmpty()) {
                        JSONObject json = new JSONObject(data).getJSONObject("stream");
                        JSONObject channel = json.getJSONObject("channel");
                        publishProgress(new ChannelInfo(channel.getString("status"), json.getString("viewers") + " Zuschauer"));
                    }
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
