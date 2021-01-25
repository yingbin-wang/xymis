package com.cn.wti.activity.report.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.cn.wti.activity.base.Report_BaseActivity;
import com.cn.wti.activity.dialog.Report_Xclbb_dialogActivity;
import com.cn.wti.entity.parms.ReportParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListRecyViewListener;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.StringUtils;

import java.util.Map;

public class Report_XclbbActivity extends Report_BaseActivity {

    private Map<String,Object> resMap,map1;
    private String spdn_mcs,splb_mcs;

    //绑定数据
    public boolean initData() {
        super.initData();
        //得到 数据
        service_name = "report";
        method_name = "findGridData";
        mContext = Report_XclbbActivity.this;
        pars = new ReportParms("0","0",AppUtils.report_limit,"ExistNumSelect","",menuid).getParms();
        hjcolumns = new String[]{"1","2"};
        return  getData();
    }

    public void initView(){
        super.initView();
        if(_dataList == null){return;}
        //初始化显示合计
        titlerow.setVisibility(View.VISIBLE);
        hj1.setVisibility(View.VISIBLE);
        hj2.setVisibility(View.VISIBLE);

        hj2.setPadding(10,0,0,0);
        hj1_title.setText("可订量");
        hj2_title.setText("在途量");
        if (_columnList != null){
            //添加标题列
            addRowTitle_col(1,R.id.layout_1_1,R.id.list_1_1,Color.BLUE,View.VISIBLE);
            addRowTitle_col(2,R.id.layout_1_2,R.id.list_1_2,Color.BLUE,View.VISIBLE);
            addRowTitle_col(3,R.id.layout_1_3,R.id.list_1_3,Color.BLUE,View.VISIBLE);
            addRowTitle_col(4,R.id.layout_1_4,R.id.list_1_4,Color.BLUE,View.VISIBLE);
            //添加标题行
            titleLinerLayout.addView(linearLayout_title);
        }

        // 添加上啦下拉刷新
       /* ((PullToRefreshLayout) findViewById(R.id.refresh_view)).setOnRefreshListener(
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
        Intent intent = new Intent();
        switch (item.getItemId()){
            case R.id.action_search:
                intent.setClass(Report_XclbbActivity.this, Report_Xclbb_dialogActivity.class);
                startActivityForResult(intent,1);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1){
            spdn_mcs = data.getStringExtra("spdn_mcs");
            splb_mcs = data.getStringExtra("splb_mcs");
            parms = pars.substring(0,pars.length()-1)+"spdn_mcs<m>"+spdn_mcs+"<dh>splb_mcs<m>"+splb_mcs+"}";
            parms = StringUtils.replaceStart(parms,"menuid:","menuid:"+menuid);

            mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "");
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if(reshData(parms)){
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

}
