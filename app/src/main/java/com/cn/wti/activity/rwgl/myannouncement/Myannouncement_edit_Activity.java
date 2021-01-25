package com.cn.wti.activity.rwgl.myannouncement;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.dialog.window.SingleChoicePopWindow;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.Constant;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.Dialog_ViewUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Myannouncement_edit_Activity extends BaseEdit_NoTable_Activity{

    /*private Map<String,String> showMap;*/
    int tablepostion = 1;
    private String pars,ywtype,service_name,method_name,ywtable_name="";
    private String[] contents;
    private Map<String,Object> select_map,parmsMap,map;
    private TextView_custom receive_emp_names_tv,receive_empids_tv,receive_deptids_tv,receive_dept_names_tv;
    private ImageView jsrxz,jsbmxz;
    private LinearLayout jsr_liner,jsbm_liner;
    private List<String> jsrids = new ArrayList<String>(),jsrnames= new ArrayList<String>();
    private List<String> jsbmids = new ArrayList<String>(),jsbmnames= new ArrayList<String>();
    private TextView tzlxxz;
    private Dialog mDialog;

    Object res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();
        ywtype = parmsMap.get("type").toString();
        if (parmsMap.get("menucode") != null){
            ywtable_name = parmsMap.get("menucode").toString();
        }

        if (ywtable_name != null && ywtable_name.equals("MyannouncementTMTA_wfqd")){
            layout = R.layout.activity_myannouncement_edit;
        }else if (ywtype != null && ywtype.equals("add")){
            layout = R.layout.activity_myannouncement_edit;
        }else{
            layout = R.layout.activity_myannouncement_show;
        }

        super.onCreate(savedInstanceState);
        initData();
        createView();
        //更新为已阅
        if (!ywtype.equals("add") && !ywtable_name.equals("MyannouncementTMTA_wfqd")){
            ActivityController.createThreadDoing(mContext,"myannouncement","updateLookTime","announcementid:"+id+",staffid:"+AppUtils.user.get_zydnId()+",lookdate:"+ DateUtil.createDate());
        }

        if (ywtype.equals("add")){
            findViewById(R.id.lin_yy).setVisibility(View.GONE);
            findViewById(R.id.img_yy).setVisibility(View.GONE);

            findViewById(R.id.lin_wy).setVisibility(View.GONE);
            findViewById(R.id.img_wy).setVisibility(View.GONE);

        }else{
            if (ywtable_name.equals("MyannouncementTMTA_wfqd")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //更新已阅未阅人员
                        res = ActivityController.getData2ByPost(mContext,"myannouncement","findLatestLooker",StringUtils.strTOJsonstr("id:"+id));
                        if (res != null && res instanceof JSONObject){
                            final JSONObject jot = (JSONObject) res;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView yyry_tv = (TextView) findViewById(R.id.yyry);
                                    yyry_tv.setText(jot.get("name").toString());
                                }
                            });
                        }

                        res = ActivityController.getData2ByPost(mContext,"myannouncement","findNoLatestLooker",StringUtils.strTOJsonstr("id:"+id));
                        if (res != null && res instanceof JSONObject){
                            final JSONObject jot = (JSONObject) res;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView wyry_tv = (TextView) findViewById(R.id.wyry);
                                    wyry_tv.setText(jot.get("name").toString());
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    public void initData() {
        menu_code = "announcement";
        menu_name="通知公告";

        initMap = new HashMap<String,String>();
        initMap.put("make_empid","_zydnId");
        initMap.put("make_emp_name","_zydnName");
        initMap.put("make_deptid","_bmdnId");
        initMap.put("trantime","trantime");
        initMap.put("createby","_loginName");
    }

    public String setParms(){
        return  "";
    }

    public  void createView(){

        mContext = Myannouncement_edit_Activity.this;
        main_title.setText("公告详情");
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();

        if (parmsMap!= null){
            try {
                id = parmsMap.get("id").toString();
                ywtype = parmsMap.get("type").toString();
                index = parmsMap.get("index").toString();
                tablepostion = Integer.parseInt(parmsMap.get("table_postion").toString());

                if (parmsMap.get("menucode") != null){
                    ywtable_name = parmsMap.get("menucode").toString();
                }

                if (ywtype.equals("add")){
                    main_data = new HashMap<String, Object>();
                    ActivityController.initUserMainData(main_data,initMap,AppUtils.user);
                }else{
                    if (parmsMap.get("mainData") instanceof JSONObject || parmsMap.get("mainData") instanceof Map){
                        main_data = (Map<String, Object>) parmsMap.get("mainData");
                    }else{
                        main_data = FastJsonUtils.strToMap(parmsMap.get("mainData").toString()) ;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //经手人父视图
        jsr_liner = (LinearLayout) findViewById(R.id.jsr_liner_);
        receive_empids_tv = (TextView_custom) findViewById(R.id.receive_empids);
        receive_emp_names_tv = (TextView_custom) findViewById(R.id.receive_emp_names);
        receive_emp_names_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                jsrids.clear();
                jsrnames.clear();
                jsr_liner.removeAllViews();
                //更新IDS 与 参与人员
                String ids = receive_empids_tv.getText().toString();
                if (!ids.equals("")){
                    String[] ids_array = ids.split(",");
                    for (String id:ids_array) {
                        if (!id.equals("") && !jsrids.contains(id)){
                            jsrids.add(id);
                        }
                    }
                }

                String names = receive_emp_names_tv.getText().toString();
                if (!names.equals("")){
                    String[] names_array = names.split(",");
                    for (String name:names_array) {
                        if (!name.equals("") && !jsrnames.contains(name)){
                            jsrnames.add(name);
                        }
                    }
                }

                TextView_custom tv;
                for (String jsr_str:jsrnames) {
                    if (!ActivityController.createTv(jsr_str,jsr_liner)){
                        tv = new TextView_custom(mContext);
                        RelativeLayout.LayoutParams layoutParams=
                                new RelativeLayout.LayoutParams(180, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        tv.setLayoutParams(layoutParams);
                        tv.setText(jsr_str);

                        if (ywtable_name != null || ywtype.equals("add")){
                            //执行删除动作
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    jsr_liner.removeView(v);
                                    TextView_custom tv1 = (TextView_custom) v;
                                    int index = jsrnames.lastIndexOf(tv1.getText());
                                    jsrnames.remove(index);
                                    jsrids.remove(index);
                                    //更新试图数据
                                    receive_empids_tv.setText(StringUtils.listStrTostrSplit(jsrids,","));
                                    receive_emp_names_tv.setText(StringUtils.listStrTostrSplit(jsrnames,","));

                                }
                            });
                        }
                        jsr_liner.addView(tv);
                    }
                }
            }
        });

        //经手部门父视图
        jsbm_liner = (LinearLayout) findViewById(R.id.jsbm_liner_);
        receive_deptids_tv = (TextView_custom) findViewById(R.id.receive_deptids);
        receive_dept_names_tv = (TextView_custom) findViewById(R.id.receive_dept_names);
        receive_dept_names_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                jsbmids.clear();
                jsbmnames.clear();
                jsbm_liner.removeAllViews();

                //更新IDS 与 参与人员
                String ids = receive_deptids_tv.getText().toString();
                if (!ids.equals("")){
                    String[] ids_array = ids.split(",");
                    for (String id:ids_array) {
                        if (!id.equals("") && !jsbmids.contains(id)){
                            jsbmids.add(id);
                        }
                    }
                }

                String names = receive_dept_names_tv.getText().toString();
                if (!names.equals("")){
                    String[] names_array = names.split(",");
                    for (String name:names_array) {
                        if (!name.equals("") && !jsbmnames.contains(name)){
                            jsbmnames.add(name);
                        }
                    }
                }

                TextView_custom tv;
                for (String jsr_str:jsbmnames) {
                    if (!ActivityController.createTv(jsr_str,jsbm_liner)){
                        tv = new TextView_custom(mContext);
                        RelativeLayout.LayoutParams layoutParams=
                                new RelativeLayout.LayoutParams(180, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        tv.setLayoutParams(layoutParams);
                        tv.setText(jsr_str);
                        if (ywtable_name != null || ywtype.equals("add")){
                            //执行删除动作
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    jsbm_liner.removeView(v);
                                    TextView_custom tv1 = (TextView_custom) v;
                                    int index = jsbmnames.lastIndexOf(tv1.getText());
                                    jsbmnames.remove(index);
                                    jsbmids.remove(index);
                                    //更新试图数据
                                    receive_deptids_tv.setText(StringUtils.listStrTostrSplit(jsbmids,","));
                                    receive_dept_names_tv.setText(StringUtils.listStrTostrSplit(jsbmnames,","));
                                }
                            });
                        }
                        jsbm_liner.addView(tv);
                    }
                }
            }
        });

        reshDataUI();
    }

    public void reshDataUI(){
        //通知类型
        updateOneUI(main_data,"notificationtype");
        /*if (main_data.get("notificationtype")!= null){
            String type = (String) main_data.get("notificationtype");
            TextView_custom notificationtypeTcm = findViewById(R.id.notificationtype_name);
            if (type == "0"){
                notificationtypeTcm.setText("部门通知");
            }else if (type == "1"){
                notificationtypeTcm.setText("人事通知");
            }else if (type == "2"){
                notificationtypeTcm.setText("财务通知");
            }
        }*/

        updateOneUI(main_data,"make_emp_name");
        updateOneUI(main_data,"trantime");

        //通知标题
        updateOneUI(main_data,"notificationtitle");
        //通知内容
        updateOneUI(main_data,"noticecontent");
        //接收人
        updateOneUI(main_data,"receive_empids");
        updateOneUI(main_data,"receive_emp_names");
        //接收部门
        updateOneUI(main_data,"receive_deptids");
        updateOneUI(main_data,"receive_dept_names");
        //新增时添加选择项
        if ((ywtype != null && ywtype.equals("add")) || (ywtable_name != null && ywtable_name.equals("MyannouncementTMTA_wfqd"))){
            tzlxxz = (TextView) findViewById(R.id.select_notificationtype);
            //新增时添加选择项
            jsrxz = (ImageView) findViewById(R.id.jsrxz);
            jsrxz.setOnClickListener(this);
            //经手部门
            jsbmxz = (ImageView) findViewById(R.id.jsbmxz);
            jsbmxz.setOnClickListener(this);

            tzlxxz.setOnClickListener(this);
            //保存
            ok = (Button) findViewById(R.id.ok);
            ok.setOnClickListener(this);
            if (ywtype.equals("add")){
                ok.setText("保存");
            }else{
                ok.setText("");
                ok.setBackground(getResources().getDrawable(R.mipmap.documentmore));
            }

            select_map = new HashMap<String, Object>();
            //通知类型
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("notificationtype","id");
            map.put("notificationtype_name","name");
            map.put("type","23");
            select_map.put("dictionaryOne~通知类型",map);

            //接收部门
            map = new LinkedHashMap<String, Object>();
            map.put("receive_deptids","id");
            map.put("receive_dept_names","name");
            select_map.put("department~接收部门",map);

            //接收人员
            map = new LinkedHashMap<String, Object>();
            map.put("receive_empids","id");
            map.put("receive_emp_names","name");
            select_map.put("staff~接收人员",map);
        }
    }

    /**
     * 保存和编辑方法
     */

    public String saveOrEdit(View v) {
        res = v.getTag();
        if (res != null) {
            // 获取数据
            map = (Map<String, Object>) res;
        }
        final Map<String, Object> finalMap = map;
        String method = "",msg="";
        service_name = "announcement";
        if (ywtype != null && ywtype.equals("add")){
            method = "save";
            msg="保存中...";
        }else{
            ywtype = "edit";
            method = "edit";
            msg="更新中...";
        }

        for (String view_name:view_names) {
            main_data.put(view_name,getVal(view_name));
        }

        //得到 ID
        if(main_data.get("id") != null){
            id = (String) main_data.get("id");
        }

        if (!validate(ywtype)){
            if (finalMap != null){
                PopupWindow popupWindow = (PopupWindow) finalMap.get("popupWindow");
                popupWindow.dismiss();
            }
            return "err";
        }

        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,msg);
        final String finalmethod = method;
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (main_data!= null){
                    /*main_data.put("projectopportunitymxid","0");
                    main_data.put("projectcontractrwglmxid","0");*/

                    String main_str = FastJsonUtils.mapToString(main_data);
                    if (!main_str.equals("")){
                        main_str = main_str.substring(1,main_str.length()-1);
                    }

                    //版本号
                    if (main_data.get("version")!= null){
                        version = main_data.get("version").toString();
                    }

                    String pars = "{\"username\":\""+ AppUtils.app_username+"\",\"id\":\""+id+"\",\"version\":\""+version+"\",\"userid\":\""+AppUtils.user.get_id()
                            +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"+"\","+main_str+"}";
                    Object res = ActivityController.getDataByPost(mContext,service_name,finalmethod,pars);
                    if (res != null) {
                        final Map<String, Object> resMap = FastJsonUtils.strToMap(res.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resMap == null) {
                                    Toast.makeText(mContext,R.string.connection_timeout,Toast.LENGTH_SHORT).show();
                                    caozuo_type = "";
                                } else if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                                    Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                    id = resMap.get("data").toString();
                                    //更新主ID
                                    main_data.put("id",id);
                                    if (finalMap == null){
                                        ok.setText("发布");
                                    }
                                    caozuo_type = "edit";
                                    ywtype = "edit";
                                    reshData(service_name, Constant.method_findById,StringUtils.strTOJsonstr("{id:"+id+"}"));

                                }else{
                                    Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                    caozuo_type = "";
                                }
                                WeiboDialogUtils.closeDialog(mDialog);
                                if (finalMap != null){
                                    PopupWindow popupWindow = (PopupWindow) finalMap.get("popupWindow");
                                    popupWindow.dismiss();
                                }
                            }
                        });
                    }
                }
            }}).start();
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back2:
                if (ywtable_name.equals("MyannouncementTMTA_wfqd") && caozuo_type.equals("update")){
                    Intent intent = new Intent();
                    intent.putExtra("index", index);
                    intent.putExtra("tabpostion", table_postion);
                    intent.putExtra("type", "update");
                    intent.putExtra("menucode",ywtable_name);
                    intent.putExtras(AppUtils.setParms("update",main_data));
                    setResult(1, intent);
                    ((Activity)mContext).finish();
                }else if (ywtable_name.equals("MyannouncementTMTA_wfqd") && caozuo_type.equals("edit")){
                    Intent intent1 = new Intent();
                    intent1.putExtra("index", index);
                    intent1.putExtra("tabpostion", table_postion);
                    intent1.putExtra("type", "edit");
                    intent1.putExtra("menucode",ywtable_name);
                    intent1.putExtras(AppUtils.setParms("", main_data));
                    setResult(1, intent1);
                }else if (tablepostion == 1 && !ywtype.equals("add") && !ywtable_name.equals("MyannouncementTMTA_wfqd")) {
                    Intent intent1 = new Intent();
                    intent1.putExtra("index", index);
                    intent1.putExtra("type", "update");
                    intent1.putExtra("tabpostion", table_postion);
                    main_data.put("estatus", 7);
                    intent1.putExtras(AppUtils.setParms("", main_data));
                    setResult(1, intent1);
                }
                this.finish();
                break;
            case R.id.select_notificationtype:
                showOptions(R.id.select_notificationtype);
                break;
            case R.id.jsrxz:
                showOptions(R.id.jsrxz);
                break;
            case R.id.jsbmxz:
                showOptions(R.id.jsbmxz);
                break;
            case R.id.ok:
                if (ok.getText().equals("保存")) {
                    saveOrEdit(v);

                } else if (ok.getText().equals("发布")) {
                    auditAll(mContext,v,"7","发布中...","{id:"+id+"}");
                } else {

                    if (ywtable_name.equals("MyannouncementTMTA_wfqd") && (tablepostion == 1 || tablepostion == 2)) {
                        if (v.getTag() != null) {
                            map = (Map<String, Object>) v.getTag();
                        } else {
                            map = new HashMap<String, Object>();
                        }
                        map.put("currentView", main_form);
                        map.put("_selectView", v);
                        String[] display_buttons = null;
                        if (ywtable_name.equals("MyannouncementTMTA_wfqd") && tablepostion == 1) {

                            if (main_data.get("estatus")!= null && main_data.get("estatus").toString().equals("7")){
                                display_buttons = new String[]{"撤回", "保存", "删除"};
                            }else{
                                display_buttons = new String[]{"发布", "保存", "删除"};
                            }

                            Dialog_ViewUtils.showPopUp(v, mContext, this, R.layout.more_button_myannouncement_wfqd, 300, 460, map,
                                    new String[]{"btn1", "btn2", "btn3"}, null, display_buttons, 1);
                        } else if (ywtable_name.equals("MyannouncementTMTA_wfqd") && tablepostion == 2) {
                            if (main_data.get("estatus")!= null && main_data.get("estatus").toString().equals("7")){
                                display_buttons = new String[]{"撤回", "保存", "删除"};
                            }else{
                                display_buttons = new String[]{"发布", "保存", "删除"};
                            }
                            Dialog_ViewUtils.showPopUp(v, mContext, this, R.layout.more_button_myannouncement_wfqd, 300, 460, map,
                                    new String[]{"btn1", "btn2", "btn3"}, null, display_buttons, 1);
                        }
                    }
                }
                break;
            case R.id.btn1:

                if (ywtable_name.equals("MyannouncementTMTA_wfqd") && tablepostion == 1) {

                    if (main_data.get("estatus")!= null && main_data.get("estatus").toString().equals("7")){
                        //执行撤回操作
                        auditAll(v, "1", "撤回中...");
                    }else{
                        auditAll(mContext,v, "7", "发布中...","{id:"+id+"}");
                    }

                } else if (ywtable_name.equals("MyannouncementTMTA_wfqd") && tablepostion == 2) {
                    if (main_data.get("estatus")!= null && main_data.get("estatus").toString().equals("7")){
                        //执行撤回操作
                        auditAll(v, "1", "撤回中...");
                    }else{
                        auditAll(mContext,v, "7", "发布中...","{id:"+id+"}");
                    }

                }

                break;
            case R.id.btn2:
                if (ywtable_name.equals("MyannouncementTMTA_wfqd") && tablepostion == 1) {
                    //保存或更新数据
                    saveOrEdit(v);
                } else if (ywtable_name.equals("MyannouncementTMTA_wfqd") && tablepostion == 2) {
                    //保存或更新数据
                    saveOrEdit(v);
                }
                break;
            case R.id.btn3:
                if (ywtable_name.equals("MyannouncementTMTA_wfqd") && tablepostion == 1) {
                    shanchu(v);
                } else if (ywtable_name.equals("MyannouncementTMTA_wfqd") && tablepostion == 2) {
                    shanchu(v);
                }
                break;
        }
    }

    @Override
    public void showOptions(int id) {
        switch (id){
            case R.id.select_notificationtype:
                //ActivityController.showDialog_UI(mContext,"notificationtype~通知类型","name",select_map,"notificationtype","list","",get_catch(),main_form,view_names);
                List<Map<String,Object>> itemList = new ArrayList<>();
                Map<String,Object> map = new HashMap<>();map.put("id",0);map.put("name","部门通知");itemList.add(map);
                map = new HashMap<>();map.put("id",1);map.put("name","人事通知");itemList.add(map);
                map = new HashMap<>();map.put("id",2);map.put("name","财务通知");itemList.add(map);
                final SingleChoicePopWindow mSingleChoicePopWindow = new SingleChoicePopWindow(mContext, main_form,itemList,
                        null,null,"","name",0,0,"下拉选择");
                mSingleChoicePopWindow.setOnOKButtonListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int which = mSingleChoicePopWindow.getSelectItem();
                        Map<String,Object> map = (Map<String, Object>) mSingleChoicePopWindow.getSelectObject();
                        Map<String,Object> res_select = (Map<String, Object>) select_map.get("dictionaryOne~通知类型");
                        if (res_select != null){
                            Set<String> keys= res_select.keySet();
                            for (String key:keys) {
                                ActivityController.updateOneUI_1(mContext,map,key,res_select.get(key).toString(),main_form,view_names);
                            }
                        }
                    }
                });
                mSingleChoicePopWindow.show(true);
                WeiboDialogUtils.closeDialog(mDialog);
                break;
            case R.id.jsrxz:
                String pars = new ListParms("0","0","20",service_name,"").getParms();
                main_form.setTag(FastJsonUtils.mergeList(jsrids,jsrnames));
                ActivityController.showDialog_MoreSelect2(mContext,"staff~接收人员","staff","list",pars,"name",main_form,view_names,select_map);
                break;
            case R.id.jsbmxz:
                pars = new ListParms("0","0","20",service_name,"").getParms();
                main_form.setTag(FastJsonUtils.mergeList(jsbmids,jsbmnames));
                ActivityController.showDialog_MoreSelect2(mContext,"department~接收部门","department","list",pars,"name",main_form,view_names,select_map);
               break;
        }
    }

    @Override
    public boolean validate(String type){

        if (type.equals("save")||type.equals("edit")||type.equals("delete")){
            if (main_data.get("estatus") != null && main_data.get("estatus").equals("7")){
                Toast.makeText(mContext,mContext.getString(R.string.save_check_text),Toast.LENGTH_SHORT).show();
                return false;
            }

            if (main_data.get("approvalstatus") != null && main_data.get("approvalstatus").equals("1")){
                Toast.makeText(mContext,mContext.getString(R.string.save_sp_text),Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (type.equals("add") || type.equals("save")||type.equals("edit")){
            if (main_data.get("notificationtype") != null && main_data.get("notificationtype").toString().equals("")){
                Toast.makeText(mContext,"必须输入通知类型",Toast.LENGTH_SHORT).show();
                return false;
            }

            if (main_data.get("notificationtitle") == null || main_data.get("notificationtitle").toString().equals("")){
                Toast.makeText(mContext,"必须输入通知标题",Toast.LENGTH_SHORT).show();
                return false;
            }

            if (main_data.get("noticecontent") == null || main_data.get("noticecontent").toString().equals("")){
                Toast.makeText(mContext,"必须输入通知内容",Toast.LENGTH_SHORT).show();
                return false;
            }

            if ( main_data.get("receive_empids").toString().equals("")&& main_data.get("receive_deptids").toString().equals("")){
                Toast.makeText(mContext,"接收人和接收部门必须有一个填写",Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return  true;
    }
}
