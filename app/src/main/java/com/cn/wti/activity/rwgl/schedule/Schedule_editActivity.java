package com.cn.wti.activity.rwgl.schedule;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.adapter.MyGridAdapter_text;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.date.CustomDatePicker;
import com.cn.wti.entity.view.custom.dialog.window.AbstractChoicePopWindow_simple2;
import com.cn.wti.entity.view.custom.dialog.window.impl.ListView_PopWindow;
import com.cn.wti.entity.view.custom.dialog.window.impl.Txsj_PopWindow;
import com.cn.wti.entity.view.custom.menu.L;
import com.cn.wti.entity.view.custom.note.NoteEditText;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.entity.view.custom.textview.textwatcher.Schedule_TextWatcher;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.MyGridAdapter;
import com.cn.wti.util.app.MyGridView;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.HttpClientUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Schedule_editActivity extends BaseEdit_NoTable_Activity implements AbstractChoicePopWindow_simple2.OnItemClickListener{

    private LayoutInflater inflater = null;
    private LinearLayout chongfu1 /*下拉父窗口*/;
    private TextView title,trantime;
    private TextView select_txsj;
    private Map<String,Object> parmsMap,select_map;
    private AbstractChoicePopWindow_simple2 mPop;
    private TextView_custom txsj_tv,tqdate_tv,tqhour_tv,tqmin_tv,scheduletype_tv;

    private TextView_custom start_first_tv,start_last_tv,start_tv,end_first_tv,end_last_tv,end_tv,cyzs_tv,cyzids_tv;
    private ImageView cyzxz,chongfu2 /*下拉横线*/;
    private RelativeLayout main_form;
    private CustomDatePicker customDatePicker1, customDatePicker2;
    private TextView currentDate, currentTime;
    private MyGridView gridView;
    private String[] img_text = new String[]{};
    private MyGridAdapter_text myGridAdapter;
    private List<String> jsrids = new ArrayList<String>(),jsrnames= new ArrayList<String>(),chongfu_list,yue_list,week_list,riqi_list;
    private boolean state = false;
    private Button wancheng_btn;

    //下拉列表
    private Spinner chongfuSpinner,yueSpinner,xingqiSpinner,riqiSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_schedule_edit;
        super.onCreate(savedInstanceState);

        initData();
        createView();

    }

    public void initData(){
        menu_code = "schedule";
        menu_name="日程管理";
        mContext = Schedule_editActivity.this;

        initMap = new HashMap<String,String>();
        initMap.put("initiatorid","_zydnId");
        initMap.put("initiator_name","_zydnName");
        initMap.put("make_deptid","_bmdnId");
        initMap.put("make_dept_name","_bmdnName");
        initMap.put("trantime","trantime");
        initMap.put("createby","_loginName");

        chongfu_list = new ArrayList<String>();
        chongfu_list.add("无");
        chongfu_list.add("按天重复");
        chongfu_list.add("按周重复");
        chongfu_list.add("按月重复");
        chongfu_list.add("按年重复");

        week_list = new ArrayList<String>();
        week_list.add("星期一");
        week_list.add("星期二");
        week_list.add("星期三");
        week_list.add("星期四");
        week_list.add("星期五");
        week_list.add("星期六");
        week_list.add("星期日");

        yue_list = new ArrayList<String>();
        yue_list.add("1月");yue_list.add("2月");yue_list.add("3月");yue_list.add("4月");yue_list.add("5月");yue_list.add("6月");
        yue_list.add("7月");yue_list.add("8月");yue_list.add("9月");yue_list.add("10月");yue_list.add("11月");yue_list.add("12月");

        riqi_list = new ArrayList<String>();
        riqi_list.add("1日");riqi_list.add("2日");riqi_list.add("3日");riqi_list.add("4日");riqi_list.add("5日");
        riqi_list.add("6日");riqi_list.add("7日");riqi_list.add("8日");riqi_list.add("9日");riqi_list.add("10日");
        riqi_list.add("11日");riqi_list.add("12日");riqi_list.add("13日");riqi_list.add("14日");riqi_list.add("15日");
        riqi_list.add("16日");riqi_list.add("17日");riqi_list.add("18日");riqi_list.add("19日");riqi_list.add("20日");
        riqi_list.add("21日");riqi_list.add("22日");riqi_list.add("23日");riqi_list.add("24日");riqi_list.add("25日");
        riqi_list.add("26日");riqi_list.add("27日");riqi_list.add("28日");riqi_list.add("29日");riqi_list.add("30日");riqi_list.add("31日");

    }

    public void  createChongfuSpinner(){
        chongfuSpinner = (Spinner) findViewById(R.id.chongfu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item,chongfu_list );
        chongfuSpinner.setAdapter(adapter);

        chongfuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner=(Spinner) parent;
                String option = spinner.getItemAtPosition(position).toString();
                if(!option.equals("")){
                   //修改控件显示
                    //初始选择数据
                    if (state){
                        state = false;
                    }else{
                        yueSpinner.setSelection(0);
                        riqiSpinner.setSelection(0);
                        xingqiSpinner.setSelection(0);
                    }

                    switch (option){
                        case "无":
                            chongfu1.setVisibility(View.GONE);
                            chongfu2.setVisibility(View.GONE);
                            //初始选择数据
                            yueSpinner.setSelection(0);
                            riqiSpinner.setSelection(0);
                            xingqiSpinner.setSelection(0);

                            break;
                        case  "按天重复":
                            break;
                        case  "按周重复":
                            chongfu1.setVisibility(View.VISIBLE);
                            chongfu2.setVisibility(View.VISIBLE);
                            xingqiSpinner.setVisibility(View.VISIBLE);

                            yueSpinner.setVisibility(View.GONE);
                            riqiSpinner.setVisibility(View.GONE);
                            break;
                        case  "按月重复":
                            chongfu1.setVisibility(View.VISIBLE);
                            chongfu2.setVisibility(View.VISIBLE);
                            yueSpinner.setVisibility(View.VISIBLE);

                            xingqiSpinner.setVisibility(View.GONE);
                            riqiSpinner.setVisibility(View.GONE);
                            break;
                        case  "按年重复":
                            chongfu1.setVisibility(View.VISIBLE);
                            chongfu2.setVisibility(View.VISIBLE);
                            yueSpinner.setVisibility(View.VISIBLE);
                            riqiSpinner.setVisibility(View.VISIBLE);

                            xingqiSpinner.setVisibility(View.GONE);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    public void  createYueSpinner(){
        yueSpinner = (Spinner) findViewById(R.id.yue123);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item,yue_list );
        yueSpinner.setAdapter(adapter);

        yueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner=(Spinner) parent;
                String option = spinner.getItemAtPosition(position).toString();
                if(!option.equals("")){
                    //修改控件显示
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    public void  createWeekSpinner(){
        xingqiSpinner = (Spinner) findViewById(R.id.xiqi123);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item,week_list );
        xingqiSpinner.setAdapter(adapter);

        xingqiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner=(Spinner) parent;
                String option = spinner.getItemAtPosition(position).toString();
                if(!option.equals("")){
                    //修改控件显示
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    public void  createRiqiSpinner(){
        riqiSpinner = (Spinner) findViewById(R.id.riqi123);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item,riqi_list );
        riqiSpinner.setAdapter(adapter);

        riqiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner=(Spinner) parent;
                String option = spinner.getItemAtPosition(position).toString();
                if(!option.equals("")){
                    //修改控件显示
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

   public void createView(){

       //日期
       trantime = (TextView) findViewById(R.id.trantime);
       title = (TextView) findViewById(R.id.title_name2);
       title.setText(menu_name);

       wancheng_btn = (Button) findViewById(R.id.ok);
       wancheng_btn.setOnClickListener(this);

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

               if (parmsMap.get("qx")!= null){
                   wancheng_btn.setVisibility(View.GONE);
               }

               if (ywtype.equals("add")){
                   main_data = new HashMap<String, Object>();
                   ActivityController.initUserMainData(main_data,initMap,AppUtils.user);
               }else{
                   main_data = parmsMap ;
               }

           } catch (Exception e) {
               e.printStackTrace();
           }
       }

       txsj_tv = (TextView_custom) findViewById(R.id.txsj);
       tqdate_tv = (TextView_custom) findViewById(R.id.tqdate);
       tqhour_tv = (TextView_custom) findViewById(R.id.tqhour);
       tqmin_tv = (TextView_custom) findViewById(R.id.tqmin);
       /*scheduletype_tv = (TextView_custom) findViewById(R.id.scheduletype);*/

        //开始日期
       start_first_tv = (TextView_custom) findViewById(R.id.start_first);
       start_last_tv = (TextView_custom) findViewById(R.id.start_last);
       start_first_tv.setOnClickListener(this);
       start_last_tv.setOnClickListener(this);
       start_tv = (TextView_custom) findViewById(R.id.start);

       //结束日期
       end_first_tv = (TextView_custom) findViewById(R.id.end_first);
       end_last_tv = (TextView_custom) findViewById(R.id.end_last);
       end_first_tv.setOnClickListener(this);
       end_last_tv.setOnClickListener(this);
       end_tv = (TextView_custom) findViewById(R.id.end);

       main_form = (RelativeLayout) findViewById(R.id.main_form);
       //日程 开始日期 与 截至日期
       start_tv.addTextChangedListener(new Schedule_TextWatcher(start_first_tv,start_last_tv,main_form,chongfu_list));
       end_tv.addTextChangedListener(new Schedule_TextWatcher(end_first_tv,end_last_tv,main_form,chongfu_list));

       //参与人
       cyzs_tv = (TextView_custom) findViewById(R.id.cyzs);
       cyzids_tv = (TextView_custom) findViewById(R.id.cyzids);
       cyzs_tv.addTextChangedListener(new TextWatcher() {

           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
           }

           @Override
           public void afterTextChanged(Editable s) {
               String strs = s.toString();
               String[] xArray = null,yArray;

               if (strs.equals("")){
                   xArray = new String[]{};
               }else{
                   xArray = strs.split(",");
               }

               yArray = myGridAdapter.getImg_text();
               if (yArray != null){
                   List<String> yList = FastJsonUtils.arrayToListStr(yArray);
                   for (String xStr:xArray) {
                       if (!StringUtils.isExitVal(yArray,xStr)){
                           yList.add(xStr);
                       }
                   }

                   yArray =  FastJsonUtils.listStrToArray(yList);
               }else{
                   yArray = xArray;
               }

               myGridAdapter.setImg_text(yArray);
               myGridAdapter.notifyDataSetChanged();

               //更新IDS 与 参与人员
               String ids = cyzids_tv.getText().toString();
               if (!ids.equals("")){
                   String[] ids_array = ids.split(",");
                   for (String id:ids_array) {
                       if (!id.equals("") && !jsrids.contains(id)){
                           jsrids.add(id);
                       }
                   }
               }

               String names = cyzs_tv.getText().toString();
               if (!names.equals("")){
                   String[] names_array = names.split(",");
                   for (String name:names_array) {
                       if (!name.equals("") && !jsrnames.contains(name)){
                           jsrnames.add(name);
                       }
                   }
               }
           }
       });

       gridView = (MyGridView) findViewById(R.id.main_gridview);
       //
       myGridAdapter = new MyGridAdapter_text(mContext);

       myGridAdapter.setImg_text(img_text);
       gridView.setAdapter(myGridAdapter);

       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //删除 grid 数据
               img_text = myGridAdapter.getImg_text();
               List<String> list = FastJsonUtils.arrayToListStr(img_text);
               list.remove(list.get(position));
               img_text = FastJsonUtils.listStrToArray(list);
               myGridAdapter.setImg_text(img_text);
               myGridAdapter.notifyDataSetChanged();
               //删除 人员选择
               /*String[] cyz_array =  cyzs_tv.getText().toString().split(",");
               String[] cyzid_array = cyzids_tv.getText().toString().split(",");

               list = FastJsonUtils.arrayToListStr(cyz_array);*/
               //IDS 人员
               jsrids.remove(position);
               cyzids_tv.setText(FastJsonUtils.listStrTOStr(jsrids));

               jsrnames.remove(position);
               cyzs_tv.setText(FastJsonUtils.listStrTOStr(jsrnames));

           }
       });

       //验证权限
       if (main_data.get("createby") == null || !main_data.get("createby").toString().equals(AppUtils.app_username)){
           wancheng_btn.setVisibility(View.GONE);
       }else{
           wancheng_btn.setVisibility(View.VISIBLE);
       }

       //初始化控件
       createChongfuSpinner();
       createYueSpinner();
       createWeekSpinner();
       createRiqiSpinner();
       //控件 窗口
       chongfu1 = (LinearLayout) findViewById(R.id.chongfu1);
       chongfu2 = (ImageView) findViewById(R.id.chongfu2);
       chongfu1.setVisibility(View.GONE);
       chongfu2.setVisibility(View.GONE);
       yueSpinner.setVisibility(View.GONE);
       riqiSpinner.setVisibility(View.GONE);
       xingqiSpinner.setVisibility(View.GONE);

       reshDataUI();
       reshDataGridView();


   }

    public void reshDataGridView(){
        if (main_data.get("cyzs") != null){
            String cyzs = main_data.get("cyzs").toString();
            if (!cyzs.equals("")){
                img_text = cyzs.split(",");
            }else{
                img_text = new String[]{};
            }

            myGridAdapter.notifyDataSetChanged();
        }
    }

    public void reshDataUI() {
        //日程详情
        updateOneUI(main_data, "title");
       /* //日程类型
        main_data.put("scheduletype","工作事务");
        updateOneUI(main_data, "scheduletype");*/
        //开始日期
        updateOneUI(main_data, "start");
        //结束日期
        updateOneUI(main_data, "end");

        if (ywtype.equals("add")){
            String  dateq = DateUtil.createDate();
            start_tv.setText(dateq.substring(0,dateq.lastIndexOf(":")));
            end_tv.setText(DateUtil.getAfterMinuteDate("30"));
        }

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                start_tv.setText(time);
            }
        }, "1910-01-01 00:00", start_tv.getText().toString()); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(true); // 显示时和分
        customDatePicker1.setIsLoop(true); // 允许循环滚动


        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                end_tv.setText(time);
            }
        }, "2010-01-01 00:00", end_tv.getText().toString()); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动

        //发起人
        if (main_data.get("initiator_name2")!= null){
            updateOneUI(main_data, "initiator_name",main_data.get("initiator_name2").toString());
        }else if (main_data.get("initiator_name")!= null){
            updateOneUI(main_data, "initiator_name");
        }
        //参与者IDS
        updateOneUI(main_data, "cyzids");
        //参与者
        updateOneUI(main_data, "cyzs");
        //提前天
        updateOneUI(main_data, "tqdate");
        //提前小时
        updateOneUI(main_data, "tqhour");
        //提前分
        updateOneUI(main_data, "tqmin");
        //重复
        updateOneUI(main_data, "chongfu",chongfu_list);
        //月
        updateOneUI(main_data, "yue123",yue_list);
        //日期
        updateOneUI(main_data, "riqi123",riqi_list);
        //周
        updateOneUI(main_data, "xiqi123",week_list);

        select_map = new HashMap<String, Object>();
        Map<String,Object> map = new HashMap<String, Object>();

        //接收人员 listZydnNoExisted
        map = new LinkedHashMap<String, Object>();
        map.put("cyzids","id");
        map.put("cyzs","name");
        select_map.put("staff~参与者",map);

        cyzxz = (ImageView) findViewById(R.id.cyzxz);
        cyzxz.setOnClickListener(this);
       /* select_scheduletype = (TextView) findViewById(R.id.select_scheduletype);
        select_scheduletype.setOnClickListener(this);*/
        select_txsj = (TextView) findViewById(R.id.select_txsj);
        select_txsj.setOnClickListener(this);

        if (ywtype.equals("add")){
            tqdate_tv.setText("0");
            tqhour_tv.setText("0");
            tqmin_tv.setText("10");
        }

        String tqdate = tqdate_tv.getText().toString(),
               tqhour = tqhour_tv.getText().toString(),
               tqmin = tqmin_tv.getText().toString();

        txsj_tv.setText("提前 "+tqdate+"天 "+tqhour+"小时 "+tqmin+"分钟");

        state = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_txsj:
                showOptions(R.id.select_txsj);
                break;
            case R.id.cyzxz:
                showOptions(R.id.cyzxz);
                break;
           /* case R.id.select_scheduletype:
                showOptions(R.id.select_scheduletype);
                break;*/
            case R.id.start_first:
                showOptions(R.id.start_first);
                break;
            case R.id.start_last:
                showOptions(R.id.start_last);
                break;
            case R.id.end_first:
                showOptions(R.id.end_first);
                break;
            case R.id.end_last:
                showOptions(R.id.end_last);
                break;
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
            ywtype = "edit";
            method = "edit";
        }

        //如果 service名称 为空 直接取 菜单编号
        if (service_name == null || service_name.equals("")){
            service_name = menu_code;
        }

        for (String view_name:view_names) {
            main_data.put(view_name,getVal(view_name));
        }

        if (jsrids!= null && jsrids.size() >0){
            main_data.put("cyzids",StringUtils.listTostr(jsrids));
        }

        if (jsrnames!= null && jsrnames.size() >0){
            main_data.put("cyzs",StringUtils.listTostr(jsrnames));
        }

        if (!validate()){return "err";}

        //得到 ID
        if(main_data.get("id") != null){
            id = (String) main_data.get("id");
        }

        if (main_data!= null){
            main_data.put("status","未完成");
            main_data.put("allDay","false");
            main_data.put("username",AppUtils.app_username);
            main_data.put("staff_id",AppUtils.user.get_zydnId());

            //更新 日期 等字段值
            updateChongfu();

            String main_str = FastJsonUtils.mapToString(main_data);
            if (!main_str.equals("")){
                main_str = main_str.substring(1,main_str.length()-1);
            }

            String pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"id\":\""+id+"\",\"version\":\""+version+"\",\"userid\":\""+AppUtils.user.get_id()
                    +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"+"\","+main_str+"}";
            final String finalmethod = method,finalpars=pars;
            mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"处理中...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Object res = ActivityController.getDataByPost(service_name,finalmethod,finalpars);
                    if (res != null && !res.toString().contains("(abcdef)")){
                        final Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(resMap == null){
                                    Toast.makeText(mContext,R.string.connection_timeout,Toast.LENGTH_SHORT).show();
                                }
                                if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
                                    Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                    id = resMap.get("data").toString();
                                    //更新主ID
                                    main_data.put("id",id);
                                    Intent intent = new Intent();
                                    intent.putExtra("type",ywtype);
                                    intent.putExtra("index",index);
                                    intent.putExtras(AppUtils.setParms(ywtype,main_data));
                                    setResult(1,intent);
                                    Schedule_editActivity.this.finish();
                                }else{
                                    Toast.makeText(mContext,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
                    }else{
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
    public void showOptions(int id) {
        switch (id){
          /*  case R.id.select_scheduletype:
                List<String> list = new ArrayList<String>();
                list.add("工作事务");
                list.add("个人事务");
                final  List<String> finalList = list;
                mPop=new ListView_PopWindow(mContext,list,"日程类型");
                mPop.setmOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index  = mPop.getSelectItem();
                        scheduletype_tv.setText(finalList.get(index));
                    }
                });
                mPop.showAtLocation(findViewById(R.id.main_form), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;*/
            case R.id.cyzxz:
                String pars = new ListParms("0","0","20",service_name,"staffids:-1").getParms();
                ActivityController.showDialog_MoreSelect2(mContext,"staff~参与者","staff","findStaffNoExistedlistPage",pars,"name",main_form,view_names,select_map);
                break;
            case R.id.select_txsj:
                mPop=new Txsj_PopWindow(mContext,tqdate_tv.getText(),tqhour_tv.getText(),tqmin_tv.getText(),"提醒时间");
                mPop.setmOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String txsj = mPop.getTxsj();
                        if (!txsj.equals("")){
                            String[] txsjs =  txsj.split(",");
                            if(txsjs.length == 3){
                                if (txsjs[0].equals("") || txsjs[1].equals("") || txsjs[2].equals("")){
                                    Toast.makeText(mContext,"提醒时间不能为空",Toast.LENGTH_SHORT).show();
                                    mPop.showAtLocation(Schedule_editActivity.this.findViewById(R.id.main_form), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                                    return;
                                }
                                txsj_tv.setText("提前 "+txsjs[0]+"天 "+txsjs[1]+"小时 "+txsjs[2]+"分钟");
                                tqdate_tv.setText(txsjs[0]);
                                tqhour_tv.setText(txsjs[1]);
                                tqmin_tv.setText(txsjs[2]);
                            }else{
                                Toast.makeText(mContext,"提醒时间不能为空",Toast.LENGTH_SHORT).show();
                                mPop.showAtLocation(Schedule_editActivity.this.findViewById(R.id.main_form), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                                return;
                            }
                        }
                    }
                });
                mPop.showAtLocation(Schedule_editActivity.this.findViewById(R.id.main_form), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
        }

        if (id == R.id.start_first || id == R.id.start_last || id == R.id.end_first || id == R.id.end_last){
            if (id == R.id.start_first || id == R.id.start_last){ id = R.id.start ;}
            if (id == R.id.end_first || id == R.id.end_last){ id = R.id.end ;}
            /*ActivityController.showDialog_Datetime(mContext,findViewById(id));*/
            if (id == R.id.start){
                customDatePicker1.show(start_tv.getText().toString());
            }
            if (id == R.id.end){
                customDatePicker2.show(end_tv.getText().toString());
            }

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

    public boolean validate(){
        if (main_data.get("title") == null || main_data.get("title").toString().equals("")){
            Toast.makeText(mContext,"日程详情不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }

        /*if (main_data.get("scheduletype") == null || main_data.get("scheduletype").toString().equals("")){
            Toast.makeText(mContext,"日程类型不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }*/

        /*if (main_data.get("cyzs") == null || main_data.get("cyzs").toString().equals("")){
            Toast.makeText(mContext,"参与人员不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }*/

        return true;
    }

    private void updateChongfu(){
        if (main_data.get("chongfu")!= null) {

            String val = main_data.get("chongfu").toString();

            switch (val) {
                case "无":
                    main_data.put("yue123", 0);
                    main_data.put("riqi123", 0);
                    main_data.put("xiqi123", 0);
                    break;
                case "按天重复":
                   /* main_data.put("yue123", 0);
                    main_data.put("xiqi123", 0);*/
                    break;
                case "按周重复":
                    main_data.put("yue123", 0);
                    main_data.put("riqi123", 0);
                    break;
                case "按月重复":
                    main_data.put("riqi123", 0);
                    main_data.put("xiqi123", 0);
                    break;
                case "按年重复":
                    main_data.put("xiqi123", 0);
                    break;
            }
        }
    }

}
