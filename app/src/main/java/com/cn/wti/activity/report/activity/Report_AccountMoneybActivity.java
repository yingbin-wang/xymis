package com.cn.wti.activity.report.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.cn.wti.activity.base.Report_BaseActivity;
import com.cn.wti.entity.parms.ReportParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListRecyViewListener;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.Map;

public class Report_AccountMoneybActivity extends Report_BaseActivity {

    private Map<String,Object> map1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //绑定数据
    @Override
    public boolean initData() {
        super.initData();
        //得到 数据
        service_name = "report";
        method_name = "findGridData";
        mContext = Report_AccountMoneybActivity.this;
        pars = new ReportParms("0","0", AppUtils.report_limit,"AccountMoney","",menuid).getParms();
        pars = StringUtils.strTOJsonstr(pars);
        hjcolumns = new String[]{"1"};
        return getData();
    }

    @Override
    public void initView() {
        super.initView();
        if(_dataList == null){return;}
        //初始化显示合计
        titlerow.setVisibility(View.VISIBLE);
        hj1.setVisibility(View.VISIBLE);

        hj1.setPadding(0,0,0,0);
        hj1_title.setText("账户余额");

        //添加标题列
        addRowTitle_col(1,R.id.layout_1_1,R.id.list_1_1,Color.BLUE,View.VISIBLE);
        addRowTitle_col(2,R.id.layout_1_2,R.id.list_1_2,Color.BLUE,View.VISIBLE);
        //添加标题行
        titleLinerLayout.addView(linearLayout_title);
        // 添加上啦下拉刷新
       /* ((PullToRefreshLayout) findViewById(R.id.refresh_view)).setOnRefreshListener(
                new UiListRecyViewListener(recordcount,pageIndex,service_name,"",method_name,pars,list_recyclerView,1,_dataList));*/
    }

}
