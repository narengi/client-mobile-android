package com.byagowi.persiancalendar;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "AlarmReceiver";
    private static MediaPlayer mediaPlayer;
    private static NotificationManager notificationManager;
    private Utils utils = Utils.getInstance();

    public static final int NOTIFICATION_ID = 0;
    public static final String ACTION_STOP_ALARM = "com.byagowi.persiancalendar.stop_athan";
    public static final String KEY_EXTRA_PRAYER_KEY = "prayer_name";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received an alarm trigger. playing sound file.");
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent athanViewIntent = null;
        athanViewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        athanViewIntent.putExtra(KEY_EXTRA_PRAYER_KEY, intent.getStringExtra(KEY_EXTRA_PRAYER_KEY));
        context.startActivity(athanViewIntent);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
        mp = null;

        notificationManager.cancel(NOTIFICATION_ID);
    }


    public static class StopAthanPlaybackReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received request to stop alarm");
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }
    }
}
