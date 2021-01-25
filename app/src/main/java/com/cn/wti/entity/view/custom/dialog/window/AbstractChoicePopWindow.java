package com.cn.wti.entity.view.custom.dialog.window;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.view.custom.AdpaterListView_custom.ListViewForScrollView;
import com.cn.wti.entity.view.pulltorefresh.ListViewListener;
import com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout;
import com.cn.wti.util.app.ActivityController;
import com.wticn.wyb.wtiapp.R;

public abstract class AbstractChoicePopWindow<T> implements OnClickListener {

	protected Context mContext;
	protected View mParentView,currentview;

	protected ScrollView mScrollView;
	protected ImageButton mButtonOK,mButtonCancel;
	protected ListView mListView;
	protected LinearLayout l_1,btn_layout;

	protected PopupWindow mPopupWindow;
	protected List<Map<String,Object>> mList;
	protected OnClickListener mOkListener,mShowListener;

	protected  PullToRefreshLayout refreshLayout;

	private ImageButton back_btn;
	//查询 框
	protected EditText editText_cs;
	protected String service_name, method_name, pars,key;
	protected int recordcount,pageIndex;
	protected  TextView title;
	private ImageButton btnShow;

	private String title_name;

	private int layout_id;

	public String getService_name() {
		return service_name;
	}

	public void setService_name(String service_name) {
		this.service_name = service_name;
	}

	public String getMethod_name() {
		return service_name;
	}

	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}

	public String getPars() {
		return pars;
	}

	public void setPars(String pars) {
		this.pars = pars;
	}

	public AbstractChoicePopWindow(Context context, View parentView,List<Map<String,Object>> list,//存储数据集
								    String service_name,String method_name,String pars,String key //查询服务器参数
								    ){
		mContext = context;
		mParentView = parentView;
		mList = list;

		this.service_name = service_name;
		this.method_name = method_name;
		this.pars = pars;
		this.key = key;
		initView(mContext);
	}

	public AbstractChoicePopWindow(Context context, View parentView,List<Map<String,Object>> list){
		mContext = context;
		mParentView = parentView;
		mList = list;
		initView(mContext);
	}

	public AbstractChoicePopWindow(Context context, View parentView,List<Map<String,Object>> list,int layout_id){
		mContext = context;
		mParentView = parentView;
		mList = list;
		this.layout_id = layout_id;
		initView(mContext);
	}

	protected void initView(Context context) {

		if (layout_id == 0){
			currentview = LayoutInflater.from(context).inflate(
					R.layout.popwindow_listview_layout, null);
		}else{
			currentview = LayoutInflater.from(context).inflate(layout_id, null);
		}
		mScrollView = (ScrollView) currentview.findViewById(R.id.scrollView);
		mButtonOK = (ImageButton) currentview.findViewById(R.id.btnOK);
		mButtonOK.setOnClickListener(this);
		mButtonCancel = (ImageButton) currentview.findViewById(R.id.btnCancel);
		mButtonCancel.setOnClickListener(this);

		mPopupWindow = new PopupWindow(currentview, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mPopupWindow.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00);
		mPopupWindow.setBackgroundDrawable(dw);

		if (currentview.findViewById(R.id.listView) instanceof ListView){
			mListView = (ListView) currentview.findViewById(R.id.listView);
		}

		editText_cs = (EditText) currentview.findViewById(R.id.showcs);
		if (editText_cs !=  null){
			editText_cs.setOnEditorActionListener(mEditTextClickListener);
		}
		btnShow = (ImageButton) currentview.findViewById(R.id.btnShow);

		if (btnShow != null){
			btnShow.setOnClickListener(this);
		}

		//搜索 栏
		l_1 = (LinearLayout) currentview.findViewById(R.id.l_1);

		refreshLayout = (PullToRefreshLayout) currentview.findViewById(R.id.refresh_view);

		title = (TextView) currentview.findViewById(R.id.title);

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

	public void show(boolean bShow) {

		if (bShow) {
			/*mScrollView.scrollTo(0, 0);*/
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

	public int getLayout_id() {
		return layout_id;
	}

	public void setLayout_id(int layout_id) {
		this.layout_id = layout_id;
	}

	public String getTitle_name() {
		return title_name;
	}

	public void setTitle_name(String title_name) {
		this.title_name = title_name;
	}

	private TextView.OnEditorActionListener mEditTextClickListener = new TextView.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

			if(actionId == EditorInfo.IME_ACTION_SEARCH){
				/*隐藏软键盘*/
				InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				//执行搜索动作
				serachAction();
				return true;
			}
			return false;
		}
	};

	public void serachAction(){
		btnShow.performClick();
	}
}
