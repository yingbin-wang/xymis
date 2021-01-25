package com.cn.wti.entity.view.custom.dialog.window;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Toast;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.dialog.adapter.MultiChoic_Dn_Adapter;
import com.cn.wti.entity.view.custom.dialog.adapter.SingleChoicAdapter;
import com.cn.wti.entity.view.custom.dialog.adapter.Utils;
import com.cn.wti.entity.view.custom.dialog.click.DialogButtonClick;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.entity.view.pulltorefresh.ListViewListener;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.IDHelper;
import com.cn.wti.util.db.FastJsonUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiChoicePopWindow_CheckDn<T> extends AbstractChoicePopWindow{

	private MultiChoic_Dn_Adapter<Object> mMultiChoicAdapter;
	private View resView;
	private String title_str,keys;
	private List<String>  lastSelectList = null;

	public MultiChoicePopWindow_CheckDn(Context context, View parentView, List<T>_list, boolean flag[],
										String service_name, String method_name, String pars,
										int recordcount,int pageIndex,
										String key,View resView,String title_str)
	{
		super(context, parentView, _list,service_name,method_name,pars,key);
		this.resView = resView;
		title.setText(title_str);
		this.pageIndex = pageIndex;
		this.recordcount = recordcount;
		this.title_str = title_str;
		initData(flag);
	}

	public MultiChoicePopWindow_CheckDn(Context context, View parentView, List<T>_list, boolean flag[],
										String service_name, String method_name, String pars,
										int recordcount,int pageIndex,
										String key,String keys,String title_str,String type)
	{
		super(context, parentView, _list,service_name,method_name,pars,key);
		title.setText(title_str);
		this.pageIndex = pageIndex;
		this.recordcount = recordcount;
		this.title_str = title_str;
		this.keys = keys;

		/**
		 * 上次选中
		 */
		if (parentView.getTag()!= null){
			lastSelectList = (List<String>) parentView.getTag();
		}

		initData(flag);
	}

	public MultiChoicePopWindow_CheckDn(Context context, View parentView, List<T>_list, boolean flag[])
	{
		super(context, parentView, _list);
		l_1.setVisibility(View.GONE);
		initData(flag);
	}

	protected void initData(boolean flag[]) {

		mMultiChoicAdapter = new MultiChoic_Dn_Adapter<Object>(mContext, mList, flag, R.drawable.selector_checkbox1,keys);
		mListView.setAdapter(mMultiChoicAdapter);
		mListView.setOnItemClickListener(mMultiChoicAdapter);
		mMultiChoicAdapter.setLastSelectList(lastSelectList);

		Utils.setListViewHeightBasedOnChildren(mListView);

		refreshLayout.setOnRefreshListener(new ListViewListener(mMultiChoicAdapter,mList,recordcount,pageIndex,service_name,"",method_name,pars,key));

		setOnOKButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String values= "";
				List<String> select_list = getSlectList();
				if(select_list != null && select_list.size() >0){

					//转字符串
					values = FastJsonUtils.listStrTOStr(select_list,",");
					if (resView != null){
						if (resView instanceof EditText){
							EditText text = (EditText) resView;
							if (values.substring(values.length()-1).equals(",")){
								values = values.substring(0,values.length()-1);
							}
							text.setText(values);
						}
						show(false);
					}
				}
			}
		});

		/*setOnOKButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String values= "";
				boolean[] items = getSelectItem();
				if(items != null && items.length >0){
					for (int i=0,n=items.length;i<n;i++){
						if(items[i]){
							Object res = mList.get(i);
							Map<String,Object>resMap = (Map<String, Object>) res;
							if(i == n - 1){
								values += resMap.get(key).toString();
							}else{
								values += resMap.get(key).toString()+",";
							}

						}
					}
					if (resView != null){
						if (resView instanceof EditText){
							EditText text = (EditText) resView;
							if (!values.equals("")){
								values = values.substring(0,values.length()-1);
							}
							text.setText(values);
						}
						show(false);
					}
				}
			}
		});*/

		//添加 查询按钮事件
		setmShowListener(new DialogButtonClick(mMultiChoicAdapter,editText_cs,service_name,method_name,pars,key,mList,title_str,mContext));
	}

	public boolean[] getSelectItem()
	{
		return mMultiChoicAdapter.getSelectItem();
	}

	public List<String> getSlectList()
	{
		return mMultiChoicAdapter.getSelectList();
	}

	@Override
	protected void onButtonOK(View v) {
		if (mOkListener != null) {
			mOkListener.onClick(v);
		}
	}

}
