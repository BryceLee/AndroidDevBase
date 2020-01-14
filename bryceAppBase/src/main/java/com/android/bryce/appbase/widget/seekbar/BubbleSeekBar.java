package com.android.appbase.widget.seekbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.biaoqing.library.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;

import static com.biaoqing.library.widget.seekbar.BubbleSeekBar.TextPosition.BELOW_SECTION_MARK;
import static com.biaoqing.library.widget.seekbar.BubbleSeekBar.TextPosition.BOTTOM_SIDES;
import static com.biaoqing.library.widget.seekbar.BubbleSeekBar.TextPosition.SIDES;

/**
 * A beautiful and powerful Android custom seek bar, which has a bubble view url progress
 * appearing upon when seeking. Highly customizable, mostly demands has been considered.
 * <p>
 * Created by woxingxiao on 2016-10-27.https://github.com/woxingxiao/BubbleSeekBar
 */
public class BubbleSeekBar extends View {

    static final int NONE = -1;
    private boolean isBiaoqingLightControl;
    private Bitmap jiantouBitmap;
    private Bitmap guangxianBitmap;
    public boolean isYellow;
    public boolean isAtionUp = true;
    private Bitmap jiantouBitmap2;
    private Bitmap guangxianBitmap2;
    private float mBottom;
    private float mTop;
    private boolean isBiaoqingHorizental;
    private boolean isBeautyMode;

    @IntDef({NONE, SIDES, BOTTOM_SIDES, BELOW_SECTION_MARK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextPosition {
        int SIDES = 0, BOTTOM_SIDES = 1, BELOW_SECTION_MARK = 2;
    }

    //TODO
    public int defaultValue = 50; // min
    public int defaultValue_offset = 5; // min

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
        invalidate();
    }

    private float mMin; // min
    private float mMax; // max
    private float mProgress; // real time value
    private boolean isFloatType; // support for float type output
    private int mTrackSize; // height of right-track(on the right of thumb)
    private int mSecondTrackSize; // height of left-track(on the left of thumb)
    private int mThumbRadius; // radius of thumb
    private int mThumbRadiusOnDragging; // radius of thumb when be dragging
    private int mTrackColor; // color of right-track
    private int mSecondTrackColor; // color of left-track
    private int mThumbColor; // color of thumb
    private int mSectionCount; // shares of whole progress(max - min)
    private boolean isShowSectionMark; // show demarcation points or not
    private boolean isAutoAdjustSectionMark; // auto scroll to the nearest section_mark or not
    private boolean isShowSectionText; // show section-text or not
    private int mSectionTextSize; // text size of section-text
    private int mSectionTextColor; // text color of section-text
    @TextPosition
    private int mSectionTextPosition = NONE; // text position of section-text relative to track
    private int mSectionTextInterval; // the interval of two section-text
    private boolean isShowThumbText; // show real time progress-text under thumb or not
    private int mThumbTextSize; // text size of progress-text
    private int mThumbTextColor; // text color of progress-text
    private boolean isShowProgressInFloat; // show bubble-progress in float or not
    private boolean isTouchToSeek; // touch anywhere on track to quickly seek
    private boolean isSeekBySection; // seek by section, the progress may not be linear
    private long mAnimDuration; // duration of animation
    private boolean isAlwaysShowBubble; // bubble shows all time
    private long mAlwaysShowBubbleDelay; // the delay duration before bubble shows all the time
    private boolean isHideBubble; // hide bubble

    private int mBubbleColor;// color of bubble
    private int mBubbleTextSize; // text size of bubble-progress
    private int mBubbleTextColor; // text color of bubble-progress

    private float mDelta; // max - min
    private float mSectionValue; // (mDelta / mSectionCount)
    private float mThumbCenterX; // X coordinate of thumb's center
    private float mTrackLength; // pixel length of whole track
    private float mSectionOffset; // pixel length of one section
    private boolean isThumbOnDragging; // is thumb on dragging or not
    private int mTextSpace; // space between text and track
    private boolean triggerBubbleShowing;
    private boolean triggerSeekBySection;

    private OnProgressChangedListener mProgressListener; // progress changing listener
    private float mLeft; // space between left of track and left of the view
    private float mRight; // space between right of track and left of the view
    private Paint mPaint;
    private Rect mRectText;

    private WindowManager mWindowManager;
    private BubbleView mBubbleView;
    private int mBubbleRadius;
    private float mBubbleCenterRawSolidX;
    private float mBubbleCenterRawSolidY;
    private float mBubbleCenterRawX;
    private WindowManager.LayoutParams mLayoutParams;
    private int[] mPoint = new int[2];
    private boolean isTouchToSeekAnimEnd = true;
    private float mPreSecValue; // previous SectionValue
    private BubbleConfigBuilder mConfigBuilder; // config attributes

    public BubbleSeekBar(Context context) {
        this(context, null);
    }

    public BubbleSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BubbleSeekBar, defStyleAttr, 0);
        mMin = a.getFloat(R.styleable.BubbleSeekBar_bsb_min, 0.0f);
        mMax = a.getFloat(R.styleable.BubbleSeekBar_bsb_max, 100.0f);
        mProgress = a.getFloat(R.styleable.BubbleSeekBar_bsb_progress, mMin);
        isFloatType = a.getBoolean(R.styleable.BubbleSeekBar_bsb_is_float_type, false);
        isBeautyMode = a.getBoolean(R.styleable.BubbleSeekBar_bsb_is_beauty, false);
        //TODO
        isBiaoqingLightControl = a.getBoolean(R.styleable.BubbleSeekBar_bsb_biaoqing_light_control, false);
        isBiaoqingHorizental = a.getBoolean(R.styleable.BubbleSeekBar_bsb_biaoqing_light_control_horizantal, true);
        mTrackSize = a.getDimensionPixelSize(R.styleable.BubbleSeekBar_bsb_track_size, BubbleUtils.dp2px(2));
        mSecondTrackSize = a.getDimensionPixelSize(R.styleable.BubbleSeekBar_bsb_second_track_size, mTrackSize + BubbleUtils.dp2px(2));
        mThumbRadius = a.getDimensionPixelSize(R.styleable.BubbleSeekBar_bsb_thumb_radius, mSecondTrackSize + BubbleUtils.dp2px(2));
        //TODO
        mThumbRadiusOnDragging = a.getDimensionPixelSize(R.styleable.BubbleSeekBar_bsb_thumb_radius, mSecondTrackSize * 2);
//        mThumbRadiusOnDragging = (int) (1.5f * a.getDimensionPixelSize(R.styleable.BubbleSeekBar_bsb_thumb_radius, mSecondTrackSize
//                * 2));
        mSectionCount = a.getInteger(R.styleable.BubbleSeekBar_bsb_section_count, 10);
        mTrackColor = a.getColor(R.styleable.BubbleSeekBar_bsb_track_color, ContextCompat.getColor(context, R.color.bubble_colorPrimary));
        mSecondTrackColor = a.getColor(R.styleable.BubbleSeekBar_bsb_second_track_color, ContextCompat.getColor(context, R.color
                .bubble_colorAccent));
        mThumbColor = a.getColor(R.styleable.BubbleSeekBar_bsb_thumb_color, mSecondTrackColor);
        isShowSectionText = a.getBoolean(R.styleable.BubbleSeekBar_bsb_show_section_text, false);
        mSectionTextSize = a.getDimensionPixelSize(R.styleable.BubbleSeekBar_bsb_section_text_size, BubbleUtils.sp2px(14));
        mSectionTextColor = a.getColor(R.styleable.BubbleSeekBar_bsb_section_text_color, mTrackColor);
        isSeekBySection = a.getBoolean(R.styleable.BubbleSeekBar_bsb_seek_by_section, false);
        int pos = a.getInteger(R.styleable.BubbleSeekBar_bsb_section_text_position, NONE);
        if (pos == 0) {
            mSectionTextPosition = SIDES;
        } else if (pos == 1) {
            mSectionTextPosition = TextPosition.BOTTOM_SIDES;
        } else if (pos == 2) {
            mSectionTextPosition = TextPosition.BELOW_SECTION_MARK;
        } else {
            mSectionTextPosition = NONE;
        }
        mSectionTextInterval = a.getInteger(R.styleable.BubbleSeekBar_bsb_section_text_interval, 1);
        isShowThumbText = a.getBoolean(R.styleable.BubbleSeekBar_bsb_show_thumb_text, false);
        mThumbTextSize = a.getDimensionPixelSize(R.styleable.BubbleSeekBar_bsb_thumb_text_size, BubbleUtils.sp2px(14));
        mThumbTextColor = a.getColor(R.styleable.BubbleSeekBar_bsb_thumb_text_color, mSecondTrackColor);
        mBubbleColor = a.getColor(R.styleable.BubbleSeekBar_bsb_bubble_color, mSecondTrackColor);
        mBubbleTextSize = a.getDimensionPixelSize(R.styleable.BubbleSeekBar_bsb_bubble_text_size, BubbleUtils.sp2px(14));
        mBubbleTextColor = a.getColor(R.styleable.BubbleSeekBar_bsb_bubble_text_color, Color.WHITE);
        isShowSectionMark = a.getBoolean(R.styleable.BubbleSeekBar_bsb_show_section_mark, false);
        isAutoAdjustSectionMark = a.getBoolean(R.styleable.BubbleSeekBar_bsb_auto_adjust_section_mark, false);
        isShowProgressInFloat = a.getBoolean(R.styleable.BubbleSeekBar_bsb_show_progress_in_float, false);
        int duration = a.getInteger(R.styleable.BubbleSeekBar_bsb_anim_duration, -1);
        mAnimDuration = duration < 0 ? 200 : duration;
        isTouchToSeek = a.getBoolean(R.styleable.BubbleSeekBar_bsb_touch_to_seek, false);
        isAlwaysShowBubble = a.getBoolean(R.styleable.BubbleSeekBar_bsb_always_show_bubble, false);
        duration = a.getInteger(R.styleable.BubbleSeekBar_bsb_always_show_bubble_delay, 0);
        mAlwaysShowBubbleDelay = duration <= 0 ? 200 : duration;
        isHideBubble = a.getBoolean(R.styleable.BubbleSeekBar_bsb_hide_bubble, false);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mRectText = new Rect();
        mTextSpace = BubbleUtils.dp2px(2);

