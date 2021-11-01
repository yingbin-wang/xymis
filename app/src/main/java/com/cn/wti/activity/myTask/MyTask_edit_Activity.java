package com.cn.wti.activity.myTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps2d.model.MarkerOptions;
import com.cn.wti.activity.WebViewActivity;
import com.cn.wti.activity.base.BaseEdit_ProcessActivity;
import com.cn.wti.activity.dialog.PopWinShare;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.dialog.window.SingleChoicePopWindow;
import com.cn.wti.entity.view.custom.note.DisplayUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.number.IniUtils;
import com.dina.ui.widget.UITableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyTask_edit_Activity extends BaseEdit_ProcessActivity {

    private Map<String,String> initMap,showMap;
    int RESULT_OK = 1,REQUEST_CODE =1,MXREQUEST_CODE =2;
    private  String pars,id,taskId = "",ywbm="",table_postion,serviceName,type;
    private Map<String,Object> resMap,dataMap;
    private IniUtils iniUtils;
    private Map<String,Object> gzMap,_selectMap = new HashMap<String, Object>();
    private RecyclerView historymx_View = null;
    private LinearLayout historymx;
    private PopWinShare popWinShare = null;
    private ImageButton title_back2,title_ok2 = null;
    private TextView title_name2 = null;

    private Map<String,Object> auditUserMap= null,//当前用户审批信息
            processAttrMap;//流程属性

                    ;
    private List<Map<String,Object>> backActivityList = new ArrayList<Map<String,Object>>(),//可驳回任务
            historyCommentList = new ArrayList<Map<String,Object>>(), //历史信息
            exportationActivityList = new ArrayList<Map<String,Object>>(),
            copyexportationActivityList = new ArrayList<Map<String,Object>>(),
            formAttrList = new ArrayList<Map<String,Object>>() ;//表单属性
    private String fqr = "",
            expression = "",
            exportationActivity_str;


    Object res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.common_edit_task;
        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");

        if(so == null){
            return;
        }

        mContext = MyTask_edit_Activity.this;
        parmsMap = so.getParms();

        if (parmsMap.get("ywtype")!= null){
            type = parmsMap.get("ywtype").toString();
            if (type.equals("task")){
                dataMap = new HashMap<String, Object>();
                dataMap.putAll(parmsMap);
                taskId = AppUtils.getMapVal(dataMap,"ID_").toString();
                table_postion = String.valueOf(parmsMap.get("table_postion"));
            }
        }else{
            dataMap = (Map<String, Object>) parmsMap.get("mainData");
            table_postion = String.valueOf(parmsMap.get("table_postion"));
            if (parmsMap.get("index")!= null){
                index = parmsMap.get("index").toString();
            }
            taskId = AppUtils.getMapVal(dataMap,"ID_").toString();
        }


        super.onCreate(savedInstanceState);

        title_back2 = findViewById(R.id.title_back2);
        title_ok2 = findViewById(R.id.title_ok2);
        title_back2.setOnClickListener(new OnClickLintener());
        title_ok2.setOnClickListener(new OnClickLintener());
        title_ok2.setBackgroundResource(R.mipmap.documentmore);
        title_name2 = findViewById(R.id.title_name2);
    }

    void showPopWindow(){
        if (popWinShare == null) {
            //自定义的单击事件
            OnClickLintener paramOnClickListener = new OnClickLintener();
            popWinShare = new PopWinShare(MyTask_edit_Activity.this, paramOnClickListener,260,400);
            //监听窗口的焦点事件，点击窗口外面则取消显示
            popWinShare.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        popWinShare.dismiss();
                    }
                }
            });
        }
        //设置默认获取焦点
        popWinShare.setFocusable(true);
        //以某个控件的x和y的偏移量位置开始显示窗口
        popWinShare.showAsDropDown(title_ok2, 0, 0);
        //如果窗口存在，则更新
        popWinShare.update();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pars = new ListParms(menu_code,"id:"+main_data.get("id")+",code:"+main_data.get("code")+",name:"+menu_code).getParms();
                List<Map<String,Object>> res_array = null;
                Object res = ActivityController.getData2ByPost(mContext,"menu","findClUploadFilesByIdAndName", StringUtils.strTOJsonstr(pars));
                if (res != null && res instanceof JSONArray){
                    res_array = (List<Map<String, Object>>) res;

                }
                final List<Map<String, Object>> finalRes_array = res_array;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (finalRes_array.size() >0){
                            popWinShare.setBadgeText(finalRes_array.size());
                        }else{
                            popWinShare.setBadgeText(0);
                        }

                    }
                });
            }
        }).start();

    }


    @Override
    public String initData() {

        String bussiness_key = AppUtils.getMapVal(dataMap,"BUSINESS_KEY_").toString();
        if(bussiness_key!=null && !bussiness_key.equals("")) {
            String[] formkeys = bussiness_key.split(":");
            serviceName = formkeys[0];
            id = formkeys[1];
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("menucode", StringUtils.firstLowerStr(serviceName));
            if(table_postion.equals(2)){
                map.put("parms", "{id:" + id + ",isProcess:1,isEdit:0,taskId:"+taskId+"}");
            }else{
                map.put("parms", "{id:" + id + ",isProcess:1,taskId:"+taskId+"}");
            }

            String res = null;
            try {
                res = HttpClientUtils.webService("findFormProperty2", FastJsonUtils.mapToString(map));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ActivityController.getPostState(res) == 0) {
                try {
                    resMap = FastJsonUtils.strToMap(res);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                menu_code = serviceName;
                menu_name = "";
            }else{
                this.finish();
                return HttpClientUtils.backMessage(ActivityController.getPostState(res));
            }

            if (resMap!=null && (resMap.get("gz") != null &&  !resMap.get("gz").toString().equals(""))){
                if (resMap.get("gz") instanceof JSONObject || resMap.get("gz") instanceof Map){
                    gzMap = (Map<String, Object>) resMap.get("gz");
                }else if (resMap.get("gz") instanceof String){
                    gzMap = FastJsonUtils.strToMap(resMap.get("gz").toString());
                }else{
                    this.finish();
                    return "未找到格式文件";
                }

                if (gzMap == null){
                    this.finish();
                    return "未找到格式文件";
                }
            }

            if (resMap != null && resMap.size() > 0) {

                try {
                    if (resMap.get("main") == null) {
                        this.finish();
                        return "未找到主表格式";
                    } else if (resMap.get("mainData") == null) {
                        this.finish();
                        return "表单数据不存在";
                    }
                    maings_list = FastJsonUtils.getBeanMapList(resMap.get("main").toString());

                    if (resMap.get("mainData") instanceof JSONObject) {
                        main_data = (Map<String, Object>) resMap.get("mainData");
                    } else {
                        main_data = FastJsonUtils.strToMap(resMap.get("mainData").toString());
                    }

                    if (gzMap.get("replace")!= null){
                        updateMainJsr((Map<String, Object>) gzMap.get("replace"),main_data);
                    }

                    //如果 没有 参数对象 根据主数据格式 封装
                    /*if (parms != null && parms.size() == 0) {
                        createMyParms();
                    }*/
                    createMyParms();

                    if (gzMap.get("select") != null){
                        Map<String,Object> select_parms = (Map<String, Object>) gzMap.get("select");
                        if (select_parms.get("main") != null){
                            parms.putAll((Map<String, Object>) select_parms.get("main"));
                            //_selectMap.putAll(parms);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                return "未找到主表格式";
            }
        }

        if (table_postion.equals("1")){
            Map<String,Object> map = new HashMap<String, Object>();
            String cs = FastJsonUtils.mapToString(dataMap);
            map.put("serviceName","process");
            map.put("methodName","findAuditUserList");
            map.put("parameters",cs);
            Object res = null;
            try {
                res = HttpClientUtils.exectePost("process","findAuditUserList",cs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(res == null || res.toString().equals("")){
                //Toast.makeText(mContext,getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                return getString(R.string.err_data);
            }
            auditUserMap = FastJsonUtils.getResultStrTOMap(res.toString());

            //可驳回任务
            if(auditUserMap != null && auditUserMap.get("backActivity")!= null){
                backActivityList = (List<Map<String,Object>>)auditUserMap.get("backActivity");
            }

            //历史审批
            if(auditUserMap != null && auditUserMap.get("historyComment")!= null){
                historyCommentList = (List<Map<String,Object>>)auditUserMap.get("historyComment");
            }
            //发起人
            if(auditUserMap != null && auditUserMap.get("fqr")!= null){
                fqr = auditUserMap.get("fqr").toString();
            }
            //流程信息
            if(auditUserMap != null && auditUserMap.get("processAttr")!= null){
                if (auditUserMap.get("processAttr") instanceof String){
                    processAttrMap = FastJsonUtils.strToMap(auditUserMap.get("processAttr").toString());
                }else if (auditUserMap.get("processAttr") instanceof JSONObject || auditUserMap.get("processAttr") instanceof Map){
                    processAttrMap = (Map<String,Object>)auditUserMap.get("processAttr");
                }

            }
            //表单属性
            if(auditUserMap != null && auditUserMap.get("formAttr")!= null){
                formAttrList = (List<Map<String,Object>>)auditUserMap.get("formAttr");
            }

        }else if (table_postion.equals("2")){
            //历史审批
            try {
                String process_instanceid = AppUtils.getMapVal(dataMap, "PROC_INST_ID_").toString();
                Map<String,String> paramsPd = new HashMap<>();
                paramsPd.put("PROC_INST_ID_",process_instanceid);
                Object res = ActivityController.getData4ByPost("process", "findProcess_Bz", FastJsonUtils.mapToString(paramsPd));
                historyCommentList.clear();
                historyCommentList.addAll((List<Map<String, Object>>) res);

            }catch (Exception e){
                    e.printStackTrace();
            }

        }

        return  "";
    }

    private void updateMainJsr(Map<String,Object> replaceMap,Map<String,Object> mainMap_) {
        if (replaceMap != null && mainMap_ != null){
            Set<String> sets = replaceMap.keySet();
            for (String key:sets) {
                if (mainMap_.get(replaceMap.get(key)) != null){
                    mainMap_.put(key,mainMap_.get(replaceMap.get(key)));
                }
            }
        }
    }

    @Override
    public  void createView(){

        //addTitle(AppUtils.getMapVal(dataMap,"NAME_").toString());
        title_name2.setText(AppUtils.getMapVal(dataMap,"NAME_").toString());
        String mx_name = null;
        if (resMap.get("mxNames") != null){
            mx_name = resMap.get("mxNames").toString();
        }
        // 如果 mx_name key值为空  根据明细 格式封装
        if (mx_name == null || mx_name.equals("")){
            mx_name = getMxNames(resMap);
        }

        if (mx_name != null && !mx_name.equals("")){
            String[] mx_names = null;
            mx_names = mx_name.split(",");
            for (String name_1: mx_names) {
                if (gzMap == null){
                    Toast.makeText(mContext,getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                    this.finish();
                    return;
                }
                Map<String,Object> resMap = ActivityController.getMxOptions(name_1, (Map<String, Object>) gzMap.get("mapAll"));
                if (resMap == null){
                    Toast.makeText(mContext,getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                    this.finish();
                    return;
                }
                String name = (String) resMap.get("gl_name");
                String title_name = resMap.get("title").toString();
                String code = resMap.get("code").toString();
                if (name == null || name.equals("")){continue;}
                int index = Integer.parseInt(name.replace("mx","").replace("gs_list",""));
                switch (index){
                    case 1:
                        mx1.setVisibility(View.VISIBLE);
                        mx1_title.setText(title_name);
                        //ActivityController.showMxRecyclerView(mContext,serviceName,code,id,title_name,index);
                        ActivityController.showMxWebview(mx1_View,serviceName,code,id,title_name);
                        //mx1_add.setOnClickListener(new ShowMxDataClickLinstener(MyTask_edit_Activity.this,id,serviceName,code,title_name));
                        break;
                    case 2:
                        mx2.setVisibility(View.VISIBLE);
                        mx2_title.setText(title_name);
                        //ActivityController.showMxRecyclerView(mContext,serviceName,code,id,title_name,index);
                        ActivityController.showMxWebview(mx2_View,serviceName,code,id,title_name);
                        //mx2_add.setOnClickListener(new ShowMxDataClickLinstener(MyTask_edit_Activity.this,id,serviceName,code,title_name));
                        break;
                    case 3:
                        mx3.setVisibility(View.VISIBLE);
                        mx3_title.setText(title_name);
                        //ActivityController.showMxRecyclerView(mContext,serviceName,code,id,title_name,index);
                        ActivityController.showMxWebview(mx3_View,serviceName,code,id,title_name);
                        /*mx3_add.setOnClickListener(new ShowMxDataClickLinstener(MyTask_edit_Activity.this,id,serviceName,code,title_name));*/
                        break;
                    case 4:
                        mx4.setVisibility(View.VISIBLE);
                        mx4_title.setText(title_name);
                        //ActivityController.showMxRecyclerView(mContext,serviceName,code,id,title_name,index);
                        ActivityController.showMxWebview(mx4_View,serviceName,code,id,title_name);
                        /*mx4_add.setOnClickListener(new ShowMxDataClickLinstener(MyTask_edit_Activity.this,id,serviceName,code,title_name));*/
                        break;
                    case 5:
                        mx5.setVisibility(View.VISIBLE);
                        mx5_title.setText(title_name);
                        //ActivityController.showMxRecyclerView(mContext,serviceName,code,id,title_name,index);
                        ActivityController.showMxWebview(mx5_View,serviceName,code,id,title_name);
                        /*mx5_add.setOnClickListener(new ShowMxDataClickLinstener(MyTask_edit_Activity.this,id,serviceName,code,title_name));*/
                        break;
                }
            }
        }

        //添加 主数据
        if (maings_list != null && maings_list.size() > 0) {
            tableView = (UITableView) findViewById(R.id.tableView);
            if (table_postion.equals("2")){
                createList(tableView, null, "2", null);
            }else{
                CustomClickListener listener = new CustomClickListener(MyTask_edit_Activity.this, maings_list, tableView, "");
                createList(tableView, listener, "", null);
            }

        }

        if (table_postion.equals("1")){

            historymx = (LinearLayout) findViewById(R.id.historymx);
            historymx.setVisibility(View.GONE);

        }else if (table_postion.equals("2")){
            //历史审批
            historymx = (LinearLayout) findViewById(R.id.historymx);
            historymx.setVisibility(View.VISIBLE);
            try {

                if (historyCommentList != null && historyCommentList.size() > 0) {
                    historymx_View = (RecyclerView) findViewById(R.id.historymx_View);
                    ActivityController.setLayoutManager(historymx_View, mContext);
                    LinearLayout historymx = (LinearLayout) findViewById(R.id.historymx);
                    historymx.setVisibility(View.VISIBLE);
                    String[] contents = new String[]{"NAME_", "USERNAME_", "JG_", "START_TIME_", "END_TIME_"};
                    //创建并设置Adapter
                    mCheck = new boolean[historyCommentList.size()];
                    mAdapter1 = new MyAdapter2(this, historyCommentList, AppUtils.getScreenWidth(mContext), new String[]{}, contents, R.layout.list_item_history, mCheck);
                    historymx_View.setAdapter(mAdapter1);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //打开明细视图
    private class ShowMxDataClickLinstener implements View.OnClickListener{

        private Context context;
        private String id,menucode,ywcode,title;

        public ShowMxDataClickLinstener(Context context,String id,String menucode,String ywcode,String title){
            this.context = context;
            this.id = id;
            this.menucode = menucode;
            this.ywcode = ywcode;
            this.title = title;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,WebViewActivity.class);
            Map<String,Object> parmsMap = new HashMap<String, Object>();
            parmsMap.put("id",id);
            parmsMap.put("menucode",menucode);
            parmsMap.put("ywcode",ywcode);
            parmsMap.put("title",title);
            intent.putExtras(AppUtils.setParms("",parmsMap));
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (table_postion != null && table_postion.equals("2")){
            return  true;
        }else{
            return super.onCreateOptionsMenu(menu);
        }

    }

    public Map<String,Object> getResMap(String res){
        resMap = FastJsonUtils.strToMap(res.toString());
        if(resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
            return (Map<String, Object>) resMap.get("data");
        }
        return  null;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 2){
            Intent intent = new Intent();
            intent.putExtra("index",index);
            setResult(2,intent);
            this.finish();
        }
    }

    /**
     * 根据 主格式数据 封装 参数数据
     */
    public void createMyParms(){
        List<Map<String,Object>> parmsMapList = new ArrayList<Map<String,Object>>();
        List<String> gldns = new ArrayList<String>();
        if (maings_list == null){return;}
        for (Map<String,Object> gsMap:maings_list ) {
            if(gsMap.get("gldn")!= null && !gsMap.get("gldn").toString().equals("")){
                String gldn = gsMap.get("gldn").toString();
                parmsMapList.add(gsMap);
                if (!gldns.contains(gldn)){
                    gldns.add(gldn);
                }
            }
        }
        Map<String,Object> map_1 = null;
        List<Map<String,Object>> resMapList = new ArrayList<Map<String,Object>>();
        for (String gldn_1:gldns) {
            List<Map<String,Object>> testList = FastJsonUtils.findListPdByKey_and_Val(parmsMapList,"gldn",gldn_1);
            map_1 = new HashMap<String, Object>();
            for (Map<String,Object> parmsMap:testList) {

                if (parmsMap.get("type").toString().equals("int")){
                    map_1.put(parmsMap.get("code").toString(),"id");
                }else if (parmsMap.get("type").toString().equals("string")){
                    if (gldn_1.indexOf("客户")>=0){
                        map_1.put(parmsMap.get("code").toString(),"sub_name");
                    }else if (gldn_1.indexOf("供应商")>=0){
                        map_1.put(parmsMap.get("code").toString(),"sub_name");
                    }else{
                        map_1.put(parmsMap.get("code").toString(),"name");
                    }
                }
            }
            if (parms.get(gldn_1) == null && map_1 != null){
                parms.put(gldn_1,map_1);
            }
        }
    }

    public void showDialog(final Context context, String title, String[] items, List<Map<String,Object>> dataList,String key){

        final String[] finalItems = items;
        final List<Map<String,Object>> finalDataList = dataList;
        final String finalkey = key,finaltaskId = taskId;

        new AlertDialog.Builder(context).setTitle(title).setIcon(
            android.R.drawable.ic_dialog_info).setSingleChoiceItems(items, 0,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Map<String,Object> dataMap = finalDataList.get(which);
                    String val = dataMap.get(finalkey).toString();
                    pars = "taskId:"+finaltaskId+",assignee:"+val;
                    pars = StringUtils.strTOJsonstr(pars);
                    boolean flag = ActivityController.execute(context,"process","transferTask",pars);
                    if(flag){
                        dialog.dismiss();
                    }
                }
            }).setNegativeButton("取消", null).show();
    }

    @Override
    protected void onDestroy() {
        Log.v("test", String.valueOf(this.getTaskId()));
        super.onDestroy();
        ActivityController.releaseAllWebViewCallback();
        mx1_View.destroy();
        mx2_View.destroy();
        mx3_View.destroy();
        mx4_View.destroy();
        mx5_View.destroy();
    }

    class OnClickLintener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int itemId = 0;
            String title = "";
            switch (v.getId()) {
                case R.id.layout_pass:
                    itemId = 1;
                    title = "通过";
                    break;
                case R.id.layout_nopass:
                    itemId = 1;
                    title = "不通过";
                    break;
                case R.id.layout_weituo:
                    itemId = 3;
                    title = "委托";
                    break;
                case R.id.layout_bohui:
                    itemId = 2;
                    title = "驳回";
                    break;
                case R.id.layout_fujian:
                    itemId = 4;
                    title = "附件";
                    break;
                case R.id.title_ok2:
                    showPopWindow();
                    break;
                case R.id.title_back2:
                    finish();
                    break;
                default:
                    break;
            }

            if (itemId != 0){
                //添加 表单验证
                if(!tableView.getEditTextValidator().validate()){
                    return;
                }
                //更新主数据
                updateMain();
                if (popWinShare != null){
                    popWinShare.dismiss();
                }
            }

            final Intent intent = new Intent();
            switch (itemId) {

                case 1:
                    intent.setClass(MyTask_edit_Activity.this,MyTask_ExamineActivity.class);
                    dataMap.put("formData",FastJsonUtils.mapToString(main_data));
                    dataMap.put("exportationActivity",exportationActivity_str);
                    dataMap.put("fqr",fqr);
                    dataMap.put("sftg",title);
                    dataMap.put("processAttr",FastJsonUtils.mapToString(processAttrMap));
                    dataMap.put("formAttr",FastJsonUtils.ListMapToListStr(formAttrList));
                    intent.putExtras(AppUtils.setParms("",dataMap));
                    startActivityForResult(intent,1);
                    break;

                case 2:
                    intent.setClass(MyTask_edit_Activity.this,MyTask_BackActivity.class);
                    dataMap.put("formData",FastJsonUtils.mapToString(main_data));
                    dataMap.put("backActivity",FastJsonUtils.ListMapToListStr(backActivityList));
                    dataMap.put("fqr",fqr);
                    dataMap.put("processAttr",FastJsonUtils.mapToString(processAttrMap));
                    dataMap.put("index",index);
                    intent.putExtras(AppUtils.setParms("",dataMap));
                    startActivityForResult(intent,2);
                    break;
                case 3:
                    pars = new ListParms("0","0",AppUtils.limit,"psndoc","workstatus:1").getParms();
                    final String testpars = StringUtils.strTOJsonstr(pars);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            res = ActivityController.getDataByPost(mContext,"psndoc","list",testpars);
                            int recordcount=0,pageIndex=0;
                            List<Map<String,Object>> result_list =null;

                            try {
                                if(!res.equals("")){
                                    resMap = getResMap(res.toString());
                                    if(resMap.get("results")!= null){
                                        recordcount = Integer.parseInt(resMap.get("results").toString());
                                        pageIndex = 1;
                                        result_list = (List<Map<String, Object>>) resMap.get("rows");
                                        String[] items = FastJsonUtils.ListMapToListStr(result_list,"name");
                                        List<String> list = FastJsonUtils.MapToListByKey(result_list,"name");

                                        final int finalRecordcount = recordcount;
                                        final List<Map<String, Object>> finalResult_list = result_list;
                                        final int finalPageIndex = pageIndex;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                final SingleChoicePopWindow mSingleChoicePopWindow = new SingleChoicePopWindow(MyTask_edit_Activity.this, main_form, finalResult_list,"psndoc","list",
                                                        pars,"name", finalRecordcount, finalPageIndex,"");
                                                mSingleChoicePopWindow.setOnOKButtonListener(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {
                                                        Map<String, Object> dataMap = (Map<String, Object>) mSingleChoicePopWindow.getSelectObject();
                                                        String val = dataMap.get("user_name").toString();
                                                        pars = "taskId:"+taskId+",assignee:"+val;
                                                        final String testpars = StringUtils.strTOJsonstr(pars);
                                                        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"委托中...");
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                boolean flag = ActivityController.execute(MyTask_edit_Activity.this,"process","transferTask",testpars);
                                                                if (flag){
                                                                    Intent intent = new Intent();
                                                                    intent.putExtra("index",index);
                                                                    setResult(2,intent);
                                                                    MyTask_edit_Activity.this.finish();
                                                                }
                                                                WeiboDialogUtils.closeDialog(mDialog);
                                                            }
                                                        }).start();

                                                    }
                                                });
                                                mSingleChoicePopWindow.show(true);
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    break;
                case 4:
                    main_data.put("menucode",menu_code);
                    ActivityController.startMyFileActivity(mContext,main_data);
                    break;
                default:
                    break;
            }

        }

    }


    /*public boolean onOptionsItemSelected(MenuItem item) {

        if (tableView == null){
            return  true;
        }

        if (tableView.getEditTextValidator() == null){
            return  true;
        }

        int itemId = 0;
        if (item.getTitle().equals("通过") || item.getTitle().equals("不通过")){
            itemId = 1;
        }else if(item.getTitle().equals("驳回")){
            itemId = 2;
        }else if(item.getTitle().equals("委托")){
            itemId = 3;
        }else if(item.getTitle().equals("附件")){
            itemId = 4;
        }

        if (itemId != 0){
            //添加 表单验证
            if(!tableView.getEditTextValidator().validate()){
                return false;
            }
            //更新主数据
            updateMain();
        }

        final Intent intent = new Intent();

        switch (itemId) {

            case 1:
                intent.setClass(MyTask_edit_Activity.this,MyTask_ExamineActivity.class);
                dataMap.put("formData",FastJsonUtils.mapToString(main_data));
                dataMap.put("exportationActivity",exportationActivity_str);
                dataMap.put("fqr",fqr);
                dataMap.put("sftg",item.getTitle());
                dataMap.put("processAttr",FastJsonUtils.mapToString(processAttrMap));
                dataMap.put("formAttr",FastJsonUtils.ListMapToListStr(formAttrList));
                intent.putExtras(AppUtils.setParms("",dataMap));
                startActivityForResult(intent,1);
                break;

            case 2:
                intent.setClass(MyTask_edit_Activity.this,MyTask_BackActivity.class);
                dataMap.put("formData",FastJsonUtils.mapToString(main_data));
                dataMap.put("backActivity",FastJsonUtils.ListMapToListStr(backActivityList));
                dataMap.put("fqr",fqr);
                dataMap.put("processAttr",FastJsonUtils.mapToString(processAttrMap));
                dataMap.put("index",index);
                intent.putExtras(AppUtils.setParms("",dataMap));
                startActivityForResult(intent,2);
                break;
            case 3:
                pars = new ListParms("0","0",AppUtils.limit,"psndoc","workstatus:1").getParms();
                final String testpars = StringUtils.strTOJsonstr(pars);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        res = ActivityController.getDataByPost(mContext,"psndoc","list",testpars);
                        int recordcount=0,pageIndex=0;
                        List<Map<String,Object>> result_list =null;

                        try {
                            if(!res.equals("")){
                                resMap = getResMap(res.toString());
                                if(resMap.get("results")!= null){
                                    recordcount = Integer.parseInt(resMap.get("results").toString());
                                    pageIndex = 1;
                                    result_list = (List<Map<String, Object>>) resMap.get("rows");
                                    String[] items = FastJsonUtils.ListMapToListStr(result_list,"name");
                                    List<String> list = FastJsonUtils.MapToListByKey(result_list,"name");

                                    final int finalRecordcount = recordcount;
                                    final List<Map<String, Object>> finalResult_list = result_list;
                                    final int finalPageIndex = pageIndex;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final SingleChoicePopWindow mSingleChoicePopWindow = new SingleChoicePopWindow(MyTask_edit_Activity.this, main_form, finalResult_list,"psndoc","list",
                                                    pars,"name", finalRecordcount, finalPageIndex,"");
                                            mSingleChoicePopWindow.setOnOKButtonListener(new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    Map<String, Object> dataMap = (Map<String, Object>) mSingleChoicePopWindow.getSelectObject();
                                                    String val = dataMap.get("user_name").toString();
                                                    pars = "taskId:"+taskId+",assignee:"+val;
                                                    final String testpars = StringUtils.strTOJsonstr(pars);
                                                    mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"委托中...");
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            boolean flag = ActivityController.execute(MyTask_edit_Activity.this,"process","transferTask",testpars);
                                                            if (flag){
                                                                Intent intent = new Intent();
                                                                intent.putExtra("index",index);
                                                                setResult(2,intent);
                                                                MyTask_edit_Activity.this.finish();
                                                            }
                                                            WeiboDialogUtils.closeDialog(mDialog);
                                                        }
                                                    }).start();

                                                }
                                            });
                                            mSingleChoicePopWindow.show(true);
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
            case 4:
                main_data.put("menucode",menu_code);
                ActivityController.startMyFileActivity(mContext,main_data);
                break;
            default:
                break;
        }
        return true;
    }*/

}