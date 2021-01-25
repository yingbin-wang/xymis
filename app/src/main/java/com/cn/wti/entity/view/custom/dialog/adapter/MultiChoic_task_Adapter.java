package com.cn.wti.entity.view.custom.dialog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.page.PageDataSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiChoic_task_Adapter<T> extends BaseAdapter  implements OnItemClickListener{

	private Context mContext;
	private List<T> mObjects = new ArrayList<T>();
	private int mCheckBoxResourceID = 0;
	private boolean mBoolean[] = null;
	private LayoutInflater mInflater;
	private static PageDataSingleton _catch = PageDataSingleton.getInstance();
	private String way_type =  "";

	public MultiChoic_task_Adapter(Context context, int checkBoxResourceId) {
		init(context, checkBoxResourceId);
	}

	public MultiChoic_task_Adapter(Context context, List<T> objects, boolean[] flag, int checkBoxResourceId) {
		init(context, checkBoxResourceId);
		if (objects != null)
		{
			mObjects = objects;
			mBoolean = flag;
		}

	}

	private void init(Context context, int checkBoResourceId) {
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCheckBoxResourceID = checkBoResourceId;
	}

	public void refreshData(List<T> objects,  boolean[] flag)
	{
		if (objects != null)
		{
			mObjects = objects;
			mBoolean = flag;
		}
	}


	public void setSelectItem( boolean[] flag)
	{
		if (flag != null)
		{
			mBoolean = flag;
			notifyDataSetChanged();
		}
	}


	public boolean[] getSelectItem()
	{
		return mBoolean;
	}

	public void clear() {
		 mObjects.clear();
		 notifyDataSetChanged();
	}

	private int tempPosition= -1;

	public void reshData() {
		notifyDataSetChanged();
	}
	public int getCount() {
		return mObjects.size();
	}

	public T getItem(int position) {
		return mObjects.get(position);
	}

	public int getPosition(T item) {
		return mObjects.indexOf(item);
	}


	public long getItemId(int position) {
		return position;
	}


	public View getView(int position, View convertView, ViewGroup parent) {

		 final ViewHolder viewHolder;

		 if (convertView == null) {
			 convertView = mInflater.inflate(R.layout.choice_tasklist_item_layout, null);
			 viewHolder = new ViewHolder();
			 viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox);
			 viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			 viewHolder.jbr = (EditText_custom) convertView.findViewById(R.id.jbr);
			 viewHolder.jbr.setCode("assignee_name");
			 viewHolder.jbrxz = (ImageButton) convertView.findViewById(R.id.jbrxz);
			 convertView.setTag(viewHolder);

			 if (mCheckBoxResourceID != 0)
			 {
				 viewHolder.mCheckBox.setButtonDrawable(mCheckBoxResourceID);
			 }

		 } else {
			 viewHolder = (ViewHolder) convertView.getTag();
		 }

		final int finalpostion = position;
		 T item = getItem(position);
		 Map<String, Object> map = null;
		 final  Map<String, Object> finalmap;
		 map = (Map<String, Object>) item;
		 finalmap = map;


		if( map.get("way_type") != null){
			way_type =  map.get("way_type").toString();
		}
		String groups = "";
		if (map.get("value")!= null){
			groups = map.get("value").toString();
		}
		final String finalgroups = groups;

		viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				finalmap.put("checked",isChecked);
				if (isChecked){
					if (way_type== null || way_type.equals("")){
						tempPosition = finalpostion;
						//reshData();
					}
				}
			}
		});

		final String name = map.get("name").toString();
		viewHolder.jbr.setVisibility(View.GONE);
		viewHolder.jbrxz.setVisibility(View.GONE);

		int len = getCount();

		if(len == 1){
			viewHolder.mCheckBox.setChecked(true);
			mBoolean[position] = true;
		}

		if(way_type.equals("排它网关") && position == 0 && len == 1){
			viewHolder.mCheckBox.setEnabled(false);
			viewHolder.mCheckBox.setChecked(true);
			mBoolean[position] = true;
		}else if(way_type.equals("排它网关") && position == 0 ){
			viewHolder.mCheckBox.setChecked(true);
			mBoolean[position] = true;
		}else if(way_type.equals("并行网关") || way_type.equals("包容网关")){
			viewHolder.mCheckBox.setEnabled(false);
			viewHolder.mCheckBox.setChecked(true);
			mBoolean[position] = true;
		}else if(position == 0){
			if (tempPosition == -1){
				viewHolder.mCheckBox.setChecked(true);
			}else{
				viewHolder.mCheckBox.setChecked(tempPosition == position);
			}

		}else{
			viewHolder.mCheckBox.setEnabled(true);
			viewHolder.mCheckBox.setChecked(tempPosition == position);
			mBoolean[position] = tempPosition == position;
		}

		String assignee ="";
		if (map.get("assignee") != null){
			assignee = map.get("assignee").toString();

		}

		final String finalassignees = assignee.replaceAll(",","<dh>");

		if(assignee.indexOf(",")>=0){
			viewHolder.jbr.setVisibility(View.VISIBLE);
			viewHolder.jbrxz.setVisibility(View.VISIBLE);
		}else{
			viewHolder.jbr.setVisibility(View.VISIBLE);
			viewHolder.jbr.setEnabled(false);
			viewHolder.jbr.setText(map.get("assignee_name").toString());
		}

		viewHolder.name.setText(map.get("name").toString());

		viewHolder.jbrxz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String pars = "{pageIndex:0,start:0,limit:10,user_id:"+ AppUtils.app_username+",rycodes:"+finalassignees+"}";
				_catch.remove("process_user");
				String[] items = ActivityController.getDialogDataByPost("process","findUserByCodeslistPage",_catch,"process_user","USER_NAME_",AppUtils.limit,pars);
				List<Map<String,Object>> dataList = (List<Map<String,Object>>) _catch.get("process_user");
				Map<String,Object> parmsMap = new HashMap<String, Object>();
				parmsMap.put("assignee","USER_ID_");
				parmsMap.put("assignee_name","USER_NAME_");
				ActivityController.showDialog(mContext,"流程用户",items,viewHolder.jbr,dataList,finalmap,parmsMap);
			}
		});

		viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				finalmap.put("checked",isChecked);
				if (isChecked){
					if (way_type== null || way_type.equals("")){
						tempPosition = finalpostion;
						reshData();
					}
				}
			}
		});

		return convertView;
	}

	public static class ViewHolder
	{
		public CheckBox mCheckBox;
		public TextView name;
		public EditText_custom jbr;
		public ImageButton jbrxz;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		mBoolean[position] = !mBoolean[position];
		notifyDataSetChanged();
	}

	public String getWay_type(){
		return way_type;
	}
}
