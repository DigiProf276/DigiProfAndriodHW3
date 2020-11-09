package com.example.digiprof;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.HolderVideo>{
    //context
    private Context context;
    //arraylist
    private ArrayList<ModelVideo> videoArrayList;

    // constructor
    public AdapterVideo(Context context, ArrayList<ModelVideo>videoArrayList){
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @NonNull
    @Override
    public HolderVideo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout row_video.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_video, parent, false);
        return new HolderVideo(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderVideo holder, int position) {
        // Get Format, set data, handle clicks, etc

        //Get Data
        ModelVideo modelVideo = videoArrayList.get(position);

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
        setVideoUri(modelVideo,holder);


    }

    private void setVideoUri(ModelVideo modelVideo, final HolderVideo holder) {
        // Show Progress
        holder.progressBar.setVisibility(View.VISIBLE);

        //get video url
        String videoUrl = modelVideo.getVideoUrl();

        // Media controller for play, pause, seekbar, timer etc
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(holder.videoView);

        Uri videoUri = Uri.parse(videoUrl);
        holder.videoView.setMediaController(mediaController);
        holder.videoView.setVideoURI(videoUri);

        holder.videoView.requestFocus();
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // video is ready to play
                mediaPlayer.start();
            }
        });
        holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                // to check if buffering, rendering etc
                switch (what){
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:{
                        // rendering started
                        holder.progressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:{
                        //Buffering Started
                        holder.progressBar.setVisibility(View.VISIBLE);
                        return true;

                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:{
                        //Buffering ended
                        holder.progressBar.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        });
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaplayer) {
                mediaplayer.start();//restart video if completed
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size(); // return size of list
    }


    // view holder class, initialize the UI views
    class HolderVideo extends RecyclerView.ViewHolder{

        //UI Views of row_video.xml
        VideoView videoView;
        TextView titleTv, timeTv;
        ProgressBar progressBar;

        public HolderVideo(@NonNull View itemView) {
            super(itemView);

            //initialize UI Views of row_video.xml
            videoView = itemView.findViewById(R.id.videoView);
            titleTv = itemView.findViewById(R.id.titleTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            progressBar = itemView.findViewById(R.id.progressBar);

        }

    }
}