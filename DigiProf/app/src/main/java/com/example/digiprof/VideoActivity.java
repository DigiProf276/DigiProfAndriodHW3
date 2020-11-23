package com.example.digiprof;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
    // UI Views
    private FloatingActionButton addVideosBtn;

    private View mView;
    private RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dataBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        // change actionbar title.
        setTitle("Videos");

        // Inside it UI views
        addVideosBtn = findViewById(R.id.addVideosBtn);

        // Click Button
        addVideosBtn.setOnClickListener(this);

        recyclerView = findViewById(R.id.video_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance();

        dataBRef = firebaseDatabase.getReference("Videos");


    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseRecyclerOptions<Video> options = new FirebaseRecyclerOptions.Builder<Video>().setQuery(dataBRef, Video.class).build();

        FirebaseRecyclerAdapter<Video, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Video, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Video model) {
                // Show all videos owned by user
                if(model.getOwner().equals(user.getEmail())) {
                    holder.DisplayVideo(getApplication(), model.getTitle(), model.getVideoUrl());
                }
                else{
                    holder.Layout_hide();
                }
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
                return new ViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addVideosBtn:
                OptionDialog(); break;
        }
    }

    private void OptionDialog(){
        // Available options
        String[] options = {"Send", "Upload"};
        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Video options").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==1){
                    startActivity(new Intent(getApplication(), AddVideoActivity.class));
                }
                else{
                    startActivity(new Intent(getApplication(), SendVideoActivity.class));
                }
            }
        }).show();
    }


}