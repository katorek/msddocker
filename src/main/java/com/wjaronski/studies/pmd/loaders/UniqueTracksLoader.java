package com.wjaronski.studies.pmd.loaders;

import com.wjaronski.studies.pmd.model.UniqueTrack;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Wojciech Jaronski
 */

public class UniqueTracksLoader {
    private static final String DEFAULT_SEPARATOR = "<SEP>";
    private static String SEPARATOR = DEFAULT_SEPARATOR;
    private static long ERRORS = 0;

    public static UniqueTrack parse(String record) {


        String[] arr = record.split(SEPARATOR);
        if (arr.length < 4) {
            UniqueTrack uq = new UniqueTrack();
            try {
                uq.setExecId(arr[0]);
                uq.setSongId(arr[1]);
                uq.setArtist(arr[2]);
            } catch (Exception e) {
                ERRORS++;
            }
            uq.setListenCounter(new AtomicLong(0));
            return uq;
        } else
            return UniqueTrack.builder()
                    .execId(arr[0])
                    .songId(arr[1])
                    .artist(arr[2])
                    .songName(arr[3])
                    .listenCounter(new AtomicLong(0))
                    .build();
    }

    public static void setSEPARATOR(String sep) {
        SEPARATOR = sep;
    }

    public static String getSEPARATOR() {
        return SEPARATOR;
    }

    public static long getERRORS() {
        return ERRORS;
    }
}