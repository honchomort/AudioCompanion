package com.gads.audiocompanion;

public class Recording {

    public String recDescription;
    public String recUrl;
    public long recDuration;

    public Recording(String recDescription, String recUrl, long recDuration) {
        this.recDescription = recDescription;
        this.recUrl = recUrl;
        this.recDuration = recDuration;
    }
}
