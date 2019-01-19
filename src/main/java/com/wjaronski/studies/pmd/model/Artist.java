package com.wjaronski.studies.pmd.model;

import lombok.Data;

@Data
public class Artist implements Comparable {
    private String artist;
    private Long count;

    @Override
    public int compareTo(Object o) {
        if (o instanceof Artist)
            return artist.compareTo(((Artist) o).artist);
        return -1;
    }

    public Artist(String artist){
        this.artist = artist;
        count = 1L;
    }

    public void increment() {
        count++;
    }
}