package com.cn.wti.activity.rwgl.myassignment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.date.CustomDatePicker;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.IDHelper;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.cn.wti.util.other.DateUtil;
import com.dina.ui.widget.UITableView;
import com.wticn.wyb.wtiapp.R;
import java.util.HashMap;
import java.util.Map;

public class MyAssignment_editActivity extends BaseEdit_NoTable_Activity{

    /*private Map<String,String> showMap;*/
    private String menu_code,menu_name,pars,id,ywtype,tabpostion;
    private String[] contents;
    private Map<String,Object> gs_map,res_map,parmsMap,main_data;
    private Map<String,String> initMap;
    private Context mContext;
    private UITableView tableView;

    private TextView_custom make_emp_name,trantime,tasktype,completetime;
    private TextView select_completetime;
    private Button ok_btn;

    Object res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_myassignment_edit;
        super.onCreate(savedInstanceState);
        initData();
        createView();
    }

    public void initData() {
        menu_code = "mytaska";
        menu_name="我的任务";

        initMap = new HashMap<String,String>();
        initMap.put("make_empid","_zydnId");
        initMap.put("make_emp_name","_zydnName");
        initMap.put("make_deptid","_bmdnId");
        initMap.put("make_dept_name","_bmdnName");
        initMap.put("trantime","trantime");
        initMap.put("createby","_loginName");
    }

    public String setParms(){
        return  "";
    }

    public  void createView(){

        mContext = MyAssignment_editActivity.this;
        main_title.setText("任务详情");

        ok_btn = (Button) findViewById(R.id.ok);
        ok_btn.setText("完成");
        ok_btn.setOnClickListener(this);

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
                if (parmsMap.get("index") != null){
                    index = String.valueOf(parmsMap.get("index"));
                }
                tabpostion = parmsMap.get("table_postion").toString();

                if (parmsMap.get("mainData") instanceof JSONObject || parmsMap.get("mainData") instanceof Map){
                    main_data = (Map<String, Object>) parmsMap.get("mainData");
                }else{
                    main_data = FastJsonUtils.strToMap(parmsMap.get("mainData").toString()) ;
                }
                reshDataUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void reshDataUI(){
        //发启人
        updateOneUI(main_data,R.id.make_emp_name,"make_emp_name");
        //业务日期
        updateOneUI(main_data,R.id.trantime,"trantime");
        //完成时间
        completetime = (TextView_custom) findViewById(R.id.completetime);
        updateOneUI(main_data,R.id.completetime,"completetime");

        if (main_data.get("completetime") == null || main_data.get("completetime").equals("")){
            //时间选择
            select_completetime = (TextView) findViewById(R.id.select_completetime);
            select_completetime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dateq = "";
                    if(completetime.getText().toString().equals("")){
                        dateq = DateUtil.createDate();
                    }else{
                        dateq =completetime.getText().toString();
                    }

                    CustomDatePicker customDatePicker1 = new CustomDatePicker(mContext, new CustomDatePicker.ResultHandler() {
                        @Override
                        public void handle(String time) { // 回调接口，获得选中的时间
                            completetime.setText(time);
                        }
                    }, "1910-01-01 00:00", dateq); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                    customDatePicker1.showSpecificTime(true); // 显示时和分
                    customDatePicker1.setIsLoop(true); // 允许循环滚动
                    customDatePicker1.show(dateq);
                }
            });

        }/*else{
            ok_btn.setVisibility(View.GONE);
        }*/

        //任务类型
        updateOneUI(main_data,R.id.tasktype,"tasktype");
        updateOneUI(main_data,R.id.taskname,"taskname");
        updateOneUI(main_data,R.id.taskdetails,"taskdetails");
        updateOneUI(main_data,R.id.completeschedule,"completeschedule");
    }

    public String updateOneUI(Map<String,Object> map,int id,String key){
        View view = findViewById(id);
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
        }

        //添加 到views 中
        if (!key.equals("")){
            view_names.add(key);
        }
        return  val;
    }

    /**
     * 保存和编辑方法
     */

    @Override
    public String saveOrEdit() {
        ywtype = "edit";
        if (ywtype.equals("edit")){
            for (String view_name:view_names) {
                main_data.put(view_name,getVal(view_name));
            }

            //得到 ID
            if(main_data.get("id") != null){
                id = (String) main_data.get("id");
            }

            if (main_data!= null){
                main_data.put("projectopportunityDetailid","0");
                main_data.put("projectcontracttaskDetailid","0");
                main_data.put("completion",main_data.get("completeschedule"));

                if (main_data.get("completetime")== null || main_data.get("completetime").toString().equals("")){
                    Toast.makeText(mContext,getString(R.string.err_myassignment_completetime),Toast.LENGTH_SHORT).show();
                    return "";
                }

                if (main_data.get("completeschedule")== null || main_data.get("completeschedule").toString().equals("")){
                    Toast.makeText(mContext,getString(R.string.err_myassignment_completeschedule),Toast.LENGTH_SHORT).show();
                    return "";
                }

                //版本号
                if (main_data.get("version")!= null){
                    version = main_data.get("version").toString();
                }
                String main_str = FastJsonUtils.mapToString(main_data);
                if (!main_str.equals("")){
                    main_str = main_str.substring(1,main_str.length()-1);
                }

                String pars = "{\"id\":\""+id+"\",\"version\":\""+version+"\",\"userid\":\""+AppUtils.user.get_id()
                        +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"+"\",\"staffid\":\""+AppUtils.user.get_zydnId()
                        +"\",\"username\":\""+ AppUtils.app_username +"\",\"staff_name\":\""+ AppUtils.user.get_zydnName()+"\",\"makeempid\":\""+ AppUtils.user.get_zydnId()
                        +"\","+main_str+"}";

                exectueThreadreturnObj(mContext,menu_code,"edit",pars,"save",null);

            }
        }
        return  id;
    }

    @Override
    public String execute_BackMethod(Object res,String type,Map<String,Object> map) {
        final Object finalRes = res;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalRes != null && !finalRes.toString().contains("(abcdef)")){
                    Map<String,Object> resMap = FastJsonUtils.strToMap(finalRes.toString());
                    if(resMap == null){
                        Toast.makeText(mContext,R.string.connection_timeout,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                        Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                        id = resMap.get("data").toString();
                        //更新主ID
                        main_data.put("id",id);
                        Intent intent = new Intent();
                        intent.putExtra("index",index);
                        if (main_data.get("completetime")== null || main_data.get("completetime").toString().equals("")){
                            intent.putExtra("type","");
                        }else{
                            intent.putExtra("type","update");
                        }
                        intent.putExtra("tabpostion",tabpostion);
                        intent.putExtra("menucode",menu_code);
                        intent.putExtras(AppUtils.setParms("update",main_data));
                        setResult(1,intent);
                        ((Activity)mContext).finish();
                    }else{
                        Toast.makeText(mContext,resMap.get("msg") == null? "操作失败" : resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (finalRes != null){
                        Toast.makeText(mContext, HttpClientUtils.backMessage(ActivityController.getPostState(finalRes.toString())).replace("(abcdef)",""),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext,mContext.getString(R.string.err_data),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return "";
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
        }
        return val;
    }

}
