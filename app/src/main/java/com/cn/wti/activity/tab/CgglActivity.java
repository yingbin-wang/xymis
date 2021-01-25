package com.cn.wti.activity.tab;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.db.ReflectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CgglActivity extends AbstractActivity {

	private Map<String,Object> map;

	public CgglActivity(){
		init();
	}

	public void init(){
		lv_listMap = new ArrayList<Map<String,Object>>();
		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.cgqgd);
		map.put("text","请购单");
		lv_listMap.add(map);


		setOnClickLinstener(new MyOnClickListener());
	}

	public class MyOnClickListener implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			map = lv_listMap.get(position);
			String name = (String)map.get("text");

			Class testClass = null;
			Intent intent = new Intent();

			switch (name){
				case "请购单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.cggl.CgqgdActivity");
					break;
				default:
					break;
			}
			intent.setClass(getApplicationContext(),testClass);
			startActivity(intent);
		}
	}

}
