package com.cn.wti.activity.base;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.adapter.handler.MyHandler;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.dialog.window.MultiChoicePopWindow_CheckTask;
import com.cn.wti.entity.view.custom.note.NoteEditText;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.Constant;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.IDHelper;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2016/11/9.
 */

public class BaseEdit_NoTable_Activity extends BaseActivity implements View.OnClickListener{

    protected  Map<String,String> initMap;
    protected String menu_code /*菜单编号*/,menu_name /*菜单名称*/,ywtype /*业务状态*/,service_name,method_name,id,index,caozuo_type=""/*操作类型*/,version/*版本号*/;
    protected  ImageButton btn_back,btn_ok;
    protected Button ok;

    protected  TextView main_title;
    protected  int layout = 0,table_postion;
    protected List<View> views;
    protected List<String> view_names = new ArrayList<String>();
    protected LinearLayout main_form;
    private static PageDataSingleton _catch = PageDataSingleton.getInstance();
    protected Context mContext;
    protected  Map<String,Object> main_data = null,current_map;
    protected Dialog mDialog;

    protected Handler ztHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                   /* Bundle data = msg.getData();
                    String serviceName = data.getString("serviceName"),
                            methodName = data.getString("methodName"),
                            parms = data.getString("pars");

                    reshData(serviceName,methodName,StringUtils.strTOJsonstr(parms));*/

                    if (mDialog != null){
                        WeiboDialogUtils.closeDialog(mDialog);
                    }

                    if (current_map != null){
                        PopupWindow popupWindow = (PopupWindow) current_map.get("popupWindow");
                        popupWindow.dismiss();
                    }

