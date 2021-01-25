package com.cn.wti.entity.view.custom.dialog.window;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cn.wti.entity.view.custom.dialog.adapter.SingleChoicAdapter;
import com.cn.wti.util.db.FastJsonUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class AbstractChoicePopWindow_simple2 extends PopupWindow implements OnClickListener {
	protected View mPopView;
	protected OnItemClickListener mListener;
	protected SingleChoicAdapter<String> mSingleChoicAdapter;
	protected List<String> mList;
	protected ListView mListView;
	protected ImageButton mButtonOK,btnCancel;
	protected OnClickListener mOkListener;
	protected TextView title;

	private int layout_ = 0;

	public AbstractChoicePopWindow_simple2(Context context) {
		super(context);
		/*setPopupWindow();*/
	}

	public AbstractChoicePopWindow_simple2(Context context,List<String> mList) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mList = mList;
		init(context);
		setPopupWindow();

	}

	/**
	 * 初始化
	 *
	 * @param context
	 */
	public void init(Context context) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		//绑定布局
		if (layout_ == 0){
			mPopView = inflater.inflate(R.layout.popwindow_select_dialog, null);
		}else{
			mPopView = inflater.inflate(layout_, null);
		}

		mButtonOK = (ImageButton) mPopView.findViewById(R.id.btnOK);
		mButtonOK.setOnClickListener(this);
		btnCancel = (ImageButton) mPopView.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);

		mSingleChoicAdapter = new SingleChoicAdapter<String>(context, FastJsonUtils.listStringToListMap(mList),
				R.drawable.selector_checkbox2,mButtonOK,"name");
		mListView = (ListView) mPopView.findViewById(R.id.listView);
		mListView.setAdapter(mSingleChoicAdapter);
		mListView.setOnItemClickListener(mSingleChoicAdapter);

		//标题
		title = (TextView) mPopView.findViewById(R.id.title);

	}

	public int getSelectItem() {
		return mSingleChoicAdapter.getSelectItem();
	}

	public String getTxsj(){;
		return  "";
	}

	/**
	 * 设置窗口的相关属性
	 */
	public void setPopupWindow() {
		this.setContentView(mPopView);// 设置View
		this.setWidth(LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
		this.setHeight(LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
		this.setFocusable(true);// 设置弹出窗口可
		this.setAnimationStyle(R.style.dialog_animation);// 设置动画
		this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
		/*mPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int height = mPopView.findViewById(R.id.rel_select).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});*/
	}

	public void setmOkListener(OnClickListener mOkListener) {
		this.mOkListener = mOkListener;
	}

	/**
	 * 定义一个接口，公布出去 在Activity中操作按钮的单击事件
	 */
	public interface OnItemClickListener {
		void setOnItemClick(View v);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnOK:
				onButtonOK(v);
				break;
			case R.id.btnCancel:
				dismiss();
				break;
			default:
				break;
		}
	}

	protected void onButtonOK(View v) {
		show(false);

		if (mOkListener != null) {
			mOkListener.onClick(v);
		}
	}

	public void show(boolean bShow) {

		if (bShow) {
		} else {
			dismiss();
		}
	}

	public void setLayout_(int layout_) {
		this.layout_ = layout_;
	}
}
