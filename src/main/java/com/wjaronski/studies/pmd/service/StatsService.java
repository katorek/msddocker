package com.wjaronski.studies.pmd.service;

import com.google.common.base.Stopwatch;
import com.wjaronski.studies.pmd.Application;
import com.wjaronski.studies.pmd.loaders.TripletsLoader;
import com.wjaronski.studies.pmd.loaders.UniqueTracksLoader;
import com.wjaronski.studies.pmd.model.Artist;
import com.wjaronski.studies.pmd.model.Triplet;
import com.wjaronski.studies.pmd.model.UniqueTrack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Wojciech Jaronski
 */


@Service
public class StatsService {
    private static final String TRIPLET_FILE = "triplets_sample_20p.txt";
    private static final String UNIQUE_FILE = "unique_tracks.txt";

    private ArrayList<UniqueTrack> songsList = new ArrayList<>(50000);
    private HashMap<String, HashSet<UniqueTrack>> uniqueUserSongs = new HashMap<>();

    private ArrayList<Artist> artists = new ArrayList<>();
    private HashMap<String, Long> queenSongs = new HashMap<>();
    private List<Long> monthCount = Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);


    @Value("${debug.info}")
    private boolean showDebiguInfo;

    public StatsService(@Value("${stats.generate}") Boolean getStats){
        if(getStats != null && getStats){
            getStats();
        }
    }

    private void getStats(){
        if(showDebiguInfo) getUsage();
        Stopwatch s = Stopwatch.createStarted();

        loadUniqueTracks(UNIQUE_FILE);
        if(showDebiguInfo)getUsage();
        sortTracksById();

        loadTriplets(TRIPLET_FILE);
        if(showDebiguInfo)getUsage();

        get10MostListenableSongs();
        get5MostUsersWithUniquePlaylist();
        getListenableArtist();
        printDates();
        getQueensFans();
        if(showDebiguInfo) System.out.println(s.stop());
        getUsage();
    }

    private void sortTracksById() {
        Stopwatch s = Stopwatch.createStarted();
        songsList
                .sort(Comparator.comparing(UniqueTrack::getSongId));
        if(showDebiguInfo)System.out.println(s.stop());
    }

    private void getUsage() {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if(showDebiguInfo) System.out.println("Total:\t" + total + "\tFree:\t" + free + "\tUsed:\t" + used);
    }

    private static Comparator<UniqueTrack> getComp() {
        return Comparator.comparing(UniqueTrack::getSongId);
    }

    private UniqueTrack getTrackBySongId(String songId) {


        int idx = Collections.binarySearch(songsList, new UniqueTrack(songId), getComp());

        return songsList.get(idx);
//iso 8859-1

    }

    private void getQueensFans() {
        List<UniqueTrack> top3 = queenSongs.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(e -> getTrackBySongId(e.getKey()))
                .collect(Collectors.toList());

        uniqueUserSongs.entrySet()
                .stream()
                .filter(e -> e.getValue().containsAll(top3))
                .map(Map.Entry::getKey)
                .sorted(String::compareTo)
                .limit(10)
                .forEach(System.out::println);
    }

    private void printDates() {
        for (int i = 0; i < monthCount.size(); i++) {
            System.out.println(i + 1 + " " + monthCount.get(i));
        }
    }

    private void getListenableArtist() {
        artists.stream()
                .max(Comparator.comparing(Artist::getCount))
                .ifPresent(a -> System.out.println(a.getArtist() + " " + a.getCount()));
    }


    private void loadUniqueTracks(String file) {
        if(showDebiguInfo) System.out.println("Loading unique tracks");
        int progressDot = 500000;
        String line = "";
        int c = 0;
        int idx = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(new File(file)), StandardCharsets.ISO_8859_1))) {
            while ((line = br.readLine()) != null) {
                UniqueTrack u = UniqueTracksLoader.parse(line);
                songsList.add(idx++, u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(showDebiguInfo) System.out.println("Loaded unique tracks");
    }

    private void loadTriplets(String file) {
        int progressDot = 500000;
        if(showDebiguInfo)System.out.println("Loading triplets");

        long counter = 0;
        String line = "";
        int c = 0;
        int l = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))))) {

            while ((line = br.readLine()) != null) {
                Triplet tr = TripletsLoader.parse(line);
                increment(tr);
                ++counter;
                addToUserList(tr);
                addArtistsTriplet(tr);
                addToMonthCount(tr);
                addIfQueen(tr);

                if(showDebiguInfo)printProgress(progressDot);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
        if(showDebiguInfo)System.out.println("Loaded triplets");
    }

    int progress = 0;

    private void printProgress(int step) {
        if (progress++ > step) {
            System.out.print(".");
            progress = 0;
        }
    }

    private void addIfQueen(Triplet tr) {
//        UniqueTrack uq = songsMap.get(tr.getSongId());
        UniqueTrack uq = getTrackBySongId(tr.getSongId());

        if (uq.getArtist().equals("Queen")) {
            long l;
            if (queenSongs.containsKey(uq.getSongId())) {
                l = queenSongs.get(uq.getSongId()) + 1;
            } else {
                l = 1;
            }
            queenSongs.put(uq.getSongId(), l);
        }

    }

    private void addToMonthCount(Triplet tr) {
        LocalDateTime date = LocalDateTime.ofEpochSecond(tr.getEpochDate(), 0, ZoneOffset.UTC);

        int month = date.getMonthValue() - 1;
        long count = monthCount.get(month) + 1;
        monthCount.set(month, count);
    }


    private void addToUserList(Triplet tr) {
//        UniqueTrack uq = songsMap.get(tr.getSongId());
        UniqueTrack uq = getTrackBySongId(tr.getSongId());
        if (uniqueUserSongs.containsKey(tr.getUserId())) {
            uniqueUserSongs.get(tr.getUserId()).add(uq);
        } else {
            HashSet<UniqueTrack> set = new HashSet<>();
            set.add(uq);
            uniqueUserSongs.put(tr.getUserId(), set);
        }
    }

    private void get5MostUsersWithUniquePlaylist() {
        uniqueUserSongs.entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .limit(5)
                .forEach(e -> {
                    String user = e.getKey();
                    int size = e.getValue().size();
                    System.out.println(user + " " + size);
                });
    }

    private void get10MostListenableSongs() {
        songsList
                .stream()
                .sorted((f1, f2) -> Long.compare(f2.getCounter(), f1.getCounter()))
                .limit(10)
                .forEach(e -> System.out.println(e.tenMostFormat()));
    }

    private static Comparator<Artist> getCompArtist() {
        return Comparator.comparing(Artist::getArtist);
    }

    private void addArtistsTriplet(Triplet tr) {
//        String artist = songsMap.get(tr.getSongId()).getArtist();
        String artist = getTrackBySongId(tr.getSongId()).getArtist();

        Artist a = new Artist(artist);
        int idx = Collections.binarySearch(artists, a, getCompArtist());

        if(idx < 0){
            //nie ma
            idx++;
            artists.add(Math.abs(idx), a);
        }else{
            artists.get(idx).increment();
        }
    }

    private Long cnt = 0L;

    private void increment(Triplet uq) {
        getTrackBySongId(uq.getSongId()).incrementCounter();
    }
}
