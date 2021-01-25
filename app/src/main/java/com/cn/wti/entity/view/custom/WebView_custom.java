package com.cn.wti.entity.view.custom;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

import java.lang.reflect.Method;

/**
 * Created by wyb on 2017/7/12.
 */

public class WebView_custom extends WebView{

    private ZoomButtonsController zoom_controll;

    public WebView_custom(Context context) {
        super(context);
    }

    public WebView_custom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebView_custom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WebView_custom(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }


    /**
     * Disable the controls
     */
    private void disableControls() {
        WebSettings settings = this.getSettings();
        //基本的设置
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);//support zoom
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= 8) {
            settings.setPluginState(WebSettings.PluginState.ON);
        }
        this.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        //去掉滚动条
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);

        //去掉缩放按钮
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Use the API 11+ calls to disable the controls
            this.getSettings().setBuiltInZoomControls(true);
            this.getSettings().setDisplayZoomControls(false);
        } else {
            // Use the reflection magic to make it work on earlier APIs
            getControlls();
        }
    }

    /**
     * This is where the magic happens :D
     */
    private void getControlls() {
        try {
            Class webview = Class.forName("android.webkit.WebView");
            Method method = webview.getMethod("getZoomButtonsController");
            zoom_controll = (ZoomButtonsController) method.invoke(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
