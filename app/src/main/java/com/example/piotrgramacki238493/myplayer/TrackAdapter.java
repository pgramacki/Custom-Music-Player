package com.example.piotrgramacki238493.myplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Piotrek on 2018-05-18.
 */

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    private OnListsPlayClickListener listener;
    private ArrayList<Track> tracks;

    public TrackAdapter(Context context) {
        listener = (OnListsPlayClickListener) context;
        tracks = TracksManager.getTracks();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Track track = tracks.get(position);

        holder.title.setText(track.getTitle());
        holder.author.setText(track.getAuthor());
        holder.duration.setText(track.getDuration());

        holder.playButton.setOnClickListener(v -> listener.onListsPlayClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public interface OnListsPlayClickListener {
        void onListsPlayClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout itemView;
        TextView title;
        TextView author;
        TextView duration;
        ImageButton playButton;

        public ViewHolder(ConstraintLayout itemView) {
            super(itemView);
            this.itemView = itemView;
            this.title = itemView.findViewById(R.id.list_title_textView);
            this.author = itemView.findViewById(R.id.list_author_textView);
            this.duration = itemView.findViewById(R.id.list_duration_textView);
            this.playButton = itemView.findViewById(R.id.list_play_button);
        }
    }
}
