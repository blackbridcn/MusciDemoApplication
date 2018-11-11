package com.music.utils;

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
}
