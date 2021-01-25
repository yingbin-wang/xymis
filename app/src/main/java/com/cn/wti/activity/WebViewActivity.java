package com.cn.wti.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wti.entity.System_one;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.TipUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.Map;

/**
 * Created by wyb on 2017/2/8.
 */

public class WebViewActivity extends Activity {

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
                WebViewActivity.this.finish();
            }
        });

        wv = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);
        String url = "";
        if (AppUtils.isIp(AppUtils.app_address)){
            url = "http://"+AppUtils.app_address+":8080/wtmis/formproperty/goMxView?menucode="+parmsMap.get("menucode").toString()+"&ywcode="+parmsMap.get("ywcode").toString()+"&id="+parmsMap.get("id").toString();
        }else{
            url = "http://"+AppUtils.app_address+"/wtmis/formproperty/goMxView?menucode="+parmsMap.get("menucode").toString()+"&ywcode="+parmsMap.get("ywcode").toString()+"&id="+parmsMap.get("id").toString();
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
