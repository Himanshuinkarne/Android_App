package com.svc.sml.Model;

import java.io.Serializable;

/**
 * Created by himanshu on 3/25/16.
 */
public class VideoFeed implements Serializable {
    private String thumbnailKey;
    private int pointsToUnlock;
    private String title;
    private String videoLink;

    public String getThumbnailKey() {
        return thumbnailKey;
    }

    public void setThumbnailKey(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public int getPointsToUnlock() {
        return pointsToUnlock;
    }

    public void setPointsToUnlock(int pointsToUnlock) {
        this.pointsToUnlock = pointsToUnlock;
    }
}
