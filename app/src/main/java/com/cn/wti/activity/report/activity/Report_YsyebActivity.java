package com.cn.wti.activity.report.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.cn.wti.activity.base.Report_BaseActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.parms.ReportParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.ReportViewListener;
import com.cn.wti.entity.view.pulltorefresh.UiListRecyViewListener;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.Dialog_ViewUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.other.StringUtils;

import java.util.Map;

public class Report_YsyebActivity extends Report_BaseActivity {

    private Map<String,Object> resMap,map1;

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
        mContext = Report_YsyebActivity.this;
        pars = new ReportParms("0","0", AppUtils.report_limit,"ReceBalance","",menuid).getParms();
        parms = StringUtils.strTOJsonstr(pars);
        hjcolumns = new String[]{"1","2"};
        return  getData();
    }

    @Override
    public void initView() {
        super.initView();
        if(_dataList == null){return;}
        //初始化显示合计
        titlerow.setVisibility(View.VISIBLE);
        hj1.setVisibility(View.VISIBLE);
        hj2.setVisibility(View.VISIBLE);

        hj2.setPadding(10,0,0,0);
        hj1_title.setText("合计预收");
        hj2_title.setText("合计应收");

        if (_columnList != null){
            //添加标题列
            addRowTitle_col(1,R.id.layout_1_1,R.id.list_1_1,Color.BLUE,View.VISIBLE);
            addRowTitle_col(2,R.id.layout_1_2,R.id.list_1_2,Color.BLUE,View.VISIBLE);
            addRowTitle_col(3,R.id.layout_1_3,R.id.list_1_3,Color.BLUE,View.VISIBLE);
            //添加标题行
            titleLinerLayout.addView(linearLayout_title);
        }

        // 添加上啦下拉刷新
        /*((PullToRefreshLayout) findViewById(R.id.refresh_view)).setOnRefreshListener(
                new UiListRecyViewListener(recordcount,pageIndex,service_name,"",method_name,pars,list_recyclerView,1,_dataList));*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_search:
                String parms = new ListParms("0","0",AppUtils.limit,"customer").getParms();
                Dialog_ViewUtils.showReportCxtj(mContext,"customer","list",parms,rootView,"sub_name","客户档案",mHandler);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getSelectParms(String parms) {
        return  pars.substring(0,pars.length()-1)+"<dh>khjc_mcs<m>"+parms+"}";
    }

}
