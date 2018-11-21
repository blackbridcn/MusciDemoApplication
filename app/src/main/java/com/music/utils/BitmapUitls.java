package com.music.utils;

import android.graphics.Bitmap;
import android.os.Build;

import static java.lang.System.gc;

public class BitmapUitls {

    /**
     * 得到bitmap的大小KB
     */
    public static int getBitmapSize(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        switch (config) {
            case ALPHA_8://每个像素都需要1（8位）个字节的内存，只存储位图的透明度，没有颜色信息

                break;
            case ARGB_4444://A(Alpha)占4位的精度，R(Red)占4位的精度，G(Green)占4位的精度，B（Blue）占4位的精度，加起来一共是16位的精度，折合是2个字节，也就是一个像素占两个字节的内存，同时存储位图的透明度和颜色信息。不过由于该精度的位图质量较差，官方不推荐使用

                break;
            case ARGB_8888://只是A,R,G,B各占8个位的精度，所以一个像素占4个字节的内存。由于该类型的位图质量较好，官方特别推荐使用

                break;
            case RGB_565://R占5位精度，G占6位精度，B占5位精度，一共是16位精度，折合两个字节。这里注意的时，这个类型存储的只是颜色信息，没有透明度信息

                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount() / 1024;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount() / 1024;
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight() / 1024;                //earlier version
    }

    /**
     * 复制位图<br>
     * 根据该位图的大小产生一个新位图，根据指定的结构设置新位图的结构，然后把位图的像素拷贝到新位图中。如果不支持该转换，或者分配内存失败，
     * 那就返回NULL。返回的位图和原图有同样的像素密度 方 法 名：copy <br>
     * @param paramBitmap 源
     * @param config
     * @param isMutable
     * @return Bitmap
     */
    public static Bitmap copy(Bitmap paramBitmap, Bitmap.Config config, boolean isMutable) {
        Bitmap bitmap;
        try {
            bitmap = paramBitmap.copy(config, isMutable);
        } catch (OutOfMemoryError localOutOfMemoryError) {
            gc();
            bitmap = paramBitmap.copy(config, isMutable);
        }
        return bitmap;
    }


}
