// ModelVideo Header
// Group 13: DigiProf
// Primary Coder: Harwinder
// Modifiers: Andy
// Modifications:
// - Added Comments and Code Style
// - Code Review and Testing
// - Implemented MainActivity

package com.example.digiprof;


/**
 * ModelVideo Class represents the video and its related information passed through from Firebase into the app.
 * It allows the app to manage and display information about the video.
 */
public class ModelVideo {
    String id, title, timestamp, videoUrl;

    //constructor
    public ModelVideo() {
        // firebase requires empty constructor
    }

    public ModelVideo(String id, String title, String timestamp, String videoUrl) {
        this.id = id;
        this.title = title;
        this.timestamp = timestamp;
        this.videoUrl = videoUrl;
    }

    // Getters and Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}

