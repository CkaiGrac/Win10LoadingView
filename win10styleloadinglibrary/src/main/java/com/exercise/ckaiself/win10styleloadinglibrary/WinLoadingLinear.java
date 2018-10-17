package com.exercise.ckaiself.win10styleloadinglibrary;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ckai
 * @date 2018/10/6 下午1:23
 */
public class WinLoadingLinear extends RelativeLayout {

    /**
     * the width of this view
     */
    private int mWidth;
    /**
     * the height of this view
     */
    private int mHeight;

    /**
     * loading dots array
     */
    private View[] mDotViews = new View[6];
    /**
     * dot's width
     */
    private int mDotWidth;
    /**
     * dot's height
     */
    private int mDotHeight;

    private AnimatorSet mAnimSet;


    public WinLoadingLinear(Context context) {
        super(context);
    }

    public WinLoadingLinear(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WinLoadingLinear(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mAnimSet != null) {
            if (visibility == VISIBLE) {
                if (!mAnimSet.isStarted()) {
                    mAnimSet.start();
                }
            } else {
                mAnimSet.cancel();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mWidth != w || mHeight != h) {
            mWidth = w;
            mHeight = h;
        }
    }


    private void initAnim() {

        mDotWidth = (int) getResources().getDimension(R.dimen.dot_dimension);
        mDotHeight = (int) getResources().getDimension(R.dimen.dot_dimension);

        removeAllViews();

        for (int i = 0; i < mDotViews.length; i++) {
            mDotViews[i] = new View(getContext());
            View view = mDotViews[i];
            LayoutParams lp = new LayoutParams(mDotWidth, mDotHeight);
            //垂直居中
            lp.addRule(CENTER_VERTICAL);
            //左对齐
            lp.addRule(ALIGN_PARENT_LEFT);
            //set dots shape
            view.setBackgroundResource(R.drawable.loading_dot_square);
            //set dots color
            GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
            gradientDrawable.setColor(Color.rgb(0x92, 0xcb, 0x29));
            view.setVisibility(INVISIBLE);
            addView(view, lp);
        }

        mAnimSet = new AnimatorSet();

        List<Animator> animList = new ArrayList<>(5);
        for (int i = 0; i < mDotViews.length; i++) {
            animList.add(createViewAnim(mDotViews[i], i));
        }
        mAnimSet.playTogether(animList);
    }

    /**
     * @param view  dot
     * @param index
     * @return ObjectAnimator
     */
    private Animator createViewAnim(final View view, final int index) {
        //animation duration
        long duration = 5000;
        //Minimum execution unit time as a percentage of total time
        double minTimePercentage = duration / 100f / duration;
        //dot offset
        final float offset = (float) (index * (100 - 88) * minTimePercentage / (mDotViews.length - 1));
        //Y-axis coordinate in Interpolator
        final double[] mInterpolatorY = new double[]{
                0,
                33 / 100d,
                58 / 100d,
                1
        };
        //X-axis coordinate in Interpolator
        final double[] mInterpolatorX = new double[]{
                0 + offset,
                8 * minTimePercentage + offset,
                30 * minTimePercentage + offset,
                38 * minTimePercentage + offset,
        };

        //control point of Bezier (dots accelerate to come in)
        final float mControlPointY_1 = calculateLineY(mInterpolatorX[1], mInterpolatorY[1], mInterpolatorX[2], mInterpolatorY[2], mInterpolatorX[0]);
        //control point of Bezier (dots accelerate to go out)
        final float mControlPointY_2 = calculateLineY(mInterpolatorX[1], mInterpolatorY[1], mInterpolatorX[2], mInterpolatorY[2], mInterpolatorX[3]);

        ObjectAnimator mAnimator = ObjectAnimator.ofFloat(view, "translationX", -mDotWidth * index, mWidth + mDotWidth * index);
        mAnimator.setDuration(duration);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                if (input < mInterpolatorX[0]) {
                    if (view.getVisibility() != INVISIBLE) {
                        view.setVisibility(INVISIBLE);
                    }
                    return 0;
                } else if (input < mInterpolatorX[1]) {
                    // the first part: dots accelerate to come in
                    if (view.getVisibility() != VISIBLE) {
                        view.setVisibility(VISIBLE);
                    }
                    input = calculateNewPercent(mInterpolatorX[0], mInterpolatorX[1], 0, 1, input);
                    return calculateBezierQuadratic(mInterpolatorY[0], mControlPointY_1, mInterpolatorY[1], input);
                } else if (input < mInterpolatorX[2]) {
                    // the second part: dots to do uniform motion
                    return calculateLineY(mInterpolatorX[1], mInterpolatorY[1], mInterpolatorX[2], mInterpolatorY[2], input);
                } else if (input < mInterpolatorX[3]) {
                    // the third part: dots accelerate to go out
                    input = calculateNewPercent(mInterpolatorX[2], mInterpolatorX[3], 0, 1, input);
                    return calculateBezierQuadratic(mInterpolatorY[2], mControlPointY_2, mInterpolatorY[3], input);
                } else {
                    // the last part: dots complete animation and come invisible
                    if (view.getVisibility() != INVISIBLE) {
                        view.setVisibility(INVISIBLE);
                    }
                }
                return 1;
            }
        });
        return mAnimator;
    }

    /**
     * calculate a value in a new range base on the old range
     *
     * @param oldStart
     * @param oldEnd
     * @param newStart
     * @param newEnd
     * @param value
     * @return
     */
    private float calculateNewPercent(double oldStart, double oldEnd, double newStart, double newEnd, double value) {
        if ((value < oldStart && value < oldEnd) || (value > oldStart && value > oldEnd)) {
            throw new IllegalArgumentException(String.format("参数输入错误，value必须在[%f, %f]范围中", oldStart, oldEnd));
        }
        return (float) ((value - oldStart) * (newEnd - newStart) / (oldEnd - oldStart));
    }

    /**
     * Linear two-point equation
     * 直线的两点式方程
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x
     * @return
     */
    private float calculateLineY(double x1, double y1, double x2, double y2, double x) {
        if (x1 == x2) {
            return (float) y1;
        }
        return (float) ((x - x1) * (y2 - y1) / (x2 - x1) + y1);
    }

    /**
     * calculate the value of the Bezier curve at a certain moment
     *
     * @param p0
     * @param p1
     * @param p2
     * @param t
     * @return
     */
    private float calculateBezierQuadratic(double p0, double p1, double p2, @FloatRange(from = 0, to = 1) double t) {
        double tmp = 1 - t;
        return (float) (tmp * tmp * p0 + 2 * tmp * t * p1 + t * t * p2);
    }

    /**
     * show loading animation
     */
    public void show() {
        if (getVisibility() != VISIBLE) {
            initAnim();
            if (getVisibility() == INVISIBLE) {
                setVisibility(VISIBLE);
                if (!mAnimSet.isStarted()) {
                    mAnimSet.start();
                }
            } else {
                mAnimSet.cancel();
            }
        }
    }

    /**
     * hide loading animation
     */
    public void hide() {
        if (mAnimSet != null) {
            if (getVisibility() != INVISIBLE) {
                setVisibility(INVISIBLE);
            }
            if (mAnimSet.isStarted()) {
                mAnimSet.cancel();
            }
            removeAllViews();
        }
    }

}
