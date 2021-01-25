package com.cn.wti.activity.rwgl.saledaily;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.dialog.window.AbstractChoicePopWindow_simple2;
import com.cn.wti.entity.view.custom.note.NoteEditText;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.number.IniUtils;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Salesdaily_editActivity extends BaseEdit_NoTable_Activity implements AbstractChoicePopWindow_simple2.OnItemClickListener{


    private LayoutInflater inflater = null;
    private Button ok;
    private NoteEditText editText;
    private TextView select_jobtype_name,select_khdn_name;
    private ImageView scheduletypexz,txsjxz;
    private Map<String,Object> parmsMap,select_map,mxMap;
    private AbstractChoicePopWindow_simple2 mPop;
    private RelativeLayout main_form;
    private String mxType = "",current_date;
    private EditText_custom ystj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_saledaily_edit;
        super.onCreate(savedInstanceState);

        initData();
        createView();

    }

    public void initData(){
        menu_code = "salesdaily";
        menu_name="销售日报";
        mContext = Salesdaily_editActivity.this;

        initMap = new HashMap<String,String>();
        initMap.put("make_empid","_zydnId");
        initMap.put("make_deptid","_bmdnId");
        initMap.put("make_dept_name","_bmdnName");
        initMap.put("trantime","trantime");
        initMap.put("createby","_loginName");

        select_map = new HashMap<String, Object>();
        //工作类型
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("jobtypeid","id");map.put("jobtype_name","name");
        select_map.put("dictionaryOne_16~工作类型",map);
        //客户档案
        map = new HashMap<String, Object>();
        map.put("customerid","id");map.put("customer_name","sub_name");
        select_map.put("customer~客户档案",map);
    }

   public void createView(){

       Intent intent = getIntent();
       System_one so = (System_one)intent.getSerializableExtra("parms");
       if(so == null){
           return;
       }
       parmsMap = so.getParms();

       if (parmsMap!= null){
           try {
               mxType = parmsMap.get("type").toString();
               ywtype = parmsMap.get("ywtype").toString();
               if (ywtype.equals("add")){
                   main_data = new HashMap<String, Object>();
                   current_date = parmsMap.get("current_date").toString();
                   ActivityController.initUserMainData(main_data,initMap,AppUtils.user);
                   main_data.put("salebudget",parmsMap.get("salebudget"));
                   main_data.put("achieverate",parmsMap.get("achieverate"));
                   main_data.put("saleachieve",parmsMap.get("saleachieve"));
                   mxMap = new HashMap<String, Object>();
                   main_title.setText("新增销售日报");
               }else{
                   mxMap = new HashMap<String, Object>();
                   id = parmsMap.get("id").toString();
                   if (ywtype.equals("edit") && mxType.equals("add")){
                       main_title.setText("新增日报明细");
                   }else{
                       index = parmsMap.get("index").toString();
                       mxMap.putAll(parmsMap);
                       main_title.setText("编辑日报明细");
                   }
               }

           } catch (Exception e) {
               e.printStackTrace();
           }
       }

       ok = (Button) findViewById(R.id.ok);
       ok.setOnClickListener(this);

       select_jobtype_name = (TextView) findViewById(R.id.select_jobtype_name);
       select_khdn_name = (TextView) findViewById(R.id.select_khdn_name);

       select_jobtype_name.setOnClickListener(this);
       select_khdn_name.setOnClickListener(this);
       main_form = (RelativeLayout) findViewById(R.id.main_form);

       if (mxMap.get("qx")!= null && mxMap.get("qx").toString().equals("0")){
           ok.setVisibility(View.GONE);
       }else if (mxMap.get("qx")!= null && mxMap.get("qx").toString().equals("1")){
           ok.setVisibility(View.VISIBLE);
       }

       reshDataUI();

   }

    public void reshDataUI() {
        //工作类型
        updateOneUI(mxMap, "jobtypeid");
        updateOneUI(mxMap, "jobtype_name");
        //关联客户
        updateOneUI(mxMap, "customerid");
        updateOneUI(mxMap, "customer_name");
        //关联人员
        updateOneUI(mxMap, "glry");
        //工作总结
        if (mxMap.get("gzzj") != null){
            mxMap.put("gzzj",StringUtils.replaceSpecialCharterBack(mxMap.get("gzzj").toString()));
        }
        updateOneUI(mxMap, "gzzj");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_jobtype_name:
                showOptions(R.id.select_jobtype_name);
                break;
            case R.id.select_khdn_name:
                showOptions(R.id.select_khdn_name);
                break;
            default:
                break;
        }
        super.onClick(v);
    }


    public boolean validate(){
        if (mxMap.get("jobtype_name") == null || mxMap.get("jobtype_name").toString().equals("")){
            Toast.makeText(mContext,"工作类型不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mxMap.get("gzzj") == null || mxMap.get("gzzj").toString().equals("")){
            Toast.makeText(mContext,"工作总结不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public String saveOrEdit() {
        String method = "";

        //如果 service名称 为空 直接取 菜单编号
        if (service_name == null || service_name.equals("")){
            service_name = menu_code;
        }

        for (String view_name:view_names) {
            mxMap.put(view_name,getVal(view_name));
        }


        if (!validate()){return "";}

        String main_str = "";

        //新增所有 还是 新增单条明细
        if (ywtype.equals("add") && mxType.equals("add")){
            mxMap.put("salesdailyid",id);
            mxMap.put("rowid", IniUtils.getFixLenthString(5));
            mxMap.put("salesdailyDetailid", "");
            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
            list.add(mxMap);
            main_data.put("dailydate",current_date);
            main_data.put("salesdailyDetail",list);
            method = "save";
            main_str = FastJsonUtils.mapToString(main_data);
        }else if(ywtype.equals("edit") && mxType.equals("add")){
            method = "saveSalesDailyDetailForMobile";
            mxMap.put("salesdailyid",id);
            mxMap.put("rowid", IniUtils.getFixLenthString(5));
            mxMap.put("salesdailyDetailid", "");
            main_str = FastJsonUtils.mapToString(mxMap);
        }else if (ywtype.equals("edit") && mxType.equals("edit")){
            method = "editSalesDailyDetailForMobile";
            main_str = FastJsonUtils.mapToString(mxMap);
        }

        if (!main_str.equals("")){
            main_str = main_str.substring(1,main_str.length()-1);
        }

        final String pars = "{\"username\":\""+ AppUtils.app_username+"\","+main_str+"}";

        mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"处理中...");
        final String finalMethod = method;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = ActivityController.executeForResult(mContext,service_name, finalMethod,pars);
                if (res != null && !res.equals("err")){
                    id = res;
                    Intent intent = new Intent();
                    intent.putExtra("id",id);
                    intent.putExtra("method", finalMethod);
                    if (finalMethod.equals("saveSalesDailyDetailForMobile")){
                        mxMap.put("salesdailyDetailid",id);
                        intent.putExtras(AppUtils.setParms("add",mxMap));
                    }else if (finalMethod.equals("editSalesDailyDetailForMobile")){
                        mxMap.put("index",index);
                        intent.putExtras(AppUtils.setParms("add",mxMap));
                    }
                    setResult(1,intent);
                    ((Activity)mContext).finish();
                }
            }
        }).start();

        return  id;
    }

    @Override
    public void showOptions(int id) {
        switch (id){
            case R.id.select_jobtype_name:
                ActivityController.showDialog_UI(mContext,"dictionaryOne_16~工作类型","name",select_map,"dictionaryOne","list","type:16",get_catch(),main_form,view_names);
                break;
            case R.id.select_khdn_name:
                ActivityController.showDialog_UI(mContext,"customer~客户档案","sub_name~staff_name",select_map,"customer","list","",get_catch(),main_form,view_names);
                break;
        }
    }

    @Override
    public void setOnItemClick(View v) {
        switch (v.getId()){
            case R.id.btnOK:
                Toast.makeText(mContext,String.valueOf(mPop.getSelectItem()),Toast.LENGTH_SHORT).show();
                mPop.dismiss();
                break;
        }
    }

}
