package com.wjaronski.studies.pmd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Created by Wojciech Jaronski
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Triplet {
    private String userId;
    private String songId;
    private Long epochDate;
}

//• triplets_sample_20p.txt — zawiera identyfikator użytkownika, identyfikator
//utworu oraz datę odsłuchania.