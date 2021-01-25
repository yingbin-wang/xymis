package com.cn.wti.activity.report.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wti.activity.base.Report_BaseActivity;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

public class Report_JyybActivity extends Report_BaseActivity  implements View.OnClickListener{

    private Map<String,Object> resMap,map1;
    private int year,month;
    private ImageButton back,go;
    private String parms = "",date;
    private SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月");
    private LinearLayout hj_linear;
    private TextView hj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout_id(R.layout.activity_jyyb);
        super.onCreate(savedInstanceState);
    }

    //绑定数据
    @Override
    public boolean initData() {
        String day = DateUtil.getDay();
        String[] strs = day.split("-");
        year = Integer.parseInt(strs[0]);
        month = Integer.parseInt(strs[1]);

        //得到 数据
        service_name = "report";
        method_name = "create_OperateMonReport";
        mContext = Report_JyybActivity.this;
        pars = "{mon:"+year+"-"+month+",name:OperateMonReport,bussinessType:shouji,user_id:" + AppUtils.app_username + ",menuid:"+menuid+",pars:}";
        parms = StringUtils.strTOJsonstr(pars);
        hjcolumns = new String[]{"1"};
        return getData();
    }

    @Override
    public void initView() {

        //初始化标题

        hj2_title = (TextView) findViewById(R.id.hj2_title);
        hj2_tv = (TextView) findViewById(R.id.hj2_val);

        date = year+"-"+month+"-01";
        hj2_tv.setText(format.format(DateUtil.fomatDate(date)));
        back = (ImageButton) findViewById(R.id.back);
        go = (ImageButton) findViewById(R.id.go);
        back.setOnClickListener(this);
        go.setOnClickListener(this);

        //hj_linear = (LinearLayout) findViewById(R.id.hj_linear);
        //hj_linear.setVisibility(View.VISIBLE);
        hj = (TextView) findViewById(R.id.hj);

        linearLayout_title = (LinearLayout) inflater1.inflate(R.layout.list_item_tablerow_title, null);
        //添加标题列
        addRowTitle_col(1,R.id.layout_1_1,R.id.list_1_1,Color.BLUE,View.VISIBLE);
        addRowTitle_col(2,R.id.layout_1_2,R.id.list_1_2,Color.BLUE,View.VISIBLE);
        addRowTitle_col(3,R.id.layout_1_3,R.id.list_1_3,Color.BLUE,View.VISIBLE);
        //添加标题行
        titleLinerLayout.addView(linearLayout_title);
        //创建listview 控件
        listView();

        //刷新数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                reshData();
            }
        }).start();
    }
    @Override
    public boolean reshData(){

        runOnUiThread(new Runnable() {
            public void run() {
                date = year+"-"+month+"-01";
                if(hj2_tv != null){
                    hj2_tv.setText(format.format(DateUtil.fomatDate(date)));
                }
            }
        });
        if (super.reshData()){
            return  true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                month--;
                if (month == 0){
                    year--;
                    month = 12;
                }
                break;
            case R.id.go:
                month++;
                if(month  == 13){
                    month = 1;
                    year++;
                }
                break;
            default:
                break;
        }

        pars = StringUtils.replaceStart(pars,"mon:","mon:"+year+"-"+month);
        parms = StringUtils.strTOJsonstr(pars);

        mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "");
        new Thread(new Runnable() {
            @Override
            public void run() {

                if(reshData()){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            reportAdapter.refreshData();
                            WeiboDialogUtils.closeDialog(mDialog);
                            //Toast.makeText(mContext,getString(R.string.success_resh),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).start();
    }

    @Override
    public void reshHj() {
        Map<String,Object> dataMap= reportAdapter.getHj();
        if (dataMap != null){
            Set<String> sets = dataMap.keySet();
            int i = 0;
            for (String key:sets) {
                switch (i){
                    case 0:
                        hj.setText("合计 金额："+ ActivityController.numberToString(dataMap.get(key).toString()));
                        break;
                }
                i++;
            }
        }
    }
}
