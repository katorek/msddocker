package com.wjaronski.studies.pmd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Wojciech Jaronski
 * • unique_tracks.txt — zawiera informacje takie jak identyfikator utworu,
 * identyfikator wykonania, nazwę artysty oraz tytuł utworu,
 */

@Data
@Builder
@AllArgsConstructor
@Entity
public class UniqueTrack {
    @Id
    private String songId;
    @Column(length = 100)
    private String execId;
    @Column(length = 500)
    private String artist;
    @Column(length = 500)
    private String songName;
    @Column
    private AtomicLong listenCounter;

    public UniqueTrack(){
        listenCounter = new AtomicLong(0L);
    }

    public UniqueTrack(String songId) {
        this.songId = songId;
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
