package com.example.piotrgramacki238493.myplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;

/**
 * Created by Piotrek on 2018-05-28.
 */

public class MediaPlayerObject {
    private static final MediaPlayerObject ourInstance = new MediaPlayerObject();

    public static MediaPlayerObject getInstance() {
        return ourInstance;
    }

    private MediaPlayer mediaPlayer;
    private OnTrackFinishedListener onTrackFinishedListener;

    public interface OnTrackFinishedListener {
        void onTrackFinished();
    }

    public void setOnTrackFinishedListener(OnTrackFinishedListener listener) {
        onTrackFinishedListener = listener;
    }

    private MediaPlayerObject() {
        mediaPlayer = new MediaPlayer();
    }

    public void setSong(Context context, int resID) {
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(context, resID);
        mediaPlayer.setOnCompletionListener(v -> onTrackFinishedListener.onTrackFinished());
    }

    public void startPlaying() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int progress() {
        return mediaPlayer.getCurrentPosition() / 1000;
    }

    public void reset() {
        mediaPlayer.seekTo(0);
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(1000 * position);
    }

    public int duration() {
        return mediaPlayer.getDuration() / 1000;
    }
}
