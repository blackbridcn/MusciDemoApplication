package com.music.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 解决API 23 版本兼容的中 setOnChangeLinstener回调
 * onScrollChange方法只能同一包Package或者子类中在回调的问题
 * Created by yzzhang on 2017/9/11.
 */
public class ObservableScrollView extends ScrollView {

    private ObservableScrollViewListener mObservableScrollViewListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mObservableScrollViewListener != null) {
            mObservableScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnObservableScrollChangeListener(ObservableScrollViewListener mObservableScrollViewListener) {
        this.mObservableScrollViewListener = mObservableScrollViewListener;
    }

    public interface ObservableScrollViewListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}