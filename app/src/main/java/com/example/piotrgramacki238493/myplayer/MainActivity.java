package com.example.piotrgramacki238493.myplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TrackAdapter.OnListsPlayClickListener, MediaService.OnTrackChangedListener {
    private RecyclerView recyclerView;
    private TrackAdapter adapter;
    private ViewHolder currentSongLayout;

    private Intent playIntent;
    private boolean serviceBound;
    private MediaService service;
    private boolean seekBarTouched = false;
    private boolean paused;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaService.MediaBinder binder = (MediaService.MediaBinder) service;
            MainActivity.this.service = binder.getService();
            serviceBound = true;
            MainActivity.this.service.setOnTrackChangedListener(MainActivity.this);

            if (MainActivity.this.service.getCurrentPosition() != -1)
                currentSongLayout.setData(MainActivity.this.service.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setListeners();
        restore();
        setSeekBarUpdateHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }
        return false;
    }

    private void setSeekBarUpdateHandler() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MediaPlayerObject.getInstance().isPlaying() && serviceBound && !seekBarTouched) {
                    int currentPosition = MediaPlayerObject.getInstance().progress();
                    currentSongLayout.updateCurrent(currentPosition);
                }
                handler.postDelayed(this, 200);
            }
        }, 200);
    }

    private void restore() {
        if (serviceBound) {
            int current = service.getCurrentPosition();
            if (current != -1)
                currentSongLayout.setData(current);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MediaService.class);
            bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused)
            paused = false;
    }

    @Override
    protected void onPause() {
        paused = true;
        super.onPause();
    }

    private void setListeners() {
        currentSongLayout.setListeners();
    }

    private void findViews() {
        recyclerView = findViewById(R.id.main_tracks_list_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TrackAdapter(this);
        recyclerView.setAdapter(adapter);

        currentSongLayout = new ViewHolder();
    }

    private String durationToString(int duration) {
        int minutes = duration / 60;
        int secs = duration % 60;
        if (minutes < 10)
            if (secs < 10)
                return "0" + minutes + ":0" + secs;
            else return "0" + minutes + ":" + secs;
        else
        if (secs < 10)
            return "" + minutes + ":0" + secs;
        else return "" + minutes + ":" + secs;
    }

    @Override
    public void onListsPlayClick(int position) {
        playSong(position);
    }

    private void playSong(int position) {
        service.startPlaying(position);
        currentSongLayout.setData(position);
    }

    @Override
    public void onTrackChanged(int position) {
        currentSongLayout.setData(position);
    }

    private class ViewHolder {
        private ConstraintLayout layout;
        private TextView title;
        private SeekBar progress;
        private TextView currentTime;
        private TextView duration;
        private ImageButton skipBack;
        private ImageButton revBack;
        private ImageButton playPause;
        private ImageButton revForward;
        private ImageButton skipForward;

        private ViewHolder() {
            layout = findViewById(R.id.main_current_song_layout);
            title = findViewById(R.id.main_current_song_title);
            progress = findViewById(R.id.main_current_song_progress_seek_bar);
            currentTime = findViewById(R.id.main_current_song_current_time_TV);
            duration = findViewById(R.id.main_current_song_length_TV);
            skipBack = findViewById(R.id.main_current_song_skip_back_button);
            revBack = findViewById(R.id.main_current_song_rev_back_button);
            playPause = findViewById(R.id.main_current_song_play_pause_button);
            revForward = findViewById(R.id.main_current_song_rev_forward_button);
            skipForward = findViewById(R.id.main_current_song_skip_forward_button);
        }

        private void setData(int position) {
            layout.setVisibility(View.VISIBLE);
            duration.setText(TracksManager.getTracks().get(position).getDuration());
            title.setText(String.format(Locale.getDefault(), "%s - %s",
                    TracksManager.getTracks().get(position).getTitle(),
                    TracksManager.getTracks().get(position).getAuthor()));
            if (service.isPlaying())
                playPause.setImageResource(R.drawable.ic_pause_black_24dp);
            else playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            progress.setMax(0);
            progress.setMax(TracksManager.getTracks().get(position).getLength());
            int currentPosition = MediaPlayerObject.getInstance().progress();
            progress.setProgress(currentPosition);
            currentTime.setText(durationToString(currentPosition));
        }

        private void updateCurrent(int current) {
            currentTime.setText(durationToString(current));
            progress.setProgress(current);
        }

        private void setListeners() {
            playPause.setOnClickListener(v -> handlePlayPausePress());

            skipBack.setOnClickListener(v -> service.previous());

            skipForward.setOnClickListener(v -> service.next());

            progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateCurrent(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    seekBarTouched = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    service.seekTo(seekBar.getProgress());
                    seekBarTouched = false;
                }
            });

            revBack.setOnClickListener(v -> service.revBack());

            revForward.setOnClickListener(v -> service.revForward());
        }

        private void handlePlayPausePress() {
            if (!service.isPlaying()) {
                service.play();
                playPause.setImageResource(R.drawable.ic_pause_black_24dp);
            }
            else {
                service.pause();
                playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        }
    }
}
