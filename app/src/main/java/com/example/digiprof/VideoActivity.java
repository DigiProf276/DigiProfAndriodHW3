package com.example.digiprof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {
    // UI Views
    FloatingActionButton addVideosBtn;
    private RecyclerView videosRv;
    //ArrayList
    private ArrayList<ModelVideo> videoArrayList;
    //adapter
    private AdapterVideo adapterVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        // change actionbar title.
        setTitle("Videos");

        // Initialize UI views
        addVideosBtn = findViewById(R.id.addVideosBtn);
        videosRv = findViewById(R.id.videosRv);

        // function call, load videos
        loadVideosFromFirebase();

        //Handle Click Button
        addVideosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start activity to add videos.
                startActivity(new Intent(VideoActivity.this,AddVideoActivity.class));
            }
        });

    }

    private void loadVideosFromFirebase() {
        // initialize Array List before adding data into it
        videoArrayList = new ArrayList<>();

        //database reference
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear list before adding data into it
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get Data
                    ModelVideo modelVideo = ds.getValue(ModelVideo.class);
                    // add model/data into list
                    videoArrayList.add(modelVideo);
                }
                // setup adapter
                adapterVideo = new AdapterVideo(VideoActivity.this, videoArrayList);
                //set adapter to recyclerView
                videosRv.setAdapter(adapterVideo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}