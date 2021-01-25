package com.cn.wti.entity.view.custom.dialog.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cn.wti.entity.view.custom.dialog.adapter.SingleChoicAdapter;
import com.cn.wti.entity.view.custom.dialog.adapter.Utils;
import com.cn.wti.entity.view.pulltorefresh.ListViewListener;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;

public class SingleChoicePopWindow extends AbstractChoicePopWindow {

	private SingleChoicAdapter<String> mSingleChoicAdapter;
	private String[] cxcolumns;
	private List<Map<String,Object>> copyList = new ArrayList<Map<String,Object>>();
	private int item_layoutid = 0;

	/**
	 * sql 执行查询
	 * @param context
	 * @param parentView
	 * @param list
	 * @param service_name
	 * @param method_name
	 * @param pars
	 * @param key
	 * @param recordcount
     * @param pageIndex
     * @param title
     */
	public SingleChoicePopWindow(Context context, View parentView,List<Map<String,Object>> list,
								 String service_name,String method_name,String pars,String key,
								 int recordcount,int pageIndex,String title) {
		super(context, parentView, list,service_name,method_name,pars,key);

		this.recordcount = recordcount;
		this.pageIndex = pageIndex;
		if (title.equals("下拉选择")){
			LinearLayout rel_select = (LinearLayout) currentview.findViewById(R.id.rel_select);
			if (rel_select != null){ rel_select.setVisibility(View.GONE);}
		}
		this.title.setText(title);
		initData();
	}

	public SingleChoicePopWindow(Context context, View parentView,List<Map<String,Object>> list,
								 String service_name,String method_name,String pars,String key,String[] cxcolumns,
								 int recordcount,int pageIndex,String title) {
		super(context, parentView, list,service_name,method_name,pars,key);

		this.recordcount = recordcount;
		this.pageIndex = pageIndex;
		this.title.setText(title);
		this.cxcolumns = cxcolumns;
		this.copyList.clear();
		this.copyList.addAll(list);
		initData();
	}

	public View getMParentView(){
		return mParentView;
	}
	protected void initData() {

		//隐藏确定按钮
		if (mButtonOK != null){
			mButtonOK.setVisibility(View.GONE);
		}
		//20171217 wang 添加 子布局视图
		mSingleChoicAdapter = new SingleChoicAdapter(mContext,mList,R.drawable.selector_checkbox2,mButtonOK,key);
		if (item_layoutid != 0){
			mSingleChoicAdapter.setLayoutid(item_layoutid);
		}
		mListView.setAdapter(mSingleChoicAdapter);
		mListView.setOnItemClickListener(mSingleChoicAdapter);
		Utils.setListViewHeightBasedOnChildren(mListView);

		//添加 查询按钮事件
		setmShowListener(new MyClickListener());

		refreshLayout.setOnRefreshListener(new ListViewListener(mSingleChoicAdapter,mList,recordcount,pageIndex,service_name,"",method_name,pars,key));
		if (btn_layout != null){
			btn_layout.setVisibility(View.GONE);
		}

	}

	public int getTotalHeightofListView(ListView listView) {
		ListAdapter mAdapter = (ListAdapter) listView.getAdapter();
		int totalHeight = 0;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View mView = mAdapter.getView(i, null, listView);
			mView.measure(
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			totalHeight += mView.getMeasuredHeight();

		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
		int listviewHeight = params.height;

		listView.setLayoutParams(params);
		listView.requestLayout();
		return  listviewHeight;
	}

	public SingleChoicAdapter getMSingleChoicAdapter() {
		return mSingleChoicAdapter;
	}

	public int getSelectItem() {
		return mSingleChoicAdapter.getSelectItem();
	}

	public Object getSelectObject() {
		return mSingleChoicAdapter.getSelectObject();
	}

	public int getItem_layoutid() {
		return item_layoutid;
	}

	public void setItem_layoutid(int item_layoutid) {
		this.item_layoutid = item_layoutid;
	}

	public class MyClickListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()){
				case R.id.btnShow:

					String cs = editText_cs.getText().toString();
					String parms = "",css="";

					if(!cs.equals("")){
						if (!pars.equals("")){
							if (key.equals("name")){
								if (service_name.equals("staff")){
									css = key +" like 'aYdF1"+cs+"'bYdF1 or login_name like 'aYdF1"+cs+"'bYdF1";
								}else{
									css = key +" like 'aYdF1"+cs+"'bYdF1 or code like 'aYdF1"+cs+"'bYdF1";
								}

							}else if(key.indexOf("~")>=0 && (service_name.equals("customer") ||
									 service_name.equals("account") || service_name.equals("goods") ||
									 service_name.equals("goodsType") || service_name.equals("department"))){
								String[] keys = key.split("~");
								for (String key_str:keys) {
									if (css.equals("")){
										css += key_str +" like 'aYdF1"+cs+"'bYdF1 or code like 'aYdF1+cs+'bYdF1";
									}else{
										css += " or "+ key_str +" like 'aYdF1"+cs+"'bYdF1";
									}
								}
							}else if(key.indexOf("~")>=0){
								String[] keys = key.split("~");
								for (String key_str:keys) {
									if (css.equals("")){
										css += key_str +" like 'aYdF1"+cs+"'bYdF1";
									}else{
										css += " or "+key_str +" like 'aYdF1"+cs+"'bYdF1";
									}
								}
							}else{
								css = key +" like 'aYdF1"+cs+"'bYdF1";
							}

							parms = pars.subSequence(0,pars.length()-1) + ",\"cxlx\":\"1\",\"parms\":\""+css+"\"}";
						}else{
							List<Map<String,Object>> resList = FastJsonUtils.findListBycolAndval(copyList,cxcolumns,cs);

							if (resList.size() >0){
								mList.clear();
								mList.addAll(resList);
								mSingleChoicAdapter.notifyDataSetChanged();
							}

						}
					}else{
						parms = pars;
					}

					//执行查询动作
					final String finalParms = parms;
					new Thread(new Runnable() {
						@Override
						public void run() {
							Object res =  ActivityController.getData4ByPost(service_name,method_name, StringUtils.strTOJsonstr(finalParms));
							if(res != null && !res.toString().contains("(abcdef)")){
								mList.clear();
								mList.addAll((List<Map<String, Object>>) res);
							}else{
								mList.clear();
							}

							((Activity)mContext).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mSingleChoicAdapter.refreshData(mList);
								}
							});
						}
					}).start();

					break;
				default:
					break;
			}
		}
	}

}
