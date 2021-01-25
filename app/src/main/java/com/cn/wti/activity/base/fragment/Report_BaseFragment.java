package com.cn.wti.activity.base.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wti.entity.adapter.ReportAdapter;
import com.cn.wti.entity.view.custom.textview.TextView_TableRow_custom;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.number.SizheTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Report_BaseFragment extends Fragment implements ReportAdapter.IonSlidingViewClickListener{

    protected LinearLayout contentLinerLayout,titleLinerLayout;
    protected LinearLayout linearLayout_title,linearLayout_content;
    protected String[] name= null;

    protected String service_name,method_name,pars,menuid,parms;
    private Map<String,Object> resMap,map1;
    protected List<Map<String,Object>> _dataList = new ArrayList<Map<String,Object>>(),_columnList;
    protected View view;
    protected LayoutInflater inflater1;
    protected LinearLayout titlerow,hj1,hj2,hj3;
    protected TextView hj1_title,hj2_title,hj3_title,hj1_tv,hj2_tv,hj3_tv;

    // table 位置
    protected  int tab_postion,sfcx;
    protected  int recordcount,pageIndex = 1,table_title_layoutid = 0,table_data_layoutid = 0;

    protected  Map<String,Object>menuMap;

    protected RecyclerView list_recyclerView;
    protected ReportAdapter reportAdapter;
    protected String[] contents,hjcolumns,hjcolumn_names;
    private Dialog mDialog;

    /**
     * 接收通知
     */
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    reshHj();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.common_report_list,container,false);
        titleLinerLayout = (LinearLayout) view.findViewById(R.id.title);
        contentLinerLayout = (LinearLayout) view.findViewById(R.id.content);
        inflater1 = inflater;

        mDialog = WeiboDialogUtils.createLoadingDialog(getActivity(),"");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (initData()){
                    /**
                     * 格式化显示合计
                     */
                    setHjNamesVisible();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                            WeiboDialogUtils.closeDialog(mDialog);
                        }
                    });
                }else{
                    WeiboDialogUtils.closeDialog(mDialog);
                }
            }
        }).start();

        return view;
    }

    //绑定数据
    public boolean initData() {
        Bundle arc = this.getArguments();
        if(arc != null){
            tab_postion = arc.getInt("key");
            if (arc.get("parms")!= null){
                pars = arc.getString("parms");
            }

            sfcx = arc.getInt("sfcx");
            menuMap = FastJsonUtils.strToMap(arc.getString("menuMap"));
            if(menuMap != null && !menuMap.get("menu_id").toString().equals("")){
                menuid = menuMap.get("menu_id").toString();
            }
            service_name = arc.getString("service_name");
            method_name = arc.getString("method_name");
            parms = arc.getString("parms");

            if (arc.getInt("table_title_layoutid") != 0){
                table_title_layoutid = arc.getInt("table_title_layoutid");
            }

            if (arc.getInt("table_data_layoutid") != 0){
                table_data_layoutid = arc.getInt("table_data_layoutid");
            }

            hjcolumns = arc.getString("hjcolumns").split(",");
            hjcolumn_names = arc.getString("hjcolumn_names").split(",");

            return  getData();
        }
        return  true;
    }

    /**
     * 设置显示合计字段
     * @return
     */
    private void setHjNamesVisible(){
       String str ="",names="";
       int i = 1,j=0;
        if (_columnList != null){
            for (Map<String,Object> map:_columnList) {
                if (map.get("summary")!= null && Boolean.parseBoolean(map.get("summary").toString())){
                    if (str.equals("")){
                        str += j;
                        names += hjcolumn_names[i-1];
                    }else{
                        str += ","+j;
                        names += ","+hjcolumn_names[i-1];
                    }

                    i++;
                }
                j++;
            }
        }

        if (!str.equals("")){
            hjcolumns = str.split(",");
            hjcolumn_names = names.split(",");
        }
    }

    public void addRowTitle_col(int num,int layout_id,int view_id,int colorid,int visible){
        LinearLayout layout = (LinearLayout) linearLayout_title.findViewById(layout_id);
        TextView_TableRow_custom title = (TextView_TableRow_custom) linearLayout_title.findViewById(view_id);
        title.setText(name[num-1]);
        layout.setVisibility(visible);
    }

    public void addRowTitle_col(int num,int view_id,int colorid,int visible){
        TextView_TableRow_custom title = (TextView_TableRow_custom) linearLayout_title.findViewById(view_id);
        title.setText(name[num-1]);
        title.setTextColor(colorid);
        title.setVisibility(visible);
    }

    public void initView(){
        titlerow = (LinearLayout) view.findViewById(R.id.hjrow);
        hj1 = (LinearLayout) view.findViewById(R.id.hj1);
        hj2 = (LinearLayout) view.findViewById(R.id.hj2);
        hj3 = (LinearLayout) view.findViewById(R.id.hj3);

        //初始化标题
        if (table_title_layoutid == 0){
            linearLayout_title = (LinearLayout) inflater1.inflate(R.layout.list_item_tablerow_title, null);
        }else{
            linearLayout_title = (LinearLayout) inflater1.inflate(table_title_layoutid, null);
        }

        hj1_title = (TextView) view.findViewById(R.id.hj1_title);
        hj2_title = (TextView) view.findViewById(R.id.hj2_title);
        hj3_title = (TextView) view.findViewById(R.id.hj3_title);

        //更新合计
        hj1_tv = (TextView) view.findViewById(R.id.hj1_val);
        hj2_tv = (TextView) view.findViewById(R.id.hj2_val);
        hj3_tv = (TextView) view.findViewById(R.id.hj3_val);

        //列表控件
        list_recyclerView = (RecyclerView) view.findViewById(R.id.list_recyclerView);
        ActivityController.setLayoutManager(list_recyclerView,getActivity());
        reportAdapter = new ReportAdapter(Report_BaseFragment.this,_dataList, AppUtils.getScreenWidth(getActivity()),new String[]{},contents,table_data_layoutid,new boolean[_dataList.size()],mHandler,hjcolumns);
        list_recyclerView.setAdapter(reportAdapter);
    }

    /**
     * 得到 数据集中的 columns 与 list
     */
    public boolean getData(){
        String pars1 = StringUtils.strTOJsonstr(pars);
        Object res = ActivityController.getData4ByPost(service_name, method_name, pars1);
        if (!res.toString().contains("(abcdef)")) {
            resMap = (Map<String, Object>) res;
            _dataList.clear();
            _dataList.addAll((List<Map<String, Object>>) resMap.get("list"));
            _columnList = (List<Map<String, Object>>) resMap.get("columns");
            List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();

            if (_columnList != null && _columnList.size() > 0) {
                name = new String[_columnList.size()];
                int i = 0;
                for (Map<String, Object> columnMap : _columnList) {
                    if (columnMap.get("visible") != null) {
                        removeList.add(columnMap);
                        continue;
                    }
                    name[i] = columnMap.get("title").toString();
                    i++;
                }

                if (removeList.size() > 0) {
                    _columnList.removeAll(removeList);
                }

                contents = FastJsonUtils.ListMapToListStr(_columnList,"field");
            }
            if(resMap.get("results")!=null){
                recordcount = Integer.parseInt(resMap.get("results").toString());
                pageIndex =1;
            }
            return  true;
        }else{
            return  false;
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onDeleteBtnClilck(View view, int position) {

    }

    @Override
    public void onCuibanBtnClick(View view, int position) {

    }

    /**
     * 更新合计
     */
    public void reshHj() {
        Map<String,Object> dataMap= reportAdapter.getHj();
        if (dataMap != null){
            Set<String> sets = dataMap.keySet();
            int i=0;
            for (String key:sets) {
                switch (i){
                    case 0:
                        hj1_tv.setText(ActivityController.numberToString(dataMap.get(key).toString()));
                        break;
                    case 1:
                        hj2_tv.setText(ActivityController.numberToString(dataMap.get(key).toString()));
                        break;
                    case 2:
                        hj3_tv.setText(ActivityController.numberToString(dataMap.get(key).toString()));
                        break;
                }
                i++;
            }
        }
    }

    public boolean reshData(){
        String pars_ = StringUtils.strTOJsonstr(pars);
        Object res = ActivityController.getData4ByPost(service_name, method_name, pars_);
        if (!res.toString().contains("(abcdef)")) {
            resMap = (Map<String, Object>) res;
            _dataList.clear();
            _dataList.addAll((List<Map<String, Object>>) resMap.get("list"));
            if (_dataList != null){
                //reportAdapter.notifyDataSetChanged();
                return true;
            }
        }
        return  false;
    }

    public boolean reshData(String pars){

        String  pars_ = pars;
        pars_ = StringUtils.strTOJsonstr(pars_);
        Object res = ActivityController.getData4ByPost(service_name, method_name, pars_);
        if (!res.toString().contains("(abcdef)")) {
            resMap = (Map<String, Object>) res;
            reportAdapter.getDatas().clear();
            reportAdapter.getDatas().addAll((List<Map<String, Object>>) resMap.get("list"));
           /* reportAdapter.refreshData();*/
            return true;
        }
        return  false;
    }
}