                    //如果是发布任务
                    btn_back.performClick();
                    break;
            }
        }
    };

    public static PageDataSingleton get_catch() {
        return _catch;
    }

    public static void set_catch(PageDataSingleton _catch) {
        BaseEdit_NoTable_Activity._catch = _catch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);*/
        super.onCreate(savedInstanceState);
        if(layout != 0){
            setContentView(layout);
        }
        AppUtils.setStatusBarColor(this);
        //设置标题
        main_title = (TextView)findViewById(R.id.title_name2);

        //返回按钮
        Object view1 = findViewById(R.id.title_back2);
        if (view1 instanceof ImageButton){
            btn_back = (ImageButton) findViewById(R.id.title_back2);
        }
        view1 = findViewById(R.id.title_ok2);
        if (view1 instanceof ImageButton){
            btn_ok = (ImageButton)findViewById(R.id.title_ok2);
        }

        if (btn_back != null){
            btn_back.setOnClickListener(this);
        }

        if (btn_ok != null){
            btn_ok.setOnClickListener(this);
        }

        View view = findViewById(R.id.main_form);
        if (view instanceof LinearLayout){
            main_form = (LinearLayout) findViewById(R.id.main_form);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_ok2:
                saveOrEdit();
                break;
            case R.id.ok:
                saveOrEdit();
                break;
            case R.id.title_back2:
                ActivityController.hiddenInput(BaseEdit_NoTable_Activity.this);
                this.finish();
                break;
            /*case R.id.tzlxxz:
                showOptions(R.id.tzlxxz);
                break;
            case R.id.jsrxz:
                showOptions(R.id.jsrxz);
                break;
            case R.id.jsbmxz:
                showOptions(R.id.jsbmxz);
                break;*/
        }
    }

    /**
     *
     * @param id_
     * @return
     */
    public String getVal(String id_){
        String val="";
        View view = findViewById(IDHelper.getViewID(getApplicationContext(),id_));
        if (view instanceof TextView_custom){
            TextView_custom tv = (TextView_custom) view;
            val = tv.getText().toString();
        }else if (view instanceof EditText_custom){
            EditText_custom ev = (EditText_custom) view;
            val = ev.getText().toString();
        }else if (view instanceof NoteEditText){
            NoteEditText ev = (NoteEditText) view;
            val = ev.getText().toString();
        }else if (view instanceof EditText){
            EditText ev = (EditText) view;
            val = ev.getText().toString();
        }else if (view instanceof TextView){
            TextView ev = (TextView) view;
            val = ev.getText().toString();
        }else if (view instanceof Spinner){
            Spinner spinner = (Spinner) view;
            spinner.getSelectedItemPosition();
            val = spinner.getSelectedItem().toString();
            switch (val){
                case "星期一":
                    val = "1";
                    break;
                case "星期二":
                    val = "2";
                    break;
                case "星期三":
                    val = "3";
                    break;
                case "星期四":
                    val = "4";
                    break;
                case "星期五":
                    val = "5";
                    break;
                case "星期六":
                    val = "6";
                    break;
                case "星期日":
                    val = "7";
                    break;

            }

            if (val.indexOf("日")>=0){
                val = val.replace("日","");
            }else if (val.indexOf("月")>=0){
                val = val.replace("月","");
            }
        }
        return val;
    }

    /**
     * 普通 更新 UI textView 等 直接传值
     * @param map
     * @param key key 与 表单 ID
     * @param value 值
     * @return
     */
    public String updateOneUI(Map<String,Object> map,String key,String value){
        if (map == null){return ""; }
        View view = findViewById(IDHelper.getViewID(getApplicationContext(),key));
        String val="";
        if(map.get(key)!= null){
            val = map.get(key).toString();
        }else{
            val = "";
        }

        if (value != null){
            val = value;
        }

        if (view instanceof TextView_custom){
            TextView_custom tv = (TextView_custom) view;
            tv.setText(val);
        }else if (view instanceof EditText_custom){
            EditText_custom ev = (EditText_custom) view;
            ev.setText(val);
        }else if (view instanceof NoteEditText){
            NoteEditText ev = (NoteEditText) view;
            ev.initText(val);
        }else if (view instanceof TextView){
            TextView ev = (TextView) view;
            ev.setText(val);
        }else if (view instanceof EditText){
            EditText ev = (EditText) view;
            ev.setText(val);
        }

        //添加 到views 中
        if (!key.equals("")){
            view_names.add(key);
        }
        return  val;
    }

    /**
     * 普通 更新 UI textView 等
     * @param map
     * @param key
     * @return
     */
    public String updateOneUI(Map<String,Object> map,String key){
        if (map == null){return ""; }
        View view = findViewById(IDHelper.getViewID(getApplicationContext(),key));
        String val="";
        if(map.get(key)!= null){
            val = map.get(key).toString();
        }else{
            val = "";
        }

        if(StringUtils.isNumeric(val) && val.length() == 13){
            val = DateUtil.stampToDate(val);
            if (DateUtil.isDate(val)){
                map.put(key,val);
            }
        }

        if (val.indexOf("<br>")>=0){
            val = val.replaceAll("<br>","");
        }

        if (view instanceof TextView_custom){
            TextView_custom tv = (TextView_custom) view;
            if (tv != null){
                tv.setText(val);
            }
        }else if (view instanceof EditText_custom){
            EditText_custom ev = (EditText_custom) view;
            if (ev != null){
                ev.setText(val);
            }
        }else if (view instanceof NoteEditText){
            NoteEditText ev = (NoteEditText) view;
            if (ev != null){
                ev.setText(val);
            }
        }else if (view instanceof TextView){
            TextView ev = (TextView) view;
            if (ev != null){
                ev.setText(val);
            }
        }else if (view instanceof EditText){
            EditText ev = (EditText) view;
            if (ev != null){
                ev.setText(val);
            }
        }

        //添加 到views 中
        if (!key.equals("")){
            view_names.add(key);
        }
        return  val;
    }

    /**
     * 更新 UI 下拉列表 更新选择项
     * @param map
     * @param key
     * @param array
     * @return
     */
    public String updateOneUI(Map<String,Object> map,String key,List<String> array){
        if (map == null){return ""; }
        View view = findViewById(IDHelper.getViewID(getApplicationContext(),key));
        String val="";
        if(map.get(key)!= null){
            val = map.get(key).toString();
        }else{
            val = "";
        }

        if (view instanceof TextView_custom){
            TextView_custom tv = (TextView_custom) view;
            tv.setText(val);
        }else if (view instanceof EditText_custom){
            EditText_custom ev = (EditText_custom) view;
            ev.setText(val);
        }else if (view instanceof NoteEditText){
            NoteEditText ev = (NoteEditText) view;
            ev.initText(val);
        }else if (view instanceof TextView){
            TextView ev = (TextView) view;
            ev.setText(val);
        }else if (view instanceof EditText){
            EditText ev = (EditText) view;
            ev.setText(val);
        }else if (view instanceof Spinner){
            Spinner spinner = (Spinner) view;
            if (StringUtils.isNumeric(val)){
                spinner.setSelection(Integer.parseInt(val)-1);
            }else{
                int index = array.lastIndexOf(val);
                spinner.setSelection(index);
            }
        }

        //添加 到views 中
        if (!key.equals("")){
            view_names.add(key);
        }
        return  val;
    }

    public String saveOrEdit(){return "";};

    public void showOptions(int id){}

    public void deleteOne(){}

    public boolean shanchu(View view){
        Object res = view.getTag();
        final Map<String,Object> map;

        if (res != null) {
            // 获取数据
            map = (Map<String, Object>) res;
            if (!validate("delete")){
                return false;
            }

            mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"删除中...");
            new Thread(new Runnable() {
                @Override
                public void run() {

                    //版本号
                    if (main_data.get("version")!= null){
                        version = main_data.get("version").toString();
                    }

                    String pars = new ListParms(menu_code,"id:"+main_data.get("id").toString()
                            +",version:"+version+",ip:"+AppUtils.app_ip+",device:PHONE"
                    ).getParms();
                    final String isdelete =  ActivityController.executeForResultByThread(mContext,menu_code,"delete", StringUtils.strTOJsonstr(pars));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (!isdelete.equals("err") && !isdelete.equals("")){
                                main_data.put("id",id);
                                Intent intent = new Intent();
                                intent.putExtra("index",index);
                                intent.putExtra("type","delete");
                                intent.putExtras(AppUtils.setParms("",main_data));
                                intent.putExtra("table_postion",table_postion);
                                setResult(1,intent);
                                ((Activity)mContext).finish();
                            }else{
                                Toast.makeText(mContext,isdelete.replace("err",""),Toast.LENGTH_SHORT).show();
                            }

                            if (mDialog != null){
                                WeiboDialogUtils.closeDialog(mDialog);
                            }
                            PopupWindow popupWindow = (PopupWindow) map.get("popupWindow");
                            popupWindow.dismiss();
                        }
                    });
                }
            }).start();
        }
        return  true;
    }

    public boolean validate(){return  true;}
    public boolean validate(String type){return  true;}
    /**
     * 执行撤销操作
     * @param view
     * @return
     */
    public boolean auditAll(final View view, final String estatus, String msg){
        Object res = view.getTag();
        Map<String,Object> map = null;
        if (res != null) {
            // 获取数据
            map = (Map<String, Object>) res;
        }
        final Map<String,Object> finalmap = map;
        current_map = map;
        //执行审核 或 撤审操作
        if (validate()) {
            mDialog = WeiboDialogUtils.createLoadingDialog(mContext,msg);
            new  Thread(new Runnable() {
                @Override
                public void run() {
                    //版本号
                    if (main_data.get("version")!= null){
                        version = main_data.get("version").toString();
                    }
                    String pars = new ListParms(menu_code, "DATA_IDS:" + main_data.get("id").toString()
                            +",version:"+version+",ip:"+AppUtils.app_ip+",device:PHONE"
                            + ",estatus:"+estatus).getParms();
                    final String isChexiao = ActivityController.executeForResultByThread(mContext, menu_code, "auditAll", StringUtils.strTOJsonstr(pars));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isChexiao.equals("err") && !isChexiao.equals("")) {
                                main_data.put("estatus",estatus);
                                caozuo_type = "update";
                                /**
                                 * 如果执行成功 执行界面刷新动作
                                 */
                                reshData(menu_code, Constant.method_findById,StringUtils.strTOJsonstr("{id:"+id+"}"));

                                Toast.makeText(mContext,getString(R.string.success_operation),Toast.LENGTH_SHORT).show();
                            }else{
                                caozuo_type = "";
                                Toast.makeText(mContext,isChexiao.replace("err","").replaceAll("<br>",""),Toast.LENGTH_SHORT).show();
                            }

                            if (mDialog != null){
                                WeiboDialogUtils.closeDialog(mDialog);
                            }

                            if (finalmap != null){
                                PopupWindow popupWindow = (PopupWindow) finalmap.get("popupWindow");
                                popupWindow.dismiss();
                            }

                            //如果是发布任务
                            if (view.getId() == R.id.ok){
                                btn_back.performClick();
                            }
                            /*((Activity)mContext).finish();*/
                        }
                    });
                }
            }).start();
        }
        return true;
    }

    public void backMethod() {
        reshDataUI();
    };

    public void reshDataUI() {}

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
                            backMethod();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 带流程 审核动作
     * @param context
     * @param view
     * @param estatus
     * @param msg
     * @param pars
     */
    public void auditAll(Context context, final View view, final String estatus, final String msg, final String pars){
        String approvalstatus="";
        if(id.equals("")  || id == null ){
            return;
        }
        Object res = view.getTag();
        Map<String,Object> map = null;
        if (res != null) {
            // 获取数据
            map = (Map<String, Object>) res;
        }
        current_map = map;

        //审批状态
        if (main_data !=null && main_data.get("approvalstatus")!=null){
            approvalstatus  = main_data.get("approvalstatus").toString();
        }

        if(approvalstatus !=null && !approvalstatus.equals("0") && !approvalstatus.equals("") ){
            Toast.makeText(context,"单据在流程审批中不允许此动作！",Toast.LENGTH_SHORT).show();
            return;
        }

        //版本号
        if (main_data.get("version")!= null){
            version = main_data.get("version").toString();
        }
        final String auditpars = new ListParms(menu_code, "DATA_IDS:" + main_data.get("id").toString()
                +",version:"+version+",ip:"+AppUtils.app_ip+",device:PHONE"
                + ",estatus:"+estatus).getParms();

        String parms1 =new ListParms(menu_code,"id:"+id).getParms();
        parms1 = StringUtils.strTOJsonstr(parms1);

        final String finalParms = parms1;
        final Context finalContext = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object res = ActivityController.getData4ByPost("process","getUserFlowByMenucode", finalParms);

                List<Map<String,Object>> list = null;
                if(res != null && !res.equals("")){
                    list= (List<Map<String, Object>>) res;
                }
                final List<Map<String, Object>> finalList = list;
                if (list != null && list.size()>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalList != null){
                                main_data.put("index",index);
                                MultiChoicePopWindow_CheckTask multiChoicePopWindow = new MultiChoicePopWindow_CheckTask(finalContext,main_form, finalList,new boolean[finalList.size()],main_data,menu_name+" 开始流程步骤",ztHandler);
                                multiChoicePopWindow.setService_name(menu_code);
                                multiChoicePopWindow.setMethod_name("auditAll");
                                multiChoicePopWindow.setPars(auditpars);
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
                            }else {
                                auditAll(view, estatus, msg);
                            }
                        }
                    });
                }
            }
        }).start();
    }

}
