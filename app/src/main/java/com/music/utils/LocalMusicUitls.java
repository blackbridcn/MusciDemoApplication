package com.music.utils;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.music.AppContant.AppContant;
import com.music.javabean.MusicData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalMusicUitls {
    private Cursor cur;

    private LocalMusicUitls() {

    }

    private static class StaticInnnerClass {
        private static final LocalMusicUitls Instance = new LocalMusicUitls();
    }

    public static LocalMusicUitls getInstance() {
        return StaticInnnerClass.Instance;
    }

    /**
     * 根据路径遍历文件夹查找歌词
     *
     * @param  musicath  歌曲所在文件夹路径
     * @param type 歌词文件夹
     * @return 歌词文件夹path ，如果没有找到歌词return null；
     */
    private String getLrcPath(String musicath, String type) {
        File file = new File(musicath);
        if (file.exists()) {
            if (file.isDirectory()) {
                String lrcPath = null;
                File[] files = file.listFiles();
                for (File mFile : files) {
                    if (mFile.isDirectory()) {
                        if (mFile.getPath().contains("lrc")) //只遍历文件夹名中有lrc的子文件夹
                            return getLrcPath(mFile.getPath(), type);//递归遍历
                    } else {
                        lrcPath = mFile.getPath();
                        if (lrcPath.endsWith(type))
                            return lrcPath;
                    }
                }
            }
        }
        return null;
    }

    public void getMusicList(Activity mActivity) {
        if (cur != null) {
            cur.close();
            cur = null;
        }
        // MediaStore.Audio.Media.DISPLAY_NAME  歌曲名完整包括.MP3  etc 新韵传音 - 心经 (梵唱印度版).mp3
        // MediaStore.Audio.Media.TITLE,    歌曲名 etc 新韵传音 - 心经 (梵唱印度版)
        //MediaStore.Audio.Media.DATA歌曲资源路径
        ///data/hw_init/product/media/Pre-loaded/Music/Dream_It_Possible.flac
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.AudioColumns.ALBUM_ID
        };
        cur = mActivity.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection
                        , null, null, null);
        if (cur == null) {
            Toast.makeText(mActivity.getApplicationContext(), "链表为空", Toast.LENGTH_LONG).show();
            return;
        }
        int index = 1;
        while (cur.moveToNext()) {
            MusicData song = new MusicData();
            song.setMusicID(index);
            String filename = cur.getString(1);
            song.setFileName(filename);
            song.setMusicName(cur.getString(2));
            String filePath = cur.getString(9);

            long albumId = cur.getLong(10);
            song.setAlbumId(albumId);
            if (filePath != null) {
                song.setDataFilePath(filePath);
                File file = new File(filePath);
                String fileParent = file.getParent();
                if (fileParent != null) {
                    String lrcPath = getLrcPath(fileParent, filename.substring(0, filename.lastIndexOf(".")) + ".lrc");
                    if (!StringUtils.isEmpty(lrcPath)) {
                        song.setHasLrc(true);
                        song.setLrcPath(lrcPath);
                    }
                }
            }
            song.setMusicDuration(cur.getInt(3));
            song.setMusicArtist(cur.getString(4));
            song.setMusicAlbum(cur.getString(5));
            if (cur.getString(6) != null) {
                song.setMusicYear(cur.getString(6));
            } else {
                song.setMusicYear("undefine");
            }
            if ("audio/mpeg".equals(cur.getString(7).trim())) {// file type
                song.setFileType("mp3");
            } else if ("audio/x-ms-wma".equals(cur.getString(7).trim())) {
                song.setFileType("wma");
            }
            song.setFileType(cur.getString(7));
            if (cur.getString(8) != null) {// fileSize
            } else {
                song.setFileSize("undefine");
            }
            song.setFileSize(cur.getString(8));
            song.setSourceType(MusicData.sourceType.LOCAL);

            index++;
            Log.e("TAG", "getMusicList: -----------> " +song.toString());
            AppContant.PlayContant.musicData.add(song);
        }
        cur.close();
    }

    private final <T> List<T> createArrayList(T... elements) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, elements);
        return list;
    }

}
