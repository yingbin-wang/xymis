package com.cn.wti.util.other;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil {
	private final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

	private final static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat sdfDay_chinese = new SimpleDateFormat("yyyy年MM月dd");
	
	private final static SimpleDateFormat sdfDays = new SimpleDateFormat("yyyyMMdd");

	private final static SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	public  static String createDate(){
		return sdfTime.format(new Date());
	}
	/**
	 * 获取YYYY格式
	 * 
	 * @return
	 */
	public static String getYear() {
		return sdfYear.format(new Date());
	}

	/**
	 * 获取YYYY-MM-DD格式
	 * 
	 * @return
	 */
	public static String getDay() {
		return sdfDay.format(new Date());
	}

	public static String getDay(Date date){
		DateFormat f_day=new SimpleDateFormat("dd");
		return f_day.format(date).toString();
	}

	/**
	 * 获取YYYYMMDD格式
	 * 
	 * @return
	 */
	public static String getDays(){
		return sdfDays.format(new Date());
	}

	/**
	 * 获取YYYY-MM-DD HH:mm:ss格式
	 * 
	 * @return
	 */
	public static String getTime() {
		return sdfTime.format(new Date());
	}

	/**
	* @Title: compareDate
	* @Description: TODO(日期比较，如果s>=e 返回true 否则返回false)
	* @param s
	* @param e
	* @return boolean  
	* @throws
	* @author luguosui
	 */
	public static boolean compareDate(String s, String e) {
		if(fomatDate(s)==null||fomatDate(e)==null){
			return false;
		}
		return fomatDate(s).getTime() >=fomatDate(e).getTime();
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public static Date fomatDate(String date) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (StringUtils.isNumeric(date)){
				date = stampToDate(date);
			}
			return fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date fomatDateTime(String date) {
		try {
			if (StringUtils.isNumeric(date)){
				date = stampToDate(date);
			}
			return sdfTime.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 校验日期是否合法
	 * 
	 * @return
	 */
	public static boolean isValidDate(String s) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			fmt.parse(s);
			return true;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return false;
		}
	}
	public static int getDiffYear(String startTime,String endTime) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long aa=0;
			int years=(int) (((fmt.parse(endTime).getTime()-fmt.parse(startTime).getTime())/ (1000 * 60 * 60 * 24))/365);
			return years;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return 0;
		}
	}
	  /**
     * <li>功能描述：时间相减得到天数
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long 
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr,String endDateStr){
        long day=0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = null;
        Date endDate = null;
        
		try {
			if (StringUtils.isNumeric(beginDateStr)){
				beginDateStr = stampToDate(beginDateStr);
			}

			if (StringUtils.isNumeric(endDateStr)){
				endDateStr = stampToDate(endDateStr);
			}
			beginDate = format.parse(beginDateStr);
			endDate= format.parse(endDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
		//System.out.println("相隔的天数="+day);
      
        return day;
    }
    
    /**
     * 得到n天之后的日期
     * @param days
     * @return
     */
    public static String getAfterDayDate(String days) {
    	int daysInt = Integer.parseInt(days);
    	
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
        
        SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdfd.format(date);
        
        return dateStr;
    }

	/**
	 * 得到n天之后的日期
	 * @param Second
	 * @return
	 */
	public static String getAfterMinuteDate(String Second) {
		int daysInt = Integer.parseInt(Second);

		Calendar canlendar = Calendar.getInstance(); // java.util包
		canlendar.add(Calendar.MINUTE, daysInt); // 日期减 如果不够减会将月变动
		Date date = canlendar.getTime();

		SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdfd.format(date);

		return dateStr;
	}

    /**
     * 得到n天之后是周几
     * @param days
     * @return
     */
    public static String getAfterDayWeek(String days) {
    	int daysInt = Integer.parseInt(days);
    	
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
        
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);
        
        return dateStr;
    }
    
    public static void main(String[] args) {
    	System.out.println(getDays());
    	System.out.println(getAfterDayWeek("3"));
    }
    
    /**
	 * 根据给定年月,计算年月日 2015-1-->2015-1-31
	 * 
	 * @param month
	 * @return
	 * @throws ParseException
	 */
	public static String getMaxDayOfMonth(String month) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Calendar calendar = new GregorianCalendar();
		Date date1 = sdf.parse(month);
		calendar.setTime(date1); // 放入你的日期
		int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		month = month + "-" + day;
		return month;
	}

	/**
	 * 在月份前加0 2015-1-->2015-01
	 * 
	 * @param yearAndMonth
	 * @return
	 */
	public static String addZeroBeforeMonth(String yearAndMonth) {
		String[] arr = yearAndMonth.split("-");
		int year = Integer.valueOf(arr[0]);
		int month = Integer.valueOf(arr[1]);
		if (month < 10) {// 补0操作
			yearAndMonth = year + "-" + "0" + month;
		} else {
			yearAndMonth = year + "-" + month;
		}
		return yearAndMonth;
	}

	/**
	 * 获取日期期间的所有年月
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static String[] getAllMonths(String start, String end) {
		String splitSign = "-";
		String regex = "\\d{4}" + splitSign + "(([0][1-9])|([1][012]))"; // 判断YYYY-MM时间格式的正则表达式
		if (!start.matches(regex) || !end.matches(regex))
			return new String[0];

		List<String> list = new ArrayList<String>();
		if (start.compareTo(end) == 0) {

			String[] result = new String[1];

			result[0] = start;

			return result;
		}

		if (start.compareTo(end) > 0) {
			// start大于end日期时，互换
			String temp = start;
			start = end;
			end = temp;
		}

		String temp = start; // 从最小月份开始
		while (temp.compareTo(start) >= 0 && temp.compareTo(end) <= 0) {
			list.add(temp); // 首先加上最小月份,接着计算下一个月份
			String[] arr = temp.split(splitSign);
			int year = Integer.valueOf(arr[0]);
			int month = Integer.valueOf(arr[1]) + 1;
			if (month > 12) {
				month = 1;
				year++;
			}
			if (month < 10) {// 补0操作
				temp = year + splitSign + "0" + month;
			} else {
				temp = year + splitSign + month;
			}
		}

		int size = list.size();
		String[] result = new String[size];
		for (int i = 0; i < size; i++) {
			result[i] = list.get(i);
		}
		return result;
	}
	/**
	 * 获取两个日期相隔的月数
	 * @param date1
	 * @param date2
	 * @return
	 * @throws ParseException
	 */
	public static int getMonthSpace(String date1, String date2)
			throws ParseException {

		int result = 0;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sdf.parse(date1));
		c2.setTime(sdf.parse(date2));
		int years=c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		int months = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		
		return months + years*12;

	}

	public static String formDateTime(Date date1){

		String str = sdfTime.format(date1);
		return  str;
	}

	public static String formDate(Date date1){

		String str = sdfDay.format(date1);
		return  str;
	}

	public static boolean isDate(String date_str){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		// 设置日期转化成功标识
		boolean dateflag=true;
		// 这里要捕获一下异常信息
		try
		{
			Date date = format.parse(date_str);
		} catch (ParseException e){
			dateflag=false;
		}finally{
		}
		return  dateflag;
	}

	/**
	 * 时间戳转换成日期格式字符串
	 * @param seconds 精确到秒的字符串
	 * @param format
	 * @return
	 */
	public static String timeStamp2Date(String seconds,String format) {
		if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
			return "";
		}
		if(format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(seconds+"000")));
	}
	/**
	 * 日期格式字符串转换成时间戳
	 * @param date_str 字符串日期
	 * @param format 如：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String date2TimeStamp(String date_str,String format){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return String.valueOf(sdf.parse(date_str).getTime()/1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/*
     * 将时间戳转换为时间
     */
	public static String stampToDate(String s){
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long lt = new Long(s);
		Date date = new Date(lt);
		res = simpleDateFormat.format(date);
		return res;
	}

	/**
	 * 得到指定日期 向前向后 日期
	 * @param date1
	 * @param n
	 * @return
	 */
	public static  String getDateByn(String date1,int n){
		Date date= fomatDate(date1);
		Calendar   calendar   =   new   GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE,n);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime();   //这个时间就是日期往后推一天的结果
		return  sdfDay.format(date);
	}

	/**
	 * 字符串转短日期
	 * @param date
	 * @return
     */
	public static String strFomatDate(String date) {
		if (date.length()>=10){
			return  date.substring(0,10);
		}else{
			return date;
		}
	}

	/**
	 * 得到小时和分钟
	 * @param val
	 * @return
     */
	public static String getHourAndMin(String val){
		if (val!= null && !val.equals("")){
			if (StringUtils.isNumeric(val)){
				val = stampToDate(val);
			}
			Date date = DateUtil.fomatDateTime(val);

			int hour = date.getHours();
			String str_hour="",str_min="";
			if (hour < 10){
				str_hour = "0"+String.valueOf(hour);
			}else{
				str_hour = String.valueOf(hour);
			}
			int min = date.getMinutes();

			if (min < 10){
				str_min = "0"+String.valueOf(min);
			}else{
				str_min = String.valueOf(min);
			}
			val = str_hour+":"+str_min;
		}
		return  val;
	}

	public  static String getNowStamp(){
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyyMMddHHmmssSSS");
		//获取当前时间并作为时间戳
		String timeStamp=simpleDateFormat.format(new Date());
		return  timeStamp;
	}

}
