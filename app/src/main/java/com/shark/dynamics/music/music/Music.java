package com.shark.dynamics.music.music;

import java.util.Objects;

public class Music {

    String title;
    String author;
    String album;
    String path;
    int duration;
    long size;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return Objects.equals(title, music.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
