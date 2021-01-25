package com.dina.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.wticn.wyb.wtiapp.R;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.IListItem;


public class UIListTableView_MyTask extends UIListTableView {

	private View action;

	public UIListTableView_MyTask(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
	public void commit() {
		mIndexController = 0;

		if(mItemList.size() > 0) {
			BasicItem item ;
			for(IListItem obj : mItemList) {

				View tempItemView = null;
				tempItemView = mInflater.inflate(R.layout.list_item_middle_mytask, null);
				slideView = (HorizontalScrollView) tempItemView.findViewById(R.id.hsv);
				action = tempItemView.findViewById(R.id.ll_action);
				layoutContent = (LinearLayout) tempItemView.findViewById(R.id.itemContainer);
				setupItem(tempItemView, obj, mIndexController);
				tempItemView.setClickable(obj.isClickable());
				ViewGroup.LayoutParams lp = layoutContent.getLayoutParams();
				lp.width = screenWidth;
				tempItemView.setOnTouchListener(new MyOnTouchListener(slideView,action));
				mListContainer.addView(tempItemView);
				mIndexController++;
			}
		}
	}
}
