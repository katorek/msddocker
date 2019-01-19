package com.wjaronski.studies.pmd.service;

import com.google.common.base.Stopwatch;
import com.wjaronski.studies.pmd.loaders.UniqueTracksLoader;
import com.wjaronski.studies.pmd.model.UniqueTrack;
import com.wjaronski.studies.pmd.repositories.UniqueTrackRepository;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by Wojciech Jaronski
 */

//@Component
public class UniqueTracksInitializer {
    private static final String FILE_WITH_DATA = "unique_tracks.txt";


    public UniqueTracksInitializer(UniqueTrackRepository repository){
        System.out.println("Loading tracks");

        assert new File(FILE_WITH_DATA).exists();
        String line;
        Stopwatch s = Stopwatch.createStarted();
        try (BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(new File(FILE_WITH_DATA)), StandardCharsets.ISO_8859_1))) {
            int buffer = 50000;
            ArrayList<UniqueTrack> tracks = new ArrayList<>(buffer + 2);
            int cnt = 0;
            while ((line = br.readLine()) != null) {
                if(cnt++ < buffer){
                    tracks.add(UniqueTracksLoader.parse(line));
                }else{
                    System.out.print(".");
                    repository.saveAll(tracks);
                    cnt = 0;
                    tracks.clear();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Loaded tracks: "+ s.stop());
    }
}
