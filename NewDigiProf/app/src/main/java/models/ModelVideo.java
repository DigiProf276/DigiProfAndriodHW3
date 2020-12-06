package models;

public class ModelVideo {
    String title, videoUrl, timestamp, id, Owner,sender;
    boolean expanded;

    public ModelVideo(){}

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

    public String getSender(){return sender;}

    public void setSender(String sender){this.sender = sender;}

    public boolean isExpanded(){return expanded;};

    public void setExpanded(boolean expanded){this.expanded = expanded;};
}
