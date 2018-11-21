package com.music.manager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.music.R;
import com.music.javabean.MusicData;
import com.music.utils.BitmapUitls;
import com.music.utils.FileUtils;
import com.music.utils.ImageUtils;
import com.music.utils.ScreenUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CoverLoader {

    public static final int THUMBNAIL_MAX_LENGTH = 500;
    private static final String KEY_NULL = "null";
    private int roundLength ;
    private Context context;
    private Map<Type, LruCache<String, Bitmap>> cacheMap;

    private enum Type {
        THUMB,//thume
        ROUND,//round
        BLUR//blur
    }

    public static CoverLoader getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static CoverLoader instance = new CoverLoader();
    }

    private CoverLoader() {
    }

    public void init(Context mContext) {
        this.context = mContext.getApplicationContext();
        roundLength = ScreenUtils.getScreenWidth(context) / 2;
        // 获取当前进程的可用内存（单位KB）
        int maxMemory = (int) (Runtime.getRuntime().maxMemory()) / 1024;
        // 缓存大小为当前进程可用内存的1/8
        int cacheSize = maxMemory / 8;
        LruCache<String, Bitmap> thumbCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return BitmapUitls.getBitmapSize(value);
            }
        };
        LruCache<String, Bitmap> roundCache = new LruCache<>(10);
        LruCache<String, Bitmap> blurCache = new LruCache<>(10);
        cacheMap = new HashMap<>(3);
        cacheMap.put(Type.THUMB, thumbCache);
        cacheMap.put(Type.ROUND, roundCache);
        cacheMap.put(Type.BLUR, blurCache);
    }

    public Bitmap loadThumb(MusicData music) {
        return loadCover(music, Type.THUMB);
    }

    private Bitmap loadCover(MusicData data, Type type) {
        Bitmap bitmap;
        String key = getAlbumKey(data);
        LruCache<String, Bitmap> cache = cacheMap.get(type);
        if (TextUtils.isEmpty(key)) {
            bitmap = cache.get(KEY_NULL);
            if (bitmap == null) {
                bitmap = getDefaultCover(type);
                cache.put(KEY_NULL, bitmap);
            }
            return bitmap;
        }
        bitmap = cache.get(key);
        if (bitmap != null)
            return bitmap;
        bitmap = loadCoverByType(data, type);
        if (bitmap != null) {
            cache.put(key, bitmap);
            return bitmap;
        }

        return loadCover(null, type);
    }

    private Bitmap loadCoverByType(MusicData data, Type type) {
        Bitmap bitmap;
        if (data.getSourceType() == MusicData.sourceType.LOCAL) {
            bitmap = loadCoverFromMediaStore(data,data.getAlbumId());
        } else {
            bitmap = loadCoverFromFile(data.getCoverPath());
        }
        return bitmap;
    }

    private String getAlbumKey(MusicData data) {
        if (data == null) return null;
        if (data.getSourceType() == MusicData.sourceType.LOCAL && data.getAlbumId() > 0) {
            return String.valueOf(data.getAlbumId());
        } else if (data.getSourceType() == MusicData.sourceType.ONLINE && !TextUtils.isEmpty(data.getCoverPath())) {
            return data.getCoverPath();
        } else return null;


    }

    private Bitmap getDefaultCover(Type type) {
        switch (type) {
            case ROUND:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_page_default_cover);
                bitmap = ImageUtils.resizeImage(bitmap, roundLength, roundLength);
                return bitmap;
            case BLUR:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.play_page_default_bg);
            default:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_cover);
        }
    }

    /**
     * 根据Android多媒体数据库中专辑图片ID读取Bitmap
     *
     * @param albumId
     * @return
     */
    private Bitmap loadCoverFromMediaStore(MusicData data,long albumId) {
        ContentResolver contentResolver = this.context.getContentResolver();
        Uri uri = getMediaStoreAlbumCoverUri(albumId);
        InputStream inputStream;
        try {
            inputStream = contentResolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("TAG", "loadCoverFromMediaStore: ----------------> "+data );
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        FileUtils.closeStream(inputStream);
        return bitmap;
    }

    private Bitmap loadCoverFromFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Uri getMediaStoreAlbumCoverUri(long albumId) {
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, albumId);
    }
}
