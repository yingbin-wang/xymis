package com.cn.wti.activity.report.fragment;

import android.graphics.Color;
import android.view.View;

import com.cn.wti.activity.base.fragment.Report_BaseFragment;
import com.cn.wti.entity.parms.ReportParms;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.entity.view.pulltorefresh.UiListRecyViewListener;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.IDHelper;
import com.wticn.wyb.wtiapp.R;

import java.util.Map;

public class Report_one_Fragment extends Report_BaseFragment {

    private Map<String,Object> resMap,map1;


    public void initView(){
        super.initView();
        if(_dataList == null){return;}
        //初始化显示合计
        if (hjcolumns == null || hjcolumns.length == 0){
            if (titlerow != null) titlerow.setVisibility(View.GONE);
        }else{
            if (titlerow != null) titlerow.setVisibility(View.VISIBLE);
            switch (hjcolumns.length){
                case 1:
                    hj1.setPadding(0,0,0,0);
                    hj1.setVisibility(View.VISIBLE);
                    hj1_title.setText(hjcolumn_names[0]);
                    break;
                case 2:
                    hj1.setVisibility(View.VISIBLE);
                    hj2.setVisibility(View.VISIBLE);
                    hj2.setPadding(10,0,0,0);
                    hj1_title.setText(hjcolumn_names[0]);
                    hj2_title.setText(hjcolumn_names[1]);
                    break;
                case 3:
                    hj1.setVisibility(View.VISIBLE);
                    hj2.setVisibility(View.VISIBLE);
                    hj3.setVisibility(View.VISIBLE);
                    hj1_title.setText(hjcolumn_names[0]);
                    hj2_title.setText(hjcolumn_names[1]);
                    hj3_title.setText(hjcolumn_names[2]);
                    break;
            }
        }

        //添加标题列
        if(name == null){return;}
        int i=1;
        for (String test_name:name) {
            if (test_name != null){
                addRowTitle_col(i, IDHelper.getViewID(getActivity(),"layout_1_"+i),IDHelper.getViewID(getActivity(),"list_1_"+i),Color.BLUE,View.VISIBLE);
            }
            i++;
        }

       /* addRowTitle_col(2,R.id.layout_1_2,R.id.list_1_2,Color.BLUE,View.VISIBLE);
        addRowTitle_col(3,R.id.layout_1_3,R.id.list_1_3,Color.BLUE,View.VISIBLE);
        addRowTitle_col(4,R.id.layout_1_4,R.id.list_1_4,Color.BLUE,View.VISIBLE);*/
        //添加标题行
        titleLinerLayout.addView(linearLayout_title);
        // 添加上啦下拉刷新
       /* ((PullToRefreshLayout) view.findViewById(R.id.refresh_view)).setOnRefreshListener(
                new UiListRecyViewListener(recordcount,pageIndex,service_name,"",method_name,pars,list_recyclerView,1,_dataList));*/
    }

}
