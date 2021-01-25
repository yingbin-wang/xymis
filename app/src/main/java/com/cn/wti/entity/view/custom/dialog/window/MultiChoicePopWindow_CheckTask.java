package com.cn.wti.entity.view.custom.dialog.window;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.cn.wti.entity.adapter.TaskAdapter;
import com.cn.wti.entity.view.custom.dialog.adapter.MultiChoic_task_Adapter;
import com.cn.wti.entity.view.custom.dialog.adapter.Utils;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.app.dialog.WeiboDialogUtils;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.other.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiChoicePopWindow_CheckTask<T> extends AbstractChoicePopWindow{

	private TaskAdapter taskAdapter;
	public List<Map<String,Object>> activitiMapList = null,mList;
	private Map<String,Object> _dataMap;
	private Dialog mDialog;
	private  String respars = "",way_type = "";
	private  Handler ztHandler;
	private RecyclerView  mListView = null;
	private boolean[] mCheck;

	//带状态的返回
	public MultiChoicePopWindow_CheckTask(Context context, View parentView, List<Map<String,Object>>_list, boolean flag[],Map<String,Object> _dataMap,String title_name,Handler ztHandler)
	{
		super(context, parentView, _list,R.layout.popwindow_recyclerview_norefresh);
		if (l_1 != null){
			l_1.setVisibility(View.GONE);
		}
		setTitle_name(title_name);

		mListView = (RecyclerView) currentview.findViewById(R.id.listView);
		this.mList = _list;
		this._dataMap = _dataMap;
		this.ztHandler = ztHandler;
		initData(flag);
	}

	protected void initData(boolean flag[]) {

		ActivityController.setLayoutManager(mListView,mContext);
		//创建并设置Adapter
		mCheck = new boolean[mList.size()];
		taskAdapter = new TaskAdapter(mContext,mList,AppUtils.getScreenWidth(mContext),mParentView,R.layout.activity_mytask_examine_lvitem,mCheck);
		mListView.setAdapter(taskAdapter);

		title.setText(getTitle_name());

		setOnOKButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				way_type = taskAdapter.getWay_type();
				mDialog = WeiboDialogUtils.createLoadingDialog(currentview.getContext(), "审批中...");
				mHandler.sendEmptyMessageDelayed(1,1000);
			}
		});
	}

	public boolean[] getSelectItem()
	{
		return taskAdapter.getSelectItem();
	}

	@Override
	protected void onButtonOK(View v) {
		if (mOkListener != null) {
			mOkListener.onClick(v);
		}
	}

	/**
	 * 执行审批
	 */
	public void sp(){

		activitiMapList= new ArrayList<Map<String,Object>>();

		boolean[] items = taskAdapter.getSelectItem();
		if(items != null && items.length >0){
			for (int i=0,n=items.length;i<n;i++){
				if(items[i]){
					Object res = mList.get(i);
					Map<String,Object>resMap = (Map<String, Object>) res;
					String assignee = resMap.get("assignee").toString();
					String type = resMap.get("type").toString();
					if((assignee.equals("") || assignee.indexOf(",")>=0 ) && !type.equals("endEvent")){
						Toast.makeText(mContext,"当前任务没有经办人",Toast.LENGTH_SHORT).show();
						WeiboDialogUtils.closeDialog(mDialog);
						return;
					}

					resMap.put("checked",true);
					resMap.put("activityId",resMap.get("activitiId"));
					activitiMapList.add(resMap);
				}
			}

			String activitis = "";
			respars="";

			if (activitiMapList.size()>0){
				activitis = FastJsonUtils.ListMapToListStr(activitiMapList);
				pars = StringUtils.strTOJsonstr(pars);
				respars =  pars.substring(0,pars.length()-1)+",\"staffid\":\""+ AppUtils.user.get_zydnId()+"\""
						+",\"user_name\":\""+ AppUtils.app_username+"\",\"way_type\":\""+ way_type+"\""+",\"activitis\":"+activitis+"}";
			}

			//执行 审批
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean flage = ActivityController.execute(mContext,service_name,method_name,respars);
					if (flage){
						WeiboDialogUtils.closeDialog(mDialog);
						_dataMap.put("approvalstatus",1);
						ztHandler.sendEmptyMessageDelayed(3,10);
						((Activity)mContext).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								show(false);
							}
						});

					}else{
						_dataMap.put("approvalstatus",0);
						WeiboDialogUtils.closeDialog(mDialog);
					}
				}
			}).start();
		}
	}

	public  Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					sp();
					break;
			}
		}
	};

}
