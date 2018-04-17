package com.cajetanrodrigues.musicplayerapp;

/**
 * Created by Cajetan Rodrigues on 14-03-2018.
 */

public class songInfo {
    public String songName , songArtist , songUrl ;

    public songInfo() {
    }

    public songInfo(String songName, String songArtist, String songUrl) { // initializing instance variables.
        this.songName = songName;
        this.songArtist = songArtist;
        this.songUrl = songUrl;
    }
    // getters for each instance variables.
    public String getSongName(){

        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongUrl() {
        return songUrl;
    }
}
