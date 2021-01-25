package com.cn.wti.activity.rwgl.myassignment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.IDHelper;
import com.cn.wti.util.app.dialog.Dialog_ViewUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.number.IniUtils;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Createtask_editActivity extends BaseEdit_NoTable_Activity{

    /*private Map<String,String> showMap;*/
    private String pars,id,ywtype,service_name,caozuo_type;
    private String[] contents;
    private Map<String,Object> gs_map,select_map,parmsMap,main_data;
    private Context mContext;
    private int table_postion;

    private TextView_custom handle_emps_tv,handle_empids_tv,tasktype,completetime;
    private ImageView tzlxxz,jsrxz,jsbmxz;
    private ImageButton  showmore;
    private Button ok_btn;
    private LinearLayout jsr_liner;
    private List<String> jsrids = new ArrayList<String>(),jsrnames= new ArrayList<String>();
    private List<Map<String,Object>> createtaskmxList = new ArrayList<Map<String,Object>>();
    private RecyclerView createTaskmx_view;
    private boolean[] mCheck;
    private MyAdapter2 mAdapter1;

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

        if (ywtype.equals("edit")){
            layout = R.layout.activity_createtask_edit;
        }else{
            layout = R.layout.activity_createtask_edit02;
        }

        super.onCreate(savedInstanceState);
        initData();
        createView();
    }

    public void initData() {
        menu_code = "createtask";
        menu_name="发起任务";

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

        mContext = Createtask_editActivity.this;

        ok_btn = (Button) findViewById(R.id.ok);
        if (ok_btn != null){
            ok_btn.setOnClickListener(this);
        }

        //显示更多
        showmore = (ImageButton) findViewById(R.id.title_ok2);
        if (showmore != null){
            showmore.setOnClickListener(this);
            ImageButton img_btn_ok= (ImageButton) findViewById(R.id.title_ok2);
            img_btn_ok.setBackground(getResources().getDrawable(R.mipmap.documentmore));
        }

        main_form = (LinearLayout) findViewById(R.id.main_form);

        Intent intent = getIntent();
        System_one so = (System_one)intent.getSerializableExtra("parms");
        if(so == null){
            return;
        }
        parmsMap = so.getParms();

        if (parmsMap!= null){
            try {

                ywtype = parmsMap.get("type").toString();
                index = parmsMap.get("index").toString();
                table_postion = Integer.parseInt(parmsMap.get("table_postion").toString());
                if (ywtype.equals("add")){
                    main_data = new HashMap<String, Object>();
                    ActivityController.initUserMainData(main_data,initMap,AppUtils.user);
                    main_title.setText("新增任务");
                    id = "";
                }else{
                    main_title.setText("任务编辑");
                    main_data = (Map<String, Object>) parmsMap.get("mainData");
                    id = parmsMap.get("id").toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //经手人父视图
        jsr_liner = (LinearLayout) findViewById(R.id.jsr_liner_);
        handle_empids_tv = (TextView_custom) findViewById(R.id.handle_empids);
        handle_emps_tv = (TextView_custom) findViewById(R.id.handle_emps);
        handle_emps_tv.addTextChangedListener(new TextWatcher() {
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
                String ids = handle_empids_tv.getText().toString();
                if (!ids.equals("")){
                    String[] ids_array = ids.split(",");
                    for (String id:ids_array) {
                        if (!id.equals("") && !jsrids.contains(id)){
                            jsrids.add(id);
                        }
                    }
                }

                String names = handle_emps_tv.getText().toString();
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
                    if (!createTv(jsr_str)){
                        tv = new TextView_custom(mContext);
                        RelativeLayout.LayoutParams layoutParams=
                                new RelativeLayout.LayoutParams(180, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        tv.setLayoutParams(layoutParams);
                        tv.setText(jsr_str);
                        //执行删除动作
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                jsr_liner.removeView(v);
                                TextView_custom tv1 = (TextView_custom) v;
                                int index = jsrnames.lastIndexOf(tv1.getText());
                                jsrnames.remove(index);
                                jsrids.remove(index);
                                handle_empids_tv.setText(StringUtils.listStrTostrSplit(jsrids,","));
                                handle_emps_tv.setText(StringUtils.listStrTostrSplit(jsrnames,","));
                            }
                        });
                        jsr_liner.addView(tv);
                    }
                }
            }
        });

        reshDataUI();

        //显示完成情况

        createTaskmx_view = (RecyclerView) findViewById(R.id.createTaskmx_view);
        ActivityController.setLayoutManager(createTaskmx_view,mContext);
        String[] contents= new String[]{"jsr_name","completetime","completion"};
        //创建并设置Adapter
        mAdapter1 = new MyAdapter2(this,createtaskmxList,AppUtils.getScreenWidth(mContext),new String[]{},contents,R.layout.list_item_createtaskmx);
        createTaskmx_view.setAdapter(mAdapter1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Object res = ActivityController.getData2ByPost(mContext,menu_code,"findCreatetaskDetailByCreatetaskid",StringUtils.strTOJsonstr("id:"+id));
                if (res != null && res instanceof JSONArray){
                    createtaskmxList.addAll((List<Map<String, Object>>) res);
                }
        }
        }).start();

    }

    public boolean createTv(String val){
        if (jsr_liner != null){
            int count = jsr_liner.getChildCount();
            View view = null;
            TextView_custom tv = null;
            for (int i= 0;i<count;i++) {
                view = jsr_liner.getChildAt(i);
                if (view != null && view instanceof TextView_custom){
                    tv = (TextView_custom) view;
                    if (tv.getText().toString().equals(val)){
                        return true;
                    }
                }
            }
        }

        return  false;
    }

    public void reshDataUI(){
        //任务事件
        updateOneUI(main_data,"taskname");
        //任务描述
        updateOneUI(main_data,"taskdetails");

        //接收人
        updateOneUI(main_data,"handle_empids");
        updateOneUI(main_data,"handle_emps");

        //新增时添加选择项
        jsrxz = (ImageView) findViewById(R.id.jsrxz);
        jsrxz.setOnClickListener(this);

        select_map = new HashMap<String, Object>();
        Map<String,Object> map = new HashMap<String, Object>();

        //接收人员
        map = new LinkedHashMap<String, Object>();
        map.put("handle_empids","id");
        map.put("handle_emps","name");
        select_map.put("staff~接收人员",map);
    }

    /**
     * 保存和编辑方法
     */

    @Override
    public String saveOrEdit() {

        String method = "";
        if (ywtype != null && ywtype.equals("add")){
            method = "save";
            service_name = "createtask";
        }else{
            ywtype = "edit";
            method = "edit";
            service_name = "createtask";
        }

        for (String view_name:view_names) {
            main_data.put(view_name,getVal(view_name));
        }

        if (jsrids!= null && jsrids.size() >0){
            main_data.put("handle_empids",StringUtils.listTostr(jsrids));
        }

        if (jsrnames!= null && jsrnames.size() >0){
            main_data.put("handle_emps",StringUtils.listTostr(jsrnames));
        }

        //得到 ID
        if(main_data.get("id") != null){
            id = (String) main_data.get("id");
        }

        if (main_data!= null){
            main_data.put("projectopportunityid","0");
            main_data.put("projectcontractid","0");
            main_data.put("projectcontractrwglmxid","0");

            main_data.put("currentuserid",AppUtils.user.get_zydnId());

            if (method.equals("save")){

                String jsrids = main_data.get("handle_empids").toString();
                String jsrs = main_data.get("handle_emps").toString();
                if (jsrids != null && !jsrids.equals("")){
                    String[] jsr_idArray = jsrids.split(",");
                    String[] jsr_nameArray = jsrs.split(",");
                    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
                    Map<String,Object> map = null;
                    int i = 0;
                    for (String jsrid: jsr_idArray) {
                        map = new HashMap<String,Object>();
                        map.put("createtaskid","");
                        map.put("createtaskmxid","");
                        map.put("jsr_name",jsr_nameArray[i]);
                        map.put("jsrid",jsrid);
                        map.put("projectcontractrwglmxid","0");
                        map.put("projectopportunitymxid","0");
                        map.put("rowid", IniUtils.getFixLenthString(5));
                        map.put("zt", 1);
                        i++;
                        list.add(map);
                    }
                    if (list!= null && list.size()>0){
                        main_data.put("createtaskmx",list);
                        main_data.put("createtaskfilemx","[]");

                    }
                }
            }

            String main_str = FastJsonUtils.mapToString(main_data);
            if (!main_str.equals("")){
                main_str = main_str.substring(1,main_str.length()-1);
            }

            String pars =  new ListParms(menu_code,"id:"+id).getParms();
            String testpars = StringUtils.strTOJsonstr(pars);
            String parms = testpars.substring(0,testpars.length()-1)+","+ main_str +"}";
            res = ActivityController.getDataByPost(mContext,service_name,method,parms);
            return  res.toString();
        }
        return  "err";
    }

    public List<String> mergeList(List<String> list1,List<String> list2){
        List<String> resList = new ArrayList<String>();
        if (list1 != null && list1.size()>0 && list2 != null && list2.size()>0){
            int i = 0;
            for (String str1:list1) {
                resList.add(str1+","+list2.get(i));
                i++;
            }
        }
        return  resList;
    }

    @Override
    public void showOptions(int id) {
        switch (id){
            case R.id.jsrxz:
                String pars = new ListParms("0","0","20",service_name,"").getParms();
                View reshVIew = findViewById(R.id.receive_emp_names);
                main_form.setTag(mergeList(jsrids,jsrnames));
                ActivityController.showDialog_MoreSelect2(mContext,"staff~接收人员","staff","list",pars,"name",main_form,view_names,select_map);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Map<String,Object> map = null;
        switch (v.getId()){
            case R.id.title_back2:
                Intent intent = new Intent();
                intent.putExtra("index",index);
                intent.putExtra("type","update");
                intent.putExtra("tabpostion",table_postion);
                intent.putExtras(AppUtils.setParms("update",main_data));
                setResult(1,intent);
                Createtask_editActivity.this.finish();
                ActivityController.hiddenInput(Createtask_editActivity.this);
                break;
            case R.id.title_ok2:

                if (!ywtype.equals("add")){

                    if (v.getTag() != null){
                        map = (Map<String, Object>) v.getTag();
                    }else{
                        map = new HashMap<String, Object>();
                    }
                    map.put("currentView",main_form);
                    map.put("_selectView",v);
                    Dialog_ViewUtils.showPopUp(v,mContext,this,R.layout.more_button,300,460,map,new String[]{"faqi","chexiao","shanchu"},null,1);

                }

                break;
            case R.id.ok:

                if (!validate()){return;}

                final Dialog mDialog1 = WeiboDialogUtils.createLoadingDialog(mContext,"发起中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String isSave = saveOrEdit();
                        if (!isSave.equals("err") && !isSave.equals("")){
                            runOnUiThread(ActivityController.createWaitMiniteCz(mContext,isSave,mDialog1));
                            Createtask_editActivity.this.finish();
                        }
                    }
                }).start();
                break;
            case R.id.faqi:
                res = v.getTag();
                if (res != null) {
                    // 获取数据
                    map = (Map<String, Object>) res;

                    if (main_data.get("estatus") != null && main_data.get("estatus").equals("7")){
                        Toast.makeText(mContext,mContext.getString(R.string.save_task_faqi_text),Toast.LENGTH_SHORT).show();
                        PopupWindow popupWindow = (PopupWindow) map.get("popupWindow");
                        popupWindow.dismiss();
                        return;
                    }

                    if (main_data.get("taskname") != null && main_data.get("taskname").toString().equals("")){
                        Toast.makeText(mContext,"必须输入任务事件",Toast.LENGTH_SHORT).show();
                        PopupWindow popupWindow = (PopupWindow) map.get("popupWindow");
                        popupWindow.dismiss();
                        return;
                    }

                    if (main_data.get("handle_empids") == null || main_data.get("handle_emps") == null){
                        Toast.makeText(mContext,"必须输入接收人员",Toast.LENGTH_SHORT).show();
                        PopupWindow popupWindow = (PopupWindow) map.get("popupWindow");
                        popupWindow.dismiss();
                        return;
                    }

                    final Dialog mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"");
                    final Map<String, Object> finalMap = map;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String isSave = saveOrEdit();
                            if (!isSave.equals("err") && !isSave.equals("")){

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isSave != null){
                                            Map<String,Object> resMap = FastJsonUtils.strToMap(isSave.toString());

                                            if(resMap == null){
                                                Toast.makeText(mContext,R.string.connection_timeout,Toast.LENGTH_SHORT).show();
                                                WeiboDialogUtils.closeDialog(mDialog);
                                                PopupWindow popupWindow = (PopupWindow) finalMap.get("popupWindow");
                                                popupWindow.dismiss();
                                                return;
                                            }
                                            if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                                                Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                                id = resMap.get("data").toString();
                                                if (id.indexOf("err")>=0){
                                                    Toast.makeText(mContext,id.replace("err,",""),Toast.LENGTH_SHORT).show();
                                                    WeiboDialogUtils.closeDialog(mDialog);
                                                    PopupWindow popupWindow = (PopupWindow) finalMap.get("popupWindow");
                                                    popupWindow.dismiss();
                                                    return;
                                                }else{
                                                    //更新主ID
                                                    Intent intent = new Intent();
                                                    main_data.put("id",id);
                                                    main_data.put("estatus","7");
                                                    intent.putExtra("index",index);
                                                    intent.putExtra("type","update");
                                                    intent.putExtra("tabpostion",table_postion);
                                                    intent.putExtras(AppUtils.setParms("update",main_data));
                                                    setResult(1,intent);
                                                    Createtask_editActivity.this.finish();
                                                }

                                            }else{
                                                Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                                WeiboDialogUtils.closeDialog(mDialog);
                                                PopupWindow popupWindow = (PopupWindow) finalMap.get("popupWindow");
                                                popupWindow.dismiss();
                                                return;
                                            }
                                        }

                                        WeiboDialogUtils.closeDialog(mDialog);
                                        PopupWindow popupWindow = (PopupWindow) finalMap.get("popupWindow");
                                        popupWindow.dismiss();
                                    }
                                });
                            }
                        }
                    }).start();
                }

                break;
            case R.id.chexiao:
                res = v.getTag();
                if (res != null) {
                    // 获取数据
                    map = (Map<String, Object>) res;
                    if (main_data.get("estauts") != null && main_data.get("estauts").equals("1")){
                        Toast.makeText(mContext,mContext.getString(R.string.save_check_text),Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //版本号
                    if (main_data.get("version")!= null){
                        version = main_data.get("version").toString();
                    }

                    pars = new ListParms(menu_code,"DATA_IDS:"+main_data.get("id").toString()+",estatus:1"
                            +",version:"+version+",ip:"+AppUtils.app_ip+",device:PHONE").getParms();
                    exectueThreadreturnObj(mContext,menu_code,"auditAll",StringUtils.strTOJsonstr(pars),"chexiao",map);

                }
                break;
            case R.id.shanchu:
                res = v.getTag();
                if (res != null) {
                    // 获取数据
                    map = (Map<String, Object>) res;
                    if (main_data.get("estauts") != null && main_data.get("estauts").equals("7")){
                        Toast.makeText(mContext,mContext.getString(R.string.save_check_text),Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //版本号
                    if (main_data.get("version")!= null){
                        version = main_data.get("version").toString();
                    }

                    pars = new ListParms(menu_code,"DATA_IDS:"+main_data.get("id").toString()
                            +",version:"+version+",ip:"+AppUtils.app_ip+",device:PHONE").getParms();
                    exectueThreadreturnObj(mContext,menu_code,"deleteAll",StringUtils.strTOJsonstr(pars),"shanchu",map);
                }
                break;

            case R.id.jsrxz:
                showOptions(R.id.jsrxz);
                break;

            default:
                break;
        }
    }

    @Override
    public String execute_BackMethod(Object res, String type,Map<String,Object> map) {

        res = super.execute_BackMethod(res,type,map);
        final String finaltype = type,finalres = res.toString();
        final Map<String,Object> finalMap = map;
        final PopupWindow popupWindow = (PopupWindow) finalMap.get("popupWindow");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (finaltype){
                    case "chexiao":
                        String isChexiao =  finalres.toString();
                        if (isChexiao.indexOf("err")<0 && !isChexiao.equals("")){
                            sendBackMessage(mContext,getString(R.string.success_createtask_chexiao));
                            main_data.put("estatus","1");
                            caozuo_type = "chexiao";
                            reshData(menu_code,"findById",StringUtils.strTOJsonstr("{id:"+id+"}"));
                        }else{
                            sendBackMessage(mContext,isChexiao);
                        }
                        popupWindow.dismiss();
                        break;
                    case "shanchu":
                        String isdelete =  finalres;
                        if (isdelete.indexOf("err")<0 && !isdelete.equals("")){
                            main_data.put("id",id);
                            Intent intent = new Intent();
                            intent.putExtra("index",index);
                            intent.putExtra("type","delete");
                            intent.putExtra("tabpostion",table_postion);
                            intent.putExtras(AppUtils.setParms("update",main_data));
                            setResult(1,intent);
                            Createtask_editActivity.this.finish();
                        }else{
                            sendBackMessage(mContext,isdelete);
                        }
                        popupWindow.dismiss();
                        break;
                }
            }
        });
        return "";
    }

    public void reshData(final String service_name, final String method_name, final String pars){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object res = ActivityController.getData4ByPost(service_name,method_name,pars);
                if (res != null && res instanceof JSONObject){
                    main_data.putAll((Map<String,Object>) res);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reshDataUI();
                        }
                    });
                }
            }
        }).start();
    }

    public boolean validate(){
        for (String view_name:view_names) {
            main_data.put(view_name,getVal(view_name));
        }

        if (main_data.get("taskname") == null || main_data.get("taskname").toString().equals("")){
            Toast.makeText(mContext,"任务事件不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (main_data.get("taskdetails") == null || main_data.get("taskdetails").toString().equals("")){
            Toast.makeText(mContext,"任务描述不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }

        //接收人必须填写
        if (main_data.get("handle_emps")== null || main_data.get("handle_emps").toString().equals("")){
            Toast.makeText(mContext,getString(R.string.error_invalid_jsry),Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }
}