        initConfigByPriority();

        if (isHideBubble) return;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // init BubbleView
        mBubbleView = new BubbleView(context);
        mBubbleView.setProgressText(isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf(getProgress()));

        calculateRadiusOfBubble();


    }

    private void initConfigByPriority() {
        if (mMin == mMax) {
            mMin = 0.0f;
            mMax = 100.0f;
        }
        if (mMin > mMax) {
            float tmp = mMax;
            mMax = mMin;
            mMin = tmp;
        }
        if (mProgress < mMin) {
            mProgress = mMin;
        }
        if (mProgress > mMax) {
            mProgress = mMax;
        }
        if (mSecondTrackSize < mTrackSize) {
            mSecondTrackSize = mTrackSize + BubbleUtils.dp2px(2);
        }
        if (mThumbRadius <= mSecondTrackSize) {
            mThumbRadius = mSecondTrackSize + BubbleUtils.dp2px(2);
        }
        if (mThumbRadiusOnDragging <= mSecondTrackSize) {
            mThumbRadiusOnDragging = mSecondTrackSize * 2;
        }
        if (mSectionCount <= 0) {
            mSectionCount = 10;
        }
        mDelta = mMax - mMin;
        mSectionValue = mDelta / mSectionCount;

        if (mSectionValue < 1) {
            isFloatType = true;
        }
        if (isFloatType) {
            isShowProgressInFloat = true;
        }
        if (mSectionTextPosition != NONE) {
            isShowSectionText = true;
        }
        if (isShowSectionText) {
            if (mSectionTextPosition == NONE) {
                mSectionTextPosition = TextPosition.SIDES;
            }
            if (mSectionTextPosition == TextPosition.BELOW_SECTION_MARK) {
                isShowSectionMark = true;
            }
        }
        if (mSectionTextInterval < 1) {
            mSectionTextInterval = 1;
        }
        if (isAutoAdjustSectionMark && !isShowSectionMark) {
            isAutoAdjustSectionMark = false;
        }
        if (isSeekBySection) {
            mPreSecValue = mMin;
            if (mProgress != mMin) {
                mPreSecValue = mSectionValue;
            }
            isShowSectionMark = true;
            isAutoAdjustSectionMark = true;
            isTouchToSeek = false;
        }
        if (isHideBubble) {
            isAlwaysShowBubble = false;
        }
        if (isAlwaysShowBubble) {
            setProgress(mProgress);
        }

        mThumbTextSize = isFloatType || isSeekBySection || (isShowSectionText && mSectionTextPosition == TextPosition
                .BELOW_SECTION_MARK) ? mSectionTextSize : mThumbTextSize;
    }

    /**
     * Calculate radius of bubble according to the Min and the Max
     */
    private void calculateRadiusOfBubble() {
        mPaint.setTextSize(mBubbleTextSize);

        // 计算滑到两端气泡里文字需要显示的宽度，比较取最大值为气泡的半径
        String text;
        if (isShowProgressInFloat) {
            text = float2String(mMin);
        } else {
            text = getMinText();
        }
        mPaint.getTextBounds(text, 0, text.length(), mRectText);
        int w1 = (mRectText.width() + mTextSpace * 2) >> 1;

        if (isShowProgressInFloat) {
            text = float2String(mMax);
        } else {
            text = getMaxText();
        }
        mPaint.getTextBounds(text, 0, text.length(), mRectText);
        int w2 = (mRectText.width() + mTextSpace * 2) >> 1;

        mBubbleRadius = BubbleUtils.dp2px(14); // default 14dp
        int max = Math.max(mBubbleRadius, Math.max(w1, w2));
        mBubbleRadius = max + mTextSpace;
    }

    private int defaultHeight = 6;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //TODO
        int height = mThumbRadiusOnDragging * defaultHeight; // 默认高度为拖动时thumb圆的直径
        if (isShowThumbText) {
            mPaint.setTextSize(mThumbTextSize);
            mPaint.getTextBounds("j", 0, 1, mRectText); // “j”是字母和阿拉伯数字中最高的
            height += mRectText.height() + mTextSpace; // 如果显示实时进度，则原来基础上加上进度文字高度和间隔
        }
        if (isShowSectionText && mSectionTextPosition >= TextPosition.BOTTOM_SIDES) { // 如果Section值在track之下显示，比较取较大值
            mPaint.setTextSize(mSectionTextSize);
            mPaint.getTextBounds("j", 0, 1, mRectText);
            height = Math.max(height, mThumbRadiusOnDragging * 2 + mRectText.height() + mTextSpace);
        }
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);

        mLeft = getPaddingLeft() + mThumbRadiusOnDragging;
        mRight = getMeasuredWidth() - getPaddingRight() - mThumbRadiusOnDragging;
        if (isBiaoqingLightControl) {
            mTop = mRight;
            mBottom = mLeft;
        }
        Log.d("mLeft", "mLeft:" + mLeft + ",mRight:" + mRight + ",mBottom:" + mBottom + ",mTop:" + mTop);
        if (isShowSectionText) {
            mPaint.setTextSize(mSectionTextSize);

            if (mSectionTextPosition == TextPosition.SIDES) {
                String text = getMinText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                mLeft += (mRectText.width() + mTextSpace);

                text = getMaxText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                mRight -= (mRectText.width() + mTextSpace);
            } else if (mSectionTextPosition >= TextPosition.BOTTOM_SIDES) {
                String text = getMinText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                float max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
                mLeft = getPaddingLeft() + max + mTextSpace;

                text = getMaxText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
                mRight = getMeasuredWidth() - getPaddingRight() - max - mTextSpace;
            }
        } else if (isShowThumbText && mSectionTextPosition == NONE) {
            mPaint.setTextSize(mThumbTextSize);

            String text = getMinText();
            mPaint.getTextBounds(text, 0, text.length(), mRectText);
            float max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
            mLeft = getPaddingLeft() + max + mTextSpace;

            text = getMaxText();
            mPaint.getTextBounds(text, 0, text.length(), mRectText);
            max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
            mRight = getMeasuredWidth() - getPaddingRight() - max - mTextSpace;
        }

        mTrackLength = mRight - mLeft;
        mSectionOffset = mTrackLength * 1f / mSectionCount;

        if (isHideBubble) return;

        mBubbleView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        locatePositionOnScreen();
    }

    /**
     * In fact there two parts of the BubbleSeeBar, they are the BubbleView and the SeekBar.
     * <p>
     * The BubbleView is added to Window by the WindowManager, so the only connection between
     * BubbleView and SeekBar is their origin raw coordinates on the screen.
     * <p>
     * It's easy to compute the coordinates(mBubbleCenterRawSolidX, mBubbleCenterRawSolidY) of point
     * when the Progress equals the Min. Then compute the pixel length increment when the Progress is
     * changing, the result is mBubbleCenterRawX. At last the WindowManager calls updateViewLayout()
     * to update the LayoutParameter.x of the BubbleView.
     * <p>
     * 气泡BubbleView实际是通过WindowManager动态添加的一个视图，因此与SeekBar唯一的位置联系就是它们在屏幕上的
     * 绝对坐标。
     * 先计算进度mProgress为mMin时BubbleView的中心坐标（mBubbleCenterRawSolidX，mBubbleCenterRawSolidY），
     * 然后根据进度来增量计算横坐标mBubbleCenterRawX，再动态设置LayoutParameter.x，就实现了气泡跟随滑动移动。
     */
    private void locatePositionOnScreen() {
        getLocationOnScreen(mPoint);
        //TODO
        if (mBubbleView == null) {
            return;
        }
        mBubbleCenterRawSolidX = mPoint[0] + mLeft - mBubbleView.getMeasuredWidth() / 2f;
        mBubbleCenterRawX = mBubbleCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;
        mBubbleCenterRawSolidY = mPoint[1] - mBubbleView.getMeasuredHeight();
        mBubbleCenterRawSolidY -= BubbleUtils.dp2px(24);
        if (BubbleUtils.isMIUI()) {
            mBubbleCenterRawSolidY += BubbleUtils.dp2px(4);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float xLeft = getPaddingLeft();
        float xRight;
        if (isBiaoqingHorizental) {
            xRight = getMeasuredWidth() - getPaddingRight();
        } else {
            xRight = getMeasuredHeight() - getPaddingTop();
        }
        //TODO onmeasure 基准高度是mThumbRadiusOnDragging*3，这里想让seekbar居中，所有设置为1.5*mThumbRadiusOnDragging
        float yTop = getPaddingTop() + mThumbRadiusOnDragging * (defaultHeight / 2f);

        // draw sectionText SIDES or BOTTOM_SIDES
        if (isShowSectionText) {
            mPaint.setTextSize(mSectionTextSize);
            mPaint.setColor(mSectionTextColor);

            if (mSectionTextPosition == TextPosition.SIDES) {
                float y_ = yTop + mRectText.height() / 2f;

                String text = getMinText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                canvas.drawText(text, xLeft + mRectText.width() / 2f, y_, mPaint);
                xLeft += mRectText.width() + mTextSpace;

                text = getMaxText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                canvas.drawText(text, xRight - mRectText.width() / 2f, y_, mPaint);
                xRight -= (mRectText.width() + mTextSpace);

            } else if (mSectionTextPosition >= TextPosition.BOTTOM_SIDES) {
                float y_ = yTop + mThumbRadiusOnDragging + mTextSpace;

                String text = getMinText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                y_ += mRectText.height();
                xLeft = mLeft;
                if (mSectionTextPosition == TextPosition.BOTTOM_SIDES) {
                    canvas.drawText(text, xLeft, y_, mPaint);
                }

                text = getMaxText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                xRight = mRight;
                if (mSectionTextPosition == TextPosition.BOTTOM_SIDES) {
                    canvas.drawText(text, xRight, y_, mPaint);
                }
            }
        } else if (isShowThumbText && mSectionTextPosition == NONE) {
            xLeft = mLeft;
            xRight = mRight;
        }

        if ((!isShowSectionText && !isShowThumbText) || mSectionTextPosition == TextPosition.SIDES) {
            xLeft += mThumbRadiusOnDragging;
            xRight -= mThumbRadiusOnDragging;
        }

        boolean isShowTextBelowSectionMark = isShowSectionText && mSectionTextPosition == TextPosition.BELOW_SECTION_MARK;
        boolean conditionInterval = mSectionCount % 2 == 0;


        if (!isThumbOnDragging || isAlwaysShowBubble) {
            mThumbCenterX = mTrackLength / mDelta * (mProgress - mMin) + xLeft;
        }

        // draw thumbText
        if (isShowThumbText && !isThumbOnDragging && isTouchToSeekAnimEnd) {
            mPaint.setColor(mThumbTextColor);
            mPaint.setTextSize(mThumbTextSize);
            mPaint.getTextBounds("0123456789", 0, "0123456789".length(), mRectText); // compute solid height
            float y_ = yTop + mRectText.height() + mThumbRadiusOnDragging + mTextSpace;

            if (isFloatType || (isShowProgressInFloat && mSectionTextPosition == TextPosition.BOTTOM_SIDES && mProgress != mMin &&
                    mProgress != mMax)) {
                canvas.drawText(String.valueOf(getProgressFloat()), mThumbCenterX, y_, mPaint);
            } else {
                canvas.drawText(String.valueOf(getProgress()), mThumbCenterX, y_, mPaint);
            }
        }
        if (isYellow) {
            mPaint.setColor(Color.parseColor("#fde752"));
        } else {
            // draw track
            mPaint.setColor(mSecondTrackColor);
        }

        mPaint.setStrokeWidth(mSecondTrackSize);
        canvas.drawLine(xLeft, yTop, mThumbCenterX, yTop, mPaint);

        // draw second track
        if (isYellow) {
            mPaint.setColor(Color.parseColor("#fde752"));
        } else {
            mPaint.setColor(mTrackColor);
        }
        mPaint.setStrokeWidth(mTrackSize);
        canvas.drawLine(mThumbCenterX, yTop, xRight, yTop, mPaint);


        // draw sectionMark & sectionText BELOW_SECTION_MARK
        if (isShowTextBelowSectionMark || isShowSectionMark) {
            float r = (mThumbRadiusOnDragging - BubbleUtils.dp2px(2)) / 2f;
            float junction = mTrackLength / mDelta * Math.abs(mProgress - mMin) + mLeft; // 交汇点
            mPaint.setTextSize(mSectionTextSize);
            mPaint.getTextBounds("0123456789", 0, "0123456789".length(), mRectText); // compute solid height

            float x_;
            float y_ = yTop + mRectText.height() + mThumbRadiusOnDragging + mTextSpace;
            //TODO 在这里画结点
            for (int i = 0; i <= mSectionCount; i++) {
                x_ = xLeft + i * mSectionOffset;
                mPaint.setColor(Color.WHITE);
//                mPaint.setColor(x_ <= junction ? mSecondTrackColor : mTrackColor);
                // sectionMark 默认数值结点
                //不是表情结点
                if (i == defaultValue && !isBiaoqingLightControl) {
                    canvas.drawCircle(x_, yTop, r, mPaint);
                }

                // sectionText belows section
                if (isShowTextBelowSectionMark) {
                    mPaint.setColor(mSectionTextColor);

                    if (mSectionTextInterval > 1) {
                        if (conditionInterval && i % mSectionTextInterval == 0) {
                            float m = mMin + mSectionValue * i;
                            canvas.drawText(isFloatType ? float2String(m) : (int) m + "", x_, y_, mPaint);
                        }
                    } else {
                        float m = mMin + mSectionValue * i;
                        canvas.drawText(isFloatType ? float2String(m) : (int) m + "", x_, y_, mPaint);
                    }
                }
            }
        }


        // draw thumb
        mPaint.setColor(mThumbColor);
        if (isBiaoqingLightControl) {
            drawLightControlUI(canvas, yTop);
        } else {
            if (isBeautyMode){
                mPaint.setColor(Color.parseColor("#ffffff"));
            }
            canvas.drawCircle(mThumbCenterX, yTop, isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadiusOnDragging, mPaint);
        }
//        int height = getMeasuredHeight();
//        canvas.drawCircle(mThumbCenterX, yTop, isThumbOnDragging ? height/2 :  height/2, mPaint);
//        canvas.drawCircle(mThumbCenterX, yTop, isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadiusOnDragging, mPaint);
    }

    private void drawLightControlUI(Canvas canvas, float yTop) {
        if (isYellow) {
            if (!isAtionUp) {
                if (jiantouBitmap2 == null) {
                    jiantouBitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.jiantou_light_2);
                    jiantouBitmap2 = ImageUtils.rotate(jiantouBitmap2, 90, jiantouBitmap2.getWidth() / 2,
                            jiantouBitmap2.getHeight() / 2, false);
                }
                if (jiantouBitmap2 != null)
                    canvas.drawBitmap(jiantouBitmap2, mThumbCenterX - jiantouBitmap2.getWidth() / 2, getPaddingTop(), mPaint);
            }


            if (guangxianBitmap2 == null) {
                guangxianBitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.guangxian2);
                guangxianBitmap2 = Bitmap.createScaledBitmap(guangxianBitmap2, 2 * mThumbRadiusOnDragging, 2 *
                        mThumbRadiusOnDragging, true);
//                jiantouBitmap = ImageUtils.rotate(jiantouBitmap, 90, jiantouBitmap.getWidth() / 2, jiantouBitmap.getHeight() / 2,
//                        false);
            }
            if (guangxianBitmap2 != null)
                canvas.drawBitmap(guangxianBitmap2, mThumbCenterX - guangxianBitmap2.getWidth() / 2, yTop -
                        guangxianBitmap2.getHeight() / 2, mPaint);
        } else {
            if (!isAtionUp) {
                if (jiantouBitmap == null) {
                    jiantouBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.jiantou_light);
                    jiantouBitmap = ImageUtils.rotate(jiantouBitmap, 90, jiantouBitmap.getWidth() / 2, jiantouBitmap.getHeight()
                            / 2, false);
                }
                if (jiantouBitmap != null)
                    canvas.drawBitmap(jiantouBitmap, mThumbCenterX - jiantouBitmap.getWidth() / 2, getPaddingTop(), mPaint);
            }


            if (guangxianBitmap == null) {
                guangxianBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.guangxian);
                guangxianBitmap = Bitmap.createScaledBitmap(guangxianBitmap, 2 * mThumbRadiusOnDragging, 2 *
                        mThumbRadiusOnDragging, true);
