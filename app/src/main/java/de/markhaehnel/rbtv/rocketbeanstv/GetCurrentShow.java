package de.markhaehnel.rbtv.rocketbeanstv;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

public class GetCurrentShow extends AsyncTask<Void, String, String> {
    protected String doInBackground(Void... voids) {
        while (true) {
            try {
                try {
                    String response = HttpRequest.get("http://api.twitch.tv/api/channels/rocketbeanstv").body();
                    JSONObject json = new JSONObject(response);
                    publishProgress(json.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Thread.sleep(15000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onProgressUpdate(String... strings) {
        String currentShow = strings[0];
        if (currentShow.length() != 0) {
            MainActivity.getInstance().setCurrentShow(currentShow);
        }
    }
}
