package com.music.lrcmodel;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.music.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LrcView extends View {
    private static final long ADJUST_DURATION = 100;
    private static final long TIMELINE_KEEP_TIME = 4 * DateUtils.SECOND_IN_MILLIS;

    private List<LrcEntry> mLrcEntryList = new ArrayList<>();
    private TextPaint mLrcPaint = new TextPaint();
    private TextPaint mTimePaint = new TextPaint();

    private float lrcTextSize;
    private float mDividerHeight;
    private int defDuration;
    private int mAnimationDuration;
    private int mNormalTextColor;
    private int mCurrentTextColor;
    private int mTimelineTextColor;
    private String mDefaultLabel;
    private float mLrcPadding;
    private int mTimelineColor;
    private float timelineHeight;
    private Drawable mPlayDrawable;
    private int mTimeTextColor;
    private float timeTextSize;
    private int mTextGravity;
    private int mDrawableWidth;
    private int mTimeTextWidth;
    private Paint.FontMetrics mTimeFontMetrics;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private float mOffset;
    private ValueAnimator mAnimator;

    private Object mFlag;

    public LrcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LrcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LrcView);
        lrcTextSize = typedArray.getDimension(R.styleable.LrcView_lrcTextSize, getResources().getDimension(R.dimen.lrc_text_size));
        mDividerHeight = typedArray.getDimension(R.styleable.LrcView_lrcDividerHeight, getResources().getDimension(R.dimen.lrc_divider_height));
        defDuration = getResources().getInteger(R.integer.lrc_animation_duration);
        mAnimationDuration = typedArray.getInt(R.styleable.LrcView_lrcAnimationDuration, defDuration);
        mAnimationDuration = (mAnimationDuration < 0) ? defDuration : mAnimationDuration;
        mNormalTextColor = typedArray.getColor(R.styleable.LrcView_lrcNormalTextColor, getResources().getColor(R.color.lrc_normal_text_color));
        mCurrentTextColor = typedArray.getColor(R.styleable.LrcView_lrcCurrentTextColor, getResources().getColor(R.color.lrc_current_text_color));
        mTimelineTextColor = typedArray.getColor(R.styleable.LrcView_lrcTimelineTextColor, getResources().getColor(R.color.lrc_timeline_text_color));
        mDefaultLabel = typedArray.getString(R.styleable.LrcView_lrcLabel);
        mDefaultLabel = TextUtils.isEmpty(mDefaultLabel) ? "暂无歌词" : mDefaultLabel;
        mLrcPadding = typedArray.getDimension(R.styleable.LrcView_lrcPadding, 0);
        mTimelineColor = typedArray.getColor(R.styleable.LrcView_lrcTimelineColor, getResources().getColor(R.color.lrc_timeline_color));
        timelineHeight = typedArray.getDimension(R.styleable.LrcView_lrcTimelineHeight, getResources().getDimension(R.dimen.lrc_timeline_height));
        mPlayDrawable = typedArray.getDrawable(R.styleable.LrcView_lrcPlayDrawable);
        mPlayDrawable = (mPlayDrawable == null) ? getResources().getDrawable(R.drawable.lrc_play) : mPlayDrawable;
        mTimeTextColor = typedArray.getColor(R.styleable.LrcView_lrcTimeTextColor, getResources().getColor(R.color.lrc_time_text_color));
        timeTextSize = typedArray.getDimension(R.styleable.LrcView_lrcTimeTextSize, getResources().getDimension(R.dimen.lrc_time_text_size));
        mTextGravity = typedArray.getInteger(R.styleable.LrcView_lrcTextGravity, LrcEntry.GRAVITY_CENTER);

        typedArray.recycle();

        mDrawableWidth = (int) getResources().getDimension(R.dimen.lrc_drawable_width);
        mTimeTextWidth = (int) getResources().getDimension(R.dimen.lrc_time_width);

        mLrcPaint.setAntiAlias(true);
        mLrcPaint.setTextSize(lrcTextSize);
        mLrcPaint.setTextAlign(Paint.Align.LEFT);
        mTimePaint.setAntiAlias(true);
        mTimePaint.setTextSize(timeTextSize);
        mTimePaint.setTextAlign(Paint.Align.CENTER);
        //noinspection SuspiciousNameCombination
        mTimePaint.setStrokeWidth(timelineHeight);
        mTimePaint.setStrokeCap(Paint.Cap.ROUND);
        mTimeFontMetrics = mTimePaint.getFontMetrics();

        mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
        mScroller = new Scroller(getContext());
    }

    private boolean isTouching;
    private boolean isShowTimeline;
    private boolean isFling;
    private int mCurrentLine;
    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (hasLrc() && mOnPlayClickListener != null) {
                mScroller.forceFinished(true);
                removeCallbacks(hideTimelineRunnable);
                isTouching = true;
                isShowTimeline = true;
                invalidate();
                return true;
            }
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (hasLrc()) {
                mOffset += -distanceY;
                mOffset = Math.min(mOffset, getOffset(0));
                mOffset = Math.max(mOffset, getOffset(mLrcEntryList.size() - 1));
                invalidate();
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (hasLrc()) {
                mScroller.fling(0, (int) mOffset, 0, (int) velocityY, 0, 0, (int) getOffset(mLrcEntryList.size() - 1), (int) getOffset(0));
                isFling = true;
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (hasLrc() && isShowTimeline && mPlayDrawable.getBounds().contains((int) e.getX(), (int) e.getY())) {
                int centerLine = getCenterLine();
                long centerLineTime = mLrcEntryList.get(centerLine).getTime();
                // onPlayClick 消费了才更新 UI
                if (mOnPlayClickListener != null && mOnPlayClickListener.onPlayClick(centerLineTime)) {
                    isShowTimeline = false;
                    removeCallbacks(hideTimelineRunnable);
                    mCurrentLine = centerLine;
                    invalidate();
                    return true;
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    };

    private Runnable hideTimelineRunnable = new Runnable() {
        @Override
        public void run() {
            if (hasLrc() && isShowTimeline) {
                isShowTimeline = false;
                scrollTo(mCurrentLine);
            }
        }
    };

    private int getCenterLine() {
        int centerLine = 0;
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < mLrcEntryList.size(); i++) {
            if (Math.abs(mOffset - getOffset(i)) < minDistance) {
                minDistance = Math.abs(mOffset - getOffset(i));
                centerLine = i;
            }
        }
        return centerLine;
    }

    /**
     * 将中心行微调至正中心
     */
    private void adjustCenter() {
        scrollTo(getCenterLine(), ADJUST_DURATION);
    }

    /**
     * 歌词是否有效
     *
     * @return true，如果歌词有效，否则false
     */
    public boolean hasLrc() {
        return !mLrcEntryList.isEmpty();
    }

    /**
     * 加载歌词文件
     *
     * @param lrcText 歌词文本
     */
    @SuppressLint("StaticFieldLeak")
    public void loadLrc(final String lrcText) {
        runOnUi(() -> {
            reset();
            setFlag(lrcText);
            new AsyncTask<String, Integer, List<LrcEntry>>() {
                @Override
                protected List<LrcEntry> doInBackground(String... strings) {
                    return LrcEntry.parseLrc(strings[0]);
                }

                @Override
                protected void onPostExecute(List<LrcEntry> lrcEntries) {
                    super.onPostExecute(lrcEntries);
                    if (lrcText == getFlag()) {
                        onLrcLoaded(lrcEntries);
                        setFlag(null);
                    }
                }
            }.execute(lrcText);
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void loadLrc(final File lrcfile){
        reset();
        setFlag(lrcfile);
        new AsyncTask<File,Integer,List<LrcEntry>>(){
            @Override
            protected List<LrcEntry> doInBackground(File... files) {
                return LrcEntry.parseLrc(files[0]);
            }
            @Override
            protected void onPostExecute(List<LrcEntry> lrcEntries) {
                super.onPostExecute(lrcEntries);
                if(lrcfile==getFlag()){
                    onLrcLoaded(lrcEntries);
                    setFlag(null);
                }
            }
        }.execute(lrcfile);

    }

    /**
     * 刷新歌词
     *
     * @param time 当前播放时间
     */
    public void updateTime(final long time) {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                if (!hasLrc()) {
                    return;
                }

                int line = findShowLine(time);
                if (line != mCurrentLine) {
                    mCurrentLine = line;
                    if (!isShowTimeline) {
                        scrollTo(line);
                    } else {
                        invalidate();
                    }
                }
            }
        });
    }

    /**
     * 二分法查找当前时间应该显示的行数（最后一个 <= time 的行数）
     */
    private int findShowLine(long time) {
        int left = 0;
        int right = mLrcEntryList.size();
        while (left <= right) {
            int middle = (left + right) / 2;
            long middleTime = mLrcEntryList.get(middle).getTime();

            if (time < middleTime) {
                right = middle - 1;
            } else {
                if (middle + 1 >= mLrcEntryList.size() || time < mLrcEntryList.get(middle + 1).getTime()) {
                    return middle;
                }

                left = middle + 1;
            }
        }

        return 0;
    }

    private void onLrcLoaded(List<LrcEntry> entryList) {
        if (entryList != null && !entryList.isEmpty()) {
            mLrcEntryList.addAll(entryList);
        }
        initEntryList();
        invalidate();
    }

    private void initEntryList() {
        if (!hasLrc() || getWidth() == 0) return;
        Collections.sort(mLrcEntryList);
        for (LrcEntry mLrcEntry : mLrcEntryList) {
            mLrcEntry.init(mLrcPaint, (int) getLrcWidth(), mTextGravity);
        }
        mOffset = getHeight() / 2;
    }

    private float getLrcWidth() {
        return getWidth() - mLrcPadding * 2;
    }

    private float getOffset(int line) {
        if (mLrcEntryList.get(line).getOffset() == Float.MIN_VALUE) {
            float offset = getHeight() / 2;
            for (int i = 1; i <= line; i++) {
                offset -= (mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight()) / 2 + mDividerHeight;
            }
            mLrcEntryList.get(line).setOffset(offset);
        }

        return mLrcEntryList.get(line).getOffset();
    }

    /**
     * 滚动到某一行
     */
    private void scrollTo(int line) {
        scrollTo(line, mAnimationDuration);
    }

    private void scrollTo(int line, long duration) {
        float offset = getOffset(line);
        endAnimation();

        mAnimator = ValueAnimator.ofFloat(mOffset, offset);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }

    private void reset() {
        endAnimation();
        mScroller.forceFinished(true);
        isShowTimeline = false;
        isTouching = false;
        isFling = false;
        removeCallbacks(hideTimelineRunnable);
        mLrcEntryList.clear();
        mOffset = 0;
        mCurrentLine = 0;
        invalidate();
    }

    private void endAnimation(){
        if(mAnimator!=null&&mAnimator.isRunning()){
            mAnimator.end();
        }
    }

    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            post(r);
        }
    }

    private Object getFlag() {
        return mFlag;
    }

    private void setFlag(Object flag) {
        this.mFlag = flag;
    }

    private OnPlayClickListener mOnPlayClickListener;
    /**
     * 播放按钮点击监听器，点击后应该跳转到指定播放位置
     */
    public interface OnPlayClickListener {
        /**
         * 播放按钮被点击，应该跳转到指定播放位置
         *
         * @return 是否成功消费该事件，如果成功消费，则会更新UI
         */
        boolean onPlayClick(long time);
    }
    /**
     * 设置播放按钮点击监听器
     *
     * @param onPlayClickListener 如果为非 null ，则激活歌词拖动功能，否则将将禁用歌词拖动功能
     */
    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        mOnPlayClickListener = onPlayClickListener;
    }

}
