package com.wjaronski.studies.pmd;

import com.wjaronski.studies.pmd.loaders.TripletsLoader;
import com.wjaronski.studies.pmd.loaders.UniqueTracksLoader;
import com.wjaronski.studies.pmd.model.Triplet;
import com.wjaronski.studies.pmd.model.UniqueTrack;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Wojciech Jaronski
 */

public class Application {
    private static final String TRIPLET_FILE = "triplets_sample_20p.txt";
    private static final String UNIQUE_FILE = "unique_tracks.txt";

    private HashMap<String, UniqueTrack> songsMap = new HashMap<>(10000000);
    private HashMap<String, HashSet<UniqueTrack>> uniqueUserSongs = new HashMap<>();
    private HashMap<String, Long> mostListenableArtist = new HashMap<>();
    private HashMap<String, Long> queenSongs = new HashMap<>();
    private List<Long> monthCount = Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);

    public static void main(String[] args) {
        Application app = new Application();

        app.loadUniqueTracks(UNIQUE_FILE);
        app.loadTriplets(TRIPLET_FILE);
        app.get10MostListenableSongs();
        app.get5MostUsersWithUniquePlaylist();
        app.getListenableArtist();
        app.printDates();
        app.getQueensFans();

//        System.out.println(stopwatch.stop());

    }

    private void getQueensFans() {
        List<UniqueTrack> top3 = queenSongs.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(e -> songsMap.get(e.getKey()))
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
//        System.out.println(minDate.getYear()+"-"+minDate.getMonthValue()+"-"+minDate.getDayOfMonth());
//        System.out.println(maxDate.getYear()+"-"+maxDate.getMonthValue()+"-"+maxDate.getDayOfMonth());
    }

    private void getListenableArtist() {
//        Collections.max(mostListenableArtist, )
        mostListenableArtist
                .entrySet()
                .stream()
                .max((r1, r2) -> Long.compare(r1.getValue(), r2.getValue()))
                .ifPresent(e -> System.out.println(e.getKey() + " " + e.getValue()));
    }


    private void loadUniqueTracks(String file) {
        System.out.println("Loading unique tracks");
        int progressDot = 500000;
        String line = "";
        int c = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))))) {
            while ((line = br.readLine()) != null) {
                UniqueTrack u = UniqueTracksLoader.parse(line);
                if (!songsMap.containsKey(u.getSongId())) {
                    songsMap.put(u.getSongId(), u);
                }
                /*++c;
                if (c > progressDot) {
                    System.out.print(".");
                    c = 0;
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*System.out.println("\nErrors: " + UniqueTracksLoader.getERRORS());
        System.out.println("Items: " + songsMap.size());
        System.out.println("Loaded unique tracks");*/
        System.out.println("Loaded unique tracks");

    }

    private void loadTriplets(String file) {
        int progressDot = 500000;
        System.out.println("Loading triplets");

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
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
        System.out.println("Loaded triplets");
        /*System.out.println("\nItems: " + counter);
        System.err.println("Not incremented " + cnt + " times.");*/

    }

    private void addIfQueen(Triplet tr) {
        UniqueTrack uq = songsMap.get(tr.getSongId());
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
        LocalDateTime date = LocalDateTime.ofEpochSecond(tr.getEpochDate(),0, ZoneOffset.UTC);

        /*minDate = (date.isBefore(minDate)) ? date : minDate;
        maxDate = (date.isAfter(maxDate)) ? date : maxDate;*/

        int month = date.getMonthValue() - 1;
        long count = monthCount.get(month) + 1;
        monthCount.set(month, count);
    }


    private void addToUserList(Triplet tr) {
        UniqueTrack uq = songsMap.get(tr.getSongId());
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
        songsMap
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .sorted((f1, f2) -> Long.compare(f2.getCounter(), f1.getCounter()))
                .limit(10)
                .forEach(e -> System.out.println(e.tenMostFormat()));
    }

    private void addArtistsTriplet(Triplet tr) {
        String artist = songsMap.get(tr.getSongId()).getArtist();
        long l;
        if (mostListenableArtist.containsKey(artist)) {
            l = mostListenableArtist.get(artist) + 1;
        } else {
            l = 1;
        }
        mostListenableArtist.put(artist, l);
    }

    private Long cnt = 0L;

    private void increment(Triplet uq) {
        if (!songsMap.containsKey(uq.getSongId())) {
            cnt++;
        } else {
            songsMap.get(uq.getSongId()).incrementCounter();
        }
    }
}
