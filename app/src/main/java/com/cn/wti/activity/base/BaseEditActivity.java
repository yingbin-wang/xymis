package com.cn.wti.activity.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ObbInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.web.FilePreviewActivity;
import com.cn.wti.entity.Sys_user;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.adapter.handler.MyHandler;
import com.cn.wti.entity.avalidations.EditTextValidator;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.Button_custom;
import com.cn.wti.entity.view.custom.date.CustomDatePicker;
import com.cn.wti.entity.view.custom.dialog.window.MultiChoicePopWindow_CheckTask;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.Constant;
import com.cn.wti.util.app.TableUtils;
import com.cn.wti.util.app.qx.BussinessUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.cn.wti.util.number.SizheTool;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.number.IniUtils;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.IListItem;
import com.dina.ui.widget.ClickListener;
import com.dina.ui.widget.UITableMxView;
import com.dina.ui.widget.UITableView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyb on 2016/11/9.
 */

public class BaseEditActivity extends BaseActivity implements MyAdapter2.IonSlidingViewClickListener{

    protected  String menu_code,menu_name,serviceName="";
    protected  String /*menu_code,menu_name,*/ywtype,approvalstatus,id,current_type="",table_postion,index;
    protected  LayoutInflater inflater;
    protected  ImageButton mx1_add,mx2_add,mx3_add,mx4_add,mx5_add;
    protected EditTextValidator editTextValidator;
    protected  boolean isUpdate = false;
    //加载数据
    protected boolean[] mCheck1,mCheck2,mCheck3,mCheck4,mCheck5;
    protected MyAdapter2 myAdapter1,myAdapter2,myAdapter3,myAdapter4,myAdapter5;
    protected MyHandler myHandler;
    protected Dialog mDialog;
    protected  int isEdit = 0,request_code_file=100891,mClickIndex;

    private boolean isOnlyCheck = true,isCheck = true;

    /**
     * parms 参数 key val
     * main_data 主数据
     * gsMap 公式 Map
     * ggs_colsMap 公式 字段Map
     * hqcolVal 后取数据 href
     */
    protected  Map<String,Object> parms = new HashMap<String, Object>(),main_data,mxSetMap = new HashMap<String, Object>(),
            gsMap = new HashMap<String, Object>(),
            gs_colsMap = new HashMap<String, Object>(),basicMap = new HashMap<String,Object>(),upLoadMap = new HashMap<>(),
            hqcolValMap= new HashMap<String, Object>(),
            actionMap = new HashMap<String, Object>(),
            ruleMap = new HashMap<String, Object>();
    protected  List<Map<String,Object>> maings_list,mx1gs_list,mx2gs_list,mx3gs_list,mx4gs_list,mx5gs_list,mx1_data = new ArrayList<Map<String,Object>>(),mx2_data = new ArrayList<Map<String,Object>>(),mx3_data = new ArrayList<Map<String,Object>>(),mx4_data = new ArrayList<Map<String,Object>>(),mx5_data = new ArrayList<Map<String,Object>>();

    protected Context mContext;

    /**
     * 编辑界面表单结构
     */
    protected LinearLayout main_form,mx1,mx2,mx3,mx4,mx5;
    protected UITableView tableView;
    protected RecyclerView mx1_rView,mx2_rView,mx3_rView,mx4_rView,mx5_rView;

    /**
     * 标题
     *
     */
    protected TextView main_title,mx1_title,mx2_title,mx3_title,mx4_title,mx5_title;

    protected  int layout = 0;

    protected Handler ztHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    current_type = "审批";
                    Intent intent = new Intent();
                    intent.putExtra("type","shenpi");
                    intent.putExtra("table_postion",2);
                    intent.putExtra("index",index);
                    intent.putExtras(AppUtils.setParms("",main_data));
                    setResult(1,intent);
                    ((Activity)mContext).finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Resources.Theme  theme = getTheme();
        setTheme(R.style.CustomActionBarTheme);
        boolean state = true;
        try {
            if (layout != 0){
                setContentView(layout);
            }else{
                setContentView(R.layout.common_edit_recyclerview);
            }
        }catch (Exception e){
            e.printStackTrace();
            state = false;
            if (layout != 0){
                setContentView(layout);
            }else{
                setContentView(R.layout.common_edit_recyclerview);
            }
        }

