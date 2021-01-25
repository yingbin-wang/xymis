package com.cn.wti.activity.base;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.ReportAdapter;
import com.cn.wti.entity.adapter.handler.MyHandler;
import com.cn.wti.entity.view.custom.textview.TextView_TableRow_custom;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.number.SizheTool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Report_BaseActivity extends BaseActivity implements ReportAdapter.IonSlidingViewClickListener {

    protected LinearLayout contentLinerLayout,titleLinerLayout;
    protected LinearLayout linearLayout_title,linearLayout_content;
    protected String[] name= null;

    protected String service_name,method_name,pars,parms = "";
    private Map<String,Object> resMap,map1;
    protected List<Map<String,Object>> _dataList = new ArrayList<Map<String,Object>>(),_columnList;
    protected  double hj_1 = 0,hj_2=0,hj_3 = 0;
    protected LayoutInflater inflater1;
    protected LinearLayout titlerow,hj1,hj2,hj3;
    protected  TextView hj1_tv,hj2_tv,hj3_tv,hj1_title,hj2_title,hj3_title;
    protected  int recordcount,pageIndex = 1;
    protected  View view,rootView;

    protected int layout_id = 0,listitem_layoutid=0,listtitle_layoutid = 0;
    //菜单ID
    protected  String menuid;
    protected  Map<String,Object> menuMap;

    // table 位置
    protected  int tab_postion;
    protected ActionBar actionBar;
    //数据列表
    protected Context mContext;
    protected RecyclerView list_recyclerView;
    protected ReportAdapter reportAdapter;
    protected String[] titles,contents,hjcolumns;

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
                case 11:

                    Bundle b = msg.getData();
                    String pars = b.getString("pars");
                    final String parms  = getSelectParms(pars);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (reshData(parms)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        reportAdapter.refreshData();
                                        Toast.makeText(mContext,getString(R.string.success_resh),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();


                    break;
            }
        }
    };

    public String getSelectParms(String pars) { return "";}

    private MyHandler myHandler = null;
    protected Dialog mDialog = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater1 = this.getLayoutInflater();
        setTheme(R.style.CustomActionBarTheme);
        if (layout_id != 0){
            setContentView(layout_id);
        }else{
            setContentView(R.layout.common_report_list);
        }

        AppUtils.setStatusBarColor(this);

        actionBar = this.getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setLogo(R.mipmap.navigationbar_back);
        titleLinerLayout = (LinearLayout) this.findViewById(R.id.title);
        contentLinerLayout = (LinearLayout) this.findViewById(R.id.content);

        mDialog = WeiboDialogUtils.createLoadingDialog(this,"");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (initData()){
                    runOnUiThread(new Runnable() {
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

        myHandler = new MyHandler(mContext);
    }

    //绑定数据
    public boolean initData() {
        //得到菜单数据
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return false;
        }
        menuMap = so.getParms();
        if (menuMap != null && menuMap.get("menu_id")!= null){
            menuid = menuMap.get("menu_id").toString();
        }
        return  true;
    }

    public void addRowTitle_col(int num,int layout_id,int view_id,int colorid,int visible){
        LinearLayout layout = (LinearLayout) linearLayout_title.findViewById(layout_id);
        TextView_TableRow_custom title = (TextView_TableRow_custom) linearLayout_title.findViewById(view_id);
        if(name != null){
            title.setText(name[num-1]);
        }
        //title.setTextColor(colorid);
        layout.setVisibility(visible);
    }

    public void addRowTitle_col(int num,int view_id,int colorid,int visible){
        TextView title = (TextView) linearLayout_title.findViewById(view_id);
        title.setText(name[num-1]);
        title.setTextColor(colorid);
        title.setVisibility(visible);
    }

    /**
     * 无类型
     * @param view_id
     * @param hj_num
     * @param colorid
     * @param visible
     * @param map1
     */
    public void addRowContent_col(int view_id,int hj_num,int colorid,int visible,Map<String,Object> map1){
        TextView txt = (TextView) linearLayout_content.findViewById(view_id);
        txt.setText(String.valueOf(getVal(hj_num,"field",map1,"1")));
        txt.setVisibility(View.VISIBLE);
    }

    /**
     * 带判断 类型
     * @param view_id
     * @param hj_num
     * @param colorid
     * @param visible
     * @param map1
     * @param type
     */
    public void addRowContent_col(int view_id,int hj_num,int colorid,int visible,Map<String,Object> map1,String type){
        TextView txt = (TextView) linearLayout_content.findViewById(view_id);
        txt.setText(String.valueOf(getVal(hj_num,"field",map1,type)));
        txt.setVisibility(View.VISIBLE);
    }

    public void listView(){
        //列表控件
        list_recyclerView = (RecyclerView) findViewById(R.id.list_recyclerView);
        ActivityController.setLayoutManager(list_recyclerView,mContext);
        reportAdapter = new ReportAdapter(mContext,_dataList, AppUtils.getScreenWidth(mContext),titles,contents,listitem_layoutid,new boolean[_dataList.size()],mHandler,hjcolumns);
        reportAdapter.setColumns_list(_columnList);
        list_recyclerView.setAdapter(reportAdapter);
    }

    public void initView(){
        titlerow = (LinearLayout) findViewById(R.id.hjrow);
        hj1 = (LinearLayout) findViewById(R.id.hj1);
        hj2 = (LinearLayout) findViewById(R.id.hj2);
        hj3 = (LinearLayout) findViewById(R.id.hj3);
        //初始化标题
        if (listtitle_layoutid ==0){
            linearLayout_title = (LinearLayout) inflater1.inflate(R.layout.list_item_tablerow_title, null);
        }else{
            linearLayout_title = (LinearLayout) inflater1.inflate(listtitle_layoutid, null);
        }

        linearLayout_content = (LinearLayout) inflater1.inflate(R.layout.list_item_tablerow_, null,false);

        //更新合计
        hj1_title = (TextView) findViewById(R.id.hj1_title);
        hj2_title = (TextView) findViewById(R.id.hj2_title);
        hj3_title = (TextView) findViewById(R.id.hj3_title);

        hj1_tv = (TextView) findViewById(R.id.hj1_val);
        hj2_tv = (TextView) findViewById(R.id.hj2_val);
        hj3_tv = (TextView) findViewById(R.id.hj3_val);

        //列表控件
        list_recyclerView = (RecyclerView)findViewById(R.id.list_recyclerView);
        ActivityController.setLayoutManager(list_recyclerView,mContext);
        reportAdapter = new ReportAdapter(mContext,_dataList, AppUtils.getScreenWidth(mContext),titles,contents,listitem_layoutid,new boolean[_dataList.size()],mHandler,hjcolumns);
        list_recyclerView.setAdapter(reportAdapter);

        rootView = findViewById(R.id.rootView);
    }

    /**
     * 得到 数据集中的 columns 与 list
     */
    public boolean getData(){
        String parms_1 ="";
        if (!parms.equals("")){
            parms_1 = parms;
        }else{
            parms_1 = pars;
        }
        parms_1 = StringUtils.strTOJsonstr(parms_1);

        Object res = ActivityController.getData4ByPost(service_name, method_name, parms_1);
        if (res != null && !res.toString().contains("abcdef")) {
            resMap = (Map<String, Object>) res;
            _dataList.clear();
            _dataList.addAll((List<Map<String, Object>>) resMap.get("list"));
            try {
                _columnList = (List<Map<String, Object>>) resMap.get("columns");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // _columnList = (List<Map<String, Object>>) resMap.get("columns");
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
                titles = FastJsonUtils.ListMapToListStr(_columnList,"title");
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

    private String getVal(int i,String key,Map<String,Object> map,String type){
        String key1 = ((Map<String,Object>) _columnList.get(i)).get("field").toString(),val;

        if(map.get(key1) == null){
            val = "";
        }else{
            val = map.get(key1).toString();
            if (type.equals("1")){
                if(StringUtils.isNumeric(val)){
                    double val_ = Double.parseDouble(val);
                    if(i == 1){
                        hj_1 += val_;
                    }else if(i==2){
                        hj_2 += val_;
                    }else if(i == 3){
                        hj_3 += val_;
                    }

                    if(Math.abs(val_) > 10000 ){
                        val = SizheTool.jq2wxs(String.valueOf(val_ /10000),"2")+"万";
                    }/*else if(Math.abs(val_) >1000){
                        val = SizheTool.jq2wxs(String.valueOf(val_ /1000))+"千";
                    }*/
                }
            }

        }
        return val;
    }

    public  void removeAllView(){
        hj_1 = 0;hj_2=0;hj_3 = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu_reshdata, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_refresh:
                mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(reshData()){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    reportAdapter.refreshData();
                                    WeiboDialogUtils.closeDialog(mDialog);
                                    Toast.makeText(mContext,getString(R.string.success_resh),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        WeiboDialogUtils.closeDialog(mDialog);
                    }
                }).start();

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getLayout_id() {
        return layout_id;
    }

    public void setLayout_id(int layout_id) {
        this.layout_id = layout_id;
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

       String  pars_ = pars;
        pars_ = StringUtils.strTOJsonstr(pars_);
        final Object res = ActivityController.getData4ByPost(service_name, method_name, pars_);
        if (res != null && !res.toString().contains("abcdef")) {
            resMap = (Map<String, Object>) res;
            reportAdapter.getDatas().clear();
            reportAdapter.getDatas().addAll((List<Map<String, Object>>) resMap.get("list"));
            //reportAdapter.refreshData();
            return true;
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (res != null){
                        Toast.makeText(mContext,HttpClientUtils.backMessage(ActivityController.getPostState(res.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        WeiboDialogUtils.closeDialog(mDialog);
        return  false;
    }

    public boolean reshData(String pars){

        String  pars_ = pars;
        pars_ = StringUtils.strTOJsonstr(pars_);
        final Object res = ActivityController.getData4ByPost(service_name, method_name, pars_);
        if (res != null && !res.toString().contains("abcdef")) {
            resMap = (Map<String, Object>) res;
            reportAdapter.getDatas().clear();
            reportAdapter.getDatas().addAll((List<Map<String, Object>>) resMap.get("list"));
            return true;
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (res != null){
                        Toast.makeText(mContext,HttpClientUtils.backMessage(ActivityController.getPostState(res.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        WeiboDialogUtils.closeDialog(mDialog);
        return  false;
    }
}
