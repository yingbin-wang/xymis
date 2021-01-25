package com.cn.wti.entity.view.custom.dialog;

import java.util.List;

import android.content.Context;

import com.cn.wti.entity.view.custom.dialog.adapter.SingleChoicAdapter;
import com.cn.wti.entity.view.custom.dialog.adapter.Utils;
import com.wticn.wyb.wtiapp.R;

public class SingleChoiceDialog extends AbstractChoickDialog {

	private SingleChoicAdapter<String> mSingleChoicAdapter;

	public SingleChoiceDialog(Context context, List<String> list) {
		super(context, list);

		initData();
	}

	protected void initData() {
		/*mSingleChoicAdapter = new SingleChoicAdapter<String>(mContext, mList,
				R.drawable.selector_checkbox2);*/

		mListView.setAdapter(mSingleChoicAdapter);
		mListView.setOnItemClickListener(mSingleChoicAdapter);

		Utils.setListViewHeightBasedOnChildren(mListView);

	}

	public int getSelectItem() {
		return mSingleChoicAdapter.getSelectItem();
	}

}
