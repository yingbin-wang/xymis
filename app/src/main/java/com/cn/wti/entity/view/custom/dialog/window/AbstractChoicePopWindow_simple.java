package com.cn.wti.entity.view.custom.dialog.window;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.wticn.wyb.wtiapp.R;

import java.util.List;

public abstract class AbstractChoicePopWindow_simple<T> implements OnClickListener {

	protected Context mContext;
	protected View mParentView;

	protected ScrollView mScrollView;
	protected TextView mTVTitle;
	protected ImageButton mButtonOK;
	protected ImageButton mButtonCancel;
	protected ImageButton mButtonShow;

	protected PopupWindow mPopupWindow;
	protected List<T> mList;
	protected OnClickListener mOkListener,mShowListener;

	public AbstractChoicePopWindow_simple(Context context, View parentView, List<T> list){
		mContext = context;
		mParentView = parentView;
		mList = list;
		initView(mContext);
	}

	protected void initView(Context context) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.popwindow_xclbb_dialog, null);
		mScrollView = (ScrollView) view.findViewById(R.id.scrollView);
		mTVTitle = (TextView) view.findViewById(R.id.tvTitle);
		mButtonOK = (ImageButton) view.findViewById(R.id.btnOK);
		mButtonOK.setOnClickListener(this);
		mButtonCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		mButtonCancel.setOnClickListener(this);

		mButtonShow = (ImageButton) view.findViewById(R.id.btnShow);
		mButtonShow.setOnClickListener(this);

		mPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mPopupWindow.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00);
		mPopupWindow.setBackgroundDrawable(dw);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnOK:
			onButtonOK(v);
			break;
		case R.id.btnShow:
			onButtonShow(v);
			break;
		case R.id.btnCancel:
			onButtonCancel(v);
			break;
		}
	}

	public void setOnOKButtonListener(OnClickListener onClickListener) {
		mOkListener = onClickListener;
	}

	public void setTitle(String title) {
		mTVTitle.setText(title);
	}

	public void show(boolean bShow) {

		if (bShow) {
			mScrollView.scrollTo(0, 0);
			mPopupWindow.showAtLocation(mParentView, Gravity.TOP, 0, 0);
		} else {
			mPopupWindow.dismiss();
		}
	}

	protected void onButtonOK(View v) {
		show(false);

		if (mOkListener != null) {
			mOkListener.onClick(v);
		}
	}

	protected void onButtonShow(View v) {

		if (mShowListener != null) {
			mShowListener.onClick(v);
		}
	}

	protected void onButtonCancel(View v) {
		show(false);
	}

	public void setmShowListener(OnClickListener mShowListener) {
		this.mShowListener = mShowListener;
	}
}
