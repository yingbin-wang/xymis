package com.cn.wti.activity.rwgl.schedule;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.base.BaseEdit_NoTable_Activity;
import com.cn.wti.entity.adapter.ScheduleAdapter;
import com.cn.wti.entity.view.custom.dialog.SimplePromptMessageDialog;
import com.cn.wti.entity.view.custom.note.NoteEditText;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.Dialog_ViewUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleActivity extends BaseEdit_NoTable_Activity implements ScheduleAdapter.IonSlidingViewClickListener,DialogInterface.OnClickListener{

    private LayoutInflater inflater = null;
    private Button title_back2,title_ok2;
    private NoteEditText editText;
    private TextView trantime,title,fanhuijintian;
    private ImageView schedulemore;
    private Map<String,Object> parmsMap;
    private MaterialCalendarView mcv;
    private List<Map<String,Object>> scheduleList = new ArrayList<Map<String,Object>>();
    ImageView qiehuan_btn,add_btn;
    private String pars,type="周";
    private int screenWidth ,screenHeight;
    private LinearLayout ts_llt;
    private RecyclerView schedulemx_View;
    private boolean [] mCheck;
    private ScheduleAdapter adapter;

    private View currentView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_schedule;
        super.onCreate(savedInstanceState);
        initData();
        createView();

    }

    public void initData(){
        menu_code = "schedule";
        menu_name="日程管理";
        mContext = ScheduleActivity.this;
    }

   public void createView(){
       title = (TextView) findViewById(R.id.title_name2);
       title.setText(menu_name);

       mcv = (MaterialCalendarView) findViewById(R.id.calendarView);

       schedulemx_View = (RecyclerView) findViewById(R.id.schedulemx_View);
       //创建并设置Adapter
       mCheck = new boolean[scheduleList.size()];
       adapter = new ScheduleAdapter(mContext,scheduleList,AppUtils.getScreenWidth(mContext),new String[]{"start","title","start","end"},R.layout.list_item_schedule,mCheck);
       schedulemx_View.setAdapter(adapter);

       ActivityController.setLayoutManager(schedulemx_View,mContext);

       mcv.setOnDateChangedListener(new OnDateSelectedListener() {
           @Override
           public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
               reshDataThread();
           }
       });

       qiehuan_btn = (ImageView) findViewById(R.id.qiehuan);
       qiehuan_btn.setOnClickListener(this);

       add_btn = (ImageView) findViewById(R.id.add);
       add_btn.setOnClickListener(this);

       ts_llt = (LinearLayout) findViewById(R.id.ts_llt);
       mcv.setCurrentDate(DateUtil.fomatDate(DateUtil.getDay()));
       mcv.setSelectedDate(DateUtil.fomatDate(DateUtil.getDay()));

       mcv.setSelectionColor(getResources().getColor(R.color.color_3490c2));
       mcv.setTileHeight(80);

       fanhuijintian = (TextView) findViewById(R.id.fanhuijintian);
       fanhuijintian.setOnClickListener(this);

       WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
       screenWidth = wm.getDefaultDisplay().getWidth();
       screenHeight = wm.getDefaultDisplay().getHeight();
       reshDataThread();
   }

   public void reshDataThread(){
       mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"处理中...");
       new Thread(){
           @Override
           public void run() {
               reshData();
           }
       }.start();
   }

    public List<String> createMain(Map<String,Object> data,String[] keys){
        List<String> resList = new ArrayList<String>();
        String key = "",val;
        boolean isDrqf = false;

        for (int i=0,n=keys.length;i<n; i++){
            key = keys[i];
            if(key.indexOf("(drq)")>=0){
                key = key.replace("(drq)","");
                isDrqf = true;
            }else{
                isDrqf = false;
            }

            if(isDrqf){
                val = data.get(key).toString();
                val = DateUtil.strFomatDate(val);
            }else{
                val = getVal(key,data);
            }

            if (i ==0 && key.equals("start")){
                if (data.get("allDay") != null && data.get("allDay").equals("true")){
                    val = "全天";
                }else{
                    val = DateUtil.getHourAndMin(val);
                }
            }else if(key.equals("start") || key.equals("end")){
                val = DateUtil.getHourAndMin(val);
            }

            resList.add(val);
        }

        return  resList;
    }

    public String getVal(String key,Map<String,Object>map){
        String res="";
        if (key.equals("")){
            res = "";
        }else{
            if (map.get(key) != null){
                res = map.get(key).toString();
            }else{
                res = "";
            }

        }
        return  res;
    }

    @RequiresApi(api = 29)
    @Override
    public void onClick(View v) {
        Object res;
        switch (v.getId()){
            case R.id.qiehuan:
                ImageView qiehuan = (ImageView) findViewById(R.id.qiehuan);
                if (type.equals("月")){
                    onSetMonthMode();
                    type = "周";
                    qiehuan.setBackground(getResources().getDrawable(R.mipmap.scheduleweek));
                }else if (type.equals("周")){
                    onSetWeekMode();
                    type = "月";
                    qiehuan.setBackground(getResources().getDrawable(R.mipmap.schedulemonth));
                }
                break;
            case R.id.add:
                addOne();
                break;
            case R.id.wancheng:
                res = v.getTag();
                Button button = null;
                if (v instanceof  Button){
                    button = (Button) v;
                }
                if (res != null){
                    // 获取数据
                    Map<String,Object> map = (Map<String, Object>) res;
                    int index = (int) map.get("index");
                    View currentView = (View) map.get("currentView");
                    Map<String,Object> dataMap = scheduleList.get(index);

                    if (button.getText().equals("完成")){
                        dataMap.put("status","已完成");
                    }else{
                        dataMap.put("status","未完成");
                    }

                    CalendarDay calendarDay = mcv.getSelectedDate();
                    Calendar calendar = calendarDay.getCalendar();
                    String year = String.valueOf(calendar.get(Calendar.YEAR));
                    String month = String.valueOf(calendar.get(Calendar.MONTH)+1 <10 ? "0"+ (calendar.get(Calendar.MONTH)+1):calendar.get(Calendar.MONTH)+1);
                    String date1 = String.valueOf(calendar.get(Calendar.DATE) <10 ? "0"+ calendar.get(Calendar.DATE) : calendar.get(Calendar.DATE));

                    dataMap.put("completetime",year+"-"+month+"-"+date1);

                    //更新状态
                    String id = String.valueOf(dataMap.get("id"));

                    String main_str = FastJsonUtils.mapToString(dataMap);
                    if (!main_str.equals("")){
                        main_str = main_str.substring(1,main_str.length()-1);
                    }

                    String pars = "{\"username\":\""+ AppUtils.app_username+"\",\"scheduleid\":\""+id+"\","+main_str+"}",method;

                    if (dataMap.get("chongfu")!= null && !dataMap.get("chongfu").toString().equals("无")){
                        method = "edit4";
                    }else{
                        method = "edit3";
                    }

                    final String finalmethod = method,finalpars = pars;
                    final Button finalbutton = button;
                    final View finalcurrentView = currentView;
                    final Map<String,Object> fianlmap = map;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isedit = ActivityController.execute(mContext,menu_code,finalmethod,finalpars);
                            if (isedit){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView status_ivw = (TextView) finalcurrentView.findViewById(R.id.status);
                                        if (finalbutton.getText().equals("完成")){
                                            status_ivw.setBackground(getResources().getDrawable(R.drawable.green01_dot_bg));
                                        }else{
                                            status_ivw.setBackground(getResources().getDrawable(R.drawable.red01_dot_bg));
                                        }
                                        reshView(fianlmap);
                                    }
                                });

                            }
                        }
                    }).start();
                }
                break;
            case R.id.shanchu:
                res = v.getTag();
                if (res != null){
                    Map<String,Object> map = (Map<String, Object>) res;
                    int index = (int) map.get("index");
                    //删除服务器日程
                    Map<String,Object> dataMap = scheduleList.get(index);
                    if (dataMap != null && !dataMap.get("chongfu").equals("无")){
                        //提示信息
                        if (ActivityController.isActivityRunning(mContext,"com.cn.wti.activity.rwgl.schedule.ScheduleActivity")){
                            mDialog = SimplePromptMessageDialog.getInstance(mContext,"删除日程","该任务为重复任务，是否删除该日期以后的所有任务?");
                            mDialog.show();
                        }

                        currentView =  v;
                    }else{
                        currentView =  v;
                        shanchu();
                    }
                    reshView(map);
                }

                break;
            case R.id.xiangqing:
                res = v.getTag();
                if (res != null) {
                    // 获取数据
                    Map<String, Object> map = (Map<String, Object>) res;
                    int index = (int) map.get("index");
                    View currentView = (View) map.get("currentView");
                    Map<String, Object> dataMap = scheduleList.get(index);
                    if (dataMap != null){
                        dataMap.put("index",index);
                        if (map.get("qx") != null){
                            dataMap.put("qx",1);
                        }
                        Intent intent = new Intent();
                        intent.putExtras(AppUtils.setParms("edit",dataMap));
                        intent.setClass(mContext,Schedule_editActivity.class);
                        startActivityForResult(intent,1);
                    }
                    reshView(map);
                }
                break;
            case R.id.fanhuijintian:
                mcv.setSelectedDate(DateUtil.fomatDate(DateUtil.getDay()));
                reshDataThread();
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    /**
     * 刷新视图
     * @param map
     */
    public  void reshView(Map<String,Object> map){
        PopupWindow popupWindow = (PopupWindow) map.get("popupWindow");
        popupWindow.dismiss();
    }

    public  void addOne(){
        Intent intent = new Intent();
        intent.setClass(mContext,Schedule_editActivity.class);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("id","");
        map.put("index",0);
        map.put("type","add");
        intent.putExtras(AppUtils.setParms("add",map));
        startActivityForResult(intent,1);
    }

    public void onSetWeekMode() {
        mcv.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
    }

    public void onSetMonthMode() {
        mcv.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
    }

    public void reshData(){

        CalendarDay calendarDay = mcv.getSelectedDate();
        Calendar calendar = calendarDay.getCalendar();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String moth = String.valueOf(calendar.get(Calendar.MONTH)+1 <10 ? "0"+ (calendar.get(Calendar.MONTH)+1):calendar.get(Calendar.MONTH)+1);
        String date1 = String.valueOf(calendar.get(Calendar.DATE) <10 ? "0"+ calendar.get(Calendar.DATE) : calendar.get(Calendar.DATE));
        String pars = "initiatorid:"+AppUtils.user.get_zydnId()+",starttime:"+year+"-"+moth+"-"+date1;

        Object res = ActivityController.getData4ByPost("schedule","listOneday", StringUtils.strTOJsonstr(pars));
        if (res != null && !res.toString().contains("(abcdef)")){
            scheduleList.clear();
            scheduleList.addAll((List<Map<String, Object>>)res);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (scheduleList!= null && scheduleList.size()>0){
                    adapter.refreshData(adapter.getSelectItem());
                    ts_llt.setVisibility(View.GONE);
                }else{
                    ts_llt.setVisibility(View.VISIBLE);
                }
            }
        });

        if (mDialog != null){
            WeiboDialogUtils.closeDialog(mDialog);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1){
            //刷新视图
            reshDataThread();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Map<String, Object> dataMap = scheduleList.get(position);
        if (dataMap != null){
            dataMap.put("index",position);
            Intent intent = new Intent();
            intent.putExtras(AppUtils.setParms("edit",dataMap));
            intent.setClass(mContext,Schedule_editActivity.class);
            startActivityForResult(intent,1);
        }
    }

    @Override
    public void onShowMoreBtnClick(View view, int position) {
        Activity activity = (Activity) mContext;
        int width_ = 0;
        Map<String,Object> map = null,copyMap = null;
        if (view.getTag() != null){
            copyMap = (Map<String, Object>) view.getTag();
        }

        int index = Integer.parseInt(copyMap.get("index").toString());
        map = adapter.getDatas().get(index);
        String[] buttons = null;

        if (map.get("createby")== null){
            if (map.get("initiatorid2") == null){
                Toast.makeText(mContext,"未找到创建人",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (map.get("createby") == null || !map.get("createby").toString().equals(AppUtils.app_username)){
            buttons = new String[]{"wancheng","xiangqing"};
        }else{
            buttons = new String[]{"wancheng","shanchu","xiangqing"};
        }

        //判断状态
        String status = "";

        if (map.get("statusmx") != null){
            status = map.get("statusmx").toString();
            if (status.equals("")){
                status = map.get("status").toString();
            }
        }else{
            status = map.get("status").toString();
        }
        copyMap.put("status",status);
        Dialog_ViewUtils.showPopUp(view,mContext,this,R.layout.list_item_button,500,100,copyMap,new String[]{"wancheng","shanchu","xiangqing"},buttons,2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1){
            shanchu();
            SimplePromptMessageDialog.mDialog = null;
        }else if (which == -2){
            Object res = null;
            if (currentView == null){return;}
            res = currentView.getTag();
            if (res != null){
                Map<String,Object> map = (Map<String, Object>) res;
                reshView(map);
            }
            SimplePromptMessageDialog.mDialog = null;
        }
    }

   public void shanchu(){
       Object res;
       if (currentView == null){return;}
        res = currentView.getTag();
        if (res != null){
            Map<String,Object> map = (Map<String, Object>) res;
            int index = (int) map.get("index");

            //删除服务器日程
            Map<String,Object> dataMap = scheduleList.get(index);
            if (dataMap != null){
                CalendarDay calendarDay = mcv.getSelectedDate();
                Calendar calendar = calendarDay.getCalendar();
                String year = String.valueOf(calendar.get(Calendar.YEAR));
                String moth = String.valueOf(calendar.get(Calendar.MONTH)+1 <10 ? "0"+ (calendar.get(Calendar.MONTH)+1):calendar.get(Calendar.MONTH)+1);
                String date1 = String.valueOf(calendar.get(Calendar.DATE) <10 ? "0"+ calendar.get(Calendar.DATE) : calendar.get(Calendar.DATE));

                String deletetime = year+"-"+moth+"-"+date1,method;
                String id = String.valueOf(dataMap.get("id")) ;

                //版本号
                if (dataMap.get("version")!= null){
                    version = dataMap.get("version").toString();
                }

                if (dataMap.get("start").toString().substring(0,10).equals(deletetime)){
                    method = "delete";
                    pars = "{\"username\":\""+ AppUtils.app_username+"\",\"id\":\""+id+"\",\"version\":\""+version
                            +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"+"\"}";
                }else{
                    method = "editdeletetime";
                    pars = "{\"username\":\""+ AppUtils.app_username+"\",\"id\":\""+id+"\",\"version\":\""+version
                            +"\",\"ip\":\""+AppUtils.app_ip+"\",\"device\":\"PHONE"+"\",\"deletetime\":\""+deletetime+"\"}";
                }

                final String finalmethod = method;
                final int finalindex = index;
                if (!id.equals("")){
                    mDialog = WeiboDialogUtils.createLoadingDialog(mContext,"删除中...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isDelete = ActivityController.execute(mContext,menu_code,finalmethod,pars);
                            if (isDelete){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.getDatas().remove(finalindex);
                                        adapter.notifyDataSetChanged();
                                        WeiboDialogUtils.closeDialog(mDialog);
                                    }
                                });
                            }else{
                                WeiboDialogUtils.closeDialog(mDialog);
                            }
                        }
                    }).start();
                }
            }
        }
   }
}
