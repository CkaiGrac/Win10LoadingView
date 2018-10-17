package com.exercise.ckaiself.win10styleloading;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mBtToWinLoading_2;
    private Button mBtToWinLoading_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        mBtToWinLoading_2 = findViewById(R.id.toWinLoading_2);
        mBtToWinLoading_3 = findViewById(R.id.toWinLoading_3);

        mBtToWinLoading_2.setOnClickListener(this);
        mBtToWinLoading_3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toWinLoading_2:
                startActivity(new Intent(MainActivity.this, WinLoadingCircle2Activity.class));
                break;
            case R.id.toWinLoading_3:
                startActivity(new Intent(MainActivity.this, WinLoadingLinearActivity.class));
                break;
        }
    }
}
