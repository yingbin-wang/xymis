package com.cn.wti.entity.view.pulltorefresh;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.ReportUtils;
import com.cn.wti.util.number.SizheTool;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.Page;
import com.wticn.wyb.wtiapp.R;

import java.util.List;
import java.util.Map;

public class ReportViewListener_Ddgz implements PullToRefreshLayout.OnRefreshListener
{
	private List<Map<String,Object>> _columnList, _datalist = null,resList = null;;
	private String
			service_name="",
			method_name= "",
			pars="",
			type ="",
	        date = "";

	private LinearLayout contentLinerLayout,row_lay;
	private LayoutInflater inflater1;
	private Map<String,Object> map1,resMap;
	private int[] hjl; // 合计列

	/**
	 *
	 * @param _datalist 数据
	 * @param service_name
	 * @param method_name
	 * @param pars
	 * @param view 视如
	 * @param inflater1
     * @param date
     */
	public ReportViewListener_Ddgz(List<Map<String,Object>>  _columnList,List<Map<String,Object>>  _datalist,
								   String service_name, String method_name, String pars, View view, LayoutInflater inflater1,String date){

		this._columnList = _columnList;
		this._datalist = _datalist;
		this.service_name = service_name;
		this.method_name = method_name;
		this.pars = pars.replace("<m>",":");
		this.date = date;
		this.inflater1 = inflater1;
		contentLinerLayout = (LinearLayout) view.findViewById(R.id.content);
	}

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout)
	{
		// 下拉刷新操作
		new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				// 千万别忘了告诉控件刷新完毕了哦！
				Log.v("test","下拉刷新中。。。。。。");
				date = DateUtil.getDateByn(date,-1);
				resList = null;
				type = "下拉";
				String parms = "";
				parms = StringUtils.replaceStart_mh(pars,"rq:","rq<m>"+date);

				loadData(service_name,method_name,parms);
				if (resList!= null){
					addData();
				}
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout)
	{
		// 加载操作
		new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				// 千万别忘了告诉控件加载完毕了哦！
				Log.v("test","上拉加载中。。。。。。");
				resList = null;
				type = "上拉";
				date = DateUtil.getDateByn(date,1);

				String parms = "";
				parms = StringUtils.replaceStart_mh(pars,"rq:","rq<m>"+date);

				loadData(service_name,method_name,parms);
				if (resList!= null){
					addData();
				}
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 1000);
	}

	/**
	 * 加载数据
	 * @param service_name
	 * @param method_name
	 * @param pars
	 */
	public  void loadData(String service_name,String method_name,String pars){

		pars = StringUtils.strTOJsonstr(pars);

		Object res =  ActivityController.getData4ByPost(service_name,method_name,pars);
		if(res != null && !res.equals("")) {
			resMap = (Map<String, Object>) res;
			resList = (List<Map<String, Object>>) resMap.get("list");
		}
		if(resList == null){return;}

		_datalist = resList;
	}

	public void addData(){
		//初始化内容
		int number = 1;
		contentLinerLayout.removeAllViews();
		for (int i=0,n = resList.size();i<n;i++) {
			row_lay = (LinearLayout) inflater1.inflate(R.layout.list_item_tablerow_ddgz, null,false);
			map1 = resList.get(i);

			ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_1,0,map1);
			ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_2,2,map1);
			ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_3,1,map1);
			ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_4,3,map1);
			ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_5,4,map1);
			ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_6,5,map1);
			contentLinerLayout.addView(row_lay);
			number++;
		}

	}

	private String getVal(List<Map<String,Object>>_columnList , int i){
		double hj_1 = 0;
		String key1 ="",val = "";

		if(hjl != null){
			int a = hjl[i-1];
			key1 = ((Map<String,Object>) _columnList.get(a)).get("field").toString();
		}else{
			key1 = ((Map<String,Object>) _columnList.get(i)).get("field").toString();
		}

		for (Map<String,Object> map:_datalist ) {
			if(map.get(key1) == null){
				val = "";
			}else{
				val = map.get(key1).toString();
				if(StringUtils.isNumeric(val)){
					double val_ = Double.parseDouble(val);
					hj_1 += val_;
				}
			}
		}

		val = "";
		if(Math.abs(hj_1) > 10000 ){
			val = SizheTool.jq2wxs(String.valueOf(hj_1 /10000),"2")+"万";
		}/*else if(Math.abs(hj_1) >1000){
			val = SizheTool.jq2wxs(String.valueOf(hj_1 /1000))+"千";
		}*/else{
			val = String.valueOf(hj_1);
		}
		return val;
	}

	public int[] getHjl() {
		return hjl;
	}

	public void setHjl(int[] hjl) {
		this.hjl = hjl;
	}
}
