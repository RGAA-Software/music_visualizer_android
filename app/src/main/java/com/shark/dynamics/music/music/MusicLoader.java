package com.shark.dynamics.music.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class MusicLoader {

    private static MusicLoader sInstance = new MusicLoader();

    public static MusicLoader getInstance() {
        return sInstance;
    }

    public List<Music> loadMusicData(Context context) {
        List<Music> musics = new ArrayList<Music>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.Media.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Music music = new Music();
                music.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                music.author = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                music.album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                music.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                music.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                music.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (music.duration > 1000 * 10) {
                    musics.add(music);
                }
            }
            cursor.close();
        }
        return musics;
    }

}
