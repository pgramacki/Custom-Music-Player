package com.example.piotrgramacki238493.myplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;


/**
 * Created by Piotrek on 2018-05-24.
 */

public class MediaService extends Service implements MediaPlayerObject.OnTrackFinishedListener {
    private static final int CHANNEL_ID = 72;
    private static final int PLAY = 123;
    private static final int NEXT = 124;
    private static final int PREV = 125;
    private static final int DEL = 126;
    private static final String COMMAND = "cmd";
    private final IBinder binder = new MediaBinder();
    private MediaPlayerObject player = MediaPlayerObject.getInstance();
    private int currentPosition = -1;
    private boolean isPlaying = false;
    private OnTrackChangedListener onTrackChangedListener;
    {
        player.setOnTrackFinishedListener(this);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int command = intent.getIntExtra(COMMAND, -1);

            switch (command) {
                case PLAY:
                    if (isPlaying)
                        pause();
                    else play();
                    break;
                case NEXT:
                    next();
                    break;
                case PREV:
                    previous();
                    break;
                case DEL:
                    stopSelf();
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private Notification createNotification() {
        ArrayList<PendingIntent> controlsIntents = createControlsIntents();
        PendingIntent activityIntent = createActivityIntent();
        PendingIntent deleteIntent = createDeleteIntent();

        int playPauseIcon;
        String playPauseTitle;
        if (isPlaying) {
            playPauseIcon = R.drawable.ic_pause_black_24dp;
            playPauseTitle = getString(R.string.pause);
        }
        else {
            playPauseIcon = R.drawable.ic_play_arrow_black_24dp;
            playPauseTitle = getString(R.string.play);
        }

        String trackTitle = "";
        String trackAuthor = "";

        if (currentPosition != -1) {
            trackTitle = TracksManager.getTracks().get(currentPosition).getTitle();
            trackAuthor = TracksManager.getTracks().get(currentPosition).getAuthor();
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "def");
        builder
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_album_black_24dp)
                .addAction(R.drawable.ic_skip_previous_black_24dp, getString(R.string.skip_prev), controlsIntents.get(2))
                .addAction(playPauseIcon, playPauseTitle, controlsIntents.get(1))
                .addAction(R.drawable.ic_skip_next_black_24dp, getString(R.string.skip_next), controlsIntents.get(0))
                .setContentTitle(trackTitle)
                .setContentText(trackAuthor)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(activityIntent)
                .setDeleteIntent(deleteIntent);

        return builder.build();
    }

    private PendingIntent createDeleteIntent() {
        Intent deleteIntent = new Intent(this, MediaService.class);
        deleteIntent.putExtra(COMMAND, DEL);
        return PendingIntent.getService(this, 7, deleteIntent, 0);
    }

    private ArrayList<PendingIntent> createControlsIntents() {
        ArrayList<PendingIntent> intents = new ArrayList<>();

        Intent nextIntent = new Intent(this, MediaService.class);
        nextIntent.putExtra(COMMAND, NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 8, nextIntent, 0);
        intents.add(nextPendingIntent);

        Intent playIntent = new Intent(this, MediaService.class);
        playIntent.putExtra(COMMAND, PLAY);
        PendingIntent playPendingIntent = PendingIntent.getService(this, 9, playIntent, 0);
        intents.add(playPendingIntent);

        Intent prevIntent = new Intent(this, MediaService.class);
        prevIntent.putExtra(COMMAND, PREV);
        PendingIntent prevPendingIntent = PendingIntent.getService(this, 10, prevIntent, 0);
        intents.add(prevPendingIntent);

        return intents;
    }

    private PendingIntent createActivityIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, 11, intent, 0);
    }

    @Override
    public void onTrackFinished() {
        if (Settings.playNext)
            next();
    }

    public class MediaBinder extends Binder {
        MediaService getService() {
            return MediaService.this;
        }
    }

    public interface OnTrackChangedListener {
        void onTrackChanged(int position);
    }

    public void setOnTrackChangedListener(OnTrackChangedListener listener) {
        onTrackChangedListener = listener;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void startPlaying(int position) {
        isPlaying = true;
        currentPosition = position;
        player.setSong(getApplicationContext(), resID(position));
        player.startPlaying();
        startForeground(CHANNEL_ID, createNotification());
    }

    public void play() {
        isPlaying = true;
        player.startPlaying();
        onTrackChangedListener.onTrackChanged(currentPosition);
        startForeground(CHANNEL_ID, createNotification());
    }

    public void pause() {
        isPlaying = false;
        player.pause();
        onTrackChangedListener.onTrackChanged(currentPosition);
        startForeground(CHANNEL_ID, createNotification());
        stopForeground(false);
    }

    public void next() {
        player.pause();
        if (Settings.looping)
            currentPosition = (currentPosition + 1) % TracksManager.getTracks().size();
        else currentPosition++;

        if (currentPosition < TracksManager.getTracks().size()) {
            player.setSong(getApplicationContext(), resID(currentPosition));
            onTrackChangedListener.onTrackChanged(currentPosition);
            if (isPlaying) {
                player.startPlaying();
                startForeground(CHANNEL_ID, createNotification());
            }
        } else currentPosition--;

    }

    public void previous() {
        if (player.progress() > 2) {
            player.reset();
            player.startPlaying();
        } else {
            if (currentPosition == 0)
                if (Settings.looping)
                    currentPosition = TracksManager.getTracks().size() - 1;
                else {
                    player.reset();
                }
            else currentPosition--;

            player.setSong(getApplicationContext(), resID(currentPosition));
            onTrackChangedListener.onTrackChanged(currentPosition);
            if (isPlaying) {
                player.startPlaying();
                startForeground(CHANNEL_ID, createNotification());
            }
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    private int resID(int position) {
        return TracksManager.getTracks().get(position).getResID();
    }

    public void seekTo(int position) {
        player.seekTo(position);
    }

    public void revForward() {
        int current = player.progress();
        if (current + 10 < player.duration())
            player.seekTo(current + 10);
        else player.seekTo(player.duration());
    }

    public void revBack() {
        int current = player.progress();
        if (current - 10 >= 0)
            player.seekTo(current - 10);
        else player.seekTo(0);
    }
}
