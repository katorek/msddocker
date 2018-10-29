package com.wjaronski.studies.pmd.loaders;

import com.wjaronski.studies.pmd.model.Triplet;

/**
 * Created by Wojciech Jaronski
 */

public class TripletsLoader {
    private static final String DEFAULT_SEPARATOR = "<SEP>";
    private static String SEPARATOR = DEFAULT_SEPARATOR;

    public static Triplet parse(String record) {
        String[] arr = record.split(SEPARATOR);
        return Triplet.builder()
                .userId(arr[0])
                .songId(arr[1])
                .epochDate(Long.parseLong(arr[2]))
                .build();
    }

    public static void setSEPARATOR(String sep) {
        SEPARATOR = sep;
    }

    public static String getSEPARATOR() {
        return SEPARATOR;
    }

}
