package com.cn.wti.util.app.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.number.DatePickerUtils;
import com.cn.wti.util.other.DateUtil;
import com.dina.ui.model.BasicItem;
import com.dina.ui.widget.UITableView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.wticn.wyb.wtiapp.R;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickDialogUtil{
	private MaterialCalendarView mcv;
	public AlertDialog ad;
	private String dateTime ="";
	private String initDateTime;
	private Activity activity;
	private OnDateSelectedListener selectedListener;
	private ImageButton title_back2;

	/**
	 * 日期时间弹出选择框构造函数
	 *
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DatePickDialogUtil(Activity activity, String initDateTime) {
		this.activity = activity;
		this.dateTime = initDateTime;
	}

	public void init() {
		if (dateTime.equals("")){
			dateTime = DateUtil.getDay();
		};

		if (mcv != null){
			mcv.setSelectedDate(DateUtil.fomatDate(dateTime));
		}

	}

	/**
	 * 弹出日期时间选择框方法
	 *
	 * @param
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog() {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.common_date, null);

		mcv = (MaterialCalendarView) dateTimeLayout.findViewById(R.id.calendarView);

		if (selectedListener != null){
			mcv.setOnDateChangedListener(selectedListener);
		}

		title_back2 = (ImageButton) dateTimeLayout.findViewById(R.id.title_back2);
		title_back2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});

		init();
		ad = new AlertDialog.Builder(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen).show();
		ad.setCanceledOnTouchOutside(false);
		Window window = ad.getWindow();
		window.setGravity(Gravity.TOP);
		WindowManager.LayoutParams p = ad.getWindow().getAttributes(); //获取对话框当前的参数值
		p.width = AppUtils.getScreenWidth(activity);
		window.setAttributes(p); //设置生效

		window.setContentView(dateTimeLayout);
		return ad;
	}

	public void setSelectedListener(OnDateSelectedListener selectedListener) {
		this.selectedListener = selectedListener;
	}

	public void close(){
		if (ad != null){
			ad.dismiss();
		}
	}

}
