package com.cn.wti.entity.view.custom.dialog.window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.cn.wti.entity.view.custom.dialog.adapter.MultiChoic_task_Adapter;
import com.cn.wti.entity.view.custom.dialog.adapter.Utils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;

public class MultiChoicePopWindow <T> extends AbstractChoicePopWindow{

	private MultiChoic_task_Adapter<Object> mMultiChoicAdapter;
	private List<Map<String,Object>> activitiMapList = new ArrayList<Map<String,Object>>();
	
	public MultiChoicePopWindow(Context context, View parentView, List<Object>_list, boolean flag[],
								String service_name, String method_name, String pars, String key)
	{
		super(context, parentView, _list,service_name,method_name,pars,key);
		initData(flag);
	}

	public MultiChoicePopWindow(Context context, View parentView, List<T>_list, boolean flag[])
	{
		super(context, parentView, _list);
		l_1.setVisibility(View.GONE);
		initData(flag);
	}

	protected void initData(boolean flag[]) {

		mMultiChoicAdapter = new MultiChoic_task_Adapter<Object>(mContext, mList, flag, R.drawable.selector_checkbox1);
		mListView.setAdapter(mMultiChoicAdapter);
		mListView.setOnItemClickListener(mMultiChoicAdapter);
		Utils.setListViewHeightBasedOnChildren(mListView);

		setOnOKButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean[] items = getSelectItem();
				if(items != null && items.length >0){
					for (int i=0,n=items.length;i<n;i++){
						if(items[i]){
							Object res = mList.get(i);
							Map<String,Object>resMap = (Map<String, Object>) res;
							if("".equals(resMap.get("assignee").toString())){
								Toast.makeText(mContext,"当前任务没有经办人",Toast.LENGTH_SHORT).show();
								return;
							}
							activitiMapList.add((Map<String, Object>) res);
						}
					}

					String activitis = "";

					if (activitiMapList.size()>0){
						activitis = FastJsonUtils.ListMapToListStr(activitiMapList);
						pars += "activitis:"+activitis;
						pars = StringUtils.strTOJsonstr(pars);
					}

					ActivityController.execute(mContext,service_name,method_name,pars);
				}
			}
		});
	}

	public boolean[] getSelectItem()
	{
		return mMultiChoicAdapter.getSelectItem();
	}

	@Override
	protected void onButtonOK(View v) {
		if (mOkListener != null) {
			mOkListener.onClick(v);
		}
	}
}
