package com.cn.wti.util.app;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseActivity;

/**
 * @author : Sxf
 * @date : 2018/1/15
 */

public class ToastUtils {
    private static Runnable runnable;
    private static Toast toast;
    private static long lastToastTime;

    public static void showToast(final Context mContext, final String text) {
        if (lastToastTime == 0) {
            lastToastTime = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - lastToastTime < 2000) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            final Handler handler = new Handler(handlerThread.getLooper());
            if (runnable != null) {
                runnable = null;
            }
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
                    toast.show();
                    //销毁线程
                    handler.removeCallbacks(runnable);
                }
            };
            handler.post(runnable);
        } else {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
