package com.cn.wti.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.cn.wti.activity.tab.MyFragmentActivity;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.TipUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.app.version.UpdateService;
import com.wticn.wyb.wtiapp.R;

/**
 * Created by wyb on 2017/2/8.
 */

public class SplashActivity  extends Activity {

    //延迟3秒
    private static final long SPLASH_DELAY_MILLIS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /**
         * 检测系统更新
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
            //启动服务
            Intent service = new Intent(SplashActivity.this,UpdateService.class);
            service.putExtra("version", AppUtils.getVersion(SplashActivity.this));
            service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(service);
            }
        }).start();

        // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
        new Handler().postDelayed(new Runnable() {
            public void run() {
                goHome();
            }
        }, SPLASH_DELAY_MILLIS);

        if(!this.isTaskRoot()){
            finish();
            return;
        }
    }

    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
}
