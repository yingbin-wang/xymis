package com.cn.wti.activity.tab;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.cn.wti.activity.MenuActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.other.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFragment extends AbstractFragment {

	private Map<String,Object> map,_childMap = new HashMap<String, Object>();
	private ExpandableListView expandableListView;
	private List<String> selectedList = new ArrayList<String>();
	private List<String> menu_nameList = new ArrayList<String>();
	private LayoutInflater inflater;
	private List<Map<String,Object>> menuList;
	private LinearLayout ts_llt,testView;
	public ReportFragment(){}
	private Dialog mDialog = null;
	private ImageView tstb = null;
	private TextView tsxx= null;
	MyExpandableListAdapter adapter= null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if(rootView == null){
			rootView = inflater.inflate(R.layout.activity_report_menu, null);
			expandableListView = (ExpandableListView)rootView.findViewById(R.id.men_moreList);
			this.inflater = inflater;
			adapter =  new MyExpandableListAdapter();
			expandableListView.setAdapter(adapter);
			//刷新数据
			refreshData();
		}

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void init(){

		if (AppUtils.fwq_reportmenuList == null || AppUtils.fwq_reportmenuList.size() ==0){
			String pars = "{user_name:"+ AppUtils.app_username+"}";
			Object res = ActivityController.getData4ByPost("mobilerole","findMenuQxByUserId", StringUtils.strTOJsonstr(pars));
			if (res != null && !res.toString().contains("(abcdef)")){
				lv_listMap = new ArrayList<Map<String,Object>>();
				if (res != null && !res.equals("")){
					if (res instanceof JSONArray){
						List<Map<String,Object>> _list = (List<Map<String, Object>>) res;
						if (_list != null && _list.size() >0){
							Map<String,Object> map = _list.get(0);
							if (map.get("children") != null){
								if (map.get("children") instanceof  String){
									try{
										String children = map.get("children").toString().replace("\\","");
										AppUtils.fwq_reportmenuList = FastJsonUtils.getBeanMapList(children);
									}catch (Exception e){
										e.printStackTrace();
									}

								}else{
									AppUtils.fwq_reportmenuList = (List<Map<String, Object>>) map.get("children");
								}

							}
						}else{
							AppUtils.fwq_reportmenuList = new ArrayList<Map<String, Object>>();
						}

					}
				}
			}else{

				if (res!= null){
					final String finalres = res.toString();
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getActivity(),finalres.replace("(abcdef)",""),Toast.LENGTH_LONG).show();
							ts_llt = (LinearLayout) rootView.findViewById(R.id.ts_llt);
							tstb = (ImageView) rootView.findViewById(R.id.tstb);
							tsxx = (TextView) rootView.findViewById(R.id.tsxx);
							testView = (LinearLayout) rootView.findViewById(R.id.testView);
							if (AppUtils.fwq_reportmenuList == null ||  AppUtils.fwq_reportmenuList .size() == 0){
								ts_llt.setVisibility(View.VISIBLE);
								if (finalres.contains("网络") || finalres.contains("服务器")){
									tstb.setBackgroundResource(R.mipmap.wuwangluo);
									tsxx.setText(finalres.replace("(abcdef)",""));
								}
								testView.setVisibility(View.GONE);
							}else{
								testView.setVisibility(View.VISIBLE);
								ts_llt.setVisibility(View.GONE);
							}

							if(mDialog != null){
								WeiboDialogUtils.closeDialog(mDialog);
							}
						}
					});
				}

			}
		}
	}


	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		//设置组视图的图片
		int[] logos = null;
		//设置组视图的显示文字
		private String[] category = null;
		//子视图显示文字
		private String[][] subcategory = null;
		//子视图图片
		public int[][] sublogos = null;

		public MyExpandableListAdapter(){
			setMenu();
		}

		@Override
		public int getGroupCount() {
			if (category == null){
				return  0;
			}else{
				return category.length;
			}
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (subcategory != null && subcategory.length >= groupPosition && subcategory[groupPosition] != null){
				return subcategory[groupPosition].length;
			}else{
				return 0;
			}
		}

		@Override
		public Object getGroup(int groupPosition) {
			return category[groupPosition];
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return subcategory[groupPosition][childPosition];
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

			View view = inflater.inflate(R.layout.menu_listview_group,null);
			TextView tv1 = (TextView)view.findViewById(R.id.menu_group_name);
			tv1.setText(category[groupPosition]);
			return  view;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

			View view = inflater.inflate(R.layout.menu_listview_item,null);
			ImageView iv1 = (ImageView) view.findViewById(R.id.menu_item_logo);
			iv1.setImageResource(sublogos[groupPosition][childPosition]);

			final TextView tv1 = (TextView)view.findViewById(R.id.menu_item_name);
			tv1.setText(subcategory[groupPosition][childPosition]);
			if (_childMap != null && _childMap.get(String.valueOf(groupPosition)+"_"+String.valueOf(childPosition))!= null){
				Map<String,Object> map = (Map<String, Object>) _childMap.get(String.valueOf(groupPosition)+"_"+String.valueOf(childPosition));
				view.setOnClickListener(new MyOnClickLinstener(tv1.getText().toString(),map));
			}

			return  view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		private  boolean setMenu(){
			menuList = AppUtils.fwq_reportmenuList;

			if (menuList != null){
				int i = 0,j=0,menu_size= 0;
				JSONArray jay1 = null;
				List<Map<String,Object>> list = null;
				menu_size = menuList.size();
				logos = new int[menu_size];
				category = new String[menu_size];

				sublogos = new int[menu_size][];
				subcategory = new String[menu_size][];

				for (Map<String,Object> map1:menuList) {
					j = 0;
					if(map1.get("children")!= null){
						list = (List<Map<String,Object>>)map1.get("children");

						//添加组图标
						logos[i] = AppUtils.getPic(map1.get("menu_icon"));
						//添加组文字
						category[i] = map1.get("text").toString();

						subcategory[i] = new String[list.size()];
						sublogos[i] = new int[list.size()];

						for (Map<String,Object> map2:list) {
							//添加子视图文本
							subcategory[i][j] = map2.get("text").toString();
							sublogos[i][j] = AppUtils.getPic(map2.get("menu_icon"));
							_childMap.put(String.valueOf(i)+"_"+String.valueOf(j),map2);
							j++;
						}
					}else{

						if (logos != null && category != null){
							logos = FastJsonUtils.removeIntByIndex(logos,i);

							List<String> categorys = FastJsonUtils.arrayToListStr(category);
							categorys.remove(i);
							category = FastJsonUtils.listStrToArray(categorys);
						}
					}

					i++;
				}
			}else{
				return  false;
			}
			return  true;
		}
	}

	public class MyOnClickLinstener implements View.OnClickListener{

		private String name;
		private Map<String,Object> map;

		public  MyOnClickLinstener(String name,Map<String,Object> map){
			this.name = name;
			this.map = map;
		}

		@Override
		public void onClick(View v) {
			Class testClass = null;
			Intent intent = new Intent();

			switch (name){
				case "部门销售日报":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.BmxsrbActivity");
					break;
				case "产品销售日报":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.CpxsrbActivity");
					break;
				case "现存量报表":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.Report_XclbbActivity");
					break;
				case "订单跟踪":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.Report_DdgzbActivity");
					break;
				case "应收超期预警":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.Report_YscqyjbActivity");
					break;
				case "经营月报":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.Report_JyybActivity");
					break;
				case "应收余额":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.Report_YsyebActivity");
					break;
				case "应付余额":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.Report_YfyebActivity");
					break;
				case "费用查询":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.Report_FycxbActivity");
					break;
				case "资金余额":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.report.activity.Report_AccountMoneybActivity");
					break;
				default:
					break;
			}
			intent.setClass(rootView.getContext(),testClass);
			intent.putExtras(AppUtils.setParms("",map));
			startActivity(intent);
		}
	}

	private void refreshData(){
		mDialog = WeiboDialogUtils.createLoadingDialog(getActivity(),"加载中...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				init();
				if (adapter.setMenu()){
					for(int i = 0; i < adapter.getGroupCount(); i++){
						expandableListView.expandGroup(i);
					}
				}

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ts_llt = (LinearLayout) rootView.findViewById(R.id.ts_llt);
						testView = (LinearLayout) rootView.findViewById(R.id.testView);
						if (AppUtils.fwq_reportmenuList == null ||  AppUtils.fwq_reportmenuList .size() == 0){
							ts_llt.setVisibility(View.VISIBLE);
							ImageView tstb1  = (ImageView) (getActivity().findViewById(R.id.tstb));
							tstb1.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									refreshData();
								}
							});

							testView.setVisibility(View.GONE);
						}else{
							testView.setVisibility(View.VISIBLE);
							ts_llt.setVisibility(View.GONE);
						}

						if(mDialog != null){
							WeiboDialogUtils.closeDialog(mDialog);
						}
					}
				});
			}
		}).start();
	}
	
}
