package com.cn.wti.entity.view.custom.dialog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.page.PageDataSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MultiChoic_Dn_Adapter<T> extends BaseAdapter  implements OnItemClickListener{

	 	private Context mContext;
		private List<T> mObjects = new ArrayList<T>();
		private int mCheckBoxResourceID = 0;
		private boolean mBoolean[] = null;
		private LayoutInflater mInflater;
		private static PageDataSingleton _catch = PageDataSingleton.getInstance();
		private List<String> selectList = new ArrayList<String>();
		private String keys="name";
	    private List<String>  lastSelectList = null;


	    public MultiChoic_Dn_Adapter(Context context, int checkBoxResourceId) {
	        init(context, checkBoxResourceId);
	    }

	    public MultiChoic_Dn_Adapter(Context context, List<T> objects, boolean[] flag, int checkBoxResourceId) {
	        init(context, checkBoxResourceId);
	        if (objects != null)
	    	{
	        	mObjects = objects;
	        	mBoolean = flag;
	    	}

	    }

	public MultiChoic_Dn_Adapter(Context context, List<T> objects, boolean[] flag, int checkBoxResourceId,String keys) {
		init(context, checkBoxResourceId);
		if (objects != null)
		{
			mObjects.clear();
			mObjects.addAll(objects);
			mBoolean = flag;

		}
		if (keys != null){
			this.keys = keys;
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
				mObjects.clear();
				mObjects.addAll(objects) ;
				mBoolean = flag;
				/*if (objects.size() > flag.length){
					mBoolean= new boolean[mObjects.size()];
					for(int x=0;x<flag.length;x++){
						mBoolean[x] = flag[x];
					}
					boolean[] array = new boolean[objects.size() - flag.length];
					for(int y=0;y<array.length;y++){
						mBoolean[flag.length+y]=array[y];
					}
				}else{
					mBoolean = flag;
				}*/
				notifyDataSetChanged();
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
		    	 convertView = mInflater.inflate(R.layout.choice_dn_item_layout, null);
		         viewHolder = new ViewHolder();
		         viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox);
				 viewHolder.name = (TextView) convertView.findViewById(R.id.name);
		         convertView.setTag(viewHolder);
		         
		         if (mCheckBoxResourceID != 0)
		         {
		        	 viewHolder.mCheckBox.setButtonDrawable(mCheckBoxResourceID);
		         }

		     } else {
		         viewHolder = (ViewHolder) convertView.getTag();
		     }

			/*if (position >= mBoolean.length){
				mBoolean[position] = false;
			}*/

			boolean state = false;

			T item = getItem(position);
			Map<String, Object> map = null;
			map = (Map<String, Object>) item;
			String val = "";
			if (getSelectList() != null && getSelectList().size() >0){
				val = getVal(map);
				if (getSelectList().contains(val)){
					viewHolder.mCheckBox.setChecked(true);
					//mBoolean[position] = true;
					state = true;
				}else{
					viewHolder.mCheckBox.setChecked(false);
					//mBoolean[position] = false;
					state = false;
				}
			}else{
				//viewHolder.mCheckBox.setChecked(mBoolean[position]);
				viewHolder.mCheckBox.setChecked(state);
			}

			viewHolder.name.setText(map.get("name").toString());

			return convertView;
	    }

	public List<String> getSelectList() {
		return selectList;
	}

	public List<String> getLastSelectList() {
		return lastSelectList;
	}

	public void setLastSelectList(List<String> lastSelectList) {
		this.lastSelectList = lastSelectList;
		if (lastSelectList != null && lastSelectList.size() >0){
			this.selectList = lastSelectList;
		}
	}

	public static class ViewHolder
	    {
			public CheckBox mCheckBox;
	    	public TextView name;
	    }

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			mBoolean[position] = !mBoolean[position];
			//记录选择的值
			String val="";
			if (getItem(position)!= null){
				Map<String,Object> map = (Map<String, Object>) getItem(position);
				if (!keys.equals("")){
					String[] keyArray = keys.split(",");
					for (String key:keyArray) {
						if (map.get(key)!= null){
							if (val.equals("")){
								val += map.get(key).toString();
							}else{
								val += ","+map.get(key).toString();
							}
						}
					}
				}
			}
			if (mBoolean[position]){
				if (!selectList.contains(val)){
					selectList.add(val);
				}
			}else{
				if (selectList.contains(val)){
					selectList.remove(val);
				}
			}
			notifyDataSetChanged();
		}

	private String getVal(Map<String,Object> map){
		String val = "";
		if (getSelectList() != null && getSelectList().size() >0){
			if (!keys.equals("")){
				String[] keyArray = keys.split(",");
				for (String key:keyArray) {
					if (map.get(key)!= null){
						if (val.equals("")){
							val += map.get(key).toString();
						}else{
							val += ","+map.get(key).toString();
						}
					}
				}
			}
		}
		return  val;
	}
		
}
