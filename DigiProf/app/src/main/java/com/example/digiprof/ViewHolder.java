// VideoActivity Header
// Primary Coder: Hieu
// Modifiers: Andy, Harwinder
// Modifications:
// - Added Comments and Code Style
// - Code Review and Testing
// - users are only able to view videos that they "own"
// - Implemented ViewHolder
package com.example.digiprof;

import android.app.Application;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * ViewHolder Class Displaying videos on the main screen.
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    private static LinearLayout.LayoutParams params;

    private SimpleExoPlayer player;
    private PlayerView playerView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void DisplayVideo(Application app, String name, final String url) {
        TextView textView = itemView.findViewById(R.id.VidTitle);
        playerView = itemView.findViewById(R.id.exoplayerView);

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textView.setText(name);

        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(app).build();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            ExoPlayer exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(app);
            Uri video = Uri.parse(url);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {

        }

    }

    public void Layout_hide() {
        params.height = 0;
        itemView.setLayoutParams(params); //This One.
    }
}

