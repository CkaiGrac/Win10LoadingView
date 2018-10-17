package com.exercise.ckaiself.win10styleloading;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.exercise.ckaiself.win10styleloadinglibrary.WinLoadingCircle;
import com.exercise.ckaiself.win10styleloadinglibrary.WinLoadingCircle_2;

public class WinLoadingCircle2Activity extends AppCompatActivity {

    private WinLoadingCircle_2 circle2;
    private LinearLayout ll_parnet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_loading_circle2);
        circle2 = findViewById( R.id.win10Loading);
        ll_parnet = findViewById(R.id.ll_parent);

        ll_parnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = ll_parnet.getChildCount();
                Toast.makeText(WinLoadingCircle2Activity.this,"count="+count,Toast.LENGTH_SHORT).show();
                for(int i=0;i<count;i++){
                    View view = ll_parnet.getChildAt(i);
                    if (view instanceof WinLoadingCircle_2) {
                        view.setVisibility(View.VISIBLE);
                        Toast.makeText(WinLoadingCircle2Activity.this,"已判断",Toast.LENGTH_SHORT).show();
                    }
                }
                //Toast.makeText(WinLoadingCircle2Activity.this,"clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
