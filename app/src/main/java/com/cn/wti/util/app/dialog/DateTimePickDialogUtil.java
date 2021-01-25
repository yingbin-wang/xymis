package com.cn.wti.util.app.dialog;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.number.DatePickerUtils;
import com.wticn.wyb.wtiapp.R;
import com.dina.ui.model.BasicItem;
import com.dina.ui.widget.UITableView;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 *
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 *
 *           } });
 *
 * @author
 */
public class DateTimePickDialogUtil implements OnDateChangedListener,
		OnTimeChangedListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	public AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;

	private int isTime = 1;

	private Button confirm_btn,cancel_btn;


	/**
	 * 日期时间弹出选择框构造函数
	 *
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialogUtil(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;

	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar = Calendar.getInstance();
		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			String time = "";
			if (isTime == 1){
				time =  ":"
						+ calendar.get(Calendar.MINUTE)
						+ calendar.get(Calendar.SECOND);
			}else{
				time = "";
			}

			initDateTime = calendar.get(Calendar.YEAR) + "-"
					+ calendar.get(Calendar.MONTH) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "- "
					+ calendar.get(Calendar.HOUR_OF_DAY)+time ;
		}

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH) -1,
				calendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
	}

	/**
	 * 弹出日期时间选择框方法
	 *
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final EditText inputDate) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.common_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);

		DatePickerUtils.resizePikcer(datePicker);
		DatePickerUtils.resizePikcer(timePicker);

		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);
		timePicker.setIs24HourView(true);
		ad = new AlertDialog.Builder(activity)
				/*.setTitle(initDateTime)*/
				/*.setView(dateTimeLayout)*/
				/*.setPositiveButton("设置", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						inputDate.setText(dateTime);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						inputDate.setText("");
					}
				})*/.show();
		ad.setCanceledOnTouchOutside(false);
		Window window = ad.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setContentView(dateTimeLayout);
		WindowManager.LayoutParams p = ad.getWindow().getAttributes(); //获取对话框当前的参数值
		p.height = datePicker.getHeight(); //宽度设置为屏幕
		window.setAttributes(p); //设置生效

		onDateChanged(null, 0, 0, 0);
		return ad;
	}


	/**
	 * 弹出日期时间选择框方法
	 *
	 * @param item
	 * :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog_(final UITableView tableView, final View v, final BasicItem item, final int index,int isTime) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.common_datetime, null);
		this.isTime = isTime;
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);

		DatePickerUtils.resizePikcer(datePicker);
		DatePickerUtils.resizePikcer(timePicker);

		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);
		timePicker.setIs24HourView(true);
		if (isTime == 0 || isTime == 2){
			timePicker.setVisibility(View.GONE);
		}
		//隐藏日期 中的 day
		if (isTime == 2){
			//通过反射机制，访问private的mDaySpinner成员，并隐藏它
			try {
				Field daySpinner =datePicker.getClass().getDeclaredField("mDaySpinner");
				daySpinner.setAccessible(true);
				((View)daySpinner.get(datePicker)).setVisibility(View.GONE);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		ad = new AlertDialog.Builder(activity)
				/*.setTitle(initDateTime)*/
				/*.setView(dateTimeLayout)*/
				/*.setPositiveButton("设置", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						item.setSubtitle(dateTime);
						tableView.setupBasicItemValue(v,item,index);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						*//*item.setSubtitle("");*//*
					}
				})*/.show();
		ad.setCanceledOnTouchOutside(false);
		Window window = ad.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialog_animation);  //添加动画
		window.setContentView(dateTimeLayout);

		confirm_btn = (Button) window.findViewById(R.id.confirm);
		//添加确认事件
		confirm_btn.setOnClickListener(new MyOnClickListener_1(dateTime,item,tableView,v,index));

		cancel_btn = (Button) window.findViewById(R.id.cancel);
		cancel_btn.setOnClickListener(new MyOnClickListener_1());

		WindowManager.LayoutParams p = ad.getWindow().getAttributes(); //获取对话框当前的参数值
		p.height = 400; //宽度设置为屏幕
		window.setAttributes(p); //设置生效
		onDateChanged(null, 0, 0, 0);
		return ad;
	}


	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
							  int dayOfMonth) {
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();
		timePicker.setIs24HourView(true);
		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());

		SimpleDateFormat sdf =  null;
		if (isTime == 1){
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else if (isTime == 2){
			sdf = new SimpleDateFormat("yyyy-MM");
		}else{
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}
		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(dateTime);
	}

	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 *
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
		// 日期
		String test_date = spliteString(initDateTime, "-", "2", "back"),dayStr;
			dayStr = spliteString(test_date, " ", "1", "front");

		// 时间
		String time = spliteString(test_date, " ", "1", "back");

		String nyr = spliteString(initDateTime, " ", "1", "front");

		if(isTime == 0 || isTime == 2){
			nyr = initDateTime.substring(0,10);
		}
		// 年份
		String yearStr = nyr.substring(0,4);
		String test_ym = spliteString(nyr, "-", "1", "front");
		//月份
		String monthStr = spliteString(test_ym, "-", "1", "back"); // 月
		dayStr = nyr.substring(nyr.length()-2);

		String hourStr, testminute,minuteStr; // 分

		int currentYear,currentMonth,currentDay,currentHour = 0,currentMinute = 0,currentSecond = 0;

		if(isTime == 1){
			hourStr = spliteString(time, ":", "index", "front"); // 时
			testminute = spliteString(time, ":", "index", "back");
			minuteStr = spliteString(testminute, ":", "index", "front"); // 分
		}else{
			hourStr = ""; // 时
			testminute = "";
			minuteStr = ""; // 分
		}

		currentYear = Integer.valueOf(yearStr.trim()).intValue();
		currentMonth = Integer.valueOf(monthStr.trim()).intValue();
		currentDay = Integer.valueOf(dayStr.trim()).intValue();
		if (isTime == 1){
			currentHour = Integer.valueOf(hourStr.trim()).intValue();
			currentMinute = Integer.valueOf(minuteStr.trim()).intValue();
			currentSecond = Integer.valueOf(time.substring(6).trim()).intValue();
			calendar.set(currentYear, currentMonth, currentDay, currentHour,
					currentMinute,currentSecond);
		}else{
			calendar.set(currentYear, currentMonth, currentDay);
		}
		return calendar;
	}

	/**
	 * 截取子串
	 *
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern,
									  String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}

	private class MyOnClickListener_1 implements View.OnClickListener{

		private BasicItem item;
		private UITableView tableView;
		private int index;
		private View view;

		public MyOnClickListener_1(String datetime,BasicItem item,UITableView tableView,View view,int index){
			this.item = item;
			this.tableView = tableView;
			this.index = index;
			this.view = view;
		}

		public MyOnClickListener_1(View view){
			this.view = view;
		}

		public MyOnClickListener_1(){}

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.confirm:
					if (tableView != null){
						item.setSubtitle(dateTime);
						tableView.setupBasicItemValue(view,item,index);
					}else{
						if (view instanceof TextView_custom){
							TextView_custom tv1 = (TextView_custom) view;
							tv1.setText(dateTime);
						}else if(view instanceof EditText_custom){
							EditText_custom ed1 = (EditText_custom) view;
							ed1.setText(dateTime);
						}
					}
					ad.dismiss();
					break;
				case R.id.cancel:
					ad.dismiss();
					break;
				default:
					break;
			}
		}
	}


	/**
	 * 弹出日期时间选择框方法
	 *
	 *
	 * :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog_view(final View v,int isTime) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.common_datetime, null);
		this.isTime = isTime;
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);

		DatePickerUtils.resizePikcer(datePicker);
		DatePickerUtils.resizePikcer(timePicker);

		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		if (isTime == 0 || isTime == 2){
			timePicker.setVisibility(View.GONE);
		}
		//隐藏日期 中的 day
		if (isTime == 2){
			//通过反射机制，访问private的mDaySpinner成员，并隐藏它
			try {
				Field daySpinner =datePicker.getClass().getDeclaredField("mDaySpinner");
				daySpinner.setAccessible(true);
				((View)daySpinner.get(datePicker)).setVisibility(View.GONE);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		ad = new AlertDialog.Builder(activity).show();
		ad.setCanceledOnTouchOutside(false);
		Window window = ad.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialog_animation);  //添加动画
		window.setContentView(dateTimeLayout);

		confirm_btn = (Button) window.findViewById(R.id.confirm);
		//添加确认事件
		confirm_btn.setOnClickListener(new MyOnClickListener_1(v));

		cancel_btn = (Button) window.findViewById(R.id.cancel);
		cancel_btn.setOnClickListener(new MyOnClickListener_1());

		WindowManager.LayoutParams p = ad.getWindow().getAttributes(); //获取对话框当前的参数值
		p.height = 400; //宽度设置为屏幕
		window.setAttributes(p); //设置生效
		onDateChanged(null, 0, 0, 0);
		return ad;
	}

}
