package com.cn.wti.activity.report.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wti.activity.base.Report_BaseActivity;
import com.cn.wti.entity.parms.ReportParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.ReportViewListener;
import com.cn.wti.entity.view.pulltorefresh.ReportViewListener_Ddgz;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Map;

public class Report_DdgzbActivity extends Report_BaseActivity implements View.OnClickListener{

    private Map<String,Object> resMap,map1;
    private String date;
    private ImageButton back,go;
    private SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout_id = R.layout.activity_jyyb;
        super.onCreate(savedInstanceState);
    }

    //绑定数据
    public boolean initData() {
        super.initData();
        //得到 数据
        service_name = "report";
        method_name = "findGridData";
        mContext = Report_DdgzbActivity.this;
        listitem_layoutid = R.layout.list_item_tablerow_ddgz;
        date = DateUtil.getDay();
        pars = new ReportParms("OrdersTracking","rq<m>"+date,menuid).getParms();
        return  getData();
    }

    public void initView(){
        hj2 = (LinearLayout) findViewById(R.id.hj2);
        hj2.setVisibility(View.VISIBLE);
        hj2_tv = (TextView) findViewById(R.id.hj2_val);

        hj2_tv.setText(format.format(DateUtil.fomatDate(date)));

        back = (ImageButton) findViewById(R.id.back);
        go = (ImageButton) findViewById(R.id.go);
        back.setOnClickListener(this);
        go.setOnClickListener(this);
        if(_dataList == null){return;}
        listView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                date = DateUtil.getDateByn(date,-1);
                break;
            case R.id.go:
                date = DateUtil.getDateByn(date,1);
                break;
            default:
                break;
        }
        hj2_tv.setText(format.format(DateUtil.fomatDate(date)));
        pars = new ReportParms("OrdersTracking","rq<m>"+date,menuid).getParms();

        mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "");
        new Thread(new Runnable() {
            @Override
            public void run() {

                if(reshData()){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            reportAdapter.refreshData();
                            WeiboDialogUtils.closeDialog(mDialog);
                        }
                    });
                }

            }
        }).start();
    }
}
