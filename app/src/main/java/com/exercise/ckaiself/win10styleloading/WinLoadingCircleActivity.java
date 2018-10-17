package com.exercise.ckaiself.win10styleloading;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.exercise.ckaiself.win10styleloadinglibrary.WinLoadingCircle;

import java.util.Random;

public class WinLoadingCircleActivity extends AppCompatActivity {
    private LinearLayout ll_parent;
    private Button btn_start;
    private WinLoadingCircle win10Loading;
    private int[] mDotColors = {Color.RED, Color.GREEN, Color.BLUE, Color.BLACK, Color.CYAN, Color.YELLOW};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_loading_circle);

        initView();
    }
    private void initView() {
        ll_parent = findViewById(R.id.ll_parent);
        win10Loading = findViewById(R.id.win10Loading);
        btn_start = findViewById(R.id.btn_start);

        stopAnim();
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_color:
                win10Loading.setDotColor(mDotColors[new Random().nextInt(mDotColors.length)]);
                break;
            case R.id.btn_start:
                startOrStopAnim();
                break;
        }
    }

    private void startOrStopAnim() {
        if ("开始".equals(btn_start.getText().toString())) {
            btn_start.setText("停止");
            startAnim();
        } else {
            btn_start.setText("开始");
            stopAnim();
        }
    }

    private void startAnim() {
        int count = ll_parent.getChildCount();
        Toast.makeText(WinLoadingCircleActivity.this,"count="+count,Toast.LENGTH_SHORT).show();

        for (int i = 0; i < count; i++) {
            View view = ll_parent.getChildAt(i);
            if (view instanceof WinLoadingCircle) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }


    private void stopAnim() {
        int count = ll_parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = ll_parent.getChildAt(i);
            if (view instanceof WinLoadingCircle) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

}
