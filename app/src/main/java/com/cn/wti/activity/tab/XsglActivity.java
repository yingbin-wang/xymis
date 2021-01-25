package com.cn.wti.activity.tab;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.db.ReflectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XsglActivity extends AbstractActivity {

	private Map<String,Object> map;

	public XsglActivity(){
		init();
	}

	public void init(){
		lv_listMap = new ArrayList<Map<String,Object>>();
		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.saleorder);
		map.put("text","销售订单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.orderdiscount);
		map.put("text","销售订单折扣");
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
				case "销售订单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.xsgl.xsdd.XsddActivity");
					break;
				case "销售订单折扣":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.xsgl.xsddzk.XsddzkActivity");
					break;
				default:
					break;
			}
			intent.setClass(getApplicationContext(),testClass);
			startActivity(intent);
		}
	}

}
