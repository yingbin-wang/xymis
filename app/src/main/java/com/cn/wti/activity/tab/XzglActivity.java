package com.cn.wti.activity.tab;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.db.ReflectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XzglActivity extends AbstractActivity {

	private Map<String,Object> map;

	public XzglActivity(){
		init();
	}

	public void init(){
		lv_listMap = new ArrayList<Map<String,Object>>();
		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.leaveorder);
		map.put("text","请假申请单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.publictravelapply);
		map.put("text","公出申请单");
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
				case "请假申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.rsgl.qjsqd.QjsqdActivity");
					break;
				case "公出申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.rsgl.gcsqd.GcsqdActivity");
					break;
				default:
					break;
			}
			intent.setClass(getApplicationContext(),testClass);
			startActivity(intent);
		}
	}

}
