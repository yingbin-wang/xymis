package com.cn.wti.entity.view.pulltorefresh;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wti.activity.report.activity.Report_XclbbActivity;
import com.cn.wti.entity.view.custom.dialog.adapter.SingleChoicAdapter;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.IDHelper;
import com.cn.wti.util.app.ResourceHelper;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.ReportUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.number.SizheTool;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReportViewListener implements PullToRefreshLayout.OnRefreshListener
{
	private List<Map<String,Object>> _columnList,_datalist = null,resList = null;;
	private String
			service_name="",
			method_name= "",
			pars="",
	        parms = "",
		    key = "",
			start = "",
			type ="";

	private int recordcount,pageIndex,content_rowcount,hj_count,lay_row,pagesize;
	private LinearLayout contentLinerLayout,row_lay;
	private LayoutInflater inflater1;
	private Map<String,Object> map1,resMap;
	private View view;
	private TextView hj1_tv,hj2_tv,hj3_tv;
	private int[] hjl; // 合计列

	/**
	 *
	 * @param _columnList 列名称
	 * @param _datalist 数据
	 * @param recordcount 行数
	 * @param pageIndex
	 * @param service_name
	 * @param method_name
	 * @param pars
	 * @param view 视如
	 * @param lay_row 行视图
	 * @param inflater1
     * @param content_rowcount
     * @param hj_count
     */
	public ReportViewListener(List<Map<String,Object>>_columnList,List<Map<String,Object>>  _datalist,int recordcount, int pageIndex,
							  String service_name, String method_name, String pars, View view,int lay_row,LayoutInflater inflater1,
							  int content_rowcount,int hj_count){

		this._columnList = _columnList;
		this._datalist = _datalist;
		this.recordcount  = recordcount;
		this.pageIndex = pageIndex;
		this.service_name = service_name;
		this.method_name = method_name;
		this.pars = pars;
		this.view = view;
		this.content_rowcount = content_rowcount;
		this.hj_count = hj_count;
		this.lay_row = lay_row;
		this.inflater1 = inflater1;
		contentLinerLayout = (LinearLayout) view.findViewById(R.id.content);
		//截取pagesize
		String test = pars.substring(pars.indexOf("limit"),pars.length());
		String limit = test.substring(test.indexOf("limit"),test.indexOf(","));
		pagesize = Integer.parseInt(limit.replace("limit","").replace(":","").replace("\"",""));
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
				resList = null;
				String parms = "";
				parms = StringUtils.replaceStart(pars,"start:","start:0");
				parms = StringUtils.replaceStart(parms,"pageIndex:","pageIndex:0");
				type = "下拉";

				loadData(service_name,method_name,parms);

				if (resList!= null && resList.size()>0){

					pageIndex = 1;

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
				String parms = "";
				if (pageIndex >0){
					int  testPageIndex = pageIndex + 1;
					Page page = new Page(recordcount,testPageIndex,pagesize);
					start = String.valueOf(page.getPageindex());
					parms = StringUtils.replaceStart(pars,"start:","start:"+start);
					parms = StringUtils.replaceStart(parms,"pageIndex:","pageIndex:"+pageIndex);
				}

				loadData(service_name,method_name,parms);
				if (resList!= null && resList.size()>0){
					addData();
					pageIndex++;
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
		int start = pars.indexOf("start:");
		Object res =  ActivityController.getData4ByPost(service_name,method_name,pars);
		if(res != null) {
			resMap = (Map<String, Object>) res;
			resList = (List<Map<String, Object>>) resMap.get("list");
		}
		if(resList == null){return;}

		if (type.equals("下拉") || start == -1){_datalist.clear();}

		if (resList != null && resList.size()>0){
			_datalist.addAll(resList);
		}else{
			_datalist = resList;
		}
	}

	public void addData(){
		//初始化内容
		int number = 1;
		for (int i=0,n = resList.size();i<n;i++) {
			row_lay = (LinearLayout) inflater1.inflate(R.layout.list_item_tablerow_, null,false);
			map1 = resList.get(i);
			for (int j=0;j< content_rowcount;j++) {
				switch (j){
					case 0:
						ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_1,0,map1);
						break;
					case 1:
						ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_2,1,map1);
						break;
					case 2:
						ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_3,2,map1);
						break;
					case 3:
						ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_4,3,map1);
						break;
					case 4:
						ReportUtils.addRowContent_col(row_lay,_columnList,R.id.list_1_5,4,map1);
						break;
				}

			}

			int color = 0xffd3dde6;
			if (i%2 == 1){
				row_lay.setBackgroundColor(color);
			}else{
				color = 0xffffff;
				row_lay.setBackgroundColor(color);
			}

			contentLinerLayout.addView(row_lay);
			number++;
		}

		//更新合计
		for (int i=1;i<= hj_count;i++){
			switch (i){
				case 1:
					hj1_tv = (TextView) view.findViewById(R.id.hj1_val);
					hj1_tv.setText(getVal(_columnList,i));
					break;
				case 2:
					hj2_tv = (TextView) view.findViewById(R.id.hj2_val);
					hj2_tv.setText(getVal(_columnList,i));
					break;
				case 3:
					hj3_tv = (TextView) view.findViewById(R.id.hj3_val);
					hj3_tv.setText(getVal(_columnList,i));
					break;
				default:
					break;
			}
		}

/*		hj1_tv.setText(StringUtils.getVal(String.valueOf(hj_1)));
		hj2_tv.setText(StringUtils.getVal(String.valueOf(hj_2)));*/
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
			val = SizheTool.jq2wxs(String.valueOf(hj_1 /1000),"2")+"千";
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
