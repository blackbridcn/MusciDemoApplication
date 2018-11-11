package com.music.utils;

import android.graphics.Bitmap;
import android.os.Build;

public class BitmapUitls {

    /**
     * 得到bitmap的大小KB
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount()/1024;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount()/1024;
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight()/1024;                //earlier version
    }


}
