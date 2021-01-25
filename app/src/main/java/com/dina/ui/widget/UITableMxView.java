package com.dina.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
import com.wticn.wyb.wtiapp.R;

import com.dina.ui.model.BasicItem;
import com.dina.ui.model.BasicItem_List;
import com.dina.ui.model.IListItem;
import com.dina.ui.model.ViewItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UITableMxView extends LinearLayout {

	private int mIndexController = 0;
	private LayoutInflater mInflater;
	private LinearLayout mMainContainer;
	private LinearLayout mListContainer;
	private List<IListItem> mItemList;
	private ClickListener mClickListener;
	private Context mContext;
	private HorizontalScrollView slideView;
	private LinearLayout layoutContent;
	private int screenWidth,screenHeight;
	private TextView delBtn;
	private String service_name;
	private List<Map<String,Object>> mxDataList,mxgs_list;
	private View action;
	private UITableView mainTableView;
	private String[] contents;
	private WindowManager.LayoutParams wmParams = null;

	private OnClickListener onClickListener;

	private int layoutid = 0;

	public UITableMxView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItemList = new ArrayList<IListItem>();
		mMainContainer = (LinearLayout)  mInflater.inflate(R.layout.list_container, null);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		addView(mMainContainer, params);
		mListContainer = (LinearLayout) mMainContainer.findViewById(R.id.buttonsContainer);
		mContext = getContext();

		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
		screenHeight = wm.getDefaultDisplay().getHeight();

	}

	/**
	 * @param code
	 * @param title
	 * @param color
	 * @param _clickable
	 * @param type
	 */
	public void addBasicItem(String id,String code,String title, String sub_title,String content_1, String content_2,
							 String content_3, String content_4, String content_5, String content_6,
							 String content_7,
							 int color, boolean _clickable,int type) {
		mItemList.add(new BasicItem_List(code,title,sub_title,content_1,content_2,content_3,content_4,content_5,content_6,content_7,color,_clickable,type));
	}

	public void addBasicItem(String id,String code,String title, String sub_title,String content_1, String content_2,
							 String content_3, String content_4, String content_5, String content_6,
							 int color, boolean _clickable,int type) {
		mItemList.add(new BasicItem_List(id,code,title,sub_title,content_1,content_2,content_3,content_4,content_5,content_6,color,_clickable,type));
	}

	public void addBasicItem(String id,String code,String title,String sub_title,
							 int color, boolean _clickable,int type) {
		mItemList.add(new BasicItem_List(id,code,title,sub_title,color,_clickable,type));
	}

	public void addBasicItem(String id,String code,String[] titles,String[] contents,
							 int color, boolean _clickable,int type) {
		mItemList.add(new BasicItem_List(id,code,titles,contents,color,_clickable,type,0,0));
	}

	/**
	 *
	 * @param id
	 * @param code
	 * @param title
	 * @param sub_title
	 * @param color
	 * @param _clickable
     * @param type
     */
	public void addBasicItem(String id,String code,String title,String sub_title,String content_1, String content_2,
							 String content_3, String content_4,
							 int color, boolean _clickable,int type) {
		mItemList.add(new BasicItem_List(id,code,title,sub_title,content_1,content_2,content_3,content_4,color,_clickable,type));
	}

	/**
	 *
	 * @param itemView
	 */
	public void addViewItem(ViewItem itemView) {
		mItemList.add(itemView);
	}

	public void commit() {
		mIndexController = 0;

		if(mItemList.size() > 0) {
			//when the list has more than one item
			BasicItem item ;
			for(IListItem obj : mItemList) {

				View tempItemView = null;

				if (layoutid == 0){
					tempItemView = mInflater.inflate(R.layout.list_item_middle3_01, null);
				}else{
					tempItemView = mInflater.inflate(layoutid, null);
				}

				slideView = (HorizontalScrollView) tempItemView.findViewById(R.id.hsv);
				if (slideView != null){
					action = tempItemView.findViewById(R.id.ll_action);
					layoutContent = (LinearLayout) tempItemView.findViewById(R.id.itemContainer);
					delBtn = (TextView) tempItemView.findViewById(R.id.btn_delte);
					// 设置内容view的大小为屏幕宽度,这样按钮就正好被挤出屏幕外
					ViewGroup.LayoutParams lp = layoutContent.getLayoutParams();
					lp.width = screenWidth;

					delBtn.setOnClickListener(new MyOnclickListener(mClickListener,mListContainer,mItemList,getMxDataList(),this));
					delBtn.setTag(mIndexController);
				}

				setupItem(tempItemView, obj, mIndexController);
				tempItemView.setClickable(obj.isClickable());
				tempItemView.setOnTouchListener(new MyOnTouchListener(slideView,action));
				mListContainer.addView(tempItemView);

				mIndexController++;


			}
		}
	}

	public void commit(boolean scroll) {
		mIndexController = 0;

		if(mItemList.size() > 0) {
			//when the list has more than one item
			BasicItem item ;
			for(IListItem obj : mItemList) {

				View tempItemView = null;

				if (layoutid == 0){
					tempItemView = mInflater.inflate(R.layout.list_item_middle3_01, null);
				}else{
					tempItemView = mInflater.inflate(layoutid, null);
				}

				slideView = (HorizontalScrollView) tempItemView.findViewById(R.id.hsv);
				if (slideView != null){
					action = tempItemView.findViewById(R.id.ll_action);
					layoutContent = (LinearLayout) tempItemView.findViewById(R.id.itemContainer);
					delBtn = (TextView) tempItemView.findViewById(R.id.btn_delte);
					if (!scroll){
						delBtn.setVisibility(GONE);
					}else{
						delBtn.setOnClickListener(new MyOnclickListener(mClickListener,mListContainer,mItemList,getMxDataList(),this));
						delBtn.setTag(mIndexController);
					}
					// 设置内容view的大小为屏幕宽度,这样按钮就正好被挤出屏幕外
					ViewGroup.LayoutParams lp = layoutContent.getLayoutParams();
					lp.width = screenWidth;
				}

				setupItem(tempItemView, obj, mIndexController);
				tempItemView.setClickable(obj.isClickable());
				tempItemView.setOnTouchListener(new MyOnTouchListener(slideView,action));
				mListContainer.addView(tempItemView);

				mIndexController++;


			}
		}
	}

	private void setupItem(View view, IListItem item, int index) {
		if(item instanceof BasicItem_List) {
			BasicItem_List tempItem = (BasicItem_List) item;
			setupBasicItem(view, tempItem, mIndexController);
		}
		else if(item instanceof ViewItem) {
			ViewItem tempItem = (ViewItem) item;
			setupViewItem(view, tempItem, mIndexController);
		}
	}

	/**
	 *
	 * @param view
	 * @param item
	 * @param index
	 */
	private void setupBasicItem(final View view, final BasicItem_List item, final int index) {

		View v;
		TextView_custom tc1;
		String val;

		if(item.getDrawable() > -1) {
			((ImageView) view.findViewById(R.id.image)).setBackgroundResource(item.getDrawable());
		}

		if(item.getTitle_1()!= null){
			setItemVal(R.id.title_1,item,view,item.getTitle_1());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_1);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_2()!= null){
			setItemVal(R.id.title_2,item,view,item.getTitle_2());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_2);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_3()!= null){
			setItemVal(R.id.title_3,item,view,item.getTitle_3());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_3);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_4()!= null){
			setItemVal(R.id.title_4,item,view,item.getTitle_4());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_4);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_5()!= null){
			setItemVal(R.id.title_5,item,view,item.getTitle_5());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_5);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_6()!= null){
			setItemVal(R.id.title_6,item,view,item.getTitle_6());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_6);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		//内容
		if(item.getSubtitle() != null) {
			setItemVal(R.id.subtitle,item,view,item.getSubtitle());
		}else {
			setItem_Display(view,R.id.subtitle);
		}

		if(item.getContent_1()!= null){
			setItemVal(R.id.content_1,item,view,item.getContent_1());
		}else {
			setItem_Display(view,R.id.content_1);
		}

		if(item.getContent_2()!= null){
			setItemVal(R.id.content_2,item,view,item.getContent_2());
		}else {
			setItem_Display(view,R.id.content_2);
		}

		if(item.getContent_3()!= null){
			setItemVal(R.id.content_3,item,view,item.getContent_3());
		}else {
			setItem_Display(view,R.id.content_3);
		}

		if(item.getContent_4()!= null){
			setItemVal(R.id.content_4,item,view,item.getContent_4());
		}else {
			setItem_Display(view,R.id.content_4);
		}

		if(item.getContent_5()!= null){
			setItemVal(R.id.content_5,item,view,item.getContent_5());
		}else {
			setItem_Display(view,R.id.content_5);
		}

		if(item.getContent_6()!= null){
			setItemVal(R.id.content_6,item,view,item.getContent_6());
		}else {
			setItem_Display(view,R.id.content_6);
		}

		((TextView) view.findViewById(R.id.title)).setText(item.getTitle());
		if(item.getColor() > -1) {
			((TextView) view.findViewById(R.id.title)).setTextColor(item.getColor());
		}
		view.setTag(index);
		if(item.isClickable()) {
			LinearLayout testView = (LinearLayout) view.findViewById(R.id.itemContainer);
			testView.setTag(index);
			testView.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View view) {
					if(mClickListener != null)
						mClickListener.onClick((Integer) view.getTag());
				}

			});

			ImageView chevron = (ImageView) view.findViewById(R.id.chevron);
			if(chevron != null){
				chevron.setVisibility(View.VISIBLE);
			}
		}
		else {
			ImageView chevron = (ImageView) view.findViewById(R.id.chevron);
			if(chevron != null){
				chevron.setVisibility(View.VISIBLE);
			}
		}

		//更新图片状态
		String code = item.getCode();
		if (code != null && !code.equals("")){
			TextView status = (TextView) view.findViewById(R.id.status);
			if (code.equals("未完成")){
				status.setBackground(getResources().getDrawable(R.drawable.red01_dot_bg));
			}else{
				status.setBackground(getResources().getDrawable(R.drawable.green01_dot_bg));
			}
		}

		//验证是否 存在 点击按钮
		final ImageView _selectView = (ImageView) view.findViewById(R.id.schedulemore);
		if (_selectView != null){
			_selectView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Activity activity = (Activity) mContext;
					Map<String,Object> map = null;

					if (_selectView.getTag() != null){
						map = (Map<String, Object>) _selectView.getTag();
					}else{
						map = new HashMap<String, Object>();
					}

					if (view.getTag() != null){
						map.put("index",view.getTag());
					}else{
						map.put("index",index);
					}
					int[] location = new int[2];
					_selectView.getLocationOnScreen(location);
					int x = location[0];
					int y = location[1];
					map.put("currentView",view);
					map.put("_selectView",_selectView);
					map.put("val",item.getCode());
					map.put("item",item);
					ActivityController.showPopUp(_selectView,mContext,onClickListener,map,new String[]{"wancheng","shanchu","xiangqing"});
					/*View _childView = ActivityController.openLeftBigWindow(activity.getWindowManager(),activity.getLayoutInflater(),onClickListener,map,x,y,item.getCode());
					_selectView.setTag(map);*/
				}
			});
		}
	}

	public void reshViewIndex(){
		int count = mListContainer.getChildCount();
		View view;
		for (int i=0,n = count;i<n;i++){
			view = mListContainer.getChildAt(i);
			view.setTag(i);
		}
	}

	/**
	 * 填充Item 值
	 * @param id
	 * @param item
	 * @param view
	 * @param val
	 */
	public void setItemVal(int id,BasicItem_List item,View view,String val){
		View v;
		TextView_custom tc1;
		v = view.findViewById(id);
		if (v instanceof TextView_custom){
			tc1 = (TextView_custom)v;
			tc1.setText(val);
		}
	}

	/**
	 *
	 * @param view
	 * @param itemView
	 * @param index
	 */
	private void setupViewItem(View view, ViewItem itemView, int index) {
		if(itemView.getView() != null) {
			LinearLayout itemContainer = (LinearLayout) view.findViewById(R.id.itemContainer);
			itemContainer.removeAllViews();
			//itemContainer.removeAllViewsInLayout();
			itemContainer.addView(itemView.getView());
		}
	}

	public String getService_name() {
		return service_name;
	}

	public void setService_name(String service_name) {
		this.service_name = service_name;
	}

	public List<Map<String, Object>> getMxDataList() {
		return mxDataList;
	}

	public void setMxDataList(List<Map<String, Object>> mxDataList) {
		this.mxDataList = mxDataList;
	}

