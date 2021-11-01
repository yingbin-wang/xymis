package com.cn.wti.activity.myTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.WebViewActivity;
import com.cn.wti.activity.base.BaseEdit_ProcessActivity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.number.IniUtils;
import com.cn.wti.util.other.StringUtils;
import com.dina.ui.widget.UITableView;
import com.wticn.wyb.wtiapp.R;;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApply_edit_Activity extends BaseEdit_ProcessActivity implements View.OnClickListener {

    private  String pars,id,taskId = "",serviceName;
    private Map<String,Object> resMap,dataMap,gzMap;
    private RecyclerView historymx_View = null;;
    private List<Map<String,Object>> historyCommentList = new ArrayList<Map<String,Object>>(); //历史信息

    private ImageButton title_back2,title_ok2 = null;
    private TextView title_name2 = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.common_edit_task;
        mContext = MyApply_edit_Activity.this;
        super.onCreate(savedInstanceState);

        title_back2 = findViewById(R.id.title_back2);
        title_ok2 = findViewById(R.id.title_ok2);
        title_back2.setOnClickListener(this);
        title_ok2.setVisibility(View.GONE);
        title_name2 = findViewById(R.id.title_name2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public String initData() {

        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");

        if(so == null){
            return "";
        }

        parmsMap = so.getParms();
        if (parmsMap == null){ return "";}

        dataMap = (Map<String, Object>) parmsMap.get("mainData");
        taskId = AppUtils.getMapVal(dataMap,"ID_").toString();

        String bussiness_key = AppUtils.getMapVal(dataMap,"BUSINESS_KEY_").toString();
        if(bussiness_key!=null && !bussiness_key.equals("")) {
            String[] formkeys = bussiness_key.split(":");
            serviceName = formkeys[0];
            id = formkeys[1];
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("menucode", StringUtils.firstLowerStr(serviceName));
            map.put("parms", "{id:" + id + ",isProcess:1,isEdit:0}");
            String res = null;
            try {
                res = HttpClientUtils.webService("findFormProperty2", FastJsonUtils.mapToString(map));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (res != null && !res.toString().contains("(abcdef)")) {
                try {
                    resMap = FastJsonUtils.strToMap(res);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                menu_code = serviceName;
                menu_name = "";
            }else{
                this.finish();
                return "未找到格式文件";
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

                if (gzMap.get("select") != null){
                    Map<String,Object> select_parms = (Map<String, Object>) gzMap.get("select");
                    if (select_parms.get("main") != null){
                        parms = (Map<String, Object>) select_parms.get("main");
                    }
                }
            }

            if (resMap != null && resMap.size() > 0) {

                try {
                    if (resMap.get("main") == null) {
                        this.finish();
                        return "未找到主表格式";
                    } else if (resMap.get("mainData") == null) {
                        this.finish();
                        return "未找到主表格式";
                    }
                    maings_list = FastJsonUtils.getBeanMapList(resMap.get("main").toString());
                    //如果 没有 参数对象 根据主数据格式 封装
                    if (parms != null && parms.size() == 0) {
                        createMyParms();
                    }

                    if (resMap.get("mainData") instanceof JSONObject) {
                        main_data = (Map<String, Object>) resMap.get("mainData");
                    } else {
                        main_data = FastJsonUtils.strToMap(resMap.get("mainData").toString());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            String process_instanceid = AppUtils.getMapVal(dataMap,"ID_").toString();
            Map<String,String> paramsPd = new HashMap<>();
            paramsPd.put("PROC_INST_ID_",process_instanceid);
            Object process_bz = ActivityController.getData4ByPost("process","findProcess_Bz",FastJsonUtils.mapToString(paramsPd));
            historyCommentList.clear();
            historyCommentList.addAll((List<Map<String, Object>>)process_bz);
        }
        return  "";
    }

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
                        break;
                    case 2:
                        mx2.setVisibility(View.VISIBLE);
                        mx2_title.setText(title_name);
                        //ActivityController.showMxRecyclerView(mContext,serviceName,code,id,title_name,index);
                        ActivityController.showMxWebview(mx2_View,serviceName,code,id,title_name);
                        break;
                    case 3:
                        mx3.setVisibility(View.VISIBLE);
                        mx3_title.setText(title_name);
                        //ActivityController.showMxRecyclerView(mContext,serviceName,code,id,title_name,index);
                        ActivityController.showMxWebview(mx3_View,serviceName,code,id,title_name);
                        break;
                    case 4:
                        mx4.setVisibility(View.VISIBLE);
                        mx4_title.setText(title_name);
                        //ActivityController.showMxRecyclerView(mContext,serviceName,code,id,title_name,index);
                        ActivityController.showMxWebview(mx4_View,serviceName,code,id,title_name);
                        break;
                    case 5:
                        mx5.setVisibility(View.VISIBLE);
                        mx5_title.setText(title_name);
                        //ActivityController.showMxRecyclerView(mContext,serviceName,code,id,title_name,index);
                        ActivityController.showMxWebview(mx5_View,serviceName,code,id,title_name);
                        break;
                }
            }
        }

        //添加 主数据
        if (maings_list != null && maings_list.size() > 0) {
            tableView = (UITableView) findViewById(R.id.tableView);
            tableView.setReadOnly(true);
            createList(tableView);
        }

        //历史审批
        try {

            if (historyCommentList!= null && historyCommentList.size()>0){
                historymx_View = (RecyclerView) findViewById(R.id.historymx_View);
                ActivityController.setLayoutManager(historymx_View,mContext);
                LinearLayout historymx = (LinearLayout) findViewById(R.id.historymx);
                historymx.setVisibility(View.VISIBLE);
                String[] contents= new String[]{"NAME_","USERNAME_","JG_","END_TIME_"};
                //创建并设置Adapter
                mCheck = new boolean[historyCommentList.size()];
                mAdapter1 = new MyAdapter2(this,historyCommentList,AppUtils.getScreenWidth(mContext),new String[]{},contents,R.layout.list_item_history,mCheck);
                historymx_View.setAdapter(mAdapter1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back2:
                finish();
                break;
             default:
                 break;
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


    /**
     * 根据 主格式数据 封装 参数数据
     */
    public void createMyParms(){
        List<Map<String,Object>> parmsMapList = new ArrayList<Map<String,Object>>();
        List<String> gldns = new ArrayList<String>();
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
                        map_1.put(parmsMap.get("code").toString(),"khjc");
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
    public  String getVal(String key,Map<String,Object>map){
        Object JG_ = map.get(key);
        String res="";

        /*if (key.indexOf("：")>=0){
            String[] _keys = key.split("：");
            String map_key="";
            int start = _keys[1].indexOf("[");
            int end = _keys[1].indexOf("]");

            if(start>=0 && end >=0){
                map_key = _keys[1].substring(start+1,end);
            }else{
                map_key = _keys[1];
            }
        }*/

        //流结果
        if(key.equals("JG_")){
            if ((JG_ ==  null || JG_.equals("正常"))&& map.get("END_TIME_") != null){
                res = "通过";
            }else {
                if(JG_ == null){
                    res = "";
                }else if (JG_.equals("正常")) {
                    res = "";
                } else {
                    res = JG_.toString();
                }
            }
        }else if(key.equals("START_TIME_")){
            if(JG_ == null || JG_.toString().equals("")){
                res = "";
            }else{
                res = JG_.toString();
            }
        }else if(key.equals("END_TIME_")){
            if(JG_ == null || JG_.toString().equals("")){
                res = "";
            }else{
                res = JG_.toString();
            }
        }else if(key.equals("MESSAGE_")){
            if(JG_ == null || JG_.toString().equals("")){
                res = "";
            }else{
                res = JG_.toString();
            }
        }else{
            if (key.equals("")){
                res = "";
            }else{
                res = map.get(key).toString();
            }

        }
        return  res;
    }

}