//                jiantouBitmap = ImageUtils.rotate(jiantouBitmap, 90, jiantouBitmap.getWidth() / 2, jiantouBitmap.getHeight() / 2,
//                        false);
            }
            if (guangxianBitmap != null)
                canvas.drawBitmap(guangxianBitmap, mThumbCenterX - guangxianBitmap.getWidth() / 2, yTop - guangxianBitmap
                        .getHeight() / 2, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (isHideBubble || !isAlwaysShowBubble) return;

        if (visibility != VISIBLE) {
            hideBubble();
        } else {
            if (triggerBubbleShowing) {
                showBubble();
            }
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        hideBubble();
        mBubbleView = null;
        super.onDetachedFromWindow();
    }

    float dx;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mProgressListener!=null){
                    mProgressListener.onTouch();
                }
                Log.d("ACTION_DOWN_mLeft", "mLeft:" + mLeft + ",mRight:" + mRight + ",mBottom:" + mBottom + ",mTop:" + mTop);
                isAtionUp = false;
                if (isBiaoqingLightControl){
                    isYellow = Math.abs(mProgress - defaultValue) < defaultValue_offset;
                }
                getParent().requestDisallowInterceptTouchEvent(true);

                isThumbOnDragging = isThumbTouched(event);
                if (isThumbOnDragging) {
                    if (isSeekBySection && !triggerSeekBySection) {
                        triggerSeekBySection = true;
                    }
                    if (isAlwaysShowBubble && !triggerBubbleShowing) {
                        triggerBubbleShowing = true;
                    }
                    if (!isHideBubble) {
                        showBubble();
                    }

                    invalidate();
                } else if (isTouchToSeek && isTrackTouched(event)) {
                    isThumbOnDragging = true;
                    if (isAlwaysShowBubble) {
                        hideBubble();
                        triggerBubbleShowing = true;
                    }
                    if (isBiaoqingHorizental) {
                        mThumbCenterX = event.getX();
                    } else {
                        mThumbCenterX = mTop - event.getY();
                    }
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    if (isBiaoqingHorizental) {
                        mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                    } else {
                        mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
//                        mProgress = mMax - ((mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin);
                    }
                    if (!isHideBubble) {
                        mBubbleCenterRawX = mBubbleCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;
                        showBubble();
                    }
                    invalidate();
                }
                if (isBiaoqingHorizental) {
                    dx = mThumbCenterX - event.getX();
                } else {
                    dx = 0;
//                    dx = mThumbCenterX - (mTop-event.getY());
                }
                Log.d("ACTION_DOWN","y:" + event.getY() + ",progress:" + mProgress + ",mThumbCenterX:" +
                        mThumbCenterX+",dx:"+dx);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mProgressListener!=null){
                    mProgressListener.onTouch();
                }
                if (isThumbOnDragging) {
                    if (isBiaoqingLightControl){
                        isYellow = Math.abs(mProgress - defaultValue) < defaultValue_offset;
                    }
                    if (isBiaoqingHorizental) {
                        mThumbCenterX = event.getX() + dx;
                    } else {
                        mThumbCenterX = (mTop-event.getY()) + dx;
                    }
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    if (isBiaoqingHorizental) {
                        mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                    } else {
                        mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
//                        mProgress = mMax - ((mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin);
                    }
                    Log.d("ACTION_MOVE",  "y:" + event.getY() + ",progress:" + mProgress + ",mThumbCenterX:" +
                            mThumbCenterX+",dx:"+dx);
                    if (!isHideBubble) {
                        mBubbleCenterRawX = mBubbleCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;
                        mLayoutParams.x = (int) (mBubbleCenterRawX + 0.5f);
                        mWindowManager.updateViewLayout(mBubbleView, mLayoutParams);
                        mBubbleView.setProgressText(isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf
                                (getProgress()));
                    }

                    invalidate();

                    if (mProgressListener != null) {
                        mProgressListener.onProgressChanged(this, getProgress(), getProgressFloat());
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isAtionUp = true;
                isYellow=false;
                getParent().requestDisallowInterceptTouchEvent(false);

                if (isAutoAdjustSectionMark) {
                    if (isTouchToSeek) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isTouchToSeekAnimEnd = false;
                                autoAdjustSection();
                            }
                        }, isThumbOnDragging ? 0 : 300);
                    } else {
                        autoAdjustSection();
                    }
                } else if (isThumbOnDragging || isTouchToSeek) {
                    if (isHideBubble) {
                        animate().setDuration(mAnimDuration).setStartDelay(!isThumbOnDragging && isTouchToSeek ? 300 : 0)
                                .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isThumbOnDragging = false;
                                invalidate();

                                if (mProgressListener != null) {
                                    mProgressListener.onProgressChanged(BubbleSeekBar.this, getProgress(), getProgressFloat());
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                isThumbOnDragging = false;
                                invalidate();
                            }
                        }).start();
                    } else {
                        mBubbleView.animate().alpha(isAlwaysShowBubble ? 1f : 0f).setDuration(mAnimDuration).setStartDelay
                                (!isThumbOnDragging && isTouchToSeek ? 300 : 0).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (!isAlwaysShowBubble) {
                                    hideBubble();
                                }

                                isThumbOnDragging = false;
                                invalidate();

                                if (mProgressListener != null) {
                                    mProgressListener.onProgressChanged(BubbleSeekBar.this, getProgress(), getProgressFloat());
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                if (!isAlwaysShowBubble) {
                                    hideBubble();
                                }

                                isThumbOnDragging = false;
                                invalidate();
                            }
                        }).start();

                    }
                }

                if (mProgressListener != null) {
                    mProgressListener.getProgressOnActionUp(this, getProgress(), getProgressFloat());
                }

                break;
        }

        return isThumbOnDragging || isTouchToSeek || super.onTouchEvent(event);
    }

    /**
     * Detect effective touch of thumb
     */
    private boolean isThumbTouched(MotionEvent event) {
        if (!isEnabled()) return false;

        float x = mTrackLength / mDelta * (mProgress - mMin) + mLeft;
        float y = getMeasuredHeight() / 2f;
        return (event.getX() - x) * (event.getX() - x) + (event.getY() - y) * (event.getY() - y) <= (mLeft + BubbleUtils.dp2px(8)) * (mLeft +
                BubbleUtils.dp2px(8));
    }

    /**
     * Detect effective touch of track
     */
    private boolean isTrackTouched(MotionEvent event) {
        return isEnabled() && event.getX() >= getPaddingLeft() && event.getX() <= getMeasuredWidth() - getPaddingRight() && event
                .getY() >= getPaddingTop() && event.getY() <= getMeasuredHeight() - getPaddingBottom();
    }

    /**
     * Showing the Bubble depends the way that the WindowManager adds a Toast type view to the Window.
     * <p>
     * 显示气泡
     * 原理是利用WindowManager动态添加一个与Toast相同类型的BubbleView，消失时再移除
     */
    private void showBubble() {
        if (mBubbleView == null || mBubbleView.getParent() != null) {
            return;
        }

        if (mLayoutParams == null) {
            mLayoutParams = new WindowManager.LayoutParams();
            mLayoutParams.gravity = Gravity.START | Gravity.TOP;
            mLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLayoutParams.format = PixelFormat.TRANSLUCENT;
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            // MIUI禁止了开发者使用TYPE_TOAST，Android 7.1.1 对TYPE_TOAST的使用更严格
            if (BubbleUtils.isMIUI() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }
        mLayoutParams.x = (int) (mBubbleCenterRawX + 0.5f);
        mLayoutParams.y = (int) (mBubbleCenterRawSolidY + 0.5f);

        mBubbleView.setAlpha(0);
        mBubbleView.setVisibility(VISIBLE);
        mBubbleView.animate().alpha(1f).setDuration(mAnimDuration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mWindowManager.addView(mBubbleView, mLayoutParams);
            }
        }).start();
        mBubbleView.setProgressText(isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf(getProgress()));
    }

    /**
     * Auto scroll to the nearest section mark
     */
    private void autoAdjustSection() {
        int i;
        float x = 0;
        for (i = 0; i <= mSectionCount; i++) {
            x = i * mSectionOffset + mLeft;
            if (x <= mThumbCenterX && mThumbCenterX - x <= mSectionOffset) {
                break;
            }
        }

        BigDecimal bigDecimal = BigDecimal.valueOf(mThumbCenterX);
        float x_ = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        boolean onSection = x_ == x; // 就在section处，不作valueAnim，优化性能

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator valueAnim = null;
        if (!onSection) {
            if (mThumbCenterX - x <= mSectionOffset / 2f) {
                valueAnim = ValueAnimator.ofFloat(mThumbCenterX, x);
            } else {
                valueAnim = ValueAnimator.ofFloat(mThumbCenterX, (i + 1) * mSectionOffset + mLeft);
            }
            valueAnim.setInterpolator(new LinearInterpolator());
            valueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mThumbCenterX = (float) animation.getAnimatedValue();
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;

                    if (!isHideBubble) {
                        mBubbleCenterRawX = mBubbleCenterRawSolidX + mThumbCenterX - mLeft;
                        mLayoutParams.x = (int) (mBubbleCenterRawX + 0.5f);
                        if (mBubbleView.getParent() != null) {
                            mWindowManager.updateViewLayout(mBubbleView, mLayoutParams);
                        }
                        mBubbleView.setProgressText(isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf
                                (getProgress()));
                    }

                    invalidate();

                    if (mProgressListener != null) {
                        mProgressListener.onProgressChanged(BubbleSeekBar.this, getProgress(), getProgressFloat());
                    }
                }
            });
        }

        if (isHideBubble) {
            if (!onSection) {
                animatorSet.setDuration(mAnimDuration).playTogether(valueAnim);
            }
        } else {
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mBubbleView, View.ALPHA, isAlwaysShowBubble ? 1 : 0);
            if (onSection) {
                animatorSet.setDuration(mAnimDuration).play(alphaAnim);
            } else {
                animatorSet.setDuration(mAnimDuration).playTogether(valueAnim, alphaAnim);
            }
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isHideBubble && !isAlwaysShowBubble) {
                    hideBubble();
                }

                mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                isThumbOnDragging = false;
                isTouchToSeekAnimEnd = true;
                invalidate();

                if (mProgressListener != null) {
                    mProgressListener.getProgressOnFinally(BubbleSeekBar.this, getProgress(), getProgressFloat());
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (!isHideBubble && !isAlwaysShowBubble) {
                    hideBubble();
                }

                mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                isThumbOnDragging = false;
                isTouchToSeekAnimEnd = true;
                invalidate();
            }
        });
        animatorSet.start();
    }

    /**
     * The WindowManager removes the BubbleView from the Window.
     */
    private void hideBubble() {
        if (mBubbleView == null) return;

        mBubbleView.setVisibility(GONE); // 防闪烁
        if (mBubbleView.getParent() != null) {
            mWindowManager.removeViewImmediate(mBubbleView);
        }
    }

    /**
     * When BubbleSeekBar's parent view is scrollable, must listener to it's scrolling and call this
     * method to correct the offsets.
     */
    public void correctOffsetWhenContainerOnScrolling() {
        if (isHideBubble) return;

        locatePositionOnScreen();

        if (mBubbleView.getParent() != null) {
            postInvalidate();
        }
    }

    private String getMinText() {
        return isFloatType ? float2String(mMin) : String.valueOf((int) mMin);
    }

    private String getMaxText() {
        return isFloatType ? float2String(mMax) : String.valueOf((int) mMax);
    }

    public float getMin() {
        return mMin;
    }

    public float getMax() {
        return mMax;
    }

    public void setProgress(float progress) {
        mProgress = progress;

        //这里不调用
//        if (mProgressListener != null) {
//            mProgressListener.onProgressChanged(this, getProgress(), getProgressFloat());
//            mProgressListener.getProgressOnFinally(this, getProgress(), getProgressFloat());
//        }

        if (isHideBubble) {
            mBubbleCenterRawX = mBubbleCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;
        }

        if (isAlwaysShowBubble) {
            hideBubble();

            int[] location = new int[2];
            getLocationOnScreen(location);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    showBubble();
                    triggerBubbleShowing = true;
                }
            }, location[0] == 0 && location[1] == 0 ? mAlwaysShowBubbleDelay : 0);
        }

        postInvalidate();
    }

    public int getProgress() {
        if (isSeekBySection && triggerSeekBySection) {
            float half = mSectionValue / 2;

            if (mProgress >= mPreSecValue) { // increasing
                if (mProgress >= mPreSecValue + half) {
                    mPreSecValue += mSectionValue;
                    return Math.round(mPreSecValue);
                } else {
                    return Math.round(mPreSecValue);
                }
            } else { // reducing
                if (mProgress >= mPreSecValue - half) {
                    return Math.round(mPreSecValue);
                } else {
                    mPreSecValue -= mSectionValue;
                    return Math.round(mPreSecValue);
                }
            }
        }

        return Math.round(mProgress);
    }

    public float getProgressFloat() {
        return formatFloat(mProgress);
    }

    public OnProgressChangedListener getOnProgressChangedListener() {
        return mProgressListener;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        mProgressListener = onProgressChangedListener;
    }

    void config(BubbleConfigBuilder builder) {
        mMin = builder.min;
        mMax = builder.max;
        mProgress = builder.progress;
        isFloatType = builder.floatType;
        mTrackSize = builder.trackSize;
        mSecondTrackSize = builder.secondTrackSize;
        mThumbRadius = builder.thumbRadius;
        mThumbRadiusOnDragging = builder.thumbRadiusOnDragging;
        mTrackColor = builder.trackColor;
        mSecondTrackColor = builder.secondTrackColor;
        mThumbColor = builder.thumbColor;
        mSectionCount = builder.sectionCount;
        isShowSectionMark = builder.showSectionMark;
        isAutoAdjustSectionMark = builder.autoAdjustSectionMark;
        isShowSectionText = builder.showSectionText;
        mSectionTextSize = builder.sectionTextSize;
        mSectionTextColor = builder.sectionTextColor;
        mSectionTextPosition = builder.sectionTextPosition;
        mSectionTextInterval = builder.sectionTextInterval;
        isShowThumbText = builder.showThumbText;
        mThumbTextSize = builder.thumbTextSize;
        mThumbTextColor = builder.thumbTextColor;
        isShowProgressInFloat = builder.showProgressInFloat;
        mAnimDuration = builder.animDuration;
        isTouchToSeek = builder.touchToSeek;
        isSeekBySection = builder.seekBySection;
        mBubbleColor = builder.bubbleColor;
        mBubbleTextSize = builder.bubbleTextSize;
        mBubbleTextColor = builder.bubbleTextColor;
        isAlwaysShowBubble = builder.alwaysShowBubble;
        mAlwaysShowBubbleDelay = builder.alwaysShowBubbleDelay;
        isHideBubble = builder.hideBubble;

        initConfigByPriority();
        calculateRadiusOfBubble();

        if (mProgressListener != null) {
            mProgressListener.onProgressChanged(this, getProgress(), getProgressFloat());
            mProgressListener.getProgressOnFinally(this, getProgress(), getProgressFloat());
        }

        mConfigBuilder = null;

        requestLayout();
    }

    public BubbleConfigBuilder getConfigBuilder() {
        if (mConfigBuilder == null) {
            mConfigBuilder = new BubbleConfigBuilder(this);
        }

        mConfigBuilder.min = mMin;
        mConfigBuilder.max = mMax;
        mConfigBuilder.progress = mProgress;
        mConfigBuilder.floatType = isFloatType;
        mConfigBuilder.trackSize = mTrackSize;
        mConfigBuilder.secondTrackSize = mSecondTrackSize;
        mConfigBuilder.thumbRadius = mThumbRadius;
        mConfigBuilder.thumbRadiusOnDragging = mThumbRadiusOnDragging;
        mConfigBuilder.trackColor = mTrackColor;
        mConfigBuilder.secondTrackColor = mSecondTrackColor;
        mConfigBuilder.thumbColor = mThumbColor;
        mConfigBuilder.sectionCount = mSectionCount;
        mConfigBuilder.showSectionMark = isShowSectionMark;
        mConfigBuilder.autoAdjustSectionMark = isAutoAdjustSectionMark;
        mConfigBuilder.showSectionText = isShowSectionText;
        mConfigBuilder.sectionTextSize = mSectionTextSize;
        mConfigBuilder.sectionTextColor = mSectionTextColor;
        mConfigBuilder.sectionTextPosition = mSectionTextPosition;
        mConfigBuilder.sectionTextInterval = mSectionTextInterval;
        mConfigBuilder.showThumbText = isShowThumbText;
        mConfigBuilder.thumbTextSize = mThumbTextSize;
        mConfigBuilder.thumbTextColor = mThumbTextColor;
        mConfigBuilder.showProgressInFloat = isShowProgressInFloat;
        mConfigBuilder.animDuration = mAnimDuration;
        mConfigBuilder.touchToSeek = isTouchToSeek;
        mConfigBuilder.seekBySection = isSeekBySection;
        mConfigBuilder.bubbleColor = mBubbleColor;
        mConfigBuilder.bubbleTextSize = mBubbleTextSize;
        mConfigBuilder.bubbleTextColor = mBubbleTextColor;
        mConfigBuilder.alwaysShowBubble = isAlwaysShowBubble;
        mConfigBuilder.alwaysShowBubbleDelay = mAlwaysShowBubbleDelay;
        mConfigBuilder.hideBubble = isHideBubble;

        return mConfigBuilder;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("save_instance", super.onSaveInstanceState());
        bundle.putFloat("progress", mProgress);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getFloat("progress");
            super.onRestoreInstanceState(bundle.getParcelable("save_instance"));

            if (mBubbleView != null) {
                mBubbleView.setProgressText(isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf(getProgress
                        ()));
            }
            setProgress(mProgress);

            return;
        }

        super.onRestoreInstanceState(state);
    }

    private String float2String(float value) {
        return String.valueOf(formatFloat(value));
    }

    private float formatFloat(float value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * Listen to progress onChanged, onActionUp, onFinally
     */
    public interface OnProgressChangedListener {

        void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat);

        void onTouch();

        void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat);

        void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat);
    }

    /**
     * Listener adapter
     * <br/>
     * usage like {@link AnimatorListenerAdapter}
     */
    public static abstract class OnProgressChangedListenerAdapter implements OnProgressChangedListener {

        @Override
        public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
        }

        @Override
        public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
        }

        @Override
        public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
        }
    }

    /*******************************************************************************************
     * ************************************  custom bubble view  ************************************
     *******************************************************************************************/
    private class BubbleView extends View {

        private Paint mBubblePaint;
        private Path mBubblePath;
        private RectF mBubbleRectF;
        private Rect mRect;
        private String mProgressText = "";

        BubbleView(Context context) {
            this(context, null);
        }

        BubbleView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            mBubblePaint = new Paint();
            mBubblePaint.setAntiAlias(true);
            mBubblePaint.setTextAlign(Paint.Align.CENTER);

            mBubblePath = new Path();
            mBubbleRectF = new RectF();
            mRect = new Rect();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            setMeasuredDimension(3 * mBubbleRadius, 3 * mBubbleRadius);

            mBubbleRectF.set(getMeasuredWidth() / 2f - mBubbleRadius, 0, getMeasuredWidth() / 2f + mBubbleRadius, 2 * mBubbleRadius);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            mBubblePath.reset();
            float x0 = getMeasuredWidth() / 2f;
            float y0 = getMeasuredHeight() - mBubbleRadius / 3f;
            mBubblePath.moveTo(x0, y0);
            float x1 = (float) (getMeasuredWidth() / 2f - Math.sqrt(3) / 2f * mBubbleRadius);
            float y1 = 3 / 2f * mBubbleRadius;
            mBubblePath.quadTo(x1 - BubbleUtils.dp2px(2), y1 - BubbleUtils.dp2px(2), x1, y1);
            mBubblePath.arcTo(mBubbleRectF, 150, 240);

            float x2 = (float) (getMeasuredWidth() / 2f + Math.sqrt(3) / 2f * mBubbleRadius);
            mBubblePath.quadTo(x2 + BubbleUtils.dp2px(2), y1 - BubbleUtils.dp2px(2), x0, y0);
            mBubblePath.close();

            mBubblePaint.setColor(mBubbleColor);
            canvas.drawPath(mBubblePath, mBubblePaint);

            mBubblePaint.setTextSize(mBubbleTextSize);
            mBubblePaint.setColor(mBubbleTextColor);
            mBubblePaint.getTextBounds(mProgressText, 0, mProgressText.length(), mRect);
            Paint.FontMetrics fm = mBubblePaint.getFontMetrics();
            float baseline = mBubbleRadius + (fm.descent - fm.ascent) / 2f - fm.descent;
            canvas.drawText(mProgressText, getMeasuredWidth() / 2f, baseline, mBubblePaint);
        }

        void setProgressText(String progressText) {
            if (progressText != null && !mProgressText.equals(progressText)) {
                mProgressText = progressText;
                invalidate();
            }
        }
    }

}
