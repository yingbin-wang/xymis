package com.cn.wti.entity.view.pulltorefresh;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.cn.wti.entity.adapter.MyAdapter1;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.adapter.ReportAdapter;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.Page;
import com.cn.wti.util.page.PageDataSingleton;
import com.dina.ui.widget.ClickListener;
import com.dina.ui.widget.UIListTableView;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UiListRecyViewListener implements PullToRefreshLayout.OnRefreshListener
{
	private List<Map<String,Object>> main_datalist = null,sencond_datalist,third_datalist,fourth_datalist,temp_datalist,resList = null,_dataList;
	private String type="",
			 menu_code="",
			menu_name = "",
			method= "",
			pars="",
			start = "";
	private String[] titles,contents = null;
	private RecyclerView mRecyclerView1 = null;
	private ClickListener listener = null;
	private int recordcount,pageIndex,postion = 0;
	private static PageDataSingleton _catch = PageDataSingleton.getInstance();
	private int pagesize;
    private PullToRefreshLayout pullToRefreshLayout;

	private Object service;

	public UiListRecyViewListener(int recordcount, int pageIndex, String menu_code, String menu_name, String method, String pars, RecyclerView mRecyclerView1, int postion,List<Map<String,Object>> _dataList){
		this.recordcount = recordcount;
		this.pageIndex = pageIndex;
		this.menu_code = menu_code;
		this.menu_name = menu_name;
		this.method = method;
		this.pars = pars;
		this.mRecyclerView1 = mRecyclerView1;
		this.postion = postion;
		this._dataList = _dataList;
		if (pars.indexOf("limit") >0){
			String test = pars.substring(pars.indexOf("limit"),pars.length());
			String limit = test.substring(test.indexOf("limit"),test.indexOf(","));
			pagesize = Integer.parseInt(limit.replace("limit","").replace(":","").replace("\"",""));
		}

	}

	private Handler reshViewHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Object adapter = mRecyclerView1.getAdapter();
                    if (adapter instanceof MyAdapter2){
                        MyAdapter2 adapter1 = (MyAdapter2) mRecyclerView1.getAdapter();
                        adapter1.refreshData(_dataList,adapter1.getSelectItem());
                    }else if(adapter instanceof ReportAdapter){
                        ReportAdapter adapter1 = (ReportAdapter) mRecyclerView1.getAdapter();
                        adapter1.refreshData(_dataList,adapter1.getSelectItem());
                    }
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
                final String finalparms = parms;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadData(menu_code,menu_name,method,finalparms)){
                            main_datalist = resList;
                            pageIndex = 1;
                            //刷新视图 记录数据
                            if (postion == 1){
                                _catch.put(menu_code+"main_recordcount",recordcount);
                                _catch.put(menu_code+"main_pageIndex",pageIndex);

                                _dataList.clear();
                                if (main_datalist != null){
                                    _dataList.addAll(main_datalist);
                                }

                            }else if(postion == 2){
                                _catch.put(menu_code+"sencond_recordcount",recordcount);
                                _catch.put(menu_code+"sencond_pageIndex",pageIndex);

                                _dataList.clear();
                                if (sencond_datalist != null){
                                    _dataList.addAll(sencond_datalist);
                                }

                            }else if(postion == 3){
                                _catch.put(menu_code+"third_recordcount",recordcount);
                                _catch.put(menu_code+"third_pageIndex",pageIndex);

                                _dataList.clear();
                                if (third_datalist != null){
                                    _dataList.addAll(third_datalist);
                                }

                            }else if(postion == 4){
                                _catch.put(menu_code+"fourth_recordcount",recordcount);
                                _catch.put(menu_code+"fourth_pageIndex",pageIndex);

                                _dataList.clear();
                                if (fourth_datalist != null){
                                    _dataList.addAll(fourth_datalist);
                                }
                            }

                        }else{
                            _dataList.clear();
                        }
                        //刷新视图
                        reshViewHandler.sendEmptyMessageDelayed(1,1000);
                    }
                }).start();


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
                String parms = "";
                type = "上拉";

                if (pageIndex >0){
                    if (mRecyclerView1.getAdapter().getItemCount()== 0){
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

                final String finalparms = parms;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadData(menu_code,menu_name,method,finalparms)){
                            if (resList!= null && resList.size()>0){
                                if(mRecyclerView1.getAdapter().getItemCount() <pagesize){
                                    _dataList.clear();
                                }
                                pageIndex++;
                            }

                            //刷新视图 记录数据
                            if (postion == 1){
                                _catch.put(menu_code+"main_recordcount",recordcount);
                                _catch.put(menu_code+"main_pageIndex",pageIndex);

                                _dataList.clear();
                                _dataList.addAll(main_datalist);

                            }else if(postion == 2){
                                _catch.put(menu_code+"sencond_recordcount",recordcount);
                                _catch.put(menu_code+"sencond_pageIndex",pageIndex);

                                _dataList.clear();
                                _dataList.addAll(sencond_datalist);

                            }else if(postion == 3){
                                _catch.put(menu_code+"third_recordcount",recordcount);
                                _catch.put(menu_code+"third_pageIndex",pageIndex);

                                _dataList.clear();
                                _dataList.addAll(third_datalist);

                            }else if(postion == 4){
                                _catch.put(menu_code+"fourth_recordcount",recordcount);
                                _catch.put(menu_code+"fourth_pageIndex",pageIndex);

                                _dataList.clear();
                                _dataList.addAll(fourth_datalist);
                            }

                        }else{
                            _dataList.clear();
                        }
                        //刷新视图
                        reshViewHandler.sendEmptyMessageDelayed(1,1000);
                    }
                }).start();
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

		String pars1 = StringUtils.strTOJsonstr(pars);
        int start = pars.indexOf("start:");
		Object res = ActivityController.getDataByPost(menu_code,method,pars1);
		if (ActivityController.getPostState(res.toString()) != 0){return  false;}
		try {
			if (res instanceof String){
				Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
				resMap = (Map<String, Object>) resMap.get("data");
				if (resMap.get("rows") != null){
					resList = FastJsonUtils.getResultList(FastJsonUtils.strToMap(res.toString()));
				}else if(resMap.get("list") != null){
					resList = (List<Map<String, Object>>) resMap.get("list");
				}

			}else{
				resList = FastJsonUtils.getResultList(FastJsonUtils.strToMap(res.toString()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (resList == null){
			return  false;
		}

		if (postion == 1){

			if (main_datalist == null){
				main_datalist = new ArrayList<Map<String,Object>>();
				main_datalist.addAll(_dataList);
			}

			if (type.equals("下拉") || start == -1){main_datalist.clear();}
			if(main_datalist != null){
				main_datalist.addAll(resList);
				_catch.put(menu_code+"_maindata",main_datalist);
			}
		}else if(postion == 2){

			if (sencond_datalist == null){
				sencond_datalist = new ArrayList<Map<String,Object>>();
				sencond_datalist.addAll(_dataList);
			}

			if (type.equals("下拉")){sencond_datalist.clear();}
			if(sencond_datalist != null){
				sencond_datalist.addAll(resList);
				_catch.put(menu_code+"_senconddata",sencond_datalist);
			}
		}else if(postion == 3){

			if (third_datalist == null){
				third_datalist = new ArrayList<Map<String,Object>>();
				third_datalist.addAll(_dataList);
			}

			if (type.equals("下拉")){third_datalist.clear();}
			if(third_datalist != null){
				third_datalist.addAll(resList);
				_catch.put(menu_code+"_thirddata",third_datalist);
			}
		}else if(postion == 4){

			if (fourth_datalist == null){
				fourth_datalist = new ArrayList<Map<String,Object>>();
				fourth_datalist.addAll(_dataList);
			}

			if (type.equals("下拉")){fourth_datalist.clear();}
			if(fourth_datalist != null){
				fourth_datalist.addAll(resList);
				_catch.put(menu_code+"_fourthdata",fourth_datalist);
			}
		}

		return  true;
	}

}
