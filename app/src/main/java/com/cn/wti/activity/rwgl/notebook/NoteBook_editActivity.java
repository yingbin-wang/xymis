package com.cn.wti.activity.rwgl.notebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.custom.note.NoteEditText;
import com.cn.wti.util.Constant;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;
import java.util.HashMap;
import java.util.Map;

public class NoteBook_editActivity extends BaseEdit_NoTable_Activity{


    private LayoutInflater inflater = null;
    private Button title_back2,title_ok2;
    private NoteEditText editText;
    private TextView trantime,title;
    private ImageView xinzeng,shanchu;
    private Map<String,Object> parmsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_notebook;
        super.onCreate(savedInstanceState);
        initData();
        createView();

    }

    public void initData(){
        menu_code = "notebook";
        menu_name="记事本";
        mContext = NoteBook_editActivity.this;

        initMap = new HashMap<String,String>();
        initMap.put("initiatorid","_zydnId");
        initMap.put("fqr_name","_zydnName");
        initMap.put("make_dept_id","_bmdnId");
        initMap.put("make_dept_name","_bmdnName");
        initMap.put("trantime","trantime");
        initMap.put("createby","_loginName");
    }

   public void createView(){
       editText = (NoteEditText) findViewById(R.id.content);
       editText.setScrollView((ScrollView)findViewById(R.id.scrollview));
       editText.setTEXT_SIZE(12);

       title_back2 = (Button) findViewById(R.id.title_back2);
       title_ok2 = (Button) findViewById(R.id.title_ok2);

       title_back2.setOnClickListener(this);
       title_ok2.setOnClickListener(this);
       //日期
       trantime = (TextView) findViewById(R.id.trantime);
       title = (TextView) findViewById(R.id.title_name2);
       title.setText("记事本");
       /*//新增 删除
       xinzeng = (ImageView) findViewById(R.id.xinzeng);
       shanchu = (ImageView) findViewById(R.id.shanchu);*/

       /*if (xinzeng != null){
           xinzeng.setOnClickListener(this);
       }

       if (shanchu != null){
           shanchu.setOnClickListener(this);
       }*/

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

       reshDataUI();

   }

    public void reshDataUI() {
        //记事本内容
        updateOneUI(main_data, "content");
        //记事本日期
        updateOneUI(main_data, "trantime");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           /* case R.id.xinzeng:
                break;
            case R.id.shanchu:
                deleteOne();
                break;*/
            default:
                break;
        }
        super.onClick(v);
    }

    @Override
    public String saveOrEdit() {
        String method = "";
        if (ywtype != null && ywtype.equals("add")){
            method = "save";
        }else{
            method = "edit";
        }

        //如果 service名称 为空 直接取 菜单编号
        if (service_name == null || service_name.equals("")){
            service_name = menu_code;
        }

        for (String view_name:view_names) {
            main_data.put(view_name,getVal(view_name));
        }

        //版本号
        if (main_data.get("version")!= null){
            version = main_data.get("version").toString();
        }

        //得到 ID
        if(main_data.get("id") != null){
            id = (String) main_data.get("id");
        }

        if (main_data!= null){

            String main_str = FastJsonUtils.mapToString(main_data);
            if (!main_str.equals("")){
                main_str = main_str.substring(1,main_str.length()-1);
            }

            String pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"id\":\""+id+"\",\"isversion\":\""+1+"\",\"userid\":\""+AppUtils.user.get_id()
                    +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"+"\","+main_str+"}";
            final String finalmethod = method,finalpars = pars;
            mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"处理中...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Object res = ActivityController.getDataByPost(service_name,finalmethod,finalpars);
                    if (res != null && !res.toString().contains("(abcdef)")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
                                if(resMap == null){
                                    Toast.makeText(mContext,R.string.connection_timeout,Toast.LENGTH_SHORT).show();
                                    WeiboDialogUtils.closeDialog(mDialog);
                                    return;
                                }
                                if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                                    Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                    if (resMap.get("data") instanceof JSONObject){
                                        JSONObject dataMap = (JSONObject) resMap.get("data");
                                        id = dataMap.get("id").toString();
                                    }else{
                                        id = resMap.get("data").toString();
                                    }

                                    /**
                                     * 刷新当前数据
                                     */
                                    reshData(menu_code, Constant.method_findById, StringUtils.strTOJsonstr("{id:"+id+"}"));

                                }else{
                                    Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res != null){
                                    Toast.makeText(mContext, HttpClientUtils.backMessage(ActivityController.getPostState(res.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(mContext,mContext.getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    WeiboDialogUtils.closeDialog(mDialog);
                }
            }).start();

        }
        return  id;
    }

    @Override
    public void deleteOne() {
        if (!id.equals("")){
            //如果 service名称 为空 直接取 菜单编号
            if (service_name == null || service_name.equals("")){
                service_name = menu_code;
            }
            //版本号
            if (main_data.get("version")!= null){
                version = main_data.get("version").toString();
            }
            String pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"DATA_IDS\":\""+id+"\",\"isversion\":\""+1+"\",\"userid\":\""+AppUtils.user.get_id()
                    +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"+"\"}";
            final  String  finalpars = pars;
            mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"删除中...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Object res = ActivityController.getDataByPost(service_name,"deleteAll",finalpars);
                    if (res != null){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
                                if(resMap == null){
                                    Toast.makeText(mContext,R.string.connection_timeout,Toast.LENGTH_SHORT).show();
                                    WeiboDialogUtils.closeDialog(mDialog);
                                    return;
                                }
                                if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                                    id = resMap.get("data").toString();
                                    Intent intent = new Intent();
                                    intent.putExtra("index",index);
                                    intent.putExtra("type","delete");
                                    setResult(1,intent);
                                    ((Activity)mContext).finish();
                                }else{
                                    Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                }

                                WeiboDialogUtils.closeDialog(mDialog);
                            }
                        });

                    }
                }
            }).start();

        }
    }

    @Override
    public void backMethod() {
        //更新主ID
        main_data.put("id",id);
        Intent intent = new Intent();
        intent.putExtra("index",index);
        intent.putExtra("type",ywtype);
        intent.putExtras(AppUtils.setParms("add",main_data));
        setResult(2,intent);
        ((Activity)mContext).finish();
    }
}
