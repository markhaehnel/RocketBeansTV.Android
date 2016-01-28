package de.markhaehnel.rbtv.rocketbeanstv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            int keycode = key.getKeyCode();
            if (key.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keycode) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        MainActivity mainActivity = MainActivity.getInstance();
                        mainActivity.togglePlayState();
                        break;
                }
            }
        }
    }
}
