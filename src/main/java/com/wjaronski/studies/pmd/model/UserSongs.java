package com.wjaronski.studies.pmd.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class UserSongs {
    private String user;
    private HashSet<UniqueTrack> songs;
}