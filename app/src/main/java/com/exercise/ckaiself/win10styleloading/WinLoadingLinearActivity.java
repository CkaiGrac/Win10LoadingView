package com.exercise.ckaiself.win10styleloading;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.exercise.ckaiself.win10styleloadinglibrary.WinLoadingLinear;

public class WinLoadingLinearActivity extends AppCompatActivity {

    private Button mBtShow,mBtStop;
    private WinLoadingLinear loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_loading_linear);

        mBtShow=findViewById(R.id.btShow);
        mBtStop=findViewById(R.id.btStop);
        loading=findViewById(R.id.loadingLinear);


        mBtStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.hide();
            }
        });

        mBtShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.show();
            }
        });
    }
}
