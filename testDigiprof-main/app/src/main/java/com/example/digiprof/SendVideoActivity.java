package com.example.digiprof;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SendVideoActivity extends AppCompatActivity implements View.OnClickListener {
    // ActionBar
    private ActionBar actionBar;
    // UI Views
    private EditText title, REmail;
    private VideoView videoView;
    private Button sendVideoButton;
    private FloatingActionButton pickVideoFab;
    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private String[] cameraPermissions;
    private Uri videoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_video);

        // ActionBar
        actionBar = getSupportActionBar();
        actionBar.setTitle("Send New Video");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // UI Views
        title = findViewById(R.id.titleEt);
        REmail = findViewById(R.id.recipientEmail);
        videoView = findViewById(R.id.videoView);

        sendVideoButton = findViewById(R.id.sendVideoButton);
        sendVideoButton.setOnClickListener(this);

        pickVideoFab = findViewById(R.id.pickVideoFab);
        pickVideoFab.setOnClickListener(this);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    // Actions on click
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sendVideoButton:
                sendVideo();
                break;
            case R.id.pickVideoFab:
                videoPickDialog();
                break;
        }
    }

    private void sendVideo() {
        // Getting title and recipient's email
        String theTitle = title.getText().toString();
        String email = REmail.getText().toString();

        // Check if all the inputs are valid
        if (theTitle.isEmpty()) {
            title.setError("Title is required");
            title.requestFocus();
        }

        if (videoUri == null) {
            Toast.makeText(this, "Pick a video before you can upload", Toast.LENGTH_LONG).show();
        }

        if (email.isEmpty()) {
            title.setError("Please enter email");
            title.requestFocus();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            REmail.setError("Please enter a valid email");
            REmail.requestFocus();
        }

        // file path and name
        final String time = "" + System.currentTimeMillis();
        String filePathAndName = "Videos/" + "video_" + time;

        // Storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        // Send video
        storageReference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Video uploaded successfully, get URL of uploaded video
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadUri = uriTask.getResult();
                if (uriTask.isSuccessful()) {
                    //Url of uploaded video is received

                    //Now add video details to firebase database
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", "" + time);
                    hashMap.put("title", "" + title);
                    hashMap.put("timestamp", "" + time);
                    hashMap.put("videoUrl", "" + downloadUri);
                    hashMap.put("Owner", "" + email);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Videos");
                    reference.child(time)
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // video details added to database
                                    Toast.makeText(SendVideoActivity.this, "Video sent...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed adding details to database
                                    Toast.makeText(SendVideoActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
    private void videoPickDialog() {
        //options to display in dialog
        String [] options = {"Camera", "Gallery"};
        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if(i==0){
                            // if camera is clicked
                            if(!checkCameraPermission()){
                                //if camera permission is not allowed, request it.
                                requestCameraPermission();
                            } else{
                                //Permissiion already allowed, take picture
                                videoPickCamera();
                            }
                        }
                        else if(i==1){
                            // if gallery is clicked
                            videoPickGallery();
                        }
                    }
                })
                .show();
    }


    private void requestCameraPermission(){
        //Request Camera Permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK)== PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }

    private void videoPickGallery(){
        // Pick Video From gallery- intent
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Videos"), VIDEO_PICK_GALLERY_CODE);
    }
    private void videoPickCamera(){
        // Pick Video From Camera- intent
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE);
    }
    private void setVideoToVideoView(){
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //set media controller to video view
        videoView.setMediaController(mediaController);
        //set video uri
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.pause();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    //check permission is allowed or not
                    boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //both Permissions allowed
                        videoPickCamera();
                    }
                    else{
                        //both or one permissions denied
                        Toast.makeText(this, "Camera & Storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Called after picking video from camera/gallery
        if(resultCode==RESULT_OK){
            if(requestCode== VIDEO_PICK_GALLERY_CODE){
                videoUri = data.getData();

                setVideoToVideoView();
            }
            else if (requestCode == VIDEO_PICK_CAMERA_CODE){
                videoUri = data.getData();

                setVideoToVideoView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();// go to previous page or action
        return super.onSupportNavigateUp();
    }
}