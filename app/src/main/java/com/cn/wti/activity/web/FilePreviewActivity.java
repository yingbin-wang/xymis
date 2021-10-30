package com.cn.wti.activity.web;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cn.wti.entity.System_one;
import com.cn.wti.util.app.AppUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.Map;

/**
 * Created by wyb on 2017/2/8.
 */

public class FilePreviewActivity extends Activity {

    private WebView wv;
    private Map<String,Object> parmsMap;
    private ImageButton iv_back;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();

        tv_title = (TextView) findViewById(R.id.title_name2);
        iv_back = (ImageButton) findViewById(R.id.title_back2);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePreviewActivity.this.finish();
            }
        });

        wv = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
        //自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //自动缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        //支持获取手势焦点
        wv.requestFocusFromTouch();

        String url = "";
        if (AppUtils.isIp(AppUtils.app_address)){
            url = "http://"+AppUtils.app_address+":8080/menu/previewFile?fileName="+parmsMap.get("fileName")+"&filePath="+"http://"+AppUtils.file_address+"/"+parmsMap.get("filePath");
        }else{
            url = "http://"+AppUtils.app_address+"/menu/previewFile?fileName="+parmsMap.get("fileName")+"&v_="+AppUtils.user.get_version()+
                    "&filePath="+"http://"+AppUtils.app_address+"/menu/previewFile2?newfilename="+parmsMap.get("filePath")+"A7654321name="+parmsMap.get("menucode")+"A7654321v_="+AppUtils.user.get_version();
        }
        wv.clearCache(true);
        wv.loadUrl(url);
        //加载数据
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    tv_title.setText(parmsMap.get("title").toString());
                } else {
                    tv_title.setText("加载中.......");
                }
            }
        });
        //这个是当网页上的连接被点击的时候
        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view,final String url) {
                wv.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);


                /**
                 * 针对 web 页面 【不同分辨率】 适配处理   同dpi 不同分辨率 显示一致
                 *  zoom = 100%    分辨率 1920 * 1080   Ui设置设计出来合适的 也是body标签默认未设置zoom属性的或者100%  例子 <body style="zoom: 50%;">
                 *  zoom = 66.67%     分辨率 1280 * 720
                 *  zoom = 71.14%     分辨率 1366 * 768
                 *  zoom = 106.67%     分辨率 2048 * 1152
                 *  zoom = 83.33%     分辨率 1600 * 900
                 *  zoom = 50%     分辨率 960 * 540
                 */
                float widthPixels = getResources().getDisplayMetrics().widthPixels;
                float heightPixels = getResources().getDisplayMetrics().heightPixels;
                Log.d("zfq", "widthPixels:" + widthPixels);
                Log.d("zfq", "heightPixels:" + heightPixels);
                float zoomPercent = widthPixels / 960 * 100;
                Log.d("zfq", "zoomPercent:" + zoomPercent);
                String javaScriptInterfaceName_zoomWeb_NeedInjectJsStr =
                        "javascript:(function(){  document.body.style.zoom='" + zoomPercent + "%'; })()";
                wv.loadUrl(javaScriptInterfaceName_zoomWeb_NeedInjectJsStr);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wv != null) {
            try {
                wv.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
