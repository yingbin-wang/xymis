package com.cn.wti.activity.tab;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.db.ReflectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CwglActivity extends AbstractActivity {

	private Map<String,Object> map;

	public CwglActivity(){
		init();
	}

	public void init(){
		lv_listMap = new ArrayList<Map<String,Object>>();
		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.zzd);
		map.put("text","暂支单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.bxzfsqd);
		map.put("text","报销支付申请单");
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
				case "暂支单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.cwgl.zzd.ZzdActivity");
					break;
				case "报销支付申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.cwgl.bxzfsqd.BxzfsqdActivity");
					break;
				default:
					break;
			}
			intent.setClass(getApplicationContext(),testClass);
			startActivity(intent);
		}
	}

}
