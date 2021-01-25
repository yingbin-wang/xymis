package com.cn.wti.activity.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;

import java.util.List;
import java.util.Map;

public class AbstractFragment extends Fragment {
	protected View rootView;
	protected ListView lv1;
	protected List<Map<String,Object>> lv_listMap;
	private Map<String,Object> map;
	private Context mContext;
	private AdapterView.OnItemClickListener listener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.page01_layout, null);
			//布局 ListView
			lv1 = (ListView) rootView.findViewById(R.id.page01_lv);
			//init();
			lv1.setAdapter(new MyLisViewAdapter());
			lv1.setOnItemClickListener(listener);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if(parent != null){
			parent.removeView(rootView);
		}
		return rootView;
	}

	/**
	 * 设置点击事件
	 * @param listener
     */
	public void setOnClickLinstener(AdapterView.OnItemClickListener listener){
		this.listener = listener;
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
					R.layout.page01_listview_item, parent, false);

			ImageView iv1 = (ImageView)convertView.findViewById(R.id.page01_lv_iv_);
			TextView tv1 = (TextView)convertView.findViewById(R.id.page01_lv_item_text_);
			map = lv_listMap.get(position);
			iv1.setImageResource((Integer) map.get("img"));
			tv1.setText((String)map.get("text"));
			return convertView;
		}

	}

}
