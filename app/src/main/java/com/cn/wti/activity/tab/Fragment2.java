package com.cn.wti.activity.tab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cn.wti.util.db.WebServiceHelper;
import com.cn.wti.util.other.DateUtil;
import com.dina.ui.widget.UIListTableView;
import com.dina.ui.widget.UITableView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.EMAConversation;
import com.hyphenate.chat.adapter.message.EMATextMessageBody;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.MyGridAdapter;
import com.cn.wti.util.app.MyGridView;
import com.cn.wti.util.db.ReflectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Fragment2 extends Fragment {
	private View rootView;
	private UIListTableView listTableView;
	private List<Map<String,String>> _dataList = new ArrayList<Map<String,String>>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.common_mesage_list, null);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if(parent != null){
			parent.removeView(rootView);
		}
		reshView();
		return rootView;
	}

	public void reshView(){
		listTableView = (UIListTableView) rootView.findViewById(R.id.tableView);
		Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
		Set<String> sets =  conversations.keySet();
		String msg = "",name,count;

		Map<String,String> map = null;

		for (String key:sets) {
			EMConversation ema = conversations.get(key);
			count = String.valueOf(ema.getAllMsgCount());
			EMMessage message =  ema.getLastMessage();
			name = message.getUserName();
			Long time = message.getMsgTime();
			String date = DateUtil.stampToDate(String.valueOf(time));
			Object body = message.getBody();
			if (body instanceof EMTextMessageBody){
				EMTextMessageBody textBody = (EMTextMessageBody) body;
				msg = textBody.getMessage();
			}
			map = new HashMap<String, String>();
			map.put("pic",String.valueOf(R.mipmap.app_aapay));
			map.put("from",name);
			map.put("time",date);
			map.put("msg",msg);
			map.put("count",count);
			_dataList.add(map);
		}
	}

	
}
