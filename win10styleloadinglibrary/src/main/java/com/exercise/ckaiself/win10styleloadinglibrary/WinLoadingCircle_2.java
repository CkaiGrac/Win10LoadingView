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
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ckai
 * @date 2018/10/3 下午5:42
 */
public class WinLoadingCircle_2 extends RelativeLayout {

    private static String TAG = "WinLoadingCircle";

    //此控件的宽
    private int mWidth;
    //此控件的高
    private int mHeight;
    private float dotDegree;
    private View[] mDotViews = new View[6];
    private AnimatorSet mAnimSet;


    public WinLoadingCircle_2(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public WinLoadingCircle_2(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "WinLoadingCircle_2: ++++++++++++++++++++++++++++++++++++++++++++++++");
        setWillNotDraw(false);
    }

    public WinLoadingCircle_2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
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
            initAnim();
            if (getVisibility() == VISIBLE) {
                if (mAnimSet != null) {
                    mAnimSet.start();
                }
            }
        }

    }

    private void initAnim() {
        float halfSize = mWidth < mHeight ? (mWidth * .5f) : (mHeight * .5f);
        float dotR = halfSize / 12;
        int dotD = (int) (2 * dotR);

        float trackR = halfSize - dotR;
        dotDegree = (float) Math.toDegrees(2 * Math.asin(dotR / trackR));

        removeAllViews();


        for (int i = 0; i < mDotViews.length; i++) {
            mDotViews[i] = new View(getContext());

            View view = mDotViews[i];
            LayoutParams lp = new LayoutParams(dotD, dotD);
            lp.addRule(ALIGN_PARENT_BOTTOM);
            lp.addRule(CENTER_HORIZONTAL);

            if (mHeight > mWidth) {
                lp.bottomMargin = (mHeight - mWidth) / 2;
            }

            view.setPivotX(dotR);
            view.setPivotY(-(halfSize - dotD));

            view.setBackgroundResource(R.drawable.loading_dot);

            GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
            gradientDrawable.setColor(Color.rgb(0x92, 0xcb, 0x29));
            view.setVisibility(INVISIBLE);
            addView(view, lp);
        }

        mAnimSet = new AnimatorSet();

        List<Animator> animList = new ArrayList<>(6);
        for (int i = 0; i < mDotViews.length; i++) {
            animList.add(createViewAnim(mDotViews[i], i));
        }
        mAnimSet.playTogether(animList);
        mAnimSet.setStartDelay(800);
    }

    private Animator createViewAnim(final View view, final int index) {
        long duration = 5000;
        final float minRunUnit = duration / 100f;
        double minRunPer = minRunUnit / duration;
        final float offset = (float) (index * (100 - 83) * minRunPer / (mDotViews.length - 1));

        final double[] mInterpolatorY = new double[]{
                0,
                0,
                165 / 720d,
                205 / 720d,
                360 / 720d,
                525 / 720d - offset*0.41,
                565 / 720d - offset*0.41,
                1
        };

        final double[] mInterpolatorX = new double[]{
                0,
                offset + 0,
                offset + 11 * minRunPer,
                offset + 31 * minRunPer,
                offset + 42 * minRunPer,
                offset + 53 * minRunPer,
                offset + 73 * minRunPer,
                offset + 84 * minRunPer
        };
        final float mControlPointY_1 = calculateLineY(mInterpolatorX[2], mInterpolatorY[2], mInterpolatorX[3], mInterpolatorY[3], mInterpolatorX[1]);
        final float mControlPointY_2 = calculateLineY(mInterpolatorX[2], mInterpolatorY[2], mInterpolatorX[3], mInterpolatorY[3], mInterpolatorX[4]);
        final float mControlPointY_3 = calculateLineY(mInterpolatorX[5], mInterpolatorY[5], mInterpolatorX[6], mInterpolatorY[6], mInterpolatorX[4]);
        final float mControlPointY_4 = calculateLineY(mInterpolatorX[5], mInterpolatorY[5], mInterpolatorX[6], mInterpolatorY[6], mInterpolatorX[7]);

        ObjectAnimator mAnimator = ObjectAnimator.ofFloat(view, "rotation", -dotDegree * index, 720 + dotDegree * index);

        // B 设置一个周期执行的时间
        mAnimator.setDuration(duration);
        // C 设置重复执行的次数：无限次重复执行下去
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        // D 设置差值器
        mAnimator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                if (input < mInterpolatorX[1]) {
                    // 1 等待开始
                    if (view.getVisibility() != INVISIBLE) {
                        view.setVisibility(INVISIBLE);
                    }
                    return 0;

                } else if (input < mInterpolatorX[2]) {
                    if (view.getVisibility() != VISIBLE) {
                        view.setVisibility(VISIBLE);
                    }
                    // 2 底部 → 左上角：贝赛尔曲线1
                    // 先转换成[0, 1]范围
                    input = calculateNewPercent(mInterpolatorX[1], mInterpolatorX[2], 0, 1, input);
                    return calculateBezierQuadratic(mInterpolatorY[1], mControlPointY_1, mInterpolatorY[2], input);

                } else if (input < mInterpolatorX[3]) {
                    // 3 左上角 → 顶部：直线
                    return calculateLineY(mInterpolatorX[2], mInterpolatorY[2], mInterpolatorX[3], mInterpolatorY[3], input);

                } else if (input < mInterpolatorX[4]) {
                    // 4 顶部 → 底部：贝赛尔曲线2
                    input = calculateNewPercent(mInterpolatorX[3], mInterpolatorX[4], 0, 1, input);
                    return calculateBezierQuadratic(mInterpolatorY[3], mControlPointY_2, mInterpolatorY[4], input);

                } else if (input < mInterpolatorX[5]) {
                    // 5 底部 → 左上角：贝赛尔曲线3
                    input = calculateNewPercent(mInterpolatorX[4], mInterpolatorX[5], 0, 1, input);
                    return calculateBezierQuadratic(mInterpolatorY[4], mControlPointY_3, mInterpolatorY[5], input);

                } else if (input < mInterpolatorX[6]) {
                    // 6 左上角 → 顶部：直线
                    return calculateLineY(mInterpolatorX[5], mInterpolatorY[5], mInterpolatorX[6], mInterpolatorY[6], input);

                } else if (input < mInterpolatorX[7]) {
                    // 7 顶部 → 底部：贝赛尔曲线4
                    input = calculateNewPercent(mInterpolatorX[6], mInterpolatorX[7], 0, 1, input);
                    return calculateBezierQuadratic(mInterpolatorY[6], mControlPointY_4, mInterpolatorY[7], input);

                } else {
                    // 8 消失
                    if (view.getVisibility() != INVISIBLE) {
                        view.setVisibility(INVISIBLE);
                    }
                    return 1;
                }
            }
        });

        return mAnimator;
    }



    /*

    private class LoadingDot extends RelativeLayout {

        private float mDotR;
        private float mDotD;
        private float halfSize;
        private View[] mDots = new View[6];
        private float dotDegree;
        private AnimatorSet mAnimSet;


        public LoadingDot(Context context) {
            super(context);
            initView();
            Log.d(TAG, "LoadingDot:+++++++++++++++++++++++++++++++++++++++++++++++++++++++++  ");
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

        private void initView() {

            halfSize = mWidth < mHeight ? mWidth * .5f : mHeight * .5f;
            mDotR = halfSize / 12;
            mDotD = mDotR * 2;
            float trackR = halfSize - mDotR;
            dotDegree = (float) Math.toDegrees(2 * Math.asin(mDotR / trackR));


            for (int i = 0; i < mDots.length; i++) {
                mDots[i] = new View(getContext());
                View dot = mDots[i];
                LayoutParams lp = new LayoutParams((int) mDotD, (int) mDotD);
                lp.addRule(ALIGN_PARENT_BOTTOM);
                lp.addRule(CENTER_HORIZONTAL);
                if (mHeight > mWidth) {
                    lp.bottomMargin = (mHeight - mWidth) / 2;
                }
                dot.setPivotX(mDotR);
                dot.setPivotY(-(halfSize - mDotD));
                dot.setBackgroundResource(R.drawable.loading_dot);
                GradientDrawable gradientDrawable = (GradientDrawable) dot.getBackground();
                gradientDrawable.setColor(Color.rgb(0x92, 0xcb, 0x29));
                dot.setVisibility(INVISIBLE);
                addView(dot, lp);
                removeAllViews();
            }
            mAnimSet = new AnimatorSet();
            List<Animator> animList = new ArrayList<>();
            for (int i = 0; i < mDots.length; i++) {
                animList.add(createViewAnim(mDots[i], i));
            }
            mAnimSet.setStartDelay(200);
            mAnimSet.playTogether(animList);

            if (getVisibility() == VISIBLE) {
                if (mAnimSet != null) {
                    mAnimSet.start();
                }
            }
        }


        private Animator createViewAnim(final View view, final int index) {
            long duration = 5000;
            //最小执行单位时间
            final float minRunUnit = duration / 100f;
            //最小执行单位时间占总时间的比例
            double minRunPer = minRunUnit / duration;
            final float offset = (float) (index * (100 - 86) * minRunPer / (mDots.length - 1));

            ObjectAnimator mAnimator = ObjectAnimator.ofFloat(view, "rotation", -dotDegree * index, 720 - dotDegree * index);
            mAnimator.setDuration(duration);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setInterpolator(new MyTimeInterpolator(view, offset));
            return mAnimator;
        }

        private View[] getDot() {
            return mDots;
        }

    }
    */


    private float calculateNewPercent(double oldStart, double oldEnd, double newStart, double newEnd, double value) {
        if ((value < oldStart && value < oldEnd) || (value > oldStart && value > oldEnd)) {
            throw new IllegalArgumentException(String.format("参数输入错误，value必须在[%f, %f]范围中", oldStart, oldEnd));
        }
        return (float) ((value - oldStart) * (newEnd - newStart) / (oldEnd - oldStart));
    }

    private float calculateLineY(double x1, double y1, double x2, double y2, double x) {
        if (x1 == x2) {
            return (float) y1;
        }
        return (float) ((x - x1) * (y2 - y1) / (x2 - x1) + y1);
    }

    private float calculateBezierQuadratic(double p0, double p1, double p2, @FloatRange(from = 0, to = 1) double t) {
        double tmp = 1 - t;
        return (float) (tmp * tmp * p0 + 2 * tmp * t * p1 + t * t * p2);
    }
}
