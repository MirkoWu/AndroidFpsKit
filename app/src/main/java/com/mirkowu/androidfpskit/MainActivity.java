package com.mirkowu.androidfpskit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tvFps;
    LimitedQueue<Integer> fpsRecord = new LimitedQueue<>(8);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvFps = findViewById(R.id.tvFps);
    }


    public void startMonitor(View view) {
        FpsMonitor.getInstance().startMonitor(new FpsMonitor.FpsListener() {
            @Override
            public void onFps(int fps) {
                fpsRecord.add(fps);
                tvFps.setText(String.format("Fps：%s", fpsRecord.toString()));
            }
        });
    }

    public void stopMonitor(View view) {
        FpsMonitor.getInstance().stopMonitor();
        tvFps.setText("已停止");
    }

    public void performBlockClick(View view) {
        try {
            Thread.sleep(200);//主线程睡眠，模拟卡顿任务
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void performBlockClick2(View view) {
        try {
            Thread.sleep(500);//主线程睡眠，模拟卡顿任务
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FpsMonitor.getInstance().stopMonitor();
    }
}