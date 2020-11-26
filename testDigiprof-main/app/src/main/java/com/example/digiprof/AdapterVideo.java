// AdapterVideo Header
// Group 13: DigiProf
// Primary Coder: Harwinder
// Modifiers: Andy
// Modifications:
// - Added Comments and Code Style
// - Code Review and Testing
// - Implemented AdapterVideo

package com.example.digiprof;

import android.app.AlertDialog;
import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * The AdapterVideo class shows all the videos that the user has uploaded, and handles video errors, playing the video,
 * showcasing video components like how long the video has gone on for, etc.
 */
public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.HolderVideo> {
    //context
    private Context context;
    //arraylist
    private ArrayList<ModelVideo> videoArrayList;
    //application
    private Application app;


    private static LinearLayout.LayoutParams params;

    // constructor
    public AdapterVideo(Context context, ArrayList<ModelVideo> videoArrayList, Application app) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        this.app = app;
    }

    // shows the videos that the user uploaded
    @NonNull
    @Override
    public HolderVideo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout row_video.xml

        View view = LayoutInflater.from(context).inflate(R.layout.row_video, parent, false);
        return new HolderVideo(view);

    }

    // Shows the videos metadata - day, time of recording
    @Override
    public void onBindViewHolder(@NonNull HolderVideo holder, int position) {
        // Get Format, set data, handle clicks, etc

        //Get Data
        ModelVideo modelVideo = videoArrayList.get(position);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(modelVideo.getOwner()!=null) {
            if(modelVideo.getOwner().equals(user.getEmail())) {
                String id = modelVideo.getId();
                String title = modelVideo.getTitle();
                String timestamp = modelVideo.getTimestamp();
                String videoUrl = modelVideo.getVideoUrl();

                //format timestamp eg. 07/09/2020 02:31PM
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(timestamp));
                String formattedDateTime = DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString();

                //set Date
                holder.titleTv.setText(title);
                holder.timeTv.setText(formattedDateTime);
                setVideoUri(modelVideo, holder);


                // Handle Click, Download Video
                holder.downloadFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadVideo(modelVideo);
                    }
                });

                // Handle Click, Delete Video
                holder.deleteFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Show Alert message to user and confirm delete
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete")
                                .setMessage("Are you sure you want to delete video:" + title)
                                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Confirmed to delete video
                                        deleteVideo(modelVideo);
                                    }
                                })
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Not Confirmed to Delete video, go back and do not delete video

                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();


                    }
                });
            }
            else{
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
        }

    }


    // shows how much of the video you have watched, and its
    private void setVideoUri(ModelVideo modelVideo, final HolderVideo holder) {
        // Show Progress

        //get video url
        String videoUrl = modelVideo.getVideoUrl();

        // Media controller for play, pause, seekbar, timer etc
//        MediaController mediaController = new MediaController(context);
//        mediaController.setAnchorView(holder.videoView);

        ExoPlayer exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(app);
        Uri videoUri = Uri.parse(videoUrl);
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("videoUri");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
        holder.videoView.setPlayer(exoPlayer);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(false);
//        holder.videoView.setMediaController(mediaController);
//        holder.videoView.setVideoURI(videoUri);


        holder.videoView.requestFocus();
//        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                // video is ready to play
//                mediaPlayer.start();
//            }
//        });
//        holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                // to check if buffering, rendering etc
//                switch (what) {
//                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
//                        // rendering started
//                        holder.progressBar.setVisibility(View.VISIBLE);
//                        return true;
//                    }
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
//                        // Buffering Started
//                        holder.progressBar.setVisibility(View.VISIBLE);
//                        return true;
//                    }
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
//                        //Buffering ended
//                        holder.progressBar.setVisibility(View.GONE);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaplayer) {
//                mediaplayer.start();//restart video if completed
//            }
//        });
    }

    private void deleteVideo(ModelVideo modelVideo) {
        String videoId = modelVideo.getId();
        String videoUrl = modelVideo.getVideoUrl();

        //1) Delete From firebase storage
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        reference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Deleted From Firebase storage
                        //2) Delete from Firebase database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Videos");
                        databaseReference.child(videoId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Deleted from Firebase Database
                                        Toast.makeText(context, "Video Deleted Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to Delete from Firebase Database, display error message
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to Delete From Firebase storage, display error message
                        Toast.makeText(context, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downloadVideo(ModelVideo modelVideo) {
        String videoUrl = modelVideo.getVideoUrl();// Video URL Used to download video

        // get video reference using video url
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        storageReference.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        // get basic video info (title, type of video)
                        String fileName = storageMetadata.getName(); // filename in Firebase storage
                        String fileType = storageMetadata.getContentType();// file type in firebase Storage eg. video/ mp4
                        String fileDirectory = Environment.DIRECTORY_DOWNLOADS;// Video will be saved in this folder ("Downloads")

                        // initialize DownloadManager
                        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);

                        // get uri of file to be downloaded
                        Uri uri = Uri.parse(videoUrl);

                        // create download request, new request for each download -we can download multiple files at once
                        DownloadManager.Request request = new DownloadManager.Request(uri);

                        //notification visibility
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        // set destination path
                        request.setDestinationInExternalPublicDir(""+ fileDirectory, ""+ fileName+".mp4");

                        // add request to queue- can be multiple requests so add it to queue
                        downloadManager.enqueue(request);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to get info
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size(); // return size of list
    }


    // view holder class, initialize the UI views
    class HolderVideo extends RecyclerView.ViewHolder {

        //UI Views of row_video.xml

        PlayerView videoView;
        TextView titleTv, timeTv;
        FloatingActionButton deleteFab, downloadFab;

        public HolderVideo(@NonNull View itemView) {
            super(itemView);

            //initialize UI Views of row_video.xml
            videoView = itemView.findViewById(R.id.videoView);
            titleTv = itemView.findViewById(R.id.titleTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            deleteFab = itemView.findViewById(R.id.deleteFab);
            downloadFab = itemView.findViewById(R.id.downloadFab);

        }
    }


}