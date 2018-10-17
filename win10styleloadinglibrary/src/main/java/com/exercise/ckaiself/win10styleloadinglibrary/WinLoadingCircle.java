package com.exercise.ckaiself.win10styleloadinglibrary;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
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
 * @date 2018/10/3 上午10:04
 */
public class WinLoadingCircle extends RelativeLayout {
    private static String TAG = "WinLoadingCircle";
    //加载dot颜色
    private int dotColor;
    //dot父控件的宽
    private int mWidth;
    //dot父控件的高
    private int mHeight;
    //一个dot距旋转中心所占的角度
    private float dotDegree;
    //用于存放加载dot的数组
    private View[] mDotViews = new View[5];
    //批量执行动画
    private AnimatorSet mAnimSet;

    public WinLoadingCircle(Context context) {
        super(context);
    }

    public WinLoadingCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "WinLoadingCircle: ++++++++++++++++++++++++++++++++++++++++++++++++");
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WinLoadingCircle);
        // 点的默认颜色是Win10中的绿色
        dotColor = ta.getColor(R.styleable.WinLoadingCircle_dotColor, Color.rgb(0x92, 0xcb, 0x29));
        ta.recycle();
    }

    public WinLoadingCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Log.d(TAG, "WinLoadingCircle: ++++++++++++++++++++++++++++++++++++++++++++++++");
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WinLoadingCircle);
        // 点的默认颜色是Win10中的绿色
        dotColor = ta.getColor(R.styleable.WinLoadingCircle_dotColor, Color.rgb(0x92, 0xcb, 0x29));
        ta.recycle();
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
    protected void onDisplayHint(int hint) {
        super.onDisplayHint(hint);
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
        //取加载dot父控件宽或高的一半，用于适配dot的大小
        float halfSize = mWidth < mHeight ? (mWidth * .5f) : (mHeight * .5f);
        //dot的半径
        float dotR = halfSize / 10;
        //dot的直径
        int dotD = (int) (dotR * 2);

        //计算一个dot距旋转中心所占的角度
        float trackR = halfSize - dotR;
        dotDegree = (float) Math.toDegrees(2 * Math.asin(dotR / trackR));
        Log.d(TAG, "dotDegree = " + dotDegree);

        removeAllViews();

        //添加新控件
        for (int i = 0; i < mDotViews.length; i++) {
            mDotViews[i] = new View(getContext());
            View view = mDotViews[i];
            LayoutParams lp = new LayoutParams(dotD, dotD);
            //添加规则，居底部水平居中
            lp.addRule(ALIGN_PARENT_BOTTOM);
            lp.addRule(CENTER_HORIZONTAL);
            //父控件宽高不统一时，调整dot的位置
            if (mHeight > mWidth) {
                lp.bottomMargin = (mHeight - mWidth) / 2;
            }
            /**
             * 设置dot的旋转中心
             * 注意，是以当前这个view为坐标系，也就是说view的左上角即为（0,0）位置
             * 因为dot的旋转中心在dot的上方。
             * 所以view.setPivotY的值是负的。
             */
            view.setPivotX(dotR);
            view.setPivotY(-(halfSize - dotD));
            //设置dot的形状
            view.setBackgroundResource(R.drawable.loading_dot);
            //设置dot的颜色
            GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
            gradientDrawable.setColor(dotColor);
            view.setVisibility(INVISIBLE);
            addView(view, lp);

        }
        //初始化动画
        initAnimSet();
    }

    private void initAnimSet() {
        mAnimSet = new AnimatorSet();

        List<Animator> animList = new ArrayList<>(5);
        for (int i = 0; i < mDotViews.length; i++) {
            animList.add(createViewAnim(mDotViews[i], i));
        }
        mAnimSet.playTogether(animList);
    }

    private Animator createViewAnim(final View view, final int index) {
        //动画执行周期
        long duration = 5000;

        //最小执行单位时间
        final float minRunUnit = duration / 100f;
        //最小执行单位时间占总时间的比例
        double minRunPer = minRunUnit / duration;
        //在插值器中实际值 Y坐标
        final double[] realRunInOne = new double[]{
                0,
                0,
                160 / 720d,
                195 / 720d,
                360 / 720d,
                520 / 720d,
                555 / 720d,
                1
        };
        //动画开始的时间比偏移量。剩下的时间均摊到每个dot上
        final float offset = (float) (index * (100 - 87) * minRunPer / (mDotViews.length - 1));
        //在插值器中理论值，X坐标，与realRunInOne对应e
        final double[] rawRunInOne = new double[]{
                0,
                offset + 0,
                offset + 11 * minRunPer,
                offset + 32 * minRunPer,
                offset + 43 * minRunPer,
                offset + 54 * minRunPer,
                offset + 75 * minRunPer,
                offset + 86 * minRunPer
        };


        Log.d(TAG, "minRunUnit=" + minRunUnit + ", minRunPer=" + minRunPer + ", offset=" + offset);

        //各贝塞尔曲线控制点的Y坐标
        final float p1_2 = calculateLineY(rawRunInOne[2], realRunInOne[2], rawRunInOne[3], realRunInOne[3], rawRunInOne[1]);
        final float p1_4 = calculateLineY(rawRunInOne[2], realRunInOne[2], rawRunInOne[3], realRunInOne[3], rawRunInOne[4]);
        final float p1_5 = calculateLineY(rawRunInOne[5], realRunInOne[5], rawRunInOne[6], realRunInOne[6], rawRunInOne[4]);
        final float p1_7 = calculateLineY(rawRunInOne[5], realRunInOne[5], rawRunInOne[6], realRunInOne[6], rawRunInOne[7]);

        //创建属性动画，绕着中心点旋转两圈
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", -dotDegree * index, 720 - index * dotDegree);
        //设置动画执行周期
        objectAnimator.setDuration(duration);
        //设置重复执行的次数
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //设置插值器
        objectAnimator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                if (input < rawRunInOne[1]) {
                    // 1 等待开始
                    if (view.getVisibility() != INVISIBLE) {
                        view.setVisibility(INVISIBLE);
                    }
                    return 0;

                } else if (input < rawRunInOne[2]) {
                    if (view.getVisibility() != VISIBLE) {
                        view.setVisibility(VISIBLE);
                    }
                    // 2 底部 → 左上角：贝赛尔曲线1
                    // 先转换成[0, 1]范围
                    input = calculateNewPercent(rawRunInOne[1], rawRunInOne[2], 0, 1, input);
                    return calculateBezierQuadratic(realRunInOne[1], p1_2, realRunInOne[2], input);

                } else if (input < rawRunInOne[3]) {
                    // 3 左上角 → 顶部：直线
                    return calculateLineY(rawRunInOne[2], realRunInOne[2], rawRunInOne[3], realRunInOne[3], input);

                } else if (input < rawRunInOne[4]) {
                    // 4 顶部 → 底部：贝赛尔曲线2
                    input = calculateNewPercent(rawRunInOne[3], rawRunInOne[4], 0, 1, input);
                    return calculateBezierQuadratic(realRunInOne[3], p1_4, realRunInOne[4], input);

                } else if (input < rawRunInOne[5]) {
                    // 5 底部 → 左上角：贝赛尔曲线3
                    input = calculateNewPercent(rawRunInOne[4], rawRunInOne[5], 0, 1, input);
                    return calculateBezierQuadratic(realRunInOne[4], p1_5, realRunInOne[5], input);

                } else if (input < rawRunInOne[6]) {
                    // 6 左上角 → 顶部：直线
                    return calculateLineY(rawRunInOne[5], realRunInOne[5], rawRunInOne[6], realRunInOne[6], input);

                } else if (input < rawRunInOne[7]) {
                    // 7 顶部 → 底部：贝赛尔曲线4
                    input = calculateNewPercent(rawRunInOne[6], rawRunInOne[7], 0, 1, input);
                    return calculateBezierQuadratic(realRunInOne[6], p1_7, realRunInOne[7], input);

                } else {
                    // 8 消失
                    if (view.getVisibility() != INVISIBLE) {
                        view.setVisibility(INVISIBLE);
                    }
                    return 1;
                }
            }
        });
        return objectAnimator;
    }

    /**
     * 根据旧范围，给定旧值，计算新范围中的值
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
     * 根据两点坐标形成的直线，计算给定X坐标在直线上对应的Y坐标值
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
     * 计算二阶贝塞尔曲线的X（或Y）的坐标值
     * 给定起点、控制点、终点的X（或Y）坐标值，和给定时间t 在[0，1]区间内
     * 算出此时贝塞尔曲线的X（或Y）的坐标值
     *
     * @param p0 起点值
     * @param p1 控制点
     * @param p2 终点
     * @param t  给定时间
     * @return 曲线的位置值
     */
    private float calculateBezierQuadratic(double p0, double p1, double p2, @FloatRange(from = 0, to = 1) double t) {
        double tmp = 1 - t;
        return (float) (tmp * tmp * p0 + 2 * tmp * t * p1 + t * t * p2);
    }

    /**
     * 三阶贝塞尔曲线
     *
     * @param p0 起点
     * @param p1 控制点1
     * @param p2 控制点2
     * @param p3 终点
     * @param t  给定的时间
     * @return 曲线对应坐标值
     */
    private float calculateBezierCubic(double p0, double p1, double p2, double p3, @FloatRange(from = 0, to = 1) double t) {
        double tmp = 1 - t;
        return (float) (tmp * tmp * tmp * p0 + 3 * tmp * tmp * t * p1 + 3 * tmp * t * t * p2 + t * t * t * p3);
    }

    public int getDotColor() {
        return dotColor;
    }

    public synchronized void setDotColor(int dotColor) {
        this.dotColor = dotColor;
        for (View view : mDotViews) {
            GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
            gradientDrawable.setColor(dotColor);
        }
    }

}
