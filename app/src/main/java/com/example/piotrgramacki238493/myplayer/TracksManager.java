package com.example.piotrgramacki238493.myplayer;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Piotrek on 2018-05-18.
 */

public class TracksManager {
    private static ArrayList<Track> tracks = new ArrayList<Track>() {{
        add(new Track("Livin' On A Prayer", "Bon Jovi", 4, 10, R.raw.bon_jovi_livin_on_a_prayer));
        add(new Track("The Pretender", "Foo Fighters", 4, 29, R.raw.foo_fighters_the_pretender));
        add(new Track("Reach Out", "George Duke", 6, 56, R.raw.george_duke_reach_out));
        add(new Track("Paradise City", "Guns n' Roses", 6, 44, R.raw.guns_n_roses_paradise_city));
        add(new Track("Get Down On It", "Kool and The Gang", 4, 51, R.raw.kool_and_the_gang_get_down_on_it));
        add(new Track("Hollywood Hills", "Sunrise Avenue", 3, 30, R.raw.sunrise_avenue_hollywood_hills));
        add(new Track("Keep On Lovin' me", "The Whispers", 5, 49, R.raw.the_whispers_keep_on_lovin_me));
        add(new Track("Walk On Water", "30 seconds to Mars", 3, 20, R.raw.thirty_seconds_to_mars_walk_on_water));
        add(new Track("Hurricane", "30 seconds to Mars", 6, 20, R.raw.thirty_seconds_to_mars_hurricane));
        add(new Track("Lola Montez", "Volbeat", 4, 27, R.raw.volbeat_lola_montez));
        sort(Comparator.comparing(Track::getTitle));
    }};

    public static ArrayList<Track> getTracks() {
        return tracks;
    }
}