/*	public interface ClickListener {
		void onClick(int index);
	}*/

	/**
	 *
	 * @return
	 */
	public int getCount() {
		return mItemList.size();
	}

	public List<IListItem> getIListItem() {
		return mItemList;
	}

	/**
	 * 得到一个Item
	 * @param index
	 * @return
	 */
	public IListItem getItem(int index) {
		return mItemList.get(index);
	}

	/**
	 * 获取当前视图w
	 * @param index
	 * @return
	 */
	public View getLayoutList(int index) {
		return mListContainer.getChildAt(index);
	}


	/**
	 * 设置 item 值
	 * @param view
	 * @param item
	 * @param index
	 */
	public void setupBasicItemValue(View view, BasicItem_List item, int index) {

		View v;
		TextView_custom tc1;
		EditText_custom ec1;

		if(item.getDrawable() > -1) {
			((ImageView) view.findViewById(R.id.image)).setBackgroundResource(item.getDrawable());
		}

		if(item.getTitle_1()!= null){
			setItemVal(R.id.title_1,item,view,item.getTitle_1());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_1);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_2()!= null){
			setItemVal(R.id.title_2,item,view,item.getTitle_2());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_2);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_3()!= null){
			setItemVal(R.id.title_3,item,view,item.getTitle_3());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_3);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_4()!= null){
			setItemVal(R.id.title_4,item,view,item.getTitle_4());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_4);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_5()!= null){
			setItemVal(R.id.title_5,item,view,item.getTitle_5());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_5);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		if(item.getTitle_6()!= null){
			setItemVal(R.id.title_6,item,view,item.getTitle_6());
		}else {
			TextView_custom tv1 = (TextView_custom) view.findViewById(R.id.title_6);
			if(tv1 != null){
				tv1.setVisibility(View.GONE);
			}
		}

		//内容
		if(item.getSubtitle() != null) {
			setItemVal(R.id.subtitle,item,view,item.getSubtitle());
		}else {
			setItem_Display(view,R.id.subtitle);
		}

		if(item.getContent_1()!= null){
			setItemVal(R.id.content_1,item,view,item.getContent_1());
		}else {
			setItem_Display(view,R.id.content_1);
		}

		if(item.getContent_2()!= null){
			setItemVal(R.id.content_2,item,view,item.getContent_2());
		}else {
			setItem_Display(view,R.id.content_2);
		}

		if(item.getContent_3()!= null){
			setItemVal(R.id.content_3,item,view,item.getContent_3());
		}else {
			setItem_Display(view,R.id.content_3);
		}

		if(item.getContent_4()!= null){
			setItemVal(R.id.content_4,item,view,item.getContent_4());
		}else {
			setItem_Display(view,R.id.content_4);
		}

		if(item.getContent_5()!= null){
			setItemVal(R.id.content_5,item,view,item.getContent_5());
		}else {
			setItem_Display(view,R.id.content_5);
		}

		if(item.getContent_6()!= null){
			setItemVal(R.id.content_6,item,view,item.getContent_6());
		}else {
			setItem_Display(view,R.id.content_6);
		}
	}

	//隐藏Item
	public void setItem_Display(View view,int id){
		TextView_custom tv1 = (TextView_custom) view.findViewById(id);
		if(tv1 != null){
			tv1.setVisibility(View.GONE);
		}
	}

	public String getBasicItemValue(View view, BasicItem item, int index) {

		View v;
		TextView_custom tc1;
		EditText_custom ec1;
		String res = "";
		if(item.getSubtitle() != null) {
			v = view.findViewById(R.id.subtitle);
			if (v instanceof TextView_custom){
				tc1 = (TextView_custom)v;
				res = tc1.getText().toString();
			}
		}
		return  res;
	}

	/**
	 *
	 */
	public void clear() {
		mItemList.clear();
		mListContainer.removeAllViews();
	}

	public void clear2() {
		mListContainer.removeAllViews();
	}

	public LinearLayout getmListContainer(){
		return  this.mListContainer;
	}

	/**
	 *
	 * @param listener
	 */
	public void setClickListener(ClickListener listener) {
		this.mClickListener = listener;
	}

	public void setOnClickListener(OnClickListener listener) {
		this.onClickListener = listener;
	}

	/**
	 *
	 */
	public void removeClickListener() {
		this.mClickListener = null;
	}

	public List<Map<String, Object>> getMxgs_list() {
		return mxgs_list;
	}

	public void setMxgs_list(List<Map<String, Object>> mxgs_list) {
		this.mxgs_list = mxgs_list;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public UITableView getMainTableView() {
		return mainTableView;
	}

	public void setMainTableView(UITableView mainTableView) {
		this.mainTableView = mainTableView;
	}

	public String[] getContents() {
		return contents;
	}

	public void setContents(String[] contents) {
		this.contents = contents;
	}

	public int getLayoutid() {
		return layoutid;
	}

	public void setLayoutid(int layoutid) {
		this.layoutid = layoutid;
	}
}
