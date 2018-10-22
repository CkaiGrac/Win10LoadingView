package com.exercise.ckaiself.win10styleloading;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mBtToWinLoading_1;
    private Button mBtToWinLoading_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        mBtToWinLoading_1 = findViewById(R.id.toWinLoading_1);
        mBtToWinLoading_2 = findViewById(R.id.toWinLoading_2);


        mBtToWinLoading_1.setOnClickListener(this);
        mBtToWinLoading_2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toWinLoading_1:
                startActivity(new Intent(MainActivity.this, WinLoadingCircleActivity.class));
                break;
            case R.id.toWinLoading_2:
                startActivity(new Intent(MainActivity.this, WinLoadingLinearActivity.class));
                break;
        }
    }
}
