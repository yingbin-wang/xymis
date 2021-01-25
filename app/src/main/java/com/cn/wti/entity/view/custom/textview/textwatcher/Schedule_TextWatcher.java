package com.cn.wti.entity.view.custom.textview.textwatcher;

import android.icu.text.SimpleDateFormat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.prolificinteractive.materialcalendarview.format.CalendarWeekDayFormatter;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wyb on 2017/5/8.
 */

public class Schedule_TextWatcher implements TextWatcher {

    private TextView_custom first,last,start_tv,end_tv;
    private Spinner chongfuSpinner;
    private List<String> chongfuList;
    private View mainForm;
    private BaseAdapter adapter;

    public Schedule_TextWatcher(TextView_custom first, TextView_custom last, View mainForm,List<String> chongfuList){
        this.first = first;
        this.last = last;
        this.mainForm = mainForm;
        this.start_tv = (TextView_custom) mainForm.findViewById(R.id.start_first);
        this.end_tv = (TextView_custom) mainForm.findViewById(R.id.end_first);
        this.chongfuSpinner = (Spinner) mainForm.findViewById(R.id.chongfu);
        this.adapter = (BaseAdapter) chongfuSpinner.getAdapter();
        this.chongfuList = chongfuList == null ? new ArrayList<String>():chongfuList;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String value =  s.toString();
        if (!value.equals("")){
            if (value.indexOf(".")>=0){
                value = value.substring(0,value.indexOf("."));
            }
            if (StringUtils.getOccur(value,":") == 1){
                value +=  ":00";
            }
            Date date =  DateUtil.fomatDateTime(value);
            Calendar calendar = Calendar.getInstance();

            calendar.setTime(date);
            String nyr = value.substring(0,10);
            int week = calendar.get(Calendar.DAY_OF_WEEK);

            first.setText( nyr +"星期"+getWeek(week-1));
            if (StringUtils.getOccur(value,":") == 2){
                value = value.substring(0,value.length()-3);
                last.setText(value.substring(value.length()-5,value.length()));
            }else{
                last.setText(value.substring(value.length()-8,value.length()));
            }

            //得到文本框值 开始 结束
            String start_str =  start_tv.getText().toString();
            String end_str = end_tv.getText().toString();
            if (start_str.indexOf("星期")>=0 && end_str.indexOf("星期")>=0){
                start_str = start_str.substring(0,start_str.indexOf("星期"));
                end_str = end_str.substring(0,end_str.indexOf("星期"));

                if (!start_str.equals("") && !end_str.equals("")){
                    start_str = start_str.replace(" ","");
                    end_str = end_str.replace(" ","");
                    long days = DateUtil.getDaySub(start_str,end_str);

                    if(days>365){
                        chongfuList.clear();
                        chongfuList.add("无");
                    }else if(days>30){
                        chongfuList.clear();
                        chongfuList.add("无");
                        chongfuList.add("按年重复");
                    }else if(days>7){
                        chongfuList.clear();
                        chongfuList.add("无");
                        chongfuList.add("按月重复");
                        chongfuList.add("按年重复");
                    }else if(days>1){
                        chongfuList.clear();
                        chongfuList.add("无");
                        chongfuList.add("按周重复");
                        chongfuList.add("按月重复");
                        chongfuList.add("按年重复");
                    }else{
                        chongfuList.clear();
                        chongfuList.add("无");
                        chongfuList.add("按天重复");
                        chongfuList.add("按周重复");
                        chongfuList.add("按月重复");
                        chongfuList.add("按年重复");
                    }
                    //更新 数据
                    if (adapter == null){
                        this.adapter = (BaseAdapter) chongfuSpinner.getAdapter();
                        adapter.notifyDataSetChanged();
                    }

                    //更新重复 下拉框为无
                    chongfuSpinner.setSelection(0);
                }
            }
        }
    }

    public String getWeek(int week){

        String week_str = "";
        switch (week){
            case 0:
                week_str = "日";
                break;
            case 1:
                week_str = "一";
                break;
            case 2:
                week_str = "二";
                break;
            case 3:
                week_str = "三";
                break;
            case 4:
                week_str = "四";
                break;
            case 5:
                week_str = "五";
                break;
            case 6:
                week_str = "六";
                break;
        }
        return  week_str;
    }
}