        if (state){
            AppUtils.setStatusBarColor(this);

            mx1_add = (ImageButton) findViewById(R.id.mx1_add);
            mx2_add = (ImageButton) findViewById(R.id.mx2_add);
            mx3_add = (ImageButton) findViewById(R.id.mx3_add);
            mx4_add = (ImageButton) findViewById(R.id.mx4_add);
            mx5_add = (ImageButton) findViewById(R.id.mx5_add);

            main_form = (LinearLayout) findViewById(R.id.main_form);
            mx1= (LinearLayout) findViewById(R.id.mx1);
            mx1_rView = (RecyclerView) findViewById(R.id.mx1_View);
            mx1_title = (TextView) findViewById(R.id.mx1_title);

            mx2= (LinearLayout) findViewById(R.id.mx2);
            mx2_rView = (RecyclerView) findViewById(R.id.mx2_View);
            mx2_title = (TextView) findViewById(R.id.mx2_title);

            mx3= (LinearLayout) findViewById(R.id.mx3);
            mx3_rView = (RecyclerView) findViewById(R.id.mx3_View);
            mx3_title = (TextView) findViewById(R.id.mx3_title);

            mx4= (LinearLayout) findViewById(R.id.mx4);
            mx4_rView = (RecyclerView) findViewById(R.id.mx4_View);
            mx4_title = (TextView) findViewById(R.id.mx4_title);

            mx5= (LinearLayout) findViewById(R.id.mx5);
            mx5_rView = (RecyclerView) findViewById(R.id.mx5_View);
            mx5_title = (TextView) findViewById(R.id.mx5_title);

            this.inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }
    }

    public void createList(UITableView tableView,ClickListener listener,String type1,Map<String,String> initData) {

        Map<String,Object> map = null,dataMap,actionMap_code=null;
        String type = "",name = "",code = "",value = "";
        int qsh=999;
        List<Map<String,Object>> _dataList = null;

        editTextValidator = new EditTextValidator(mContext);

        if (maings_list != null && maings_list.size()>0){
            _dataList = new ArrayList<Map<String,Object>>();
            _dataList.addAll(maings_list);
        }

        if (type1.equals("add")){
            main_data = new HashMap<String, Object>();
            initUserMainData(initData,AppUtils.user);
            //判断是否 存在 新增调数据
            Map<String,Object>gs_map = (Map<String, Object>) get_catch().get(menu_code+"_mapAll");
            ActivityController.updateAddMainMap(gs_map,main_data);
        }

        for (int i=0,n = maings_list.size();i<n;i++){
            map = _dataList.get(i);
            name = map.get("name").toString();
            code = map.get("code").toString();
            type = map.get("type").toString();
            Object gldn = map.get("gldn");

            if (map.get("qsh") != null){
                qsh = Integer.parseInt(map.get("qsh").toString());
            }

            //是否必填
            Object is_required = map.get("is_required");

            if (type1.equals("add")){
                if (code.equals("code") /*|| code.equals("trantime")*/){
                    map.put("is_select","");
                    map.put("is_write","");
                    map.put("is_visible_phone","");
                    map.put("type","string");
                }
            }

            Object test_ob = main_data.get(code);
            //如果存在下拉列表记录原值
            if (gldn != null && gldn.toString().indexOf(",")>=0){
                is_required= test_ob;
            }else if (map.get("is_required") == null || map.get("is_required").toString().equals("")){
                is_required ="";
            }

            if(test_ob != null){
                value = ActivityController.returnString(main_data,code,maings_list).replace(code,"")/*test_ob.toString()*/;
            }else{
                value = "";
            }

            if (value.contains("err")){
                Toast.makeText(mContext,value.replace("err","异常"),Toast.LENGTH_SHORT).show();
                return;
            }

            String gs = "";
            String[] gs_cols = null;
            if (type1.equals("2") && listener == null){
                if (map.get("is_visible_phone")!= null && !map.get("is_visible_phone").toString().equals("")&& !map.get("is_visible_phone").toString().equals("否")){
                    Object test_ob1 = main_data.get(code);
                    if(test_ob1 != null){
                        value = test_ob1.toString();
                    }else{
                        value = "";
                    }
                    if (map.get("type")!= null && map.get("type").toString().equals("cbx")){
                        if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")) {
                            //是否可选
                            if (map.get("is_write")!= null && map.get("is_write").toString().equals("是")){
                                tableView.addBasicItem(code, name, value, 1, true, 7, type, is_required);
                            }else{
                                tableView.addBasicItem(code, name, value, 0, true, 7, type, is_required);
                            }
                        }
                    }else{
                        tableView.addBasicItem(code,name, value,0,false,1,type);
                    }
                }
            }else{
                if(ruleMap != null && ruleMap.get(code)!= null &&  gsMap!= null && gsMap.get(code)!= null/* && gs_colsMap != null*/){
                    gs = gsMap.get(code).toString();
                    if ( gs_colsMap != null  &&  gs_colsMap.get(code) != null){
                        gs_cols = gs_colsMap.get(code).toString().split(",");
                    }

                    if (actionMap!= null &&  ((Map<String, Object>) actionMap.get("column")).get(code) != null){
                        actionMap_code = (Map<String, Object>) ((Map<String, Object>) actionMap.get("column")).get(code);
                        actionMap_code.put("change_code",code);
                    }

                    if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")){
                        //既能选择又能编辑
                        if (map.get("type")!= null && map.get("type").toString().equals("selectandwrite")) {
                            tableView.addBasicItem(code,name, value,0,true,6,gs_cols,gs,ruleMap.get(code).toString(),type,is_required);
                        }//日期或者时间
                        else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是") && map.get("type")!= null && (map.get("type").toString().equals("date") || map.get("type").toString().equals("datetime"))/* && (map.get("is_write")== null || map.get("is_write").toString().equals(""))*/) {
                            tableView.addBasicItem(code, name, value, 0, true, 4, gs_cols, gs, ruleMap.get(code).toString(), type, is_required);
                        } // 年 月*/
                        else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是") && map.get("type")!= null && map.get("type").toString().equals("ny")) {
                            tableView.addBasicItem(code, name,value,0,true,9,gs_cols,gs, ruleMap.get(code).toString(), type, is_required);
                        }else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是") && map.get("type")!= null && map.get("type").toString().equals("upload")) {
                            tableView.addBasicItem(code, name,value,0,true,10,gs_cols,gs, ruleMap.get(code).toString(), type, is_required);
                        }//只能选择
                        else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是")) {
                            tableView.addBasicItem(code,name,value,qsh,true,3,gs_cols,gs,ruleMap.get(code).toString(),type,is_required);
                        }else if (map.get("type")!= null && map.get("type").toString().equals("cbx")){
                            if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")) {
                                //是否可选
                                if (map.get("is_write")!= null && map.get("is_write").toString().equals("是")){
                                    tableView.addBasicItem(code, name, value, 1, true, 7, gs_cols, gs, ruleMap.get(code).toString(), type, is_required);
                                }else{
                                    tableView.addBasicItem(code, name, value, 0, true, 7, gs_cols, gs, ruleMap.get(code).toString(), type, is_required);
                                }

                            }
                        }else if (map.get("type")!= null && map.get("type").toString().equals("tszf")){
                            tableView.addBasicItem(code,name, value,0,true,5,gs_cols,gs,ruleMap.get(code).toString(),type,is_required);
                        }else if(map.get("is_write")!= null && map.get("is_write").toString().equals("是")){
                            if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")) {
                                tableView.addBasicItem(code, name, value, 0, false, 2, actionMap_code, gs, ruleMap.get(code).toString(), type, is_required);
                            }
                        }else{
                            tableView.addBasicItem(code,name, value,0,false,1,gs_cols,gs,ruleMap.get(code).toString(),type,is_required);
                        }

                    }else{
                        tableView.addBasicItem(code,name, value,0,false,0,gs_cols,gs,ruleMap.get(code).toString(),type,is_required);
                    }

                }else if (ruleMap != null && ruleMap.get(code)!= null){

                    if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")){

                        //既能选择又能编辑
                        if (map.get("type")!= null && map.get("type").toString().equals("selectandwrite")) {
                            tableView.addBasicItem(code,name, value,0,true,6,gs_cols,gs,ruleMap.get(code).toString(),type,is_required);
                        }else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是") && map.get("type")!= null && (map.get("type").toString().equals("date") || map.get("type").toString().equals("datetime"))/*&& (map.get("is_write")== null || map.get("is_write").toString().equals(""))*/) {
                            tableView.addBasicItem(code, name, value, 0, true, 4, ruleMap.get(code).toString(), type, is_required);
                        }// 年 月*/
                        else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是") && map.get("type")!= null && map.get("type").toString().equals("ny")) {
                            tableView.addBasicItem(code, name, value, 0, true, 9, ruleMap.get(code).toString(), type, is_required);
                        }//只能选择
                        else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是")) {
                            tableView.addBasicItem(code,name, value,qsh,true,3,ruleMap.get(code).toString(),type,is_required);
                        }
                        else if (map.get("type")!= null && map.get("type").toString().equals("cbx")){
                            if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")) {
                                //是否可选
                                if (map.get("is_write")!= null && map.get("is_write").toString().equals("是")){
                                    tableView.addBasicItem(code, name, value,1, true, 7, ruleMap.get(code).toString(), type, is_required);
                                }else{
                                    tableView.addBasicItem(code, name, value,0, true, 7, ruleMap.get(code).toString(), type, is_required);
                                }
                            }
                        }else if (map.get("type")!= null && map.get("type").toString().equals("tszf")){
                            tableView.addBasicItem(code,name, value,0,true,5,ruleMap.get(code).toString(),type,is_required);
                        }else if(map.get("is_write")!= null && map.get("is_write").toString().equals("是")){
                            tableView.addBasicItem(code,name, value,0,false,2,ruleMap.get(code).toString(),type,is_required);
                        }else{
                            tableView.addBasicItem(code,name, value,0,false,1,ruleMap.get(code).toString(),type,is_required);
                        }

                    }else{
                        tableView.addBasicItem(code,name, value,0,false,0,ruleMap.get(code).toString(),type,is_required);
                    }

                }else if (gsMap != null && gsMap.get(code)!= null/* && gs_colsMap !=null*/){
                    gs = gsMap.get(code).toString();
                    if (gs_colsMap!= null){
                        gs_cols = (String[]) gs_colsMap.get(code);
                    }

                    if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")){

                        //既能选择又能编辑
                        if (map.get("type")!= null && map.get("type").toString().equals("selectandwrite")) {
                            tableView.addBasicItem(code,name, value,0,true,6,gs_cols,gs,ruleMap.get(code).toString(),type,is_required);
                        }else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是") && map.get("type")!= null && (map.get("type").toString().equals("date") || map.get("type").toString().equals("datetime"))/* && (map.get("is_write")== null || map.get("is_write").toString().equals(""))*/) {
                            tableView.addBasicItem(code, name, value, 0, true, 4, gs_cols, gs, type, is_required);
                        }else if (map.get("type")!= null && map.get("type").toString().equals("cbx")){
                            if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")) {
                                //是否可选
                                if (map.get("is_write")!= null && map.get("is_write").toString().equals("是")){
                                    tableView.addBasicItem(code, name, value, 1, true, 7, gs_cols, gs, type, is_required);
                                }else{
                                    tableView.addBasicItem(code, name, value, 0, true, 7, gs_cols, gs, type, is_required);
                                }

                            }
                        }//只能选择
                        else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是")) {
                            tableView.addBasicItem(code,name, value,qsh,true,3,gs_cols,gs,type,is_required);
                        }else if (map.get("type")!= null && map.get("type").toString().equals("tszf")){
                            tableView.addBasicItem(code,name, value,0,true,5,gs_cols,gs,type,is_required);
                        }else if(map.get("is_write")!= null && map.get("is_write").toString().equals("是")){
                            if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")) {
                                tableView.addBasicItem(code, name, value, 0, false, 2, gs_cols, gs, type, is_required);
                            }
                        }else{
                            tableView.addBasicItem(code,name, value,0,false,1,gs_cols,gs,type,is_required);
                        }

                    }else{
                        tableView.addBasicItem(code,name, value,0,false,0,gs_cols,gs,type,is_required);
                    }

                }else{

                    if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")){

                        //既能选择又能编辑
                        if (map.get("type")!= null && map.get("type").toString().equals("selectandwrite")) {
                            tableView.addBasicItem(code,name, value,0,true,6,type,is_required);
                        }else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是") && map.get("type")!= null && (map.get("type").toString().equals("date") || map.get("type").toString().equals("datetime"))/*  && (map.get("is_write")== null || map.get("is_write").toString().equals(""))*/) {
                            tableView.addBasicItem(code, name, value, 0, true, 4, type, is_required);
                        }// 年 月*/
                        else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是") && map.get("type")!= null && map.get("type").toString().equals("ny")) {
                            tableView.addBasicItem(code, name, value,0,true,9,type,is_required);
                        }else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是") && map.get("type")!= null && map.get("type").toString().equals("upload")) {
                            tableView.addBasicItem(code, name, value,0,true,10,type,is_required);
                        }else if (map.get("type")!= null && map.get("type").toString().equals("cbx")){
                            if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")) {
                                //是否可选
                                if (map.get("is_write")!= null && map.get("is_write").toString().equals("是")){
                                    tableView.addBasicItem(code, name, value, 1, true, 7, type, is_required);
                                }else{
                                    tableView.addBasicItem(code, name, value, 0, true, 7, type, is_required);
                                }
                            }
                        }//只能选择
                        else if (map.get("is_select")!= null && map.get("is_select").toString().equals("是")) {
                            tableView.addBasicItem(code,name, value,qsh,true,3,type,is_required);
                        }else if (map.get("type")!= null && map.get("type").toString().equals("tszf")){
                            tableView.addBasicItem(code,name, value,0,true,5,type,is_required);
                        }else if(map.get("is_write")!= null && map.get("is_write").toString().equals("是")){
                            if(map.get("is_visible_phone")!= null && map.get("is_visible_phone").toString().equals("是")) {
                                tableView.addBasicItem(code, name, value, 0, false, 2, type, is_required);
                            }
                        }else{
                            tableView.addBasicItem(code,name, value,0,false,1,type,is_required);
                        }

                    }else{
                        tableView.addBasicItem(code,name, value,0,false,0,type,is_required);
                    }
                }
            }
        }

        if (listener !=null){
            tableView.setEditTextValidator(editTextValidator);
            tableView.setClickListener(listener);
        }
        tableView.setContext1(this);
        tableView.commit();
    }

    public void  initUserMainData(Map<String,String> data, Sys_user user){
        String val = "";
        if(data !=  null){
            Set<String> sets = data.keySet();
            for (String key:sets){
                val = data.get(key).toString();
                switch (val){
                    case "_zydnId":
                        main_data.put(key,user.get_zydnId());
                        break;
                    case "_zydnName":
                        main_data.put(key,user.get_zydnName());
                        break;
                    case "_bmdnId":
                        main_data.put(key,user.get_bmdnId());
                        break;
                    case "_bmdnName":
                        main_data.put(key,user.get_bmdnName());
                        break;
                    case "bmdn_name":
                        main_data.put(key,user.get_bmdnName());
                        break;
                    case "_loginName":
                        main_data.put(key,user.get_loginName());
                        break;
                    case  "trantime":
                        main_data.put(key,DateUtil.createDate());
                        break;
                    default:
                        break;
                }

                //是否含税
                if (key.equals("is_tax")){
                    main_data.put(key,1);
                    /*if (AppUtils.user.getSjjs().equals("1")){
                        main_data.put(key,1);
                    }else{
                        main_data.put(key,0);
                    }*/
                }
            }
        }
    }

    public String saveOrEdit(final Context context, List<IListItem> main_list, Map<String,Object> mxMap) {

        if (main_data.get("estatus") != null && main_data.get("estatus").equals("7")){
            Toast.makeText(mContext,mContext.getString(R.string.save_check_text),Toast.LENGTH_SHORT).show();
            return id;
        }else if (main_data.get("approvalstatus") != null && main_data.get("approvalstatus").equals("1")){
            Toast.makeText(mContext,mContext.getString(R.string.save_sp_text),Toast.LENGTH_SHORT).show();
            return id;
        }

        String code,val,method,resid="";
       /* BasicItem item = null;

        for (int i=0,n = main_list.size();i< n ;i++){
            item = (BasicItem) main_list.get(i);
            code = item.getCode();
            if (item.getVal()!= null){
                val = item.getVal();
            }else{
                val = item.getSubtitle();
            }

            if(val != null){
                main_data.put(code,val);
            }

            //清除 不要字段
            clearColumn(main_data);

        }*/

       /* if(!tableView.getEditTextValidator().validate()){
            return  id;
        }*/

        /**
         * 添加明细数据
         */
        if (mxMap != null){
            Set<String> sets = mxMap.keySet();
            for (String key:sets){
                if(mxMap.get(key) == null){
                    main_data.put(key,"[]");
                }else{
                    main_data.put(key,FastJsonUtils.ListMapToListStr((List<Map<String,Object>>) mxMap.get(key)));
                }
            }
        }

        String main_str = FastJsonUtils.mapToString(main_data);
        if (!main_str.equals("")){
            main_str = main_str.substring(1,main_str.length()-1);
        }

        //20171226 wang 如果存在 serviceName 否则 取 menucode
        if (serviceName.equals("")){
            serviceName = menu_code;
        }

        if(ywtype.equals("add")){
            method = "save";
        }else {
            method ="edit";
        }
        String version="";
        //版本号
        if (main_data.get("version")!= null){
            version = main_data.get("version").toString();
        }

        String pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"id\":\""+id
                +"\",\"version\":\""+version+"\",\"userid\":\""+AppUtils.user.get_id()
                +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"
                +"\","+main_str+"}";
        final String finalmethod = method,finalpars = pars;
        //执行请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Object res = ActivityController.getDataByPost(mContext,serviceName,finalmethod,finalpars);
                if (res != null && !res.toString().contains("(abcdef)")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
                            if(resMap == null){
                                Toast.makeText(context,R.string.connection_timeout,Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                                Toast.makeText(context,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                String resid ="";
                                if (resMap.get("data") instanceof JSONObject){
                                    JSONObject resultJot = (JSONObject) resMap.get("data");
                                    resid = resultJot.get("id").toString();
                                }else{
                                    resid = resMap.get("data").toString();
                                }

                                id = resid;
                                //更新主ID
                                main_data.put("id",resid);
                                update_colval_Main("id",resid,tableView,main_data);
                                if (finalmethod.equals("save")){
                                    main_data.put("version",1);
                                }else if (finalmethod.equals("edit")){
                                    main_data.put("version",Integer.parseInt(main_data.get("version").toString())+1);
                                }

                                main_data.put("estatus",1);
                                current_type = ywtype;
                                ywtype = "edit";
                                isUpdate = false;
                                //刷新明细数据
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        reshData();
                                    }
                                }).start();
                            }else{
                                Toast.makeText(context,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();

        return resid;
    }

    /**
     * 清除 不填写字段 20171228 wang
     * @param main_data
     */
    public void clearColumn(Map<String, Object> main_data) {}

    public boolean audit(Context context,String method,String pars){
        /*String pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"DATA_IDS\":\""+id+"\",\"estatus\":\"7\"}";*/

        //20171226 wang 如果存在 serviceName 否则 取 menucode
        if (serviceName.equals("")){
            serviceName = menu_code;
        }

        Object res = ActivityController.getDataByPost(mContext,serviceName,method,StringUtils.strTOJsonstr(pars));
        if (res != null){
            Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
            if(resMap == null){
                Toast.makeText(mContext,getString(R.string.error_invalid_network),Toast.LENGTH_SHORT).show();
                return  false;
            }
            if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                Toast.makeText(context,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                if(pars.replace("\"","").indexOf("estatus:7")>=0 ){
                    update_colval_Main("estatus","7",tableView,main_data);
                    current_type = "check";
                }else{
                    update_colval_Main("estatus","1",tableView,main_data);
                    update_colval_Main("approvalstatus","0",tableView,main_data);
                    current_type = "uncheck";
                }
                /**
                 * 刷新数据 与 视图
                 */
                reshData();
                return  true;
            }else{
                Toast.makeText(context,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                return  false;
            }
        }
        return  false;
    }

    public void auditAll(Context context,String method,String pars){
        /*String pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"DATA_IDS\":\""+id+"\",\"estatus\":\"7\"}";*/

        if(id.equals("")  || id == null ){
            return;
        }
        //审批状态
        if (main_data !=null && main_data.get("approvalstatus")!=null){
            approvalstatus  = main_data.get("approvalstatus").toString();
        }

        if(approvalstatus !=null && !approvalstatus.equals("0") && !approvalstatus.equals("") ){
            Toast.makeText(context,"单据在流程审批中不允许此动作！",Toast.LENGTH_SHORT).show();
            return;
        }

        //20171226 wang 如果存在 serviceName 否则 取 menucode
        if (serviceName.equals("")){
            serviceName = menu_code;
        }

        String parms1 =new ListParms(menu_code,"id:"+id).getParms();
        parms1 = StringUtils.strTOJsonstr(parms1);

        final String finalParms = parms1,finalpars = pars;
        final Context finalContext = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Object res = ActivityController.getData4ByPost("process","getUserFlowByMenucode", finalParms);

                List<Map<String,Object>> list = null;
                if(res != null && !res.equals("") ){
                    if (!res.toString().contains("(abcdef)")){
                        list= (List<Map<String, Object>>) res;
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext,res.toString().replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                            }
                        });

                        return;
                    }
                }
                final List<Map<String, Object>> finalList = list;
                if (list != null && list.size()>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalList != null){
                                main_data.put("index",index);
                                MultiChoicePopWindow_CheckTask multiChoicePopWindow = new MultiChoicePopWindow_CheckTask(finalContext,main_form, finalList,new boolean[finalList.size()],main_data,menu_name+" 开始流程步骤",ztHandler);
                                multiChoicePopWindow.setService_name(serviceName);
                                multiChoicePopWindow.setMethod_name("auditAll");
                                multiChoicePopWindow.setPars(finalpars);
                                multiChoicePopWindow.show(true);
                            }
                        }
                    });
                }else{

                    final List<Map<String, Object>> finalList1 = list;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalList1 != null && finalList1.size()==0){
                                Toast.makeText(mContext,getString(R.string.err_nobuzou),Toast.LENGTH_SHORT).show();
                            }else{
                                mDialog = WeiboDialogUtils.createLoadingDialog(mContext, "审核中...");
                                HandlerThread handlerThread = new HandlerThread("handlerThread");
                                handlerThread.start();
                                MyHandler handler = new MyHandler(handlerThread.getLooper(),mContext);
                                Message msg = handler.obtainMessage();
                                //利用bundle对象来传值
                                Bundle b = new Bundle();
                                b.putString("pars", finalpars);
                                msg.what = 4;
                                msg.setData(b);
                                msg.sendToTarget();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 无审批审核
     * @param pars
     * @return
     */
    public boolean noSpAudit(String pars){
        boolean flage = audit(mContext,"auditAll",StringUtils.strTOJsonstr(pars));
        if (flage){
            //update_colval_Main("estatus","7",tableView,main_data);
            //20171225 wang 添加 返回 状态
            if (!current_type.equals("")){
                Intent intent = new Intent();
                intent.putExtra("index",index);
                intent.putExtra("type",current_type);
                if (main_data != null){
                    intent.putExtras(AppUtils.setParms("",main_data));
                }else{
                    intent.putExtras(AppUtils.setParms("",new HashMap<String,Object>()));
                }

                setResult(1,intent);
            }
            this.finish();
        }else{
            update_colval_Main("estatus","1",tableView,main_data);
        }
        return  flage;
    }

    public void setMenu_code(String menu_code) {
        this.menu_code = menu_code;
    }

    public String getMenu_code() {
        return this.menu_code;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getMenu_name() {
        return this.menu_name;
    }

    public boolean isOnlyCheck() {
        return isOnlyCheck;
    }

    public void setOnlyCheck(boolean onlyCheck) {
        isOnlyCheck = onlyCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    /**
     * main 点击事件
     */
    protected class CustomClickListener implements ClickListener {

        private List<Map<String,Object>> _list;
        /*private Context context;*/
        private List<IListItem> _basicItemList;
        private BasicItem basicItem;
        private Map<String,Object> map;
        private String title;
        private UITableView tableView;
        private String parms1,parms2;

        private String mxIndex;

        public CustomClickListener(Context context,List<Map<String,Object>> _list,UITableView tableView,String parms1){
            this._list = _list;
            /*this.context = context;*/
            this.tableView = tableView;
            this.parms1 = parms1;
            _basicItemList = tableView.getIListItem();
        }

        public CustomClickListener(Context context,List<Map<String,Object>> _list,UITableView tableView,String parms1,String index){
            this._list = _list;
            /*this.context = context;*/
            this.tableView = tableView;
            this.parms1 = parms1;
            _basicItemList = tableView.getIListItem();
            this.mxIndex = index;
            tableView.setMxIndex(index);
        }

        @Override
        public void onClick(final int index,View objectView) {
            /*Toast.makeText(xsdd_editActivity.this, "item clicked: " + index, Toast.LENGTH_SHORT).show();*/
            parms2 = parms1;
            /*获取表单格式*/
            map = _list.get(index);
            /*获取数据模板*/
            basicItem = (BasicItem) _basicItemList.get(index);

            final View v  = tableView.getLayoutList(index);

            if (map.get("gldn") != null && !map.get("gldn").toString().equals("")) {

                mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"加载中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] items = null;
                        String[] gldns = null;
                        int type = 1;
                        final String gldn = map.get("gldn").toString();
                        String pars ="",service="",method="",col_name="";

                        if (gldn.equals("")) {
                            return;
                        } else if (gldn.contains(",")) {
                            items = gldn.split(",");
                            type = 1;
                        } else {

                            gldns = gldn.split("~");
                            col_name = getColumnName(gldn);
                            if (col_name.equals("err")){
                                return;
                            }
                            service = gldns[0];
                            Map<String,String> map = ActivityController.getServiceAndParamsName(service);

                            method = getMethodName(gldn);

                            //如果 方法名为空 直接返回，否则验证 方法中是否 存在 service
                            if (method.equals("")){
                                return;
                            }else{
                                if (method.indexOf("/")>=0){
                                    int start  = method.indexOf("/");
                                    service = method.substring(0,start);
                                    map = ActivityController.getServiceAndParamsName(service);
                                    method = method.substring(start+1,method.length());
                                }
                            }

                            // 模糊记忆
                            if (parms1 == null || parms1.equals("")){
                                parms2 = setParms(gldn,tableView);
                                if (parms2.indexOf("err")>=0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext,parms2.replace("err,",""),Toast.LENGTH_SHORT).show();
                                            WeiboDialogUtils.closeDialog(mDialog);
                                        }
                                    });
                                    return;
                                }
                            }

                            if (map.get("type") != null){
                                service = map.get("serviceName");
                                if (!parms2.equals("")){
                                    parms2 += ",type:"+map.get("type");
                                }else{
                                    parms2 = "type:"+map.get("type");
                                }

                            }

                            if(getLimit(gldn)){
                                pars = new ListParms("0","0",AppUtils.limit,service,parms2).getParms();
                            }else{
                                pars = new ListParms(service,parms2).getParms();
                            }

                            if(pars.indexOf("~")>=0){
                                int end = pars.indexOf("~");
                                String pars1 = pars.substring(0,end);
                                String pars2 = pars.substring(end,pars.length());
                                pars1 = StringUtils.strTOJsonstr(pars1);
                                pars = pars1 + pars2;
                            }else{
                                pars = StringUtils.replaceStart(pars,"menu_code:","menu_code:"+service);
                                pars = StringUtils.strTOJsonstr(pars);
                            }

                            if (selectMap.get(gldns[0]) != null) {
                                items = FastJsonUtils.ListMapToListStr((List<Map<String, Object>>) selectMap.get(gldns[0]), col_name);
                            } else {
                                items = ActivityController.getDialogDataByPost(service,method,get_catch(),gldn,col_name,AppUtils.limit,pars);
                            }

                            type = 2;
                        }

                        List<Map<String,Object>> testMapList;
                        int recordcount = 0 ,pageIndex = 0;
                        if(get_catch().get(gldn) != null){
                            testMapList = (List<Map<String,Object>>)get_catch().get(gldn);
                            recordcount = Integer.parseInt(get_catch().get(gldn+"_results").toString());
                            pageIndex = 1;
                        }else {
                            testMapList = new ArrayList<Map<String,Object>>();
                        }
                        Map<String,Object> testMap = null;
                        if (parms != null && parms.get(gldn) != null){
                            testMap = (Map<String, Object>) parms.get(gldn);
                        }else{
                            testMap = new HashMap<String, Object>();
                        }

                        if (gldns == null){
                            title = "下拉选择";
                        }else{
                            title = gldns[1];
                        }
                        if(items == null){
                            items = new String[]{};
                        }

                        final String[] finalitems = items;
                        final List<Map<String,Object>> finaltestMapList = testMapList;
                        final Map<String,Object> finaltestMap = testMap;
                        final String finalservice = service,finalmethod = method,finalpars = pars,finalcol_name = col_name;
                        final int finalrecordcount = recordcount,finalpageIndex = pageIndex;

                        if (true){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ActivityController.showDialog2(mContext,title,finalitems,finaltestMapList,_basicItemList,finaltestMap,tableView,index,parms2,main_form,finalservice,finalmethod,finalpars,finalcol_name,finalrecordcount,finalpageIndex);
                                    WeiboDialogUtils.closeDialog(mDialog);
                                }
                            });
                        }
                    }
                }).start();


            }else if (map.get("type")!= null && map.get("type").toString().equals("datetime")){
                String dateq = "";

                if(basicItem.getSubtitle().equals("")){
                    dateq = DateUtil.createDate();
                }else{
                    dateq = basicItem.getSubtitle();
                }

                CustomDatePicker customDatePicker1 = new CustomDatePicker(mContext, new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time) { // 回调接口，获得选中的时间
                        ActivityController.updateBasicItem(tableView,basicItem,v,index,time);
                    }
                }, "1910-01-01 00:00", dateq); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                customDatePicker1.showSpecificTime(true); // 显示时和分
                customDatePicker1.setIsLoop(true); // 允许循环滚动
                customDatePicker1.show(dateq);
                /*DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil((Activity) mContext, dateq);
                dateTimePicKDialog.dateTimePicKDialog_(tableView,v,basicItem,index,1);*/

            }else if (map.get("type")!= null && map.get("type").toString().equals("date")){

                String dateq = "";
                if(basicItem.getSubtitle().equals("")){
                    dateq = DateUtil.createDate();
                }else{
                    dateq =basicItem.getSubtitle();
                }

                CustomDatePicker customDatePicker1 = new CustomDatePicker(mContext, new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time) { // 回调接口，获得选中的时间
                        ActivityController.updateBasicItem(tableView,basicItem,v,index,time);
                    }
                }, "1910-01-01 00:00", dateq); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                customDatePicker1.showSpecificTime(false,"date"); // 显示时和分
                customDatePicker1.setIsLoop(true); // 允许循环滚动
                customDatePicker1.show(dateq);

                /*DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil((Activity) mContext, dateq);
                dateTimePicKDialog.dateTimePicKDialog_(tableView,v,basicItem,index,0);*/

            }else if (map.get("type")!= null && map.get("type").toString().equals("ny")){
                String dateq = "";
                if(basicItem.getSubtitle().equals("")){
                    dateq = DateUtil.createDate();
                }else{
                    dateq =basicItem.getSubtitle()+"-01 00:00:00";
                }

                CustomDatePicker customDatePicker1 = new CustomDatePicker(mContext, new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time) { // 回调接口，获得选中的时间
                        ActivityController.updateBasicItem(tableView,basicItem,v,index,time);
                    }
                }, "1910-01-01 00:00", dateq); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                customDatePicker1.showSpecificTime(false,"ny"); // 显示时和分
                customDatePicker1.setIsLoop(true); // 允许循环滚动
                customDatePicker1.show(dateq);

                /*DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil((Activity) mContext, dateq);
                dateTimePicKDialog.dateTimePicKDialog_(tableView,v,basicItem,index,2);*/

            }else if (map.get("type")!= null && map.get("type").toString().equals("upload")){
                mClickIndex = index;
                showFileList(map,index,objectView);
            }else{
                Toast.makeText(mContext,"无选项功能",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFileList(Map<String,Object>map,int index,View view) {
        if (view instanceof Button_custom){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf|image/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES,
                        new String[]{Constant.IMAGE,Constant.PDF});
            }
            intent.putExtra("code",map.get("code").toString());
            intent.putExtra("menucode",map.get("menucode").toString());
            intent.putExtra("name",map.get("name").toString());
            intent.putExtra("type",map.get("type").toString());
            intent.putExtra("ywcode",map.get("ywcode").toString());
            intent.putExtra("index",index);
            this.basicMap = map;
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult( Intent.createChooser(intent, "请选择要上传的文件"), request_code_file);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
            }
        }else if (view instanceof TextView_custom){
            //打开webview 显示数据
            Intent intent = new Intent(mContext,FilePreviewActivity.class);
            Map<String,Object> parmsMap = new HashMap<String, Object>();
            parmsMap.put("title","文件预览");
            parmsMap.put("fileName",main_data.get(map.get("code")));
            parmsMap.put("filePath",main_data.get(map.get("code")+"_filepath") == null ? main_data.get("newfilename") : main_data.get(map.get("code")+"_filepath"));
            intent.putExtras(AppUtils.setParms("",parmsMap));
            startActivity(intent);
        }

    }

    /**
     * 根据 根据参数 得到 参数结果
     * @param parms
     * @param tableView
     * @return
     */
    public String setDialogParms(String parms,UITableView tableView){
        String parms_1 = "";
        if (tableView != null){
            parms_1 = TableUtils.getParms(parms.split(","),tableView);
        }else{
            tableView = (UITableView) get_data().get("mainView");
            parms_1 = TableUtils.getParms(parms.split(","),tableView);
        }
        return  parms_1;
    }

    public String setParms(String gldn,UITableView tableView){
        Map<String,Object> parmsMap = (Map<String, Object>) parms.get(gldn);

        String parms = "",_parms="";
        if (parmsMap.get("parms")!=null){
            if (parmsMap.get("parms") instanceof JSONObject){
                Map<String,Object> test = (Map<String, Object>) parmsMap.get("parms"),testMap = null;
                Set<String> sets = test.keySet();
                int size = sets.size();
                /**
                 * 如果 key 等于 main 从主数据 去参数信息
                 * 否则 从自身取 参数信息
                 */
                for (String key:sets){

                    if (key.equals("main")){
                        if (get_data().get("mainView") != null ){
                            tableView = (UITableView) get_data().get("mainView");
                            testMap = (Map<String, Object>) test.get(key);
                        }else{
                            testMap = (Map<String, Object>) test.get(key);
                        }
                    }else{
                        testMap = new HashMap<String,Object>();
                        testMap.putAll(test);
                        testMap.remove("main");
                    }

                    if(testMap != null){
                        //
                        if(testMap.get("type")!= null) {
                            if (testMap.get("type").equals("validate")) {
                                String href_ = testMap.get("from").toString();
                                if (href_.indexOf("?") >= 0 && href_.indexOf(":") >= 0) {
                                    Map<String, Object> dataMap = updateSimulateMain();
                                    String[] keys = StringUtils.getKeys(href_);
                                    for (String key_ : keys) {
                                        if (dataMap.get(key_) == null) {
                                            return  "err,"+"无效参数：" + key_;
                                        }
                                        href_ = href_.replace("[" + key_ + "]", "'" + dataMap.get(key_).toString() + "'");
                                    }
                                    _parms = SizheTool.eval_back(href_);
                                }
                            }
                        }

                        Set<String> keys = testMap.keySet();
                        //如果 _parms 不为空 得到 参数 否则 取 map 中的 key
                        String[] keys_list;
                        if (!_parms.equals("")){
                            keys_list = _parms.split(",");
                        }else{
                            keys_list = FastJsonUtils.setStrToArray(keys);
                        }
                        for (String testKey:keys_list ) {
                            //如果 参数 中存在menu_code 直接过掉
                            String test_menucode="",test_val="";
                            if(testKey.indexOf("-~")>=0){
                                String[] hhs =  testKey.split("-~");
                                test_menucode = hhs[0];
                                test_val = hhs[1];
                                testKey = "menu_code";
                            }
                            if (testKey.equals("menu_code") || test_menucode.equals("menu_code")){
                                if(parms.equals("")){
                                    parms +=testKey+":"+test_val;
                                }else{
                                    parms += ","+testKey+":"+test_val;
                                }
                                continue;
                            }
                            BasicItem item = ActivityController.getItem(tableView.getIListItem(),testMap.get(testKey).toString());
                            String val = "";
                            if(item == null){
                                val = testMap.get(testKey).toString();
                            }else{
                                val = item.getSubtitle();
                            }

                            if (testKey.equals("calculate_taxes")){
                                val = AppUtils.user.getSjjs();
                            }else if (testKey.equals("company_nature")){
                                val = AppUtils.user.getGsxz();
                            }

                            if(val.indexOf("_val")>=0){
                                val = val.replace("_val","");
                            }
                            if(parms.equals("")){
                                parms +=testKey+":"+val;
                            }else{
                                parms += ","+testKey+":"+val;
                            }

                            if(testKey.equals("isdn")){
                                parms+=",parms:isdn='"+val+"'";
                            }

                            if(val.equals("")){
                                return  "err,"+"必须先选择："+testKey;
                            }
                        }
                    }
                }
            }else{
                return  "err,"+"格式文件异常";
            }
        }
        return  parms;
    }

    /**
     * 去掉 split 后面的 参数不处理
     * @param gldn
     * @param tableView
     * @param split
     * @return
     */
    public String setParms(String gldn,UITableView tableView,String split,String cs){
        Map<String,String> parmsMap = (Map<String, String>) parms.get(gldn);

        String parms = "";
        if (parmsMap.get("parms")!=null){
            Map<String,Object> test = FastJsonUtils.strToMap(parmsMap.get("parms").toString());
            Set<String> sets = test.keySet();

            int size = sets.size();
            int i = 0;
            for (String key:sets){

                BasicItem item = ActivityController.getItem(tableView.getIListItem(),test.get(key).toString());
                String val = "";
                if(item == null){
                    val = test.get(key).toString();
                }else{
                    val = item.getSubtitle();
                }

                if(i== size -1){
                    parms +=key+":"+val;
                }else{
                    parms +=key+":"+val+",";
                }
            }

        }
        return  parms + cs.split("~")[1];
    }

    public String getMethodName(String gldn){
        Map<String,String> parmsMap = (Map<String, String>) parms.get(gldn);
        String method = "";
        if (parmsMap.get("serviceName")!= null && parmsMap.get("href") == null  && parmsMap.get("methodName") == null){
            method = parmsMap.get("serviceName").toString() +"/list";
        }else if(parmsMap.get("serviceName")!= null && parmsMap.get("methodName") != null){
            method = parmsMap.get("serviceName").toString() +"/"+parmsMap.get("methodName").toString();
        }else if(parmsMap.get("href") == null){
            method = "list";
        }else{
            Object res =  parmsMap.get("href");
            if(res instanceof JSONObject){
                Map<String,Object> href_map = (Map<String, Object>)res;
                if(href_map.get("type")!= null){
                    if(href_map.get("type").equals("validate")){
                        String href_ = href_map.get("from").toString();
                        if (href_.indexOf("?")>=0 && href_.indexOf(":")>=0){
                            Map<String,Object> dataMap = updateSimulateMain();
                            String[] keys = StringUtils.getKeys(href_);
                            for (String key:keys) {
                                if(dataMap.get(key) == null){
                                    Toast.makeText(mContext,"无效参数："+key,Toast.LENGTH_SHORT).show();
                                    return "";
                                }
                                href_ = href_.replace("["+key+"]","'"+dataMap.get(key).toString()+"'");
                            }
                            method = SizheTool.eval_back(href_);
                        }
                    }
                }
            }else {
                if (parmsMap.get("serviceName")!= null){
                    method = parmsMap.get("serviceName").toString() +"/"+parmsMap.get("href").toString();
                }else{
                    method = parmsMap.get("href").toString();
                }

            }
        }
        return  method;
    }

    /**
     * 判断是否分页
     * @param gldn
     * @return
     */
    public boolean getLimit(String gldn){
        Map<String,String> parmsMap = (Map<String, String>) parms.get(gldn);
        String method = "";
        if(parmsMap.get("limit") != null) {
            return  false;
        } else{
            return  true;
        }
    }

    public String getColumnName(String gldn){
        Map<String,String> parmsMap = (Map<String, String>) parms.get(gldn);
        String method = "";

        if (parmsMap == null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,"格式文件错误",Toast.LENGTH_SHORT).show();
                    WeiboDialogUtils.closeDialog(mDialog);
                }
            });
            return "err";
        }

        if(parmsMap.get("col_name") == null){
            method = "name";
        }else{
            method = parmsMap.get("col_name").toString();
        }

        return  method;
    }

    /**
     * 创建明细控件
     * @param mx1gs_list
     * @param mx1_data
     * @param source_id
     * @param view_id
     * @param title_id
     * @param title
     * @param showMap
     */
    public void createMx(List<Map<String,Object>> mx1gs_list,List<Map<String,Object>> mx1_data,int source_id,int view_id,int title_id,int add_id,String title,Map<String,String>showMap){
        //添加明细1
        UITableView tableView = null;
        if(mx1gs_list != null && mx1gs_list.size()>0){
            RelativeLayout mx1 = (RelativeLayout) findViewById(source_id);
            mx1.setVisibility(View.VISIBLE);
            tableView = (UITableView) findViewById(view_id);
            tableView.setVisibility(View.VISIBLE);
            ImageButton imb = (ImageButton) findViewById(add_id);
            imb.setVisibility(View.GONE);
            TextView mx1_title = (TextView) findViewById(title_id);
            mx1_title.setText(title);
            createmxView2_noClick(mx1gs_list,mx1_data,tableView,showMap);
        }
    }

    /**
     * 根据数据格式 与 数据 创建 tableView 无点击事件
     * @param mx1gs_list
     * @param _datalist
     * @param tableView
     * @param showMap
     */
    public void createmxView2_noClick(List<Map<String,Object>> mx1gs_list,List<Map<String,Object>> _datalist,UITableView tableView,Map<String,String> showMap) {

        Map<String,Object> map = null,showMap1= null;
        List<IListItem> basicListItem;
        String res = "";
        if (_datalist == null || _datalist.size() == 0){return;}
        String name,code,type,val="";
        for (int i=0,n = _datalist.size();i<n;i++){
            map = _datalist.get(i);
            res = "";
            for (int k=0,nk = mx1gs_list.size();k<nk;k++){
                showMap1  = mx1gs_list.get(k);
                if((showMap1.get("is_select") !=null &&!showMap1.get("is_select").toString().equals(""))
                        || ( showMap1.get("is_write") !=null && !showMap1.get("is_write").toString().equals(""))
                        || ( showMap1.get("is_visible_phone") != null && !showMap1.get("is_visible_phone").toString().equals(""))){
                    name = showMap1.get("name").toString();
                    code = showMap1.get("code").toString();
                    type = showMap1.get("type").toString();
                    if (map.get(code) != null){
                        val  = map.get(code).toString();
                        Object resval = AppUtils.returnVal(type,val);
                        val = resval.toString();
                    }
                    res += name+":"+ val +" ";
                }
            }
            tableView.addBasicItem(String.valueOf(i+1), res,0,true,1);
        }
        tableView.commit();
    }

    public  class MyOnCliclListener implements View.OnClickListener{

        private Context context;
        private UITableView tableView,mainView;
        private Map<String,Object> mxMap = new HashMap<String, Object>();
        private int RESULT_OK,index;
        Activity activity;
        Intent intent;
        private List<Map<String,Object>> mx1gs_list,mx2gs_list;
        private Class class1;
        private String className,parms1,parms2,ywlx;

        public MyOnCliclListener(Context context,UITableView tableView,int RESULT_OK,String className,List<Map<String,Object>> mx1gs_list,UITableView mainView,String parms1){
            this.context = context;
            this.tableView = tableView;
            this.mainView = mainView;
            this.RESULT_OK =RESULT_OK;
            activity = (Activity) context;
            this.className = className;
            this.mx1gs_list = mx1gs_list;
            this.parms1 = parms1;
        }

        public MyOnCliclListener(Context context,UITableView tableView,int RESULT_OK,String className,List<Map<String,Object>> mx1gs_list,UITableView mainView,String parms1,String ywlx){
            this.context = context;
            this.tableView = tableView;
            this.mainView = mainView;
            this.RESULT_OK =RESULT_OK;
            activity = (Activity) context;
            this.className = className;
            this.mx1gs_list = mx1gs_list;
            this.parms1 = parms1;
            this.ywlx = ywlx;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.title_back2:
                    ActivityController.hiddenInput(activity);
                    activity.finish();
                    break;
                case  R.id.title_ok2:

                    List<IListItem>  list =  tableView.getIListItem();
                    BasicItem item = null;
                    String code,val,res;
                    //如果新增 则新建
                    if(ywlx != null && ywlx.equals("add")){
                        if (main_data.size() == 0 && tableView.get_zhuData() != null){
                            main_data.putAll(tableView.get_zhuData());
                        }
                    }
                    mxMap.putAll(main_data);
                    for (int i=0,n=list.size();i<n;i++){
                        item = (BasicItem) list.get(i);
                        code = item.getCode();
                        val = item.getSubtitle();
                        if (code.equals("rowid") || code.equals("dyrowid")){continue;}
                        if ((item.getDataType().equals("int") || item.getDataType().equals("double")) && val.equals("") && code.indexOf("id") == -1){
                            mxMap.put(code,"0");
                        }else{
                            mxMap.put(code,val);
                        }

                        if (item.getVal()!= null){
                            mxMap.put(code,item.getVal());
                        }
                    }
                    //将数据 写回 视图控件
                    tableView.set_zhuData(mxMap);

                    if (ruleMap != null && ruleMap.get("repetition")!=null){
                        String repetition =  ruleMap.get("repetition").toString();
                        String[] keys = repetition.split(",");
                        boolean flage = false;

                        if (RESULT_OK == 1){
                            flage = ActivityController.checkMxIsRepetition(tableView.get_dataList(),mxMap,keys,ywlx);
                        }else if(RESULT_OK == 2){
                            flage = ActivityController.checkMxIsRepetition(tableView.get_dataList(),mxMap,keys,ywlx);
                        }else if(RESULT_OK == 3){
                            flage = ActivityController.checkMxIsRepetition(tableView.get_dataList(),mxMap,keys,ywlx);
                        }

                        if (flage){
                            Toast.makeText(mContext, mContext.getString(R.string.save_mxRepetition_text), Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                    if (isOnlyCheck){
                        if(!checkXsddmx()) return;
                    }
                    //验证表达式 明细表单名称
                    if (!saveExpressionValidate(className)) return;
                    //验证销售订单 特殊 验证
                    //20171219 wang 修改验证方式
                    if (isCheck){
                        if (editTextValidator.validate()) {
                            Toast.makeText(mContext, "通过校验", Toast.LENGTH_SHORT).show();
                        }else{return;}
                    }

                    if (parms1.equals("jsr")){
                        mxMap.put("jsrid",AppUtils.user.get_zydnId());
                        mxMap.put("bmid",AppUtils.user.get_bmdnId());
                    }

                    if(ywlx != null && ywlx.equals("add") && mxMap.get("rowid") == null){
                        mxMap.put("rowid",IniUtils.getFixLenthString(5));
                        mxMap.put("zt","0");
                    }else{
                        mxMap.put("zt","1");
                    }

                    intent = new Intent();
                    intent.putExtra("state","success");
                    parmsMap.put("data",FastJsonUtils.mapToString(mxMap));
                    parmsMap.put("gldata",FastJsonUtils.ListMapToListStr(tableView.get_gxdataList()));
                    parmsMap.put("mxdata",FastJsonUtils.ListMapToListStr(tableView.get_dataList()));
                    parmsMap.put("removedata",FastJsonUtils.ListMapToListStr(tableView.get_removeList()));
                    intent.putExtras(AppUtils.setParms(ywtype,parmsMap));
                    setResult(RESULT_OK,intent);

                    //隐藏键盘
                    ActivityController.hiddenInput(activity);

                    activity.finish();
                    isUpdate =true;
                    break;
                case R.id.mx1_add:
                    intent = new Intent();
                    parmsMap = new HashMap<String, Object>();
                    parmsMap.put("mainData","{}");
                    parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx1gs_list));
                    index = tableView.getCount();
                    parmsMap.put("index",index);
                    parms2  = setParms(parms1,parms2,mainView);
                    parmsMap.put("parms",parms2);
                    intent.putExtras(AppUtils.setParms("add",parmsMap));
                    class1 = ReflectHelper.getCalss(className);
                    intent.setClass(activity,class1);
                    startActivityForResult(intent,RESULT_OK);
                    break;
                case R.id.mx2_add:
                    intent = new Intent();
                    parmsMap = new HashMap<String, Object>();
                    parmsMap.put("mainData","{}");
                    parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx1gs_list));
                    index = tableView.getCount();
                    parmsMap.put("index",index);
                    parms2  = setParms(parms1,parms2,mainView);
                    parmsMap.put("parms",parms2);
                    intent.putExtras(AppUtils.setParms("add",parmsMap));
                    class1 = ReflectHelper.getCalss(className);
                    intent.setClass(activity,class1);
                    startActivityForResult(intent,RESULT_OK);
                    break;
                case R.id.mx3_add:
                    intent = new Intent();
                    parmsMap = new HashMap<String, Object>();
                    parmsMap.put("mainData","{}");
                    parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx1gs_list));
                    index = tableView.getCount();
                    parmsMap.put("index",index);
                    parms2  = setParms(parms1,parms2,mainView);
                    parmsMap.put("parms",parms2);
                    intent.putExtras(AppUtils.setParms("add",parmsMap));
                    class1 = ReflectHelper.getCalss(className);
                    intent.setClass(activity,class1);
                    startActivityForResult(intent,RESULT_OK);
                    break;
                case R.id.mx4_add:
                    intent = new Intent();
                    parmsMap = new HashMap<String, Object>();
                    parmsMap.put("mainData","{}");
                    parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx1gs_list));
                    index = tableView.getCount();
                    parmsMap.put("index",index);
                    parms2  = setParms(parms1,parms2,mainView);
                    parmsMap.put("parms",parms2);
                    intent.putExtras(AppUtils.setParms("add",parmsMap));
                    class1 = ReflectHelper.getCalss(className);
                    intent.setClass(activity,class1);
                    startActivityForResult(intent,RESULT_OK);
                    break;
                case R.id.mx5_add:
                    intent = new Intent();
                    parmsMap = new HashMap<String, Object>();
                    parmsMap.put("mainData","{}");
                    parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx1gs_list));
                    index = tableView.getCount();
                    parmsMap.put("index",index);
                    parms2  = setParms(parms1,parms2,mainView);
                    parmsMap.put("parms",parms2);
                    intent.putExtras(AppUtils.setParms("add",parmsMap));
                    class1 = ReflectHelper.getCalss(className);
                    intent.setClass(activity,class1);
                    startActivityForResult(intent,RESULT_OK);
                    break;
                default:
                    break;
            }
        }
    }

    //特殊验证
    public  boolean checkXsddmx(){return false;};

    private boolean saveExpressionValidate(String mxCode){
        Map<String,Object> rule_map = (Map<String, Object>) get_catch().get(menu_code+"_rule");
        Map<String,Object> gs_map = (Map<String, Object>) get_catch().get(menu_code+"_mapAll");

        Map<String,Object> mxRule_map = null;
        if(rule_map != null){
            if (rule_map.get(mxCode)!= null){
                Object mxRuleMap = rule_map.get(mxCode);
                if (mxRuleMap != null && mxRuleMap instanceof JSONObject){
                    mxRule_map = (Map<String, Object>) mxRuleMap;
                }
            }

            if (mxRule_map == null){return true;}
            Object save_rule = mxRule_map.get("save");
            if(save_rule != null){
                Map<String,Object> save_map = null;
                if (save_rule instanceof JSONObject){
                    save_map = (Map<String, Object>) save_rule;
                }else{
                    Toast.makeText(mContext,getString(R.string.err_saverule_notfound),Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(save_map.get("count")!= null){
                    List<Map<String,Object>> count_list = (List<Map<String, Object>>) save_map.get("count");
                    for (Map<String,Object> count_map:count_list) {
                        if(count_map.get("from") != null){
                            String from = count_map.get("from").toString();
                            Map<String,Object> mx_map = ActivityController.getMxOptions(from,gs_map);
                            String name = (String) mx_map.get("gl_name");
                            if (name == null || name.equals("")){
                                Toast.makeText(mContext,getString(R.string.err_mxdata_notfound),Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            int index = Integer.parseInt(name.replace("mx","").replace("gs_list",""));
                            switch (index){
                                case 1:
                                    if (myAdapter1.getDatas() == null || myAdapter1.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                                case 2:
                                    if (myAdapter2.getDatas() == null || myAdapter2.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                                case 3:
                                    if (myAdapter3.getDatas() == null || myAdapter3.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                                case 4:
                                    if (myAdapter4.getDatas() == null || myAdapter4.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                                case 5:
                                    if (myAdapter5.getDatas() == null || myAdapter5.getDatas().size()==0){
                                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                    break;
                            }
                        }
                    }
                }
                if(save_map.get("validate")!= null){
                    List<Map<String,Object>> validate_list = (List<Map<String, Object>>) save_map.get("validate");
                    for (Map<String,Object> validate_map:validate_list) {
                        String expression = validate_map.get("expression").toString();
                        Object from = validate_map.get("from");
                        if (from != null){
                            if (from instanceof JSONObject){
                                Map<String,Object> fromMap = (Map<String, Object>) from;
                                if (fromMap.get("serviceName")!= null){

                                }
                            }
                        }
                        try {
                            String expn = ActivityController.replaceExpression(mContext,expression);
                            String res = SizheTool.eval2(expn,"2");
                            if (res == null){Toast.makeText(mContext,"表达式异常",Toast.LENGTH_SHORT).show();return  false;}
                            if (res.equals("1.0") || res.equals("1.00")||res.equals("1")){
                                if(!ActivityController.validateCount(mContext,validate_map,gs_map)){
                                    Toast.makeText(mContext,validate_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                    return  false;
                                };
                            }
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        return true;
    }

    public  class MyOnCliclListener2 implements View.OnClickListener{

        private Context context;
        private UITableView mainView;
        private MyAdapter2 adapter;
        private Map<String,Object> mxMap = new HashMap<String, Object>();
        private int RESULT_OK,index;
        Activity activity;
        Intent intent;
        private List<Map<String,Object>> mxgs_list,mx2gs_list;
        private Class class1;
        private String className,parms1,parms2,ywcode;
        private boolean add_jg;

        public MyOnCliclListener2(Context context,MyAdapter2 adapter,int RESULT_OK,String className,List<Map<String,Object>> mxgs_list,UITableView mainView,String parms1){
            this.context = context;
            this.adapter = adapter;
            this.mainView = mainView;
            this.RESULT_OK =RESULT_OK;
            activity = (Activity) context;
            this.className = className;
            this.mxgs_list = mxgs_list;
            this.parms1 = parms1;
        }

        public MyOnCliclListener2(Context context,MyAdapter2 adapter,int RESULT_OK,String className,List<Map<String,Object>> mxgs_list,UITableView mainView,String parms1,String code){
            this.context = context;
            this.adapter = adapter;
            this.mainView = mainView;
            this.RESULT_OK =RESULT_OK;
            activity = (Activity) context;
            this.className = className;
            this.mxgs_list = mxgs_list;
            this.parms1 = parms1;
            this.ywcode = code;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.title_back2:
                    activity.finish();
                    break;
                case  R.id.title_ok2:
                    List<IListItem>  list =  tableView.getIListItem();
                    BasicItem item = null;
                    String code,val,res;
                    for (int i=0,n=list.size();i<n;i++){
                        item = (BasicItem) list.get(i);
                        code = item.getCode();
                        val = item.getSubtitle();
                        if (item.getDataType().equals("int") && val.equals("")){
                            mxMap.put(code,"0");
                        }else{
                            mxMap.put(code,val);
                        }
                    }
                    if (parms1.equals("jsr")){
                        mxMap.put("jsrid",AppUtils.user.get_zydnId());
                        mxMap.put("bmid",AppUtils.user.get_bmdnId());
                    }
                    mxMap.put("zt",1);
                    intent = new Intent();
                    intent.putExtra("state","success");
                    parmsMap.put("data",FastJsonUtils.mapToString(mxMap));
                    intent.putExtras(AppUtils.setParms(ywtype,parmsMap));
                    setResult(RESULT_OK,intent);
                    activity.finish();
                    break;
                case R.id.mx1_add:
                    addMxAction(parms1,parms2,mainView,adapter,mxgs_list,className,activity,RESULT_OK,ywcode);
                    isUpdate = true;
                    break;
                case R.id.mx2_add:
                    addMxAction(parms1,parms2,mainView,adapter,mxgs_list,className,activity,RESULT_OK,ywcode);
                    isUpdate = true;
                    break;
                case R.id.mx3_add:
                    addMxAction(parms1,parms2,mainView,adapter,mxgs_list,className,activity,RESULT_OK,ywcode);
                    isUpdate = true;
                    break;
                case R.id.mx4_add:
                    addMxAction(parms1,parms2,mainView,adapter,mxgs_list,className,activity,RESULT_OK,ywcode);
                    isUpdate = true;
                    break;
                case R.id.mx5_add:
                    addMxAction(parms1,parms2,mainView,adapter,mxgs_list,className,activity,RESULT_OK,ywcode);
                    isUpdate = true;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 得到 数据集 编号
     * @param index
     * @return
     */
    public String getCode (int index){
        Map<String,Object> gs_map = (Map<String, Object>) get_catch().get(menu_code+"_mapAll");
        Set<String>  sets = gs_map.keySet();
        List<String> list = new ArrayList<String>();
        list.addAll(sets);
        String _key = "mx"+String.valueOf(index)+"gs_list";
        String gs_name = list.get(index-1);
        Map<String,Object> gs_hhe = (Map<String, Object>) gs_map.get(_key);
        return (String) gs_hhe.get("code");
    }

    /**
     * 更新主数据
     */
    public void updateMain(){

        List<IListItem> main_list = null;
        if(tableView == null){
            UITableView table_view = (UITableView) get_data().get("mainView");
            main_list = table_view.getIListItem();
        }else{
            main_list = tableView.getIListItem();
        }

        BasicItem item = null;
        String code,val,method,resid="";
        for (int i=0,n = main_list.size();i< n ;i++){
            item = (BasicItem) main_list.get(i);
            code = item.getCode();
            if (item.getVal()!= null){
                val = item.getVal();
            }else{
                val = item.getSubtitle();
            }

            if(val != null && !val.equals("")){
                main_data.put(code,val);
            }
        }
    }

    /**
     * 创建模拟 主 数据
     * @return
     */
    public Map<String, Object> updateSimulateMain(){

        Map<String,Object> simulateMap = new HashMap<String, Object>();
        List<IListItem> main_list = null;
        UITableView table_view = (UITableView) get_data().get("mainView");
        main_list = table_view.getIListItem();

        BasicItem item = null;
        String code,val,method,resid="";
        for (int i=0,n = main_list.size();i< n ;i++){
            item = (BasicItem) main_list.get(i);
            code = item.getCode();
            val = item.getSubtitle();
            if(val != null && !val.equals("")){
                simulateMap.put(code,val);
            }
        }
        return simulateMap;
    }

    /**
     * 得到当前数据集 新增规则
     * @param index
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public boolean add_Validate(int index) throws NoSuchFieldException, IllegalAccessException {

        String code = getCode(index);
        Map<String,Object> rule_map = (Map<String, Object>) get_catch().get(menu_code+"_rule");
        if(rule_map == null){
            return  true;
        }
        //code Map
        Map<String,Object> code_map = (Map<String, Object>) rule_map.get(code);
        if(code_map == null){return  true;}
        Map<String,Object> add_rule = (Map<String, Object>) (code_map.get("add"));
        if (add_rule != null){
            if(add_rule.get("validate")!= null){
                Map<String,Object> validate_map = (Map<String, Object>) add_rule.get("validate");
                if(validate_map.get("count") != null){
                    Map<String,Object> count_map = (Map<String, Object>) validate_map.get("count");
                    code = count_map.get("from").toString();
                    List<Map<String,Object>> list_ = getMxData(this,code);
                    if (list_.size() == 0){
                        Toast.makeText(mContext,count_map.get("msg").toString(),Toast.LENGTH_SHORT).show();
                        return  false;
                    }
                }else if(validate_map.get("required") != null){
                    Map<String,Object> required_map = (Map<String, Object>) validate_map.get("required");
                    //同步主数据
                    updateMain();
                    Set<String> requireds = required_map.keySet();
                    for (String code1:requireds) {
                        if(main_data.get(code1)== null ||(main_data.get(code1) != null && main_data.get(code1).equals(""))){
                            Toast.makeText(mContext,"请输入"+required_map.get(code1),Toast.LENGTH_SHORT).show();
                            return  false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 添加明细
     * @param parms1
     * @param parms2
     * @param mainView
     * @param className
     * @param activity
     */
    public void addMxAction(String parms1,String parms2,UITableView  mainView,MyAdapter2 adapter,List<Map<String,Object>> mxgs_list,String className, Activity activity,int RESULT_OK,String code){
        //验证add 规则
        try {
            if(!add_Validate(RESULT_OK)){
                return;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //参数
        if (!parms1.equals("")){
            parms2  = setParms(parms1,parms2,mainView);
            if(parms2.equals("false")){
                return;
            }
        }

        //验证表单
        if(!mainView.getEditTextValidator().validate()){
            return;
        }

        Intent intent = new Intent();
        parmsMap = new HashMap<String, Object>();

        Map<String,Object> mainMap = new HashMap<String,Object>();
        mainMap.put("rowid",IniUtils.getFixLenthString(5));
        mainMap.put("dyrowid",mainMap.get("rowid"));
        parmsMap.put("mainData",FastJsonUtils.mapToString(mainMap));
        parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mxgs_list));
        //主数据
        get_data().put("mainView",mainView);
        //明细数据
        parmsMap.put("mxData",FastJsonUtils.ListMapToListStr(adapter.getDatas()));
        int index = adapter.getItemCount();
        parmsMap.put("index",index);
        parmsMap.put("parms",parms2);

        parmsMap.put("menucode",menu_code);
        parmsMap.put("menuname",menu_name);
        parmsMap.put("code",code);
        parmsMap.put("RESULT_OK",RESULT_OK);

        intent.putExtras(AppUtils.setParms("add",parmsMap));
        Class class1 = ReflectHelper.getCalss(className);
        intent.setClass(activity,class1);
        startActivityForResult(intent,RESULT_OK);
    }


    public String setParms(String parms1,String parms2,UITableView mainView){
        if (!parms1.equals("")){
            String[] parms1s =  parms1.split(","),ps;
            String val1="";
            parms2 = "";
            int i=0;
            for (String key:parms1s){
                if(key.indexOf(":")>=0){
                    String[] keys = key.split(":");
                    key = keys[0];
                    BasicItem item1 = ActivityController.getItem(mainView.getIListItem(),keys[1]);
                    if(item1 == null){
                        val1 = keys[1];
                    }else {
                        val1 = item1.getSubtitle();
                    }

                }else{
                    BasicItem item1 = ActivityController.getItem(mainView.getIListItem(),key);
                    val1 = item1.getSubtitle();
                }
                if(i == parms1s.length-1){
                    parms2+= key+":"+val1;
                }else{
                    parms2+= key+":"+val1+",";
                }
                i++;
            }
        }
        return  parms2;
    }

    /**
     * 计算主数据列
     * @param mx1_data
     * @param update_col_name
     * @param add_col_name
     * @param tableView
     * @param main_data
     */
    public void Calculation_Main(List<Map<String,Object>> mx1_data,String update_col_name,String add_col_name,UITableView tableView,Map<String,Object>main_data){
        Map<String,Object> map;
        double je = 0;
        for (int i=0,n=mx1_data.size();i<n;i++){
            map = mx1_data.get(i);
            je += Double.parseDouble(map.get(add_col_name).toString());
        }
        main_data.put(update_col_name,je);
        BasicItem item = ActivityController.getItem(tableView.getIListItem(),update_col_name);
        item.setSubtitle(String.valueOf(je));
        tableView.setupBasicItemValue(tableView.getLayoutList(item.getIndex()),item,item.getIndex());
    }

    /**
     * 得到合计值
     * @param mx1_data
     * @param add_col_name
     * @return
     */
    public String Calculation_Val(List<Map<String,Object>> mx1_data,String add_col_name){
        Map<String,Object> map;
        double je = 0,val = 0;
        for (int i=0,n=mx1_data.size();i<n;i++){
            map = mx1_data.get(i);
            if (map.get(add_col_name) != null){
                if (map.get(add_col_name).equals("")){
                    val = 0;
                }else{
                    val = Double.parseDouble(map.get(add_col_name).toString());
                }
                je +=val ;
            }else{
                je += 0;
            }
        }
        return String.valueOf(je);
    }

    /**
     * 合计列
     * @param mx1_data
     * @param add_col_name
     * @return
     */
    public double getSumBycol(List<Map<String,Object>> mx1_data,String add_col_name){
        Map<String,Object> map;
        double je = 0;
        for (int i=0,n=mx1_data.size();i<n;i++){
            map = mx1_data.get(i);
            je += Double.parseDouble(map.get(add_col_name).toString());
        }
        return je;
    }


    public void update_colval_Main(String update_col_name,String val,UITableView tableView,Map<String,Object>main_data){

        main_data.put(update_col_name,val);
        BasicItem item = ActivityController.getItem(tableView.getIListItem(),update_col_name);
        if (item !=null ){
            item.setSubtitle(String.valueOf(val));
            tableView.setupBasicItemValue(tableView.getLayoutList(item.getIndex()),item,item.getIndex());
        }
    }

    public String get_colval_Main(String update_col_name,UITableView tableView){

        BasicItem item = ActivityController.getItem(tableView.getIListItem(),update_col_name);
        if(item == null){
            Toast.makeText(mContext,"未找到项目",Toast.LENGTH_SHORT).show();
            return "err";
        }
        if(item.getSubtitle().equals("")){
            return "0";
        }
        return  item.getSubtitle();
    }

    public void reshData(){

        Map<String,Object> gs_map =  (Map<String, Object>) get_catch().get(menu_code+"_mapAll"),res_map;
        Object res;
        try {
            Set<String>  sets = gs_map.keySet();
            String pars = "{id:"+id+"}";
            pars = StringUtils.strTOJsonstr(pars);

            if (ywtype.equals("edit")){
                Map<String,Object> map  = new HashMap<String, Object>();
                if (serviceName != null &&!serviceName.equals("")){
                    map.put("menucode",serviceName);
                }else{
                    map.put("menucode",menu_code);
                }

                map.put("parms",pars);

                String res1 = HttpClientUtils.webService("findFormData",FastJsonUtils.mapToString(map));
                if (ActivityController.getPostState(res1) == 0){
                    Map<String,Object> resMap = FastJsonUtils.strToMap(res1);
                    if (resMap != null){
                        //刷新数据
                        main_data.putAll ((Map<String, Object>) resMap.get("mainData"));
                        if (main_data != null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tableView.reshView(main_data,maings_list);
                                }
                            });
                        }
                        for (String key:sets) {
                            if (key.equals("add")){
                                continue;
                            }
                            int case_1 = Integer.parseInt(key.replace("mx","").replace("gs_list",""));
                            switch (case_1){
                                case 1:
                                    if(mx1gs_list == null){
                                        mx1gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx1gs_list").toString());
                                    }
                                    res_map = (Map<String, Object>) gs_map.get(key);
                                    res = resMap.get(res_map.get("code")+"Data");
                                    if (res != null){
                                        mx1_data.clear();
                                        mx1_data.addAll((List<Map<String,Object>>) res) ;
                                    }
                                    break;
                                case 2:
                                    if(mx2gs_list == null){
                                        mx2gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx2gs_list").toString());
                                    }

                                    res_map = (Map<String, Object>) gs_map.get(key);
                                    res = resMap.get(res_map.get("code")+"Data");
                                    if (res != null){
                                        mx2_data.clear();
                                        mx2_data.addAll((List<Map<String,Object>>) res) ;
                                    }

                                    break;
                                case 3:
                                    if(mx3gs_list == null){
                                        mx3gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx3gs_list").toString());
                                    }
                                    res_map = (Map<String, Object>) gs_map.get(key);
                                    res = resMap.get(res_map.get("code")+"Data");
                                    if (res != null){
                                        mx3_data.clear();
                                        mx3_data.addAll((List<Map<String,Object>>) res) ;
                                    }
                                    break;
                                case 4:
                                    if(mx4gs_list == null){
                                        mx4gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx4gs_list").toString());
                                    }
                                    res_map = (Map<String, Object>) gs_map.get(key);
                                    res = resMap.get(res_map.get("code")+"Data");
                                    if (res != null){
                                        mx4_data.clear();
                                        mx4_data.addAll((List<Map<String,Object>>) res) ;
                                    }
                                    break;
                                case 5:
                                    if(mx5gs_list == null){
                                        mx5gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx5gs_list").toString());
                                    }
                                    res_map = (Map<String, Object>) gs_map.get(key);
                                    res = resMap.get(res_map.get("code")+"Data");
                                    if (res != null){
                                        mx5_data.clear();
                                        mx5_data.addAll((List<Map<String,Object>>) res) ;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }else{
                    HttpClientUtils.sendMessageErr(ActivityController.getPostState(res1),mContext);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createMx(){

        //加载明细数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                reshData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String,Object> gs_map =  (Map<String, Object>) get_catch().get(menu_code+"_mapAll"),res_map;
                        Map<String,String> gx_map = new HashMap<String,String>();
                        Object res;
                        String package_name="",code="",class_name="";
                        String[] contents;
                        try {
                            Set<String>  sets = gs_map.keySet();
                            for (String key:sets) {
                                if(key.equals("add")){
                                    continue;
                                }

                                int case_1 = Integer.parseInt(key.replace("mx","").replace("gs_list",""));
                                class_name = "com.cn.wti.activity.common.CommonMxEditActivity";

                                switch (case_1){
                                    case 1:
                                        res_map = (Map<String, Object>) gs_map.get(key);
                                        if (mx1gs_list == null){
                                            mx1gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx1gs_list").toString());
                                        }
                                        //添加明细1
                                        if(mx1gs_list != null && mx1gs_list.size()>0){
                                            mx1.setVisibility(View.VISIBLE);
                                            mx1_title.setText(res_map.get("title").toString());
                                            contents= res_map.get("contents").toString().split(",",-1);

                                            //20171211 wang更新查看内容
                                            contents = BussinessUtils.updateContents(contents,FastJsonUtils.findListStrByKey_and_Val(mx1gs_list,"is_visible_phone","code","否"));

                                            code = res_map.get("code").toString();
                                            mCheck1 = new boolean[mx1_data.size()];
                                            ActivityController.setLayoutManager(mx1_rView,mContext);
                                            mxSetMap = new HashMap<String, Object>();
                                            mxSetMap.put("index",case_1);
                                            mxSetMap.put("menucode",menu_code);
                                            mxSetMap.put("menuname",menu_name);
                                            mxSetMap.put("code",code);
                                            mxSetMap.put("class_name",class_name);
                                            mxSetMap.put("gs",mx1gs_list);
                                            myAdapter1 = new MyAdapter2(mContext,mx1_data,AppUtils.getScreenWidth(mContext),new String[]{},contents,R.layout.list_item_middle3_01,mCheck1,mxSetMap);
                                            mx1_rView.setAdapter(myAdapter1);

                                            //隐藏添加功能
                                            res_map.put("isEdit",isEdit);
                                            boolean state = ActivityController.setVisible_addbutton(res_map,mx1_add);
                                            if (state){
                                                mx1_add.setOnClickListener(new MyOnCliclListener2(mContext,myAdapter1,1,class_name,mx1gs_list,tableView,"",code));
                                            }else{
                                                mx1_add.setVisibility(View.GONE);
                                            }
                                        }

                                        //添加明细与数据关系
                                        gx_map.put(code,"mx1_data");
                                        break;
                                    case 2:
                                        res_map = (Map<String, Object>) gs_map.get(key);
                                        contents= res_map.get("contents").toString().split(",",-1);
                                        if (mx2gs_list == null){
                                            mx2gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx2gs_list").toString());
                                        }
                                        //添加明细2
                                        if(mx2gs_list != null && mx2gs_list.size()>0){
                                            mx2.setVisibility(View.VISIBLE);
                                            mx2_title.setText(res_map.get("title").toString());
                                            contents= res_map.get("contents").toString().split(",",-1);

                                            //20171211 wang更新查看内容
                                            contents = BussinessUtils.updateContents(contents,FastJsonUtils.findListStrByKey_and_Val(mx2gs_list,"is_visible_phone","code","否"));

                                            code = res_map.get("code").toString();
                                            mCheck2 = new boolean[mx2_data.size()];
                                            ActivityController.setLayoutManager(mx2_rView,mContext);
                                            mxSetMap = new HashMap<String, Object>();
                                            mxSetMap.put("index",case_1);
                                            mxSetMap.put("menucode",menu_code);
                                            mxSetMap.put("menuname",menu_name);
                                            mxSetMap.put("code",code);
                                            mxSetMap.put("class_name",class_name);
                                            mxSetMap.put("gs",mx2gs_list);
                                            myAdapter2 = new MyAdapter2(mContext,mx2_data,AppUtils.getScreenWidth(mContext),new String[]{},contents,R.layout.list_item_middle3_01,mCheck2,mxSetMap);

                                            mx2_rView.setAdapter(myAdapter2);

                                            //隐藏添加功能
                                            res_map.put("isEdit",isEdit);
                                            boolean state = ActivityController.setVisible_addbutton(res_map,mx2_add);
                                            if (state){
                                                mx2_add.setOnClickListener(new MyOnCliclListener2(mContext,myAdapter2,2,class_name,mx2gs_list,tableView,"",code));
                                            }else{
                                                mx2_add.setVisibility(View.GONE);
                                            }
                                        }

                                        //添加明细与数据关系
                                        gx_map.put(code,"mx2_data");
                                        break;
                                    case 3:
                                        res_map = (Map<String, Object>) gs_map.get(key);
                                        contents= res_map.get("contents").toString().split(",",-1);
                                        if (mx3gs_list == null){
                                            mx3gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx3gs_list").toString());
                                        }
                                        //添加明细3
                                        if(mx3gs_list != null && mx3gs_list.size()>0){
                                            mx3.setVisibility(View.VISIBLE);
                                            mx3_title.setText(res_map.get("title").toString());
                                            contents= res_map.get("contents").toString().split(",",-1);
                                            //20171211 wang更新查看内容
                                            contents = BussinessUtils.updateContents(contents,FastJsonUtils.findListStrByKey_and_Val(mx3gs_list,"is_visible_phone","code","否"));

                                            code = res_map.get("code").toString();

                                            if (ywtype.equals("add")){
                                                if (res_map.get("init")!=null){
                                                    Map<String,Object> initMap = null;
                                                    List<Map<String,Object>> initList = (List<Map<String, Object>>) res_map.get("init");
                                                    if (initList!= null){
                                                        mx3_data.clear();
                                                        mx3_data.addAll(initList);
                                                    }
                                                }
                                            }

                                            mCheck3 = new boolean[mx3_data.size()];
                                            ActivityController.setLayoutManager(mx3_rView,mContext);
                                            mxSetMap = new HashMap<String, Object>();
                                            mxSetMap.put("index",case_1);
                                            mxSetMap.put("menucode",menu_code);
                                            mxSetMap.put("menuname",menu_name);
                                            mxSetMap.put("code",code);
                                            mxSetMap.put("class_name",class_name);
                                            mxSetMap.put("gs",mx3gs_list);
                                            myAdapter3 = new MyAdapter2(mContext,mx3_data,AppUtils.getScreenWidth(mContext),new String[]{},contents,R.layout.list_item_middle3_01,mCheck3,mxSetMap);

                                            mx3_rView.setAdapter(myAdapter3);

                                            //隐藏添加功能
                                            res_map.put("isEdit",isEdit);
                                            boolean state = ActivityController.setVisible_addbutton(res_map,mx3_add);
                                            if (state){
                                                mx3_add.setOnClickListener(new MyOnCliclListener2(mContext,myAdapter3,3,class_name,mx3gs_list,tableView,"",code));
                                            }else{
                                                mx3_add.setVisibility(View.GONE);
                                            }
                                        }

                                        //添加明细与数据关系
                                        gx_map.put(code,"mx3_data");
                                        break;
                                    case 4:
                                        res_map = (Map<String, Object>) gs_map.get(key);
                                        contents= res_map.get("contents").toString().split(",",-1);
                                        if (mx4gs_list == null){
                                            mx4gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx4gs_list").toString());
                                        }
                                        //添加明细4
                                        if(mx4gs_list != null && mx4gs_list.size()>0){
                                            mx4.setVisibility(View.VISIBLE);
                                            mx4_title.setText(res_map.get("title").toString());
                                            contents= res_map.get("contents").toString().split(",",-1);
                                            //20171211 wang更新查看内容
                                            contents = BussinessUtils.updateContents(contents,FastJsonUtils.findListStrByKey_and_Val(mx4gs_list,"is_visible_phone","code","否"));

                                            code = res_map.get("code").toString();

                                            if (ywtype.equals("add")){
                                                if (res_map.get("init")!=null){
                                                    Map<String,Object> initMap = null;
                                                    List<Map<String,Object>> initList = (List<Map<String, Object>>) res_map.get("init");
                                                    if (initList!= null){
                                                        mx4_data.clear();
                                                        mx4_data.addAll(initList);
                                                    }
                                                }
                                            }

                                            mCheck4 = new boolean[mx4_data.size()];
                                            ActivityController.setLayoutManager(mx4_rView,mContext);
                                            mxSetMap = new HashMap<String, Object>();
                                            mxSetMap.put("index",case_1);
                                            mxSetMap.put("menucode",menu_code);
                                            mxSetMap.put("menuname",menu_name);
                                            mxSetMap.put("code",code);
                                            mxSetMap.put("class_name",class_name);
                                            mxSetMap.put("gs",mx4gs_list);
                                            myAdapter4 = new MyAdapter2(mContext,mx4_data,AppUtils.getScreenWidth(mContext),new String[]{},contents,R.layout.list_item_middle3_01,mCheck4,mxSetMap);

                                            mx4_rView.setAdapter(myAdapter4);

                                            //隐藏添加功能
                                            res_map.put("isEdit",isEdit);
                                            boolean state = ActivityController.setVisible_addbutton(res_map,mx4_add);
                                            if (state){
                                                mx4_add.setOnClickListener(new MyOnCliclListener2(mContext,myAdapter4,4,class_name,mx4gs_list,tableView,"",code));
                                            }else{
                                                mx4_add.setVisibility(View.GONE);
                                            }
                                        }

                                        //添加明细与数据关系
                                        gx_map.put(code,"mx4_data");
                                        break;

                                    case 5:
                                        res_map = (Map<String, Object>) gs_map.get(key);
                                        contents= res_map.get("contents").toString().split(",",-1);
                                        if (mx5gs_list == null){
                                            mx5gs_list = FastJsonUtils.getBeanMapList(parmsMap.get("mx5gs_list").toString());
                                        }
                                        //添加明细5
                                        if(mx5gs_list != null && mx5gs_list.size()>0){
                                            mx5.setVisibility(View.VISIBLE);
                                            mx5_title.setText(res_map.get("title").toString());
                                            contents= res_map.get("contents").toString().split(",",-1);
                                            //20171211 wang更新查看内容
                                            contents = BussinessUtils.updateContents(contents,FastJsonUtils.findListStrByKey_and_Val(mx5gs_list,"is_visible_phone","code","否"));

                                            code = res_map.get("code").toString();

                                            if (ywtype.equals("add")){
                                                if (res_map.get("init")!=null){
                                                    Map<String,Object> initMap = null;
                                                    List<Map<String,Object>> initList = (List<Map<String, Object>>) res_map.get("init");
                                                    if (initList!= null){
                                                        mx5_data.clear();
                                                        mx5_data.addAll(initList);
                                                    }
                                                }
                                            }

                                            mCheck5 = new boolean[mx5_data.size()];
                                            ActivityController.setLayoutManager(mx5_rView,mContext);
                                            mxSetMap = new HashMap<String, Object>();
                                            mxSetMap.put("index",case_1);
                                            mxSetMap.put("menucode",menu_code);
                                            mxSetMap.put("menuname",menu_name);
                                            mxSetMap.put("code",code);
                                            mxSetMap.put("class_name",class_name);
                                            mxSetMap.put("gs",mx5gs_list);
                                            myAdapter5 = new MyAdapter2(mContext,mx5_data,AppUtils.getScreenWidth(mContext),new String[]{},contents,R.layout.list_item_middle3_01,mCheck5,mxSetMap);

                                            mx5_rView.setAdapter(myAdapter5);

                                            //隐藏添加功能
                                            res_map.put("isEdit",isEdit);
                                            boolean state = ActivityController.setVisible_addbutton(res_map,mx5_add);
                                            if (state){
                                                mx5_add.setOnClickListener(new MyOnCliclListener2(mContext,myAdapter5,5,class_name,mx5gs_list,tableView,"",code));
                                            }else{
                                                mx5_add.setVisibility(View.GONE);
                                            }
                                        }

                                        //添加明细与数据关系
                                        gx_map.put(code,"mx5_data");
                                        break;

                                    default:
                                        break;
                                }

                                get_data().put("gx",gx_map);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }

    /**
     * 更新 内容数据
     * @param contents
     * @return
     */
    private String[] updateContents(String[] contents) {

        String[] jssjContents = Constant.taxrate_array;
        List<String> list = FastJsonUtils.arrayToListStr(jssjContents),resList = new ArrayList<String>();
        if (AppUtils.user.getSjjs().equals("2") ) return  contents;

        int i = 0,start,end;
        String content1="";
        for (String content:contents ) {
            if (content.indexOf("[")>=0){
                start = content.indexOf("[");
                end = content.indexOf("]");
                content1 = content.substring(start+1,end);
            }

            if (!list.contains(content1)){
                resList.add(content);
            }
            i++;
        }

        if (resList.size()>0){
            contents = FastJsonUtils.listStrToArray(resList);
        }

        return  contents;

    }

    /**
     * 初始化选择框 公式 规则
     * @param code
     */
    public void initData(String code){
        Map<String,Object> _selectMap = (Map<String, Object>) get_catch().get(menu_code+"_select") /*选择数据*/,gongshi_map /*公式数据*/,guize_map /*规则数据*/;
        /*Map<String,Object> _allMap  = (Map<String, Object>) get_catch().get(menu_code+"_mapAll");*/

        if(_selectMap != null){
            parms = (Map<String, Object>) _selectMap.get(code);
        }

        gongshi_map = (Map<String, Object>) get_catch().get(menu_code+"_gs");
        if(gongshi_map != null){
            gongshi_map = (Map<String, Object>) gongshi_map.get(code);
            if(gongshi_map != null){
                gsMap = (Map<String, Object>) gongshi_map.get("column");
                gs_colsMap = (Map<String, Object>) gongshi_map.get("gs_column");
                actionMap = (Map<String, Object>) gongshi_map.get("action");
            }
        }

        if (ywtype != null && ywtype.equals("add")){
            guize_map = ((Map<String, Object>) get_catch().get(menu_code+"add_rule"));
        }else{
            guize_map = ((Map<String, Object>) get_catch().get(menu_code+"_rule"));
        }

        if(guize_map != null){
            ruleMap = (Map<String, Object>) guize_map.get(code);
        }

        //清除 main tableView
        if (code.equals("main")){
            get_data().remove("mainView");
        }
    }

    /**
     * 通过名字返回 数据
     * @param name
     * @return
     */
    public  List<Map<String,Object>> getMxData(Object service,String name) throws NoSuchFieldException, IllegalAccessException {
        Map<String,Object> resMap = (Map<String, Object>) get_data().get("gx");
        String mx_name = (String) resMap.get(name);
        return (List<Map<String,Object>>)ReflectHelper.getValueByFieldName(service,mx_name);
    }

    /**
     * 计算规则
     * @return
     */
    public boolean calculateGz(){
        try{
            //计算规则
            Map<String,Object> gongshi_map = (Map<String, Object>) get_catch().get(menu_code+"_gs");
            Map<String,Object> gongsi_map = (Map<String, Object>) gongshi_map.get("main");
            if(gongsi_map == null){
                return  true;
            }
            Map<String,Object> column_map = (Map<String, Object>) gongsi_map.get("column"),ms_map;
            Set<String> col_sets = column_map.keySet();
            String type1 = ""/*计算类型*/,from=""/*数据源*/,mx_name=""/*明细表名*/,mx_col=""/*明细字段名*/,columns /*参与字段名称*/;
            List<String> sort_list = new ArrayList<String>() /*排序集合*/;

            if(gongsi_map.get("sort")!= null){
                String[] sorts = gongsi_map.get("sort").toString().split(",");
                sort_list = Arrays.asList(sorts);
            }else{
                sort_list.addAll(col_sets) ;
            }

            for (String key:sort_list) {
                if (key.equals("sort")){
                    continue;
                }
                if (column_map.get(key) instanceof  Map<?,?>){
                    ms_map = (Map<String, Object>) column_map.get(key);

                    //执行加减法操作
                    if (ms_map.get("type") == null){
                        type1 = "";
                    }else{
                        type1 = ms_map.get("type").toString();
                    }
                    if(ms_map.get("from") != null){
                        from = ms_map.get("from").toString();
                    }

                    String expression_ = "";
                    boolean isDate = false;

                    if (from.indexOf("[")>=0 && from.indexOf("]")>=0){
                        int i =0;
                        while (from.indexOf("]")>=0){

                            if (i > 10){
                                break;
                            }

                            int start = from.indexOf("[");
                            int end = from.indexOf("]");
                            String val = from.substring(start+1,end);
                            if (ms_map.get(val)!= null){
                                Object col_1 = ms_map.get(val);
                                if (col_1 instanceof String){
                                    String test_col = col_1.toString();
                                    if (test_col.indexOf(":")>=0){
                                        String[] mhs = test_col.split(":");
                                        if (mhs[0].equals("main")){
                                            String val_1 = get_colval_Main(mhs[1],tableView).toString();
                                            if (DateUtil.isDate(val_1)){
                                                val_1 =  DateUtil.date2TimeStamp(val_1,"yyyy-MM-dd");
                                                isDate = true;
                                            }
                                            from = from.replace("["+val+"]",String.valueOf(val_1));
                                        }
                                    }
                                }else if(col_1 instanceof  Map<?,?>){
                                    Map<String,Object> colMap = (Map<String, Object>) col_1;
                                    type1 = colMap.get("type").toString();
                                    String testFrom = colMap.get("from").toString();
                                    String[] froms = testFrom.split(":");
                                    mx_name = froms[0];
                                    mx_col = froms[1];
                                    /*合计操作*/
                                    if (type1.equals("sum")) {
                                        try {
                                            String val_1 = Calculation_Val(getMxData(this, mx_name), mx_col);
                                            from = from.replace("[" + val + "]", String.valueOf(val_1));
                                        } catch (NoSuchFieldException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }else{
                                Toast.makeText(mContext,"不存在表达式：【"+val+"】",Toast.LENGTH_SHORT).show();
                            }

                            i++;
                        }
                        //执行表达式
                        String res_jg = SizheTool.eval2(from,isDate,"2").toString();
                         /*更新主map 和 视图*/
                        if (isDate && menu_code.toLowerCase().equals("travelapplication")){
                            res_jg = String.valueOf(Double.parseDouble(res_jg)+1);
                        }
                        update_colval_Main(key,res_jg,tableView,main_data);

                    }else if(from.equals("main")){
                        /*执行 替换动作*/
                        if (type1.equals("replace") && ms_map.get("value")!= null){
                            String value_str = ms_map.get("value").toString();
                            value_str = replaceExpression(value_str);
                            /*更新主map 和 视图*/
                            update_colval_Main(key,value_str,tableView,main_data);
                        }
                    }else if (from.indexOf(":")>=0){
                        String[] froms = from.split(":");
                        mx_name = froms[0];
                        mx_col = froms[1];
                        /*合计操作*/
                        if (type1.equals("sum")){
                            try {
                                Calculation_Main(getMxData(this,mx_name),key,mx_col,tableView,main_data);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }else if(type1.equals("calculate")){
                            columns = ms_map.get("columns").toString();
                            String from_expression = from,val_str;
                            String[] col_array = columns.split(",",-1);

                            for (String col_name:col_array) {
                                val_str = get_colval_Main(col_name,tableView).toString();
                                if(val_str.equals("err")){
                                    return false;
                                }
                                double val = Double.parseDouble(get_colval_Main(col_name,tableView).toString());
                                from_expression = from_expression.replace("["+col_name+"]",String.valueOf(val));

                            }
                            String res_jg = SizheTool.eval2(from_expression,"2").toString();
                            update_colval_Main(key,res_jg,tableView,main_data);
                        }
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(mContext,"存在错误",Toast.LENGTH_SHORT).show();
            return  false;
        }

        return  true;
    }

    /**
     * 替换表达式中的 值
     * @param expression
     */
    public String replaceExpression(String expression){
        String val_str;
        while (expression.indexOf("[")>=0 && expression.indexOf("]")>=0){
            int start = expression.indexOf("[");
            int end = expression.indexOf("]");
            String col_name = expression.substring(start+1,end);
            val_str = get_colval_Main(col_name,tableView).toString();
            expression = expression.replace("["+col_name+"]",String.valueOf(val_str));
        }
        //update_colval_Main(key,res_jg,tableView,main_data);
        return  expression;
    }

    public String replaceVal(String col_name){
        return   get_colval_Main(col_name,tableView).toString();
    }

    //执行 点击事件
    @Override
    public void onItemClick(View view, int position) {
        //得到数据
        Map<String,Object> parms_mxmap = (Map<String, Object>) view.getTag();
        Intent intent = new Intent();
        parmsMap = new HashMap<String, Object>();
        //验证是那个明细
        int index = (int) parms_mxmap.get("index");
        switch (index){
            case 1:
                mx1_data = myAdapter1.getDatas();
                parmsMap.put("mainData",FastJsonUtils.mapToString(mx1_data.get(position)));
                parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx1gs_list));
                parmsMap.put("mxData",FastJsonUtils.ListMapToListStr(mx1_data));
                break;
            case 2:
                mx2_data = myAdapter2.getDatas();
                parmsMap.put("mainData",FastJsonUtils.mapToString(mx2_data.get(position)));
                parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx2gs_list));
                parmsMap.put("mxData",FastJsonUtils.ListMapToListStr(mx2_data));
                break;
            case 3:
                mx3_data = myAdapter3.getDatas();
                parmsMap.put("mainData",FastJsonUtils.mapToString(mx3_data.get(position)));
                parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx3gs_list));
                parmsMap.put("mxData",FastJsonUtils.ListMapToListStr(mx3_data));
                break;
            case 4:
                mx4_data = myAdapter4.getDatas();
                parmsMap.put("mainData",FastJsonUtils.mapToString(mx4_data.get(position)));
                parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx4gs_list));
                parmsMap.put("mxData",FastJsonUtils.ListMapToListStr(mx4_data));
                break;
            case 5:
                mx5_data = myAdapter5.getDatas();
                parmsMap.put("mainData",FastJsonUtils.mapToString(mx5_data.get(position)));
                parmsMap.put("mainGs",FastJsonUtils.ListMapToListStr(mx5gs_list));
                parmsMap.put("mxData",FastJsonUtils.ListMapToListStr(mx5_data));
                break;
        }
        parmsMap.put("index",position);
        parmsMap.put("type","edit");
        parmsMap.put("menucode",parms_mxmap.get("menucode"));
        parmsMap.put("menuname",parms_mxmap.get("menuname"));
        parmsMap.put("isEdit",isEdit);
        parmsMap.put("RESULT_OK",index);
        parmsMap.put("code",parms_mxmap.get("code"));
        //写入缓存数据
        get_data().put("mainView",tableView);
        intent.putExtras(AppUtils.setParms("edit",parmsMap));
        Class testClass = ReflectHelper.getCalss(parms_mxmap.get("class_name").toString());
        intent.setClass(mContext,testClass);
        Activity activity = (Activity) mContext;
        activity.startActivityForResult(intent, index);
    }

    //执行 删除动作
    @Override
    public void onDeleteBtnClilck(View view, int position) {
        Map<String,Object> parms_mxmap = (Map<String, Object>) view.getTag();

        int index = (int) parms_mxmap.get("index");
        Map<String,Object> map = null;
        switch (index){
            case 1:
                map = myAdapter1.getDatas().get(position);
                if (map != null){

                    //执行 特殊 处理 销售订单
                    if (menu_code.equals("Xsdd")){
                        //查询 赠品List 删除 赠品数据
                        List<Map<String,Object>> zpListMap = FastJsonUtils.findListPdByKey_and_Val(myAdapter1.getDatas(),"dyrowid",map.get("rowid").toString());
                        if (zpListMap!= null && zpListMap.size() >0){
                            myAdapter1.getDatas().remove(position);
                            myAdapter1.getDatas().removeAll(zpListMap);
                        }else{
                            myAdapter1.getDatas().remove(position);
                        }
                    }else{
                        myAdapter1.getDatas().remove(position);
                    }

                    myAdapter1.notifyDataSetChanged();
                }
                break;
            case 2:
                map = myAdapter2.getDatas().get(position);
                if (map != null){
                    myAdapter2.getDatas().remove(position);
                    myAdapter2.notifyDataSetChanged();
                }
                break;
            case 3:
                map = myAdapter3.getDatas().get(position);
                if (map != null){
                    myAdapter3.getDatas().remove(position);
                    myAdapter3.notifyDataSetChanged();
                }
                break;
            case 4:
                map = myAdapter4.getDatas().get(position);
                if (map != null){
                    myAdapter4.getDatas().remove(position);
                    myAdapter4.notifyDataSetChanged();
                }
                break;
            case 5:
                map = myAdapter5.getDatas().get(position);
                if (map != null){
                    myAdapter5.getDatas().remove(position);
                    myAdapter5.notifyDataSetChanged();
                }
                break;
        }

    }

    @Override
    public void onCuibanBtnClick(View view, int position) {

    }

    @Override
    public void onZuofeiBtnClick(View view, int postion) {

    }

    @Override
    protected void onDestroy() {
        if (tableView != null){
            tableView.clear();
        }

        super.onDestroy();

    }
}
