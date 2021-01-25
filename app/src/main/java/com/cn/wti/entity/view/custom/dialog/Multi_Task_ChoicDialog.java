package com.cn.wti.entity.view.custom.dialog;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cn.wti.entity.view.custom.dialog.adapter.MultiChoicAdapter;
import com.cn.wti.entity.view.custom.dialog.adapter.Utils;
import com.wticn.wyb.wtiapp.R;

public class Multi_Task_ChoicDialog extends AbstractChoickDialog{

	private MultiChoicAdapter<String> mMultiChoicAdapter;
	private RelativeLayout rel_select;

	public Multi_Task_ChoicDialog(Context context, List<String> list, boolean[] flag) {
		super(context, list);
		rel_select = (RelativeLayout) findViewById(R.id.rel_select);
		rel_select.setVisibility(View.GONE);
		initData(flag);
	}
	

	protected void initData(boolean flag[]) {

		mMultiChoicAdapter = new MultiChoicAdapter<String>(mContext, mList, flag, R.drawable.selector_checkbox1);
		
		mListView.setAdapter(mMultiChoicAdapter);
		mListView.setOnItemClickListener(mMultiChoicAdapter);   
		
		Utils.setListViewHeightBasedOnChildren(mListView);

	}


	public boolean[] getSelectItem()
	{
		return mMultiChoicAdapter.getSelectItem();
	}
	
}
