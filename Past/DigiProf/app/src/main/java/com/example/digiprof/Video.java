// User Header
// Group 13: DigiProf
// Main Coder: Hieu
// Modifiers: Andy
// Modifications:
// - Added Comments and Code Style
// - Code Review and Testing
// - Implemented Video
package com.example.digiprof;


/**
 * Video Class stores a videos information.
 */
public class Video {
    String title, videoUrl, timestamp, id, Owner;

    public Video() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }
}
