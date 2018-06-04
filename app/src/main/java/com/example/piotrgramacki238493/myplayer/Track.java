package com.example.piotrgramacki238493.myplayer;

/**
 * Created by Piotrek on 2018-05-18.
 */

public class Track {
    private String title;
    private String author;
    private int duration; // in seconds
    private int resID;

    public Track(String title, String author, int minutes, int seconds, int resID) {
        this.title = title;
        this.author = author;
        this.resID = resID;
        this.duration = 60 * minutes + seconds;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDuration() {
        int mins = duration / 60;
        int secs = duration % 60;
        if (mins < 10)
            if (secs < 10)
                return "0" + mins + ":0" + secs;
            else return "0" + mins + ":" + secs;
        else if (secs < 10)
            return "" + mins + ":0" + secs;
        else return "" + mins + ":" + secs;
    }

    public int getLength() {
        return duration;
    }

    public int getResID() {
        return resID;
    }

}
