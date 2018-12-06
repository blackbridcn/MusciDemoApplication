package com.music.lrcmodel;

import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://github.com/wangchenyan/lrcview
 */
public class LrcEntry implements Comparable<LrcEntry> {
    private static String TAG = LrcEntry.class.getName();

    @Override
    public int compareTo(@NonNull LrcEntry entry) {
        if (entry == null) {
            return -1;
        }
        return (int) (time - entry.getTime());
    }

    private long time;
    private String text;
    private StaticLayout staticLayout;
    private float offset = Float.MIN_VALUE;
    public static final int GRAVITY_LEFT = 1;
    public static final int GRAVITY_CENTER = 2;
    public static final int GRAVITY_RIGHT = 3;

    /**
     * 时间 和 歌词
     */
    private LrcEntry(long time, String text) {
        this.time = time;
        this.text = text;
    }

    /**
     * 初始化 StaticLayout
     * StaticLayout是android中处理文字换行的一个工具类，StaticLayout已经实现了文本绘制换行处理；
     * 使用Canvas的drawText绘制文本是不会自动换行的，即使一个很长很长的字符串，
     * drawText也只显示一行，超出部分被隐藏在屏幕之外，可以逐个计算每个字符的宽度，
     * 通过一定的算法将字符串分割成多个部分，然后分别调用drawText一部分一部分的显示，
     * 但是这种显示效率会很低；
     */
    void init(TextPaint paint, int width, int gravity) {
        Layout.Alignment align = null;
        switch (gravity) {
            case GRAVITY_LEFT:
                align = Layout.Alignment.ALIGN_NORMAL;
                break;
            case GRAVITY_CENTER:
                align = Layout.Alignment.ALIGN_CENTER;
                break;
            case GRAVITY_RIGHT:
                align = Layout.Alignment.ALIGN_OPPOSITE;
        }
        // StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Alignment align, float spacingmult, float spacingadd, boolean includepad)
        //bufstart ：需要分行的字符串从第几的位置开始
        // bufend ： 需要分行的字符串到哪里结束
        //outerwidth ：layout的宽度，字符串超出宽度时自动换行
        //align ：layout的对其方式，有ALIGN_CENTER， ALIGN_NORMAL， ALIGN_OPPOSITE 三种；
        //spacing mult： 相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度
        //spacinga dd： 在基础行距上添加多少 ？ 实际行间距等于spacing mult和spacing add这两者的和。
        //includepad ：是否保留
        staticLayout = new StaticLayout(this.text, paint, width, align, 1f, 0f, false);
    }

    long getTime() {
        return time;
    }

    StaticLayout getStaticLayout() {
        return staticLayout;
    }

    int getHeight() {
        if (staticLayout == null) {
            return 0;
        }
        return staticLayout.getHeight();
    }

    /**
     * 解析歌词
     *
     * @param lrcFile
     * @return
     */
    static List<LrcEntry> parseLrc(File lrcFile) {
        if (lrcFile == null || !lrcFile.exists()) return null;
        List<LrcEntry> entryList = new ArrayList<>();
        try {
            //读取歌词文件
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.e(TAG, "parseLrc: -----------> " + line);
                List<LrcEntry> list = parseLine(line);
                if (list != null && !list.isEmpty()) {
                    entryList.addAll(list);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(entryList);
        return entryList;
    }

    static List<LrcEntry> parseLrc(String lrcText) {
        if (TextUtils.isEmpty(lrcText)) {
            return null;
        }

        List<LrcEntry> entryList = new ArrayList<>();
        //根据回车换行键，split分为数组
        String[] array = lrcText.split("\\n");
        for (String line : array) {
            List<LrcEntry> list = parseLine(line);
            if (list != null && !list.isEmpty()) {
                entryList.addAll(list);
            }
        }

        Collections.sort(entryList);
        return entryList;
    }
    /**
     * 解析lrc中每一行信息 包括
     * */
    private static List<LrcEntry> parseLine(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }
        line = line.trim();
        //Java正则匹配 :时间标签（Time-tag） 标准格式： [分钟:秒.毫秒] 歌词 : [00:00.10]
        //                                      [dd:dd.2到3位个数字]
        //        Matcher lineMatcher = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d\\d\\])+)(.+)").matcher(line);
        //+ 一次或多次匹配前面的字符或子表达式
        //.匹配除"\r\n"之外的任何单个字符
        Matcher lineMatcher = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d{2,3}\\])+)(.+)").matcher(line);
        if (!lineMatcher.matches()) {
            return null;
        }        // [01:43.33][00:16.27]她总是只留下电话号码

        String times = lineMatcher.group(1);
        String text = lineMatcher.group(3);
        List<LrcEntry> entryList = new ArrayList<>();
        Matcher timeMatcher = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d){2,3}\\]").matcher(times);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            long mil = Long.parseLong(timeMatcher.group(3));
//            long time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil * 10;
            long time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + (mil >= 100L ? mil : mil * 10);
            entryList.add(new LrcEntry(time, text));
        }
        return entryList;

    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }
}
