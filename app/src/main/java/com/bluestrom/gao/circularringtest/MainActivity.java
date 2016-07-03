package com.bluestrom.gao.circularringtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bluestrom.gao.customview.CircularRingPercentageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int DECLINE_PROGRESS = 1;

    private CircularRingPercentageView arcProgress1, arcProgress2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arcProgress1 = (CircularRingPercentageView) findViewById(R.id.circle1);
        arcProgress2 = (CircularRingPercentageView) findViewById(R.id.circle2);
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DECLINE_PROGRESS:
                    int currentProgress = arcProgress1.getProgress();
                    if (currentProgress > 0) {
                        currentProgress = currentProgress - 8;
                        arcProgress1.setProgress(currentProgress);
                        arcProgress2.setProgress(currentProgress);
                        uiHandler.sendEmptyMessageDelayed(DECLINE_PROGRESS, 50);//n毫秒改变一次
                    } else {
                        uiHandler.removeMessages(DECLINE_PROGRESS);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                arcProgress1.setProgress((int) (Math.random() * 101));
                arcProgress2.setProgress((int) (Math.random() * 101));
                uiHandler.removeMessages(DECLINE_PROGRESS);
                uiHandler.sendEmptyMessageDelayed(DECLINE_PROGRESS, 100);//n毫秒改变一次
                break;
            case R.id.change:
                arcProgress1.setProgress((int) (Math.random() * 101));
                arcProgress2.setProgress((int) (Math.random() * 101));
                break;
            default:
                break;
        }
    }
}
