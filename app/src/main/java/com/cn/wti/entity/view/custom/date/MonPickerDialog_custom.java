package com.cn.wti.entity.view.custom.date;


import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.dina.ui.model.BasicItem;
import com.dina.ui.widget.UITableView;

import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MonPickerDialog_custom extends DatePickerDialog {

    private static String ny = "";
    private static OnDateSetListener mDateSetListener;

    static Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
    static  int mYear = dateAndTime.get(Calendar.YEAR);
    static  int mMonth = dateAndTime.get(Calendar.MONTH);
    static  int mDay = dateAndTime.get(Calendar.DAY_OF_MONTH);

    static  BasicItem basicItem;
    static UITableView tableView;
    static  View v;static int index;

    static {
            mDateSetListener= new OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mYear = year;
                String mm;
                String dd;
                if (monthOfYear <= 9) {
                    mMonth = monthOfYear + 1;
                    mm = "0" + mMonth;
                } else {
                    mMonth = monthOfYear + 1;
                    mm = String.valueOf(mMonth);
                }
                if (dayOfMonth <= 9) {
                    mDay = dayOfMonth;
                    dd = "0" + mDay;
                } else {
                    mDay = dayOfMonth;
                    dd = String.valueOf(mDay);
                }
                mDay = dayOfMonth;
                ny = String.valueOf(mYear) + mm;
                setNy(basicItem,tableView,view,index);
            }
        };
    }

    public static void setNy(BasicItem basicItem, UITableView tableView,View v,int index){
        basicItem.setSubtitle(ny);
        tableView.setupBasicItemValue(v,basicItem,index);
    }


    public MonPickerDialog_custom(Context context,
                                  int year, int monthOfYear, int dayOfMonth) {
        super(context, mDateSetListener, year, monthOfYear, dayOfMonth);
        this.setTitle(year + "年" + (monthOfYear + 1) + "月");

        ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0))
                .getChildAt(0)).getChildAt(2).setVisibility(View.GONE);

    }


    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        this.setTitle(year + "年" + (month + 1) + "月");
    }

}
