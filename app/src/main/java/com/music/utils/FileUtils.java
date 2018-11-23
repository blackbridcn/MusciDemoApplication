package com.music.utils;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.IOException;

public class FileUtils {

    public static void closeStream(Closeable... closeables) {
        for (Closeable mCloseable : closeables) {
            try {
                mCloseable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String getArtistAndAlbum(String artist, String album) {
        if (TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return "";
        } else if (!TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return artist;
        } else if (TextUtils.isEmpty(artist) && !TextUtils.isEmpty(album)) {
            return album;
        } else {
            return artist + " - " + album;
        }
    }
}
