package com.cn.wti.entity.view.pulltorefresh;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.BaseAdapter;

import com.cn.wti.entity.view.custom.dialog.adapter.MultiChoic_Dn_Adapter;
import com.cn.wti.entity.view.custom.dialog.adapter.SingleChoicAdapter;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ListViewListener<T> implements PullToRefreshLayout.OnRefreshListener
{
	private List<Map<String,Object>> datalist,resList = null;;
	private String/* maxid = "",
			 minid = "",*/
			 menu_code="",
			menu_name = "",
			method= "",
			pars="",
	        parms = "",
		    key = "",
			start = "",
			type ="";
	private List<T>  mList = null;
	private BaseAdapter mSingleChoicAdapter;

	private int recordcount,pageIndex,pagesize;
	/*public ListViewListener(){ }*/
	private PullToRefreshLayout pullToRefreshLayout;
	public ListViewListener(BaseAdapter mSingleChoicAdapter, List<T> datalist, int recordcount, int pageIndex,
							String menu_code, String menu_name, String method, String pars, String key){

		this.mSingleChoicAdapter = mSingleChoicAdapter;
		this.mList = datalist;
		this.recordcount  = recordcount;
		this.pageIndex = pageIndex;
		this.menu_code = menu_code;
		this.menu_name = menu_name;
		this.method = method;
		this.pars = pars;
		this.key = key;
		//截取pagesize
		if (pars.indexOf("limit") >=0){
			String test = pars.substring(pars.indexOf("limit"),pars.length());
			String limit = test.substring(test.indexOf("limit"),test.indexOf(","));
			pagesize = Integer.parseInt(limit.replace("limit","").replace(":","").replace("\"",""));
		}

		this.datalist = new ArrayList<Map<String,Object>>();
		this.datalist.addAll((Collection<? extends Map<String, Object>>) mList);
	}

	private Handler reshViewHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					reshData();
					pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
					break;
			}
		}
	};

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout)
	{
		this.pullToRefreshLayout = pullToRefreshLayout;
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

				final String finalParms = parms;
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean isload = loadData(menu_code,menu_name,method, finalParms);
						if (isload && resList!= null && resList.size()>=0){
							mList.clear();
							mList.addAll((Collection<? extends T>) datalist);
						}else{
							datalist.clear();
							mList.clear();
							mList.addAll((Collection<? extends T>) datalist);
						}

						//刷新视图
						reshViewHandler.sendEmptyMessageDelayed(1,1000);
					}
				}).start();

				pageIndex = 1;

			}
		}.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout)
	{
		this.pullToRefreshLayout = pullToRefreshLayout;
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
				}else{
					if (pageIndex == 0 && pagesize == 0){
						parms = pars;
					}
				}
				final String finalParms = parms;
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean isload = loadData(menu_code,menu_name,method, finalParms);
						if (isload && resList!= null && resList.size()>=0){
							mList.clear();
							mList.addAll((Collection<? extends T>) datalist);
							pageIndex++;
						}else{
							datalist.clear();
							mList.clear();
							mList.addAll((Collection<? extends T>) datalist);
						}
						//刷新视图
						reshViewHandler.sendEmptyMessageDelayed(1,1000);

					}
				}).start();

			}
		}.sendEmptyMessageDelayed(0, 1000);
	}

	public void reshData(){
		if (mSingleChoicAdapter instanceof SingleChoicAdapter){
			SingleChoicAdapter adapter = (SingleChoicAdapter) mSingleChoicAdapter;
			adapter.refreshData(mList);
		}else if (mSingleChoicAdapter instanceof MultiChoic_Dn_Adapter){
			MultiChoic_Dn_Adapter adapter = (MultiChoic_Dn_Adapter) mSingleChoicAdapter;
			adapter.refreshData(mList,new boolean[mList.size()]);
		}
	}

	/**
	 * 加载数据
	 * @param service_name
	 * @param menu_name
	 * @param method_name
	 * @param pars
	 */
	public  boolean loadData(String service_name,String menu_name,String method_name,String pars){

		String pars1 = StringUtils.strTOJsonstr(pars);
		int start = pars.indexOf("start:");
		Object res =  ActivityController.getData4ByPost(service_name,method_name,pars1);
		if (res == null || res.toString().contains("(abcdef)")){return  false;}
		resList = (List<Map<String, Object>>) res;
		if(resList == null){return false;}

		if (type.equals("下拉") || start == -1){datalist.clear();}

		if (resList != null && resList.size()>=0){
			datalist.addAll(resList);
		}else{
			datalist = new ArrayList<Map<String,Object>>();
			datalist.addAll(resList);
		}
		return  true;
	}

}
