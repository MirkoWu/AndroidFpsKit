package com.mirkowu.androidfpskit;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;

import androidx.annotation.RequiresApi;

/**
 * Android 4.1 之后，Google 才将VSYNC引入到 Android 显示系统中
 * 参考 didi 检测工具doraemonkit
 * https://github.com/didi/DoraemonKit/blob/master/Android/java/doraemonkit/src/main/java/com/didichuxing/doraemonkit/kit/performance/PerformanceDataManager.java
 */
public class FpsMonitor {

    private final int FPS_INTERVAL_TIME = 1000;//采集时间
    private int fps = 0;//fps计数
    private boolean isOpenFps = false;//是否打开
    private FpsListener fpsListener;
    private FpsRunnable fpsRunnable = new FpsRunnable();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private static class Singleton {
        private static final FpsMonitor INSTANCE = new FpsMonitor();
    }

    public static FpsMonitor getInstance() {
        return Singleton.INSTANCE;
    }

    private FpsMonitor() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startMonitor(FpsListener listener) {
        if (!isOpenFps) {
            isOpenFps = true;
            fpsListener = listener;
            mainHandler.postDelayed(fpsRunnable, FPS_INTERVAL_TIME);
            Choreographer.getInstance().postFrameCallback(fpsRunnable);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopMonitor() {
        fps = 0;
        fpsListener = null;
        if (mainHandler != null) {
            mainHandler.removeCallbacks(fpsRunnable);
        }
        if (fpsRunnable != null) {
            Choreographer.getInstance().removeFrameCallback(fpsRunnable);
        }

        isOpenFps = false;
    }

    public boolean isOpenFps() {
        return isOpenFps;
    }

    private class FpsRunnable implements Choreographer.FrameCallback, Runnable {

        @Override
        public void doFrame(long frameTimeNanos) {
            fps++;
            Choreographer.getInstance().postFrameCallback(this);
        }

        @Override
        public void run() {
            if (fpsListener != null) {
                fpsListener.onFps(fps);
            }
            fps = 0;//重置
            mainHandler.postDelayed(this, FPS_INTERVAL_TIME);
        }
    }

    public static interface FpsListener {
        void onFps(int fps);
    }
}
