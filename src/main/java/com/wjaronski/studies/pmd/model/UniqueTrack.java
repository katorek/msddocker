package com.wjaronski.studies.pmd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Wojciech Jaronski
 * • unique_tracks.txt — zawiera informacje takie jak identyfikator utworu,
 * identyfikator wykonania, nazwę artysty oraz tytuł utworu,
 */

@Data
@Builder
@AllArgsConstructor
public class UniqueTrack {
    private String songId;
    private String execId;
    private String artist;
    private String songName;
    private AtomicLong listenCounter;

    public UniqueTrack(){
        listenCounter = new AtomicLong(0L);
    }

    public void incrementCounter() {
        listenCounter.incrementAndGet();
    }

    public Long getCounter(){
        return listenCounter.get();
    }

    public String tenMostFormat(){
//      <tytuł piosenki 1> <nazwa wykonawcy piosenki 1> <ilość odsłuchań piosenki 1>
        return songName + " " +artist + " " + getCounter();
    }
}
