package com.cn.wti.activity.tab;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.db.ReflectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KhglActivity extends AbstractActivity {

	private Map<String,Object> map;

	public KhglActivity(){
		init();
	}

	public void init(){
		lv_listMap = new ArrayList<Map<String,Object>>();
		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.khcysqd);
		map.put("text","客户出样申请单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.priceprotectapply);
		map.put("text","价保申请单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.monthlyrefundapply);
		map.put("text","月度返款申请");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.achievesalesrefundapply);
		map.put("text","销量达标奖励申请单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.customergiftapply);
		map.put("text","客户赠品申请单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.marketingsupport);
		map.put("text","客户市场支持申请单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.customercreditapply);
		map.put("text","客户信用申请单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.accrecievedelayapply);
		map.put("text","应收延期回款申请单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.outofcreditapply);
		map.put("text","超信用发货申请单");
		lv_listMap.add(map);

		map = new HashMap<String,Object>();
		map.put("img",R.mipmap.clientpriceadj);
		map.put("text","客户价格调整");
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
				case "客户出样申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.khcysqd.KhcysqdActivity");
					break;
				case "价保申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.jbsqd.JbsqdActivity");
					break;
				case "月度返款申请":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.ydfksqd.YdfksqdActivity");
					break;
				case "销量达标奖励申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.xldbjlsqd.XldbjlsqdActivity");
					break;
				case "客户赠品申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.khzpsqd.KhzpsqdActivity");
					break;
				case "客户市场支持申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.khsczcsqd.KhsczcsqdActivity");
					break;
				case "客户信用申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.khxysqd.KhxysqdActivity");
					break;
				case "应收延期回款申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.ysyqhksqd.YsyqhksqdActivity");
					break;
				case "超信用发货申请单":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.cxyfhsqd.CxyfhsqdActivity");
					break;
				case "客户价格调整":
					testClass = ReflectHelper.getCalss("com.cn.wti.activity.khgl.khjgtzd.KhjgtzdActivity");
					break;
				default:
					break;
			}
			intent.setClass(getApplicationContext(),testClass);
			startActivity(intent);
		}
	}

}
