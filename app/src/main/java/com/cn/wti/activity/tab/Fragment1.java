package com.cn.wti.activity.tab;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.custom.menu.SlidingMenu;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.TipUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.cn.wti.util.number.BadgeUtils;
import com.cn.wti.util.other.StringUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.db.ReflectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class Fragment1 extends Fragment implements View.OnClickListener{
	private View rootView;
	private ListView lv1;
	private List<Map<String,Object>> lv_listMap = new ArrayList<Map<String,Object>>();
	private Map<String,Object> map = new HashMap<String,Object>();

	private String[] record_counts = null;
	private SlidingMenu mMenu;
	private ImageButton imb_back;
	private static PageDataSingleton _catch = PageDataSingleton.getInstance();
	Dialog mDialog = null;
	private int precount = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.home_layout, null);
			TextView title = (TextView) rootView.findViewById(R.id.main_title_name);
			title.setText("首页");

			//布局 ListView
			lv1 = (ListView) rootView.findViewById(R.id.page01_lv);
			init();
			lv1.setAdapter(new MyLisViewAdapter());

			lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					map = lv_listMap.get(position);
					String name = (String)map.get("text");

					Class testClass = null;
					Intent intent = new Intent();

					switch (name){
						case "待办流程":
							testClass = ReflectHelper.getCalss("com.cn.wti.activity.myTask.MyTaskActivity");
							break;
						case "我的申请":
							testClass = ReflectHelper.getCalss("com.cn.wti.activity.myTask.MyApplyActivity");
							break;
						case "我的任务":
							testClass = ReflectHelper.getCalss("com.cn.wti.activity.rwgl.myassignment.MyAssignmentActivity");
							break;
						case "日程安排":
							testClass = ReflectHelper.getCalss("com.cn.wti.activity.rwgl.schedule.ScheduleActivity");
							break;
						case "通知公告":
							testClass = ReflectHelper.getCalss("com.cn.wti.activity.rwgl.myannouncement.MyannouncementActivity");
							break;
						case "系统消息":
							testClass = ReflectHelper.getCalss("com.cn.wti.activity.rwgl.systemmsg.SystemmsgActivity");
							break;
						default:
							break;
					}
					if(testClass != null){
						intent.setClass(rootView.getContext(),testClass);
						startActivity(intent);
					}
				}
			});
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if(parent != null){
			parent.removeView(rootView);
		}

		imb_back = (ImageButton) rootView.findViewById(R.id.main_title_back);
		imb_back.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.main_title_back:
				mMenu = (SlidingMenu) _catch.get("menu");
				if (mMenu != null){
					mMenu.toggle();
				}
				break;
			default:
				break;
		}
	}

	public class MyLisViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return lv_listMap.size();
		}

		@Override
		public Object getItem(int position) {
			return lv_listMap.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.home_listview_item, parent, false);

			ImageView iv1 = (ImageView)convertView.findViewById(R.id.page01_lv_iv_);
			TextView tv1 = (TextView)convertView.findViewById(R.id.page01_lv_item_text_);
			TextView bar_num = (TextView)convertView.findViewById(R.id.bar_num);
			if (record_counts == null){
				return convertView;
			}
			if (record_counts[position].equals("") || record_counts[position].equals("0")){
				bar_num.setVisibility(View.GONE);
			}else{
				bar_num.setVisibility(View.VISIBLE);
				bar_num.setText(record_counts[position]);
			}

			map = lv_listMap.get(position);
			iv1.setImageResource((Integer) map.get("img"));
			tv1.setText((String)map.get("text"));

			return convertView;
		}

	}

	public void init(){

		String parms = "user_id:"+ AppUtils.user.get_id()+",type:wwc,staffid:"+AppUtils.user.get_zydnId()+
				",staff_name:"+AppUtils.user.get_zydnName()+",user_name:"+AppUtils.user.get_loginName();
		Object wwc_count = ActivityController.getData4ByPost("menu","listAllLength", StringUtils.strTOJsonstr(parms));
		if (wwc_count != null && !wwc_count.toString().contains("(abcdef)")){
			record_counts = new String[]{"","","","","",""};
			if (wwc_count instanceof JSONObject){
				Map<String,Object> wwcMap = (Map<String, Object>) wwc_count;
				record_counts[0] = wwcMap.get("process").toString();
				record_counts[1] = wwcMap.get("myapplication").toString();
				record_counts[2] = wwcMap.get("mytaska").toString();
				record_counts[3] = wwcMap.get("schedule").toString();
				record_counts[4] = wwcMap.get("myannouncement").toString();
				record_counts[5] = wwcMap.get("systemmsg").toString();
			}
			int i= 0,count=0;
			for (String record_content:record_counts){
				if (StringUtils.isNumeric(record_content)){
					if (Integer.parseInt(record_content.toString()) > 99 ){
						record_counts[i] = "99+";
					}else if(record_content.toString().equals("0")){
						record_counts[i] = "";
					}

					count += Integer.parseInt(record_content.toString());
				}else{
					record_counts[i] = "";
				}
				i++;
			}
		}else{
			record_counts = new String[]{"","","","","",""};
		}
		map.put("img",R.mipmap.taskagents);
		map.put("text","待办流程");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.myapply);
		map.put("text","我的申请");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.myrwgl);
		map.put("text","我的任务");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.schedule);
		map.put("text","日程安排");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.announcements);
		map.put("text","通知公告");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.systemmessage);
		map.put("text","系统消息");
		lv_listMap.add(map);

	}

	public void reshData() {

		String parms = "user_id:"+ AppUtils.user.get_id()+",type:wwc,staffid:"+AppUtils.user.get_zydnId()+
				",staff_name:"+AppUtils.user.get_zydnName()+",user_name:"+AppUtils.user.get_loginName();
		Object wwc_count = ActivityController.getData4ByPost("menu","listAllLength", StringUtils.strTOJsonstr(parms));
		if (wwc_count != null &&!wwc_count.toString().contains("(abcdef)")) {
			record_counts = new String[]{"", "", "", "", "", ""};
			if (wwc_count instanceof JSONObject) {
				Map<String, Object> wwcMap = (Map<String, Object>) wwc_count;
				record_counts[0] = wwcMap.get("process").toString();
				record_counts[1] = wwcMap.get("myapplication").toString();
				record_counts[2] = wwcMap.get("mytaska").toString();
				record_counts[3] = wwcMap.get("schedule").toString();
				record_counts[4] = wwcMap.get("myannouncement").toString();
				record_counts[5] = wwcMap.get("systemmsg").toString();
			}
			int i = 0,count = 0;
			for (String record_content : record_counts) {
				if (Integer.parseInt(record_content.toString()) > 99) {
					record_counts[i] = "99+";
				}
				if (StringUtils.isNumeric(record_content)){
					count += Integer.parseInt(record_content.toString());
				}
				i++;
			}

			if (count != 0 && getActivity()!= null && precount != count){
				//设置角标
				precount = count;
				TipUtils.sendBadgeNumber(getActivity(),String.valueOf(count));//设置角标显示
			}

			final MyLisViewAdapter adapter = (MyLisViewAdapter) lv1.getAdapter();
			if (getActivity()!= null){
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
						if (mDialog != null){
							WeiboDialogUtils.closeDialog(mDialog);
						}
					}
				});
			}
		}else{
			/*lv_listMap.clear();*/
			/*final MyLisViewAdapter adapter = (MyLisViewAdapter) lv1.getAdapter();*/
			if (getActivity()!= null){
				final String fialwwc_count = wwc_count.toString();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(),fialwwc_count.replace("(abcdef)",""),Toast.LENGTH_LONG).show();
						/*adapter.notifyDataSetChanged();*/
						if (mDialog != null){
							WeiboDialogUtils.closeDialog(mDialog);
						}
					}
				});
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mDialog = WeiboDialogUtils.createLoadingDialog(getActivity(),"加载中...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				reshData();
				WeiboDialogUtils.closeDialog(mDialog);
			}
		}).start();

	}
}
