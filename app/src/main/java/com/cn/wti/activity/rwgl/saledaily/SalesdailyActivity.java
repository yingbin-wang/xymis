package com.cn.wti.activity.rwgl.saledaily;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.dialog.DatePickDialogUtil;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.RecyclerViewUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.number.IniUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesdailyActivity extends BaseEdit_NoTable_Activity{


    private ImageButton xinzengxiangqing;

    private Map<String,Object> parmsMap,main_data = new HashMap<String, Object>(),select_map,map;
    private MaterialCalendarView mcv;
    private List<Map<String,Object>> salesdailyDetail = new ArrayList<Map<String,Object>>(),salesdailytalkDetail= new ArrayList<Map<String,Object>>();
    private String pars,type="",current_date="",current_user="";
    private RecyclerView mRecyclerView1,mRecyclerView2;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter1 mAdapter1;
    private MyAdapter2 mAdapter2;
    private ScrollView scrollView;
    private LinearLayout rqxz;
    private TextView_custom year_month_tv,day_pv,ysz_pv,baifenbi_tv;
    private ImageView tijiao,reshData;
    RelativeLayout main_form;
    private ProgressBar progressBar;
    private EditText_custom answer_one,question;
    private LinearLayout huifu_layout1,question_linear;
    private ImageButton ryxz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_saledaily;
        super.onCreate(savedInstanceState);

        //加载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isInit = initData();
                if (isInit){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createView();
                        }
                    });

                }
            }
        }).start();

    }

    public boolean initData(){
        menu_code = "salesdaily";
        menu_name="销售日报";
        mContext = SalesdailyActivity.this;

        current_user = AppUtils.user.get_zydnId();
        //点进去 去判断当天是否有日报:如果有日报既是编辑，如果没日报既是新增 后去某一天的日报接口
        current_date = DateUtil.getDay();
        pars = new ListParms("0","0","20",menu_code,"dailydate:"+current_date+",lx:1,isList:1,make_empid:"+AppUtils.user.get_zydnId()+",zydnid:"+AppUtils.user.get_zydnId()).getParms();
        Object res = ActivityController.getData2ByPost(mContext,menu_code,"findMobileListAll", StringUtils.strTOJsonstr(pars));
        if (res != null && res instanceof JSONArray){
            JSONArray res_array = (JSONArray) res;
            if (res_array.size() >0){

                main_data = (Map<String, Object>) res_array.get(0);
                salesdailyDetail.clear();
                salesdailytalkDetail.clear();
                if ( main_data.get("salesdailyDetail") != null){
                    ywtype = "edit";
                    if (main_data.get("salesdailyDetail") instanceof String){
                        salesdailyDetail.addAll (FastJsonUtils.strToListMap(main_data.get("salesdailyDetail").toString()) );
                    }else{
                        salesdailyDetail.addAll ((List<Map<String, Object>>) main_data.get("salesdailyDetail"));
                    }
                }else{
                    ywtype = "add";
                }
                if ( main_data.get("salesdailytalkDetail") != null){
                    if (main_data.get("salesdailytalkDetail") instanceof String){
                        salesdailytalkDetail.addAll(FastJsonUtils.strToListMap( main_data.get("salesdailytalkDetail").toString()));
                    }else{
                        salesdailytalkDetail.addAll((List<Map<String, Object>>) main_data.get("salesdailytalkDetail"));
                    }
                }

            }else{
                ywtype = "add";
                salesdailyDetail.clear();
                salesdailytalkDetail.clear();
            }
            return  true;

        }else{
            return false;
        }
    }

   public void createView(){

       main_form = (RelativeLayout) findViewById(R.id.main_form);
       main_title.setText(menu_name);
       //新增详情
       xinzengxiangqing = (ImageButton) findViewById(R.id.xinzengxiangqing);
       xinzengxiangqing.setOnClickListener(this);

       //进度条 - 预算值
       progressBar = (ProgressBar) findViewById(R.id.jindutiao);
       ysz_pv = (TextView_custom) findViewById(R.id.ysz);
       baifenbi_tv = (TextView_custom) findViewById(R.id.baifenbi);

       //年月 点击 事件
       year_month_tv = (TextView_custom) findViewById(R.id.yearAndmonth_pv);
       day_pv = (TextView_custom) findViewById(R.id.day_pv);

       tijiao = (ImageView) findViewById(R.id.tijiao);
       tijiao.setOnClickListener(this);

       ryxz = (ImageButton) findViewById(R.id.ryxz);
       ryxz.setOnClickListener(this);
       //设置刷新数据按钮
       reshData  = (ImageView) findViewById(R.id.reshData);
       reshData.setOnClickListener(this);

       scrollView = (ScrollView) findViewById(R.id.scrollView);

       //问题明细
       mRecyclerView1 = (RecyclerView)findViewById(R.id.question_recyclerView);
       RecyclerViewUtils.setLayoutManagerHeight(mContext,mRecyclerView1,2);
       //问题
       question = (EditText_custom) findViewById(R.id.question);

       //创建并设置Adapter
       mAdapter1 = new MyAdapter1(salesdailyDetail);
       mRecyclerView1.setAdapter(mAdapter1);
       ActivityController.setViewOnTouch(mRecyclerView1,scrollView);
       //回答明细
       mRecyclerView2 = (RecyclerView)findViewById(R.id.answer_recycleView);
       mLayoutManager = new LinearLayoutManager(this);
       mRecyclerView2.setLayoutManager(mLayoutManager);
       //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
       mRecyclerView2.setHasFixedSize(true);
       RecyclerViewUtils.setLayoutManagerHeight(mContext,mRecyclerView2,2);
       //创建并设置Adapter
       mAdapter2 = new MyAdapter2(salesdailytalkDetail);
       mRecyclerView2.setAdapter(mAdapter2);
       ActivityController.setViewOnTouch(mRecyclerView2,scrollView);
       //日期选择
       rqxz = (LinearLayout) findViewById(R.id.rqxz);
       rqxz.setOnClickListener(this);

       if (!current_date.equals("")){
           if (!current_date.equals("")){
               Date date = DateUtil.fomatDate(current_date);
               String year = String.valueOf(1900+date.getYear());
               String month = date.getMonth()+1 <10? "0"+(date.getMonth()+1):""+(date.getMonth()+1);
               String day = date.getDate()<10? "0"+date.getDate():""+date.getDate();
               year_month_tv.setText(year+"年"+month+"月");
               day_pv.setText(day);
           }
       }

       answer_one = (EditText_custom) findViewById(R.id.answer_one);
       if (answer_one != null){
           answer_one.setOnEditorActionListener(mEditTextClickListener);
       }

       huifu_layout1 = (LinearLayout) findViewById(R.id.huifu_layout1);
       question_linear = (LinearLayout) findViewById(R.id.question_linear);

       if (ywtype.equals("add")){
           mRecyclerView1.setVisibility(View.GONE);
           mRecyclerView2.setVisibility(View.GONE);
           huifu_layout1.setVisibility(View.GONE);
           question_linear.setVisibility(View.GONE);
       }

       reshDataUI();
   }

    public void reshDataUI() {
        //更新日报问题
        if (main_data.get("question") != null){
            main_data.put("question", StringUtils.replaceSpecialCharterBack(main_data.get("question").toString()));
        }
        updateOneUI(main_data, "question");
        //更新完成率
        double salebudget =0,copysalebudget = 0 ;
        double saleachieve = 0 ,wcl= 0;
        if (main_data.size() !=0){
            if (main_data.get("salebudget") != null){
                salebudget = Double.parseDouble(main_data.get("salebudget").toString()) ;
            }else{
                salebudget = 0;
            }

            if (main_data.get("saleachieve") != null){
                saleachieve = Double.parseDouble(main_data.get("saleachieve").toString()) ;
            }else{
                saleachieve = 0;
            }

            if (salebudget == 0){
                copysalebudget = 1;
            }else{
                copysalebudget = salebudget;
            }
            wcl = saleachieve/copysalebudget;
            ysz_pv.setText("本月完成/预算("+saleachieve+"/"+salebudget+")");
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(String.format("%.0f", wcl*100)));
            baifenbi_tv.setText(String.format("%.0f", wcl*100)+"%");
        }else{
            ysz_pv.setText("本月完成/预算("+saleachieve+"/"+salebudget+")");
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(String.valueOf(wcl*100 ).substring(0,String.valueOf(wcl).indexOf("."))));
        }


        if (current_user.equals(AppUtils.user.get_zydnId())){
            xinzengxiangqing.setEnabled(true);
            tijiao.setEnabled(true);
            question.setEnabled(true);
        }else{
            xinzengxiangqing.setEnabled(false);
            tijiao.setEnabled(false);
            question.setEnabled(false);
        }

        //刷新视图
        reshView();
    }

    public void  reshView(){
        if(mAdapter1.getItemCount()>0){
            mRecyclerView1.setVisibility(View.VISIBLE);
            question_linear.setVisibility(View.VISIBLE);
            mRecyclerView2.setVisibility(View.VISIBLE);
            huifu_layout1.setVisibility(View.VISIBLE);
        }else {
            question_linear.setVisibility(View.GONE);
            mRecyclerView2.setVisibility(View.GONE);
            huifu_layout1.setVisibility(View.GONE);
        }
    }

    public void reshWcl(){
        //更新完成率
        double salebudget =0 ;
        double saleachieve = 0 ,wcl= 0;
        if (main_data.size() !=0){
            if (main_data.get("salebudget") != null){
                salebudget = Double.parseDouble(main_data.get("salebudget").toString()) ;
            }else{
                salebudget = 0;
            }

            if (main_data.get("saleachieve") != null){
                saleachieve = Double.parseDouble(main_data.get("saleachieve").toString()) ;
            }else{
                saleachieve = 0;
            }

            if (salebudget == 0){
                salebudget = 1;
            }
            wcl = saleachieve/salebudget;
            ysz_pv.setText("本月完成/预算("+saleachieve+"/"+salebudget+")");
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(String.format("%.0f", wcl*100)));
            baifenbi_tv.setText(String.format("%.0f", wcl*100)+"%");
        }else{
            ysz_pv.setText("本月完成/预算("+saleachieve+"/"+salebudget+")");
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(String.valueOf(wcl*100 ).substring(0,String.valueOf(wcl).indexOf("."))));
        }
    }

    @Override
    public void onClick(View v) {
        Object res;
        switch (v.getId()){
            case R.id.xinzengxiangqing:
                Intent intent = new Intent();
                intent.setClass(mContext,Salesdaily_editActivity.class);
                main_data.put("type","add");
                main_data.put("ywtype",ywtype);
                main_data.put("current_date",current_date);
                intent.putExtras(AppUtils.setParms("add",main_data));
                startActivityForResult(intent,1);
                break;
            case R.id.tijiao:

                /*隐藏软键盘*/
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                for (String view_name:view_names) {
                    main_data.put(view_name,getVal(view_name));
                }
                String main_str = FastJsonUtils.mapToStringByfor(main_data);
                if (!main_str.equals("")){
                    main_str = main_str.substring(1,main_str.length()-1);
                }

                pars = "{\"userId\":\""+ AppUtils.app_username+"\","+main_str+"}";

                //执行提交动作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isEdit = ActivityController.execute(mContext,menu_code,"edit",pars);
                        if(isEdit){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    id = main_data.get("id").toString();
                                    if (main_data.get("version")!= null){
                                        main_data.put("version",Integer.parseInt(main_data.get("version").toString())+1);
                                    }
                                    reshView();
                                }
                            });
                        }
                    }
                }).start();

                break;
            case R.id.rqxz:
                final DatePickDialogUtil dialogUtil = new DatePickDialogUtil((Activity) mContext,current_date);
                dialogUtil.setSelectedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                        Calendar calendar = date.getCalendar();
                        String year = String.valueOf(calendar.get(Calendar.YEAR));
                        String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
                        String day = String.valueOf(calendar.get(Calendar.DATE));
                        if (Integer.parseInt(month)<10){
                            month = "0"+month;
                        }
                        if (Integer.parseInt(day) <10){
                            day = "0"+day;
                        }
                        current_date = year+"-"+month+"-"+day;
                        if (!current_date.equals("")){
                            year_month_tv.setText(year+"年"+month+"月");
                            day_pv.setText(day);
                        }

                        if (!current_date.equals("") && !current_user.equals("")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if(resshData(current_user)){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter1.notifyDataSetChanged();
                                                mAdapter2.notifyDataSetChanged();
                                                reshDataUI();
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }

                        if (current_user.equals(AppUtils.user.get_zydnId())){
                            xinzengxiangqing.setEnabled(true);
                            tijiao.setEnabled(true);
                        }else{
                            xinzengxiangqing.setEnabled(false);
                            tijiao.setEnabled(false);
                        }

                        dialogUtil.close();
                    }
                });

               dialogUtil.dateTimePicKDialog();

                break;

            case R.id.ryxz:
                pars = new ListParms(menu_code,"username:"+AppUtils.app_username).getParms();
                ActivityController.showDialog_Val(mContext,"mobilerole~人员选择","name",new String[]{"login_name","name"},"mobilerole","findStaffsSalesdailyByUserId",pars,get_catch(),main_form,reshData,false);
                break;
            case R.id.reshData:
                Map<String,Object> selectMap = (Map<String, Object>) v.getTag();
                if (selectMap != null){
                    current_user = selectMap.get("id").toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isOk = resshData(current_user);
                            if (isOk){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter1.notifyDataSetChanged();
                                        mAdapter2.notifyDataSetChanged();
                                        reshDataUI();
                                    }
                                });
                            }
                        }
                    }).start();

                }

                break;
            default:
                break;
        }
        super.onClick(v);
    }

    @Override
    public Object execute_BackMethod(Object res, String type, Map<String, Object> map) {
        String res1 = super.execute_BackMethod(res, type, map).toString();
        final Map<String,Object> finalresMap = FastJsonUtils.strToMap(res1);
        final String finaltype = type;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (finaltype){
                    case "add":
                        ywtype = "add";
                        main_data.clear();
                        salesdailyDetail.clear();
                        salesdailytalkDetail.clear();
                        mAdapter1.notifyDataSetChanged();
                        mAdapter2.notifyDataSetChanged();
                        if(finalresMap != null){
                            main_data.put("salebudget",finalresMap.get("salebudget"));
                            main_data.put("saleachieve",finalresMap.get("saleachieve"));
                        }

                        reshDataUI();
                        break;
                }
            }
        });


        return "";
    }

    public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.ViewHolder> {
        public List<Map<String,Object>> datas = null;
        Map<String,Object> data_map = null;
        public MyAdapter1(List<Map<String,Object>> datas) {
            this.datas = datas;
        }

        public List<Map<String,Object>> getDatas(){
            return datas;
        }

        public void reshData(List<Map<String,Object>> datas){
            this.datas = datas;
            notifyDataSetChanged();
        }

        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_saledaily01,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            data_map = datas.get(position);
            viewHolder.jobtype_name.setText(FastJsonUtils.getVal(data_map,"jobtype_name"));
            viewHolder.khdn_name.setText(FastJsonUtils.getVal(data_map,"customer_name"));
            viewHolder.glry.setText(FastJsonUtils.getVal(data_map,"glry"));
            viewHolder.gzzj.setText(FastJsonUtils.getVal(data_map,"gzzj"));
            viewHolder.question_delete.setTag(position);
            viewHolder.question_delete.setOnClickListener(viewHolder);
            viewHolder.itemContainer.setTag(position);
            viewHolder.itemContainer.setOnClickListener(viewHolder);
        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }
        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView_custom jobtype_name, khdn_name, glry, gzzj;
            public ImageView question_delete;
            public LinearLayout itemContainer;

            public ViewHolder(View view) {
                super(view);
                jobtype_name = (TextView_custom) view.findViewById(R.id.jobtype_name);
                khdn_name = (TextView_custom) view.findViewById(R.id.khdn_name);
                glry = (TextView_custom) view.findViewById(R.id.glry);
                gzzj = (TextView_custom) view.findViewById(R.id.gzzj);
                question_delete = (ImageView) view.findViewById(R.id.question_delete);
                itemContainer = (LinearLayout) view;
            }

            @Override
            public void onClick(View v) {
                int index = 0;
                String main_str = "";
                switch (v.getId()) {
                    case R.id.question_delete:
                        if (current_user.equals(AppUtils.user.get_zydnId())){
                            if (getItemCount()== 1){
                                Toast.makeText(mContext,getString(R.string.err_oneData),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            index = (int) v.getTag();
                            data_map = datas.get(index);
                            data_map.put("id",data_map.get("salesdailyDetailid"));
                            main_str = FastJsonUtils.mapToString(data_map);
                            if (!main_str.equals("")) {
                                main_str = main_str.substring(1, main_str.length() - 1);
                            }
                            pars = "{\"userId\":\"" + AppUtils.app_username + "\"," + main_str + "}";

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean isDelete = ActivityController.execute(mContext, menu_code, "deleteSalesDailyDetailForMobile", pars);
                                    if (isDelete) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                datas.remove(data_map);
                                                mAdapter1.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }).start();

                        }

                        break;
                    case R.id.itemContainer:
                        index = (int) v.getTag();
                        Intent intent = new Intent();
                        intent.setClass(mContext,Salesdaily_editActivity.class);
                        data_map = datas.get(index);
                        data_map.put("type","edit");
                        data_map.put("ywtype","edit");
                        data_map.put("index",index);
                        if (current_user.equals(AppUtils.user.get_zydnId())){
                            data_map.put("qx",1);
                        }else{
                            data_map.put("qx",0);
                        }
                        data_map.put("id",data_map.get("salesdailyDetailid"));
                        intent.putExtras(AppUtils.setParms("edit",data_map));
                        startActivityForResult(intent,1);
                        break;
                }
            }
        }
    }

    public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.ViewHolder> {
        public List<Map<String,Object>> datas = null;
        Map<String,Object> data_map = null;

        public List<Map<String,Object>> getDatas(){
            return datas;
        }

        public void reshData(List<Map<String,Object>> datas){
            this.datas = datas;
            notifyDataSetChanged();
        }

        public MyAdapter2(List<Map<String,Object>> datas) {
            this.datas = datas;
        }
        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_saledaily02,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            data_map = datas.get(position);
            viewHolder.answer_name.setText(FastJsonUtils.getVal(data_map,"answer_name"));
            viewHolder.answer.setText(FastJsonUtils.getVal(data_map,"answer"));
            viewHolder.answerdate.setText(FastJsonUtils.getVal(data_map,"answerdate"));
            viewHolder.answer_delete.setTag(position);
        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView_custom answer_name,answer,answerdate;
            public ImageView answer_delete;
            public ViewHolder(View view){
                super(view);
                answer_name = (TextView_custom) view.findViewById(R.id.answer_name);
                answer = (TextView_custom) view.findViewById(R.id.answer);
                answerdate = (TextView_custom) view.findViewById(R.id.answerdate);
                answer_delete = (ImageView) view.findViewById(R.id.answer_delete);
                answer_delete.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int index;
                String main_str;

                switch (v.getId()) {
                    case R.id.answer_delete:
                        index = (int) v.getTag();
                        data_map = datas.get(index);
                        if (!data_map.get("answerid").equals(AppUtils.user.get_zydnId())){
                            return;
                        }
                        data_map.put("id",data_map.get("salesdailytalkDetailid"));
                        main_str = FastJsonUtils.mapToString(data_map);
                        if (!main_str.equals("")) {
                            main_str = main_str.substring(1, main_str.length() - 1);
                        }
                        pars = "{\"userId\":\"" + AppUtils.app_username + "\"," + main_str + "}";

                        //删除回复明细
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean isDelete = ActivityController.execute(mContext, menu_code, "deleteAnswer", pars);
                                if (isDelete) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            datas.remove(data_map);
                                            mAdapter2.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }).start();
                        break;
                }
            }
        }
    }

    private TextView.OnEditorActionListener mEditTextClickListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {

            if(actionId == EditorInfo.IME_ACTION_SEND){
				/*隐藏软键盘*/
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }

                String text = v.getText().toString();
                if (text.equals("")){
                    Toast.makeText(mContext,getString(R.string.err_answer_one_notempty),Toast.LENGTH_SHORT).show();
                    return true;
                }
                //执行搜索动作
                id = main_data.get("id").toString();
                final Map<String,Object> answerMap = new HashMap<String, Object>();
                answerMap.put("answer",v.getText());
                answerMap.put("answerdate",DateUtil.createDate());
                answerMap.put("answer_name",AppUtils.user.get_zydnName());
                answerMap.put("answerid",AppUtils.user.get_zydnId());
                answerMap.put("askerid",current_user);
                answerMap.put("salesdailyid",id);
                answerMap.put("salesdailytalkDetailid","");
                answerMap.put("rowid",IniUtils.getFixLenthString(5));
                answerMap.put("zt","1");
                String main_str = FastJsonUtils.mapToString(answerMap);
                if (!main_str.equals("")){
                    main_str = main_str.substring(1,main_str.length()-1);
                }
                pars = "{\"userId\":\""+ AppUtils.app_username+"\","+main_str+"}";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String isAdd = ActivityController.executeForResult(mContext,menu_code,"saveAnswer",pars);
                        if (!isAdd.equals("err")){
                            answerMap.put("salesdailytalkDetailid",isAdd);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter2.getDatas().add(answerMap);
                                    mAdapter2.notifyDataSetChanged();
                                    v.setText("");
                                }
                            });

                        }
                    }
                }).start();

                return true;
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == 1){
            String method =  intent.getStringExtra("method");
            if (method.equals("saveSalesDailyDetailForMobile") || method.equals("editSalesDailyDetailForMobile")){
                System_one so = (System_one)intent.getSerializableExtra("parms");
                if(so == null){
                    return;
                }
                Map<String,Object> mxMap = so.getParms();
                if (mxMap != null){
                    if (method.equals("saveSalesDailyDetailForMobile") ){
                        mAdapter1.getDatas().add(mxMap);
                    }else if(method.equals("editSalesDailyDetailForMobile")){
                        int index =  Integer.parseInt(mxMap.get("index").toString());
                        mAdapter1.getDatas().remove(index);
                        mAdapter1.getDatas().add(index,mxMap);
                    }

                    mAdapter1.notifyDataSetChanged();
                    reshView();
                }
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isOk = resshData(current_user);
                        if (isOk){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter1.reshData(salesdailyDetail);
                                    reshView();
                                }
                            });
                        }
                    }
                }).start();

            }

        }
    }

    public boolean resshData(String current_user){
        boolean isOk = false;
        pars = new ListParms("0","0","20",menu_code,"dailydate:"+current_date+",lx:1,make_empid:"+current_user).getParms();
        Object res = ActivityController.getData2ByPost(mContext,menu_code,"findMobileListAll", StringUtils.strTOJsonstr(pars));
        if (res != null && res instanceof JSONArray) {
            JSONArray res_array = (JSONArray) res;
            if (res_array != null){
                if (res_array.size() > 0) {

                    main_data = (Map<String, Object>) res_array.get(0);
                    if (main_data.get("version") == null){
                        main_data.put("version",1);
                    }
                    salesdailyDetail.clear();
                    if (main_data.get("salesdailyDetail") != null){
                        if ( main_data.get("salesdailyDetail") instanceof  String){
                            salesdailyDetail.addAll(FastJsonUtils.strToListMap(main_data.get("salesdailyDetail").toString()) );
                        }else{
                            salesdailyDetail.addAll((List<Map<String, Object>>) main_data.get("salesdailyDetail"));
                        }

                        ywtype = "edit";
                    }else{
                        ywtype = "add";
                    }
                    salesdailytalkDetail.clear();
                    if (main_data.get("salesdailytalkDetail") != null){
                        if (main_data.get("salesdailytalkDetail") instanceof String){
                            salesdailytalkDetail.addAll(FastJsonUtils.strToListMap( main_data.get("salesdailytalkDetail").toString()));
                        }else{
                            salesdailytalkDetail.addAll((List<Map<String, Object>>) main_data.get("salesdailytalkDetail"));
                        }
                    }
                }else{
                    ywtype = "add";
                    main_data.clear();
                    salesdailyDetail.clear();
                    salesdailytalkDetail.clear();
                }
                isOk =  true;
            }else{
                isOk =  true;
            }

        }else{
            isOk = false;
        }

        return  isOk;
    }

}
