package de.markhaehnel.rbtv.rocketbeanstv;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class GetAccesToken extends AsyncTask<Void, Void, JSONObject> {
    protected JSONObject doInBackground(Void... voids) {

        try {
            String response = HttpRequest.get("http://api.twitch.tv/api/channels/rocketbeanstv/access_token").body();
            JSONObject json = new JSONObject(response);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }

    }

    protected void onPostExecute(Long result) {

    }
}
