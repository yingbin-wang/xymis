package com.cn.wti.entity.view.custom.dialog.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.alibaba.fastjson.JSONObject;
import com.wticn.wyb.wtiapp.R;

public class SingleChoicAdapter<T> extends BaseAdapter implements
		OnItemClickListener {

	private Context mContext;
	private List<Map<String,Object>> mObjects = new ArrayList<Map<String,Object>>();
	private List<Map<String,Object>> resList_;
	private int mCheckBoxResourceID = 0,layoutid = 0;
	private int mSelectItem = 0;
	private ImageButton mButtonOK;
	private String keys;

	private LayoutInflater mInflater;

	public SingleChoicAdapter(Context context, int checkBoxResourceId) {
		init(context, checkBoxResourceId);
	}

	public SingleChoicAdapter(Context context, List<Map<String,Object>> objects,
			int checkBoxResourceId) {
		init(context, checkBoxResourceId);
		if (objects != null) {
			mObjects.clear();
			mObjects.addAll(objects);
		}
	}

	public SingleChoicAdapter(Context context,List<Map<String,Object>> objects,int checkBoxResourceId, ImageButton mButtonOK,String keys) {
		init(context, checkBoxResourceId);
		if (objects != null) {
			mObjects.clear();
			mObjects.addAll(objects);
		}
		this.mButtonOK = mButtonOK;
		this.keys = keys;

	}

	private void init(Context context, int checkBoResourceId) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCheckBoxResourceID = checkBoResourceId;
	}

	public void refreshData(List<Map<String,Object>> objects) {
		if (objects != null) {
			mObjects.clear();
			mObjects.addAll(objects);
			setSelectItem(0);
		}
	}

	public void setSelectItem(int selectItem) {
		if (selectItem >= 0 && selectItem <= mObjects.size()) {
			mSelectItem = selectItem;
			notifyDataSetChanged();
		}

	}

	public int getSelectItem() {
		return mSelectItem;
	}

	public Map<String,Object> getSelectObject() {
		return (Map<String, Object>) mObjects.get(mSelectItem);
	}

	public void clear() {
		mObjects.clear();
		notifyDataSetChanged();
	}

	public int getCount() {
		return mObjects.size();
	}

	public Map<String,Object> getItem(int position) {
		return mObjects.get(position);
	}

	public int getPosition(T item) {
		return mObjects.indexOf(item);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			if (layoutid ==0){
				convertView = mInflater.inflate(R.layout.choice_list_item_layout,null);
			}else{
				convertView = mInflater.inflate(layoutid,null);
			}

			viewHolder = new ViewHolder();
			viewHolder.text1 = (TextView) convertView.findViewById(R.id.text1);
			viewHolder.text2 = (TextView) convertView.findViewById(R.id.text2);
			viewHolder.text3 = (TextView) convertView.findViewById(R.id.text3);
			viewHolder.text4 = (TextView) convertView.findViewById(R.id.text4);
			viewHolder.h1 = (RelativeLayout) convertView.findViewById(R.id.h1);
			viewHolder.h2 = (RelativeLayout) convertView.findViewById(R.id.h2);

			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox);
			convertView.setTag(viewHolder);

			if (mCheckBoxResourceID != 0) {
				viewHolder.mCheckBox.setButtonDrawable(mCheckBoxResourceID);
			}

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.mCheckBox.setChecked(mSelectItem == position);

		Map<String,Object> item = getItem(position);
		int i=0;
		if(item != null){
			if (item instanceof JSONObject){
				Map<String,Object> map = (Map<String, Object>) item;
				String val = "";

				if (keys.indexOf("~")>=0){

					String _col = "",col_val="";
					for (String key:keys.split("~") ) {
						if (key.indexOf("[")>=0 && key.indexOf("]")>=0){
							_col = key.substring(key.indexOf("[")+1,key.indexOf("]"));
						}else{
							_col = key;
						}
						if (map.get(_col) != null){
							col_val = map.get(_col).toString();
							if (key.indexOf("[")>=0 && key.indexOf("]")>=0){
								col_val = key.substring(0,key.indexOf("["))+col_val+key.substring( key.indexOf("]")+1);
							}


							if (i==0){
								viewHolder.text1.setText(col_val);
							}else if(i == 1){
								viewHolder.text2.setText(col_val);
							}else if(i == 2){
								viewHolder.text3.setText(col_val);
							}else if(i == 3){
								viewHolder.text4.setText(col_val);
							}
						}else{
							if (i==0){
								viewHolder.text1.setVisibility(View.GONE);
							}else if(i == 1){
								viewHolder.text2.setVisibility(View.GONE);
							}else if(i == 2){
								viewHolder.text3.setVisibility(View.GONE);
							}else if(i == 3){
								viewHolder.text4.setVisibility(View.GONE);
							}
						}
						i++;
					}

					if (keys.split("~").length <= 2){
						viewHolder.h2.setVisibility(View.GONE);
					}

				}else{
					if (map.get(keys) != null){
						viewHolder.text1.setText(map.get(keys).toString());
					}

					viewHolder.text2.setVisibility(View.GONE);
					viewHolder.text3.setVisibility(View.GONE);
					viewHolder.text4.setVisibility(View.GONE);

				}

			}else if (item instanceof Map){
				Map<String,Object> map = (Map<String, Object>) item;
				//viewHolder.mTextView.setText(map.get(keys).toString());
				String val = "";
				i=0;
				if (keys.indexOf("~")>=0){

					for (String key:keys.split("~") ) {
						if (map.get(key) != null){
							if (i==0){
								viewHolder.text1.setText(map.get(key).toString());
							}else if(i == 1){
								viewHolder.text2.setText(map.get(key).toString());
							}
						}
						i++;
					}

					if (keys.split("~").length <= 2){
						viewHolder.h2.setVisibility(View.GONE);
					}

				}else{
					viewHolder.text1.setText(map.get(keys).toString());
					viewHolder.text2.setVisibility(View.GONE);
					viewHolder.h2.setVisibility(View.GONE);
				}
			}
		}
		return convertView;
	}

	public List<Map<String, Object>> getResList_() {
		return resList_;
	}

	public void setResList_(List<Map<String, Object>> resList_) {
		this.resList_ = resList_;
	}

	public int getLayoutid() {
		return layoutid;
	}

	public void setLayoutid(int layoutid) {
		this.layoutid = layoutid;
	}

	public static class ViewHolder {
		public TextView text1,text2,text3,text4;
		public CheckBox mCheckBox;
		public RelativeLayout h1,h2;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position != mSelectItem) {
			mSelectItem = position;
			notifyDataSetChanged();
		}
		mButtonOK.performClick();
	}

}
