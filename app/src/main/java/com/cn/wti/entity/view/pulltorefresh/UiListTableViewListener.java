package com.cn.wti.entity.view.pulltorefresh;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.DateUtil;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.Page;
import com.cn.wti.util.page.PageDataSingleton;
import com.dina.ui.widget.ClickListener;
import com.dina.ui.widget.UIListTableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UiListTableViewListener implements PullToRefreshLayout.OnRefreshListener
{
	private List<Map<String,Object>> main_datalist = null,sencond_datalist,third_datalist,fourth_datalist,temp_datalist,resList = null;;
	private String type="",
			 menu_code="",
			menu_name = "",
			method= "",
			pars="",
			start = "";
	private String[] titles,contents = null;
	private UIListTableView listTableView = null;
	private ClickListener listener = null;
	private int recordcount,pageIndex,postion = 0,pagesize;
	private static PageDataSingleton _catch = PageDataSingleton.getInstance();

	private Object service;

	public UiListTableViewListener(){ }

	public UiListTableViewListener(List<Map<String,Object>> main_datalist, int recordcount, int pageIndex, String menu_code, String menu_name, String method, String pars,
								   String[] contents, UIListTableView listTableView,ClickListener listener){
		this.main_datalist = main_datalist;
		/*this.maxid  = maxid;
		this.minid = minid;*/
		this.recordcount = recordcount;
		this.pageIndex = pageIndex;
		this.menu_code = menu_code;
		this.menu_name = menu_name;
		this.method = method;
		this.pars = pars;
		this.contents = contents;
		this.listTableView = listTableView;
		this.listener = listener;
		//截取pagesize
		String test = pars.substring(pars.indexOf("limit"),pars.length());
		String limit = test.substring(test.indexOf("limit"),test.indexOf(","));
		pagesize = Integer.parseInt(limit.replace("limit","").replace(":","").replace("\"",""));
	}

	public UiListTableViewListener(List<Map<String,Object>> main_datalist,int recordcount, int pageIndex, String menu_code, String menu_name, String method, String pars,
								   String[] contents, UIListTableView listTableView,ClickListener listener,int postion){
		this.recordcount = recordcount;
		this.pageIndex = pageIndex;
		this.menu_code = menu_code;
		this.menu_name = menu_name;
		this.method = method;
		this.pars = pars;
		this.contents = contents;
		this.listTableView = listTableView;
		this.listener = listener;
		this.postion = postion;
		//截取pagesize
		String test = pars.substring(pars.indexOf("limit"),pars.length());
		String limit = test.substring(test.indexOf("limit"),test.indexOf(","));
		pagesize = Integer.parseInt(limit.replace("limit","").replace(":","").replace("\"",""));

	}

	public UiListTableViewListener(Object service, int recordcount, int pageIndex, String menu_code, String menu_name, String method, String pars,UIListTableView listTableView,ClickListener listener,int postion) throws NoSuchFieldException, IllegalAccessException {
		this.service = service;
		this.recordcount = recordcount;
		this.pageIndex = pageIndex;
		this.menu_code = menu_code;
		this.menu_name = menu_name;
		this.method = method;
		this.pars = pars;
		this.listTableView = listTableView;
		this.listener = listener;
		this.postion = postion;
		this.titles = (String[]) ReflectHelper.getValueByFieldName(service,"titles");
		this.contents = (String[]) ReflectHelper.getValueByFieldName(service,"contents");
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

				loadData(menu_code,menu_name,method,parms);
				listTableView.clear();
				main_datalist = resList;
				createList(resList,contents,listTableView,listener);
				pageIndex = 1;

				if (postion == 1){
					_catch.put(menu_code+"main_recordcount",recordcount);
					_catch.put(menu_code+"main_pageIndex",pageIndex);
				}else if(postion == 2){
					_catch.put(menu_code+"sencond_recordcount",recordcount);
					_catch.put(menu_code+"sencond_pageIndex",pageIndex);
				}else if(postion == 3){
					_catch.put(menu_code+"third_recordcount",recordcount);
					_catch.put(menu_code+"third_pageIndex",pageIndex);
				}else if(postion == 4){
					_catch.put(menu_code+"fourth_recordcount",recordcount);
					_catch.put(menu_code+"fourth_pageIndex",pageIndex);
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
				String parms = "";
				type = "上拉";

				if (pageIndex >0){
					if (listTableView.get_dataList().size() == 0){
						pageIndex = 1;
						start = "0";
						parms = pars;
					}else{
						switch (postion){
							case 1:
								if (_catch.get(menu_code+"main_pageIndex")!=null){
									pageIndex = Integer.parseInt(_catch.get(menu_code+"main_pageIndex").toString());
								}else{
									pageIndex = 1;
								}
								break;
							case 2:
								if (_catch.get(menu_code+"sencond_pageIndex")!=null){
									pageIndex = Integer.parseInt(_catch.get(menu_code+"sencond_pageIndex").toString());
								}else{
									pageIndex = 1;
								}
								break;
							case 3:
								if (_catch.get(menu_code+"mthird_pageIndex")!=null){
									pageIndex = Integer.parseInt(_catch.get(menu_code+"third_pageIndex").toString());
								}else{
									pageIndex = 1;
								}
								break;
							case 4:
								if (_catch.get(menu_code+"fourth_pageIndex")!=null){
									pageIndex = Integer.parseInt(_catch.get(menu_code+"fourth_pageIndex").toString());
								}else{
									pageIndex = 1;
								}
								break;
						}

						int  testPageIndex = pageIndex + 1;
						Page page = new Page(recordcount,testPageIndex,pagesize);
						start = String.valueOf(page.getPageindex());
						parms = StringUtils.replaceStart(pars,"start:","start:"+start);
						parms = StringUtils.replaceStart(parms,"pageIndex:","pageIndex:"+pageIndex);
					}
				}else{
					int  testPageIndex = pageIndex + 1;
					Page page = new Page(recordcount,testPageIndex,pagesize);
					start = String.valueOf(page.getPageindex());
					parms = StringUtils.replaceStart(pars,"start:","start:"+start);
					parms = StringUtils.replaceStart(parms,"pageIndex:","pageIndex:"+pageIndex);
				}

				loadData(menu_code,menu_name,method,parms);
				if (resList!= null && resList.size()>0){
					/*main_datalist = new ArrayList<Map<String, Object>>();*/
					listTableView.clear2();
					if(listTableView.get_dataList().size()<10){
						listTableView.clear();
						listTableView.get_dataList().clear();
					}
					createList(resList,contents,listTableView,listener);
					pageIndex++;
				}else{
					pageIndex --;
				}

				if (postion == 1){
					_catch.put(menu_code+"main_recordcount",recordcount);
					_catch.put(menu_code+"main_pageIndex",pageIndex);
				}else if(postion == 2){
					_catch.put(menu_code+"sencond_recordcount",recordcount);
					_catch.put(menu_code+"sencond_pageIndex",pageIndex);
				}else if(postion == 3){
					_catch.put(menu_code+"third_recordcount",recordcount);
					_catch.put(menu_code+"third_pageIndex",pageIndex);
				}else if(postion == 4) {
					_catch.put(menu_code + "fourth_recordcount", recordcount);
					_catch.put(menu_code + "fourth_pageIndex", pageIndex);
				}
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 1000);
	}

	/**
	 * 加载数据
	 * @param menu_code
	 * @param menu_name
	 * @param method
	 * @param pars
	 */
	public  boolean loadData(String menu_code,String menu_name,String method,String pars){

		pars = StringUtils.strTOJsonstr(pars);
		int start = pars.indexOf("start:");
		Object res = ActivityController.getDataByPost(menu_code,method,pars);
		try {
			resList = FastJsonUtils.getResultList(FastJsonUtils.strToMap(res.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(resList == null || resList.size() == 0){
			return false;
		}

		if (postion == 1){
			main_datalist = listTableView.get_dataList();

			if (main_datalist == null){
				main_datalist = new ArrayList<Map<String,Object>>();
			}

			if (type.equals("下拉") || start == -1){main_datalist.clear();}
			if(main_datalist != null){
				main_datalist.addAll(resList);
				_catch.put(menu_code+"_maindata",main_datalist);
			}
		}else if(postion == 2){
			sencond_datalist = listTableView.get_dataList();
			if (type.equals("下拉")){sencond_datalist.clear();}
			if(sencond_datalist != null){
				sencond_datalist.addAll(resList);
				_catch.put(menu_code+"_senconddata",sencond_datalist);
			}
		}else if(postion == 3){
			third_datalist = listTableView.get_dataList();
			if (type.equals("下拉")){third_datalist.clear();}
			if(third_datalist != null){
				third_datalist.addAll(resList);
				_catch.put(menu_code+"_thirddata",third_datalist);
			}
		}else if(postion == 4){
			fourth_datalist = listTableView.get_dataList();
			if (type.equals("下拉")){fourth_datalist.clear();}
			if(fourth_datalist != null){
				fourth_datalist.addAll(resList);
				_catch.put(menu_code+"_fourthdata",fourth_datalist);
			}
		}

		return  true;
	}


	public void createList(List<Map<String,Object>> maings_list, String[] contents, UIListTableView mian_TableView, ClickListener listener) {

		Map<String,Object> map = null,dataMap;
		mian_TableView.setService_name(menu_code);

		String id = "";
		int approvalstatus = 0,estatus= 0;
		List<String> values = null;
		if(maings_list == null || maings_list.size() == 0){return;}

		for (int i=0,n = maings_list.size();i<n;i++){
			map = maings_list.get(i);
			values = createMain(map,contents);
			String[] test_contents = (String[]) values.toArray(new String[values.size()]);
			if(map.get("id") != null){
				id = map.get("id").toString();
			}

			if(map.get("approvalstatus") != null){
				approvalstatus = Integer.parseInt(map.get("approvalstatus").toString());
			}

			if(map.get("estatus") != null){
				estatus = Integer.parseInt(map.get("estatus").toString());
			}

			//创建 审批状态 列表对象
			mian_TableView.addBasicItem(id,String.valueOf(i+1), titles,test_contents, R.color.black, true, 1,approvalstatus,estatus);

		}
		//添加数据到 试图中
		switch (postion){
			case 1:
				mian_TableView.set_dataList(main_datalist);
				break;
			case 2:
				mian_TableView.set_dataList(sencond_datalist);
				break;
			case 3:
				mian_TableView.set_dataList(third_datalist);
				break;
			case 4:
				mian_TableView.set_dataList(fourth_datalist);
				break;
		}

		mian_TableView.setClickListener(listener);
		mian_TableView.commit();

	}


	/*public List<String> createMain(Map<String,Object> data,String[] keys){
		List<String> resList = new ArrayList<String>();
		String key = "",val;
		for (int i=0,n=keys.length;i<n; i++){
			key = keys[i];
			if ( data.get(key)!= null){
				val = data.get(key).toString();
			}else{
				val = "";
			}
			resList.add(val);
		}

		return  resList;
	}*/

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

			if ( data.get(key)!= null){
				if (key.equals("iscg")){
					val = data.get(key).toString();
					if (val.equals("0")){
						val = "未完成";
					}else{
						val = "已完成";
					}
				}else if(isDrqf){
					val = data.get(key).toString();
					val = DateUtil.strFomatDate(val);
				}else{
					val = data.get(key).toString();
				}

			}else{
				val = "";
			}
			resList.add(val);
		}

		return  resList;
	}

}
