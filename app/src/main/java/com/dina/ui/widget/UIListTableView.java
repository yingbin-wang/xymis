package com.dina.ui.widget;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.BasicItem_List;
import com.dina.ui.model.IListItem;
import com.dina.ui.model.ViewItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UIListTableView extends LinearLayout {

	protected int mIndexController = 0;
	protected LayoutInflater mInflater;
	protected LinearLayout mMainContainer;
	protected LinearLayout mListContainer;
	protected List<IListItem> mItemList;
	protected ClickListener mClickListener;

	protected Context mContext;
	protected HorizontalScrollView slideView;
	protected LinearLayout layoutContent;
	protected int screenWidth,screenHeight;
	/*private ImageButton editBtn,delBtn;*/
	private TextView delBtn;
	private String service_name;
	private View action;
	private List<Map<String,Object>> _dataList;

	private  int _layoutid = 0;

	public UIListTableView(Context context, AttributeSet attrs) {
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

	public void addBasicItem(String id,String code,String title, String sub_title,String content_1, String content_2,
								  String content_3, String content_4, String content_5, String content_6,
								  int color, boolean _clickable,int type,int approvalstatus,int estatus) {
		mItemList.add(new BasicItem_List(id,code,title,sub_title,content_1,content_2,content_3,content_4,content_5,content_6,color,_clickable,type,approvalstatus,estatus));
	}


	public void addBasicItem(String id,String code,String[] titles,String contents[],
							 int color, boolean _clickable,int type,int approvalstatus,int estatus) {
		mItemList.add(new BasicItem_List(id,code,titles,contents,color,_clickable,type,approvalstatus,estatus));
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
				if (_layoutid != 0){
					tempItemView = mInflater.inflate(_layoutid, null);
				}else{
					tempItemView = mInflater.inflate(R.layout.list_item_middle3_01, null);
				}
				/*tempItemView = mInflater.inflate(R.layout.list_item_middle3_01, null);*/
				slideView = (HorizontalScrollView) tempItemView.findViewById(R.id.hsv);

				if (slideView != null){
					action = tempItemView.findViewById(R.id.ll_action);
					layoutContent = (LinearLayout) tempItemView.findViewById(R.id.itemContainer);
					delBtn = (TextView) tempItemView.findViewById(R.id.btn_delte);
					// 设置内容view的大小为屏幕宽度,这样按钮就正好被挤出屏幕外
					ViewGroup.LayoutParams lp = layoutContent.getLayoutParams();
					lp.width = screenWidth;

					delBtn.setOnClickListener(new MyOnclickListener());
					delBtn.setTag(mIndexController);
					tempItemView.setOnTouchListener(new MyOnTouchListener(slideView,action));
				}

				setupItem(tempItemView, obj, mIndexController);
				tempItemView.setClickable(obj.isClickable());

				mListContainer.addView(tempItemView);

				mIndexController++;
			}
		}
	}
	
	public void setupItem(View view, IListItem item, int index) {
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
	private void setupBasicItem(View view, BasicItem_List item, int index) {

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

		//设置字体颜色

		/*if(item.getColor() > -1) {
			((TextView) view.findViewById(R.id.title)).setTextColor(item.getColor());
		}*/
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

	}

	//隐藏Item
	public void setItem_Display(View view,int id){
		TextView_custom tv1 = (TextView_custom) view.findViewById(id);
		if(tv1 != null){
			tv1.setVisibility(View.GONE);
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
			if (id == R.id.subtitle){
				switch (val){
					case "未完成":
						tc1.setBackground(getResources().getDrawable(R.drawable.right_title_02));
						break;
					case "未出库":
						tc1.setBackground(getResources().getDrawable(R.drawable.right_title_02));
						break;
					case "未支付":
						tc1.setBackground(getResources().getDrawable(R.drawable.right_title_02));
						break;
					case "未还":
						tc1.setBackground(getResources().getDrawable(R.drawable.right_title_02));
						break;
				}
			}
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
		if(item.getSubtitle() != null) {
			v = view.findViewById(R.id.subtitle);
			if (v instanceof TextView_custom){
				tc1 = (TextView_custom)v;
				tc1.setText(item.getSubtitle());
			}
		}
		if(item.getContent_1() != null) {
			v = view.findViewById(R.id.content_1);
			if (v instanceof TextView_custom){
				tc1 = (TextView_custom)v;
				tc1.setText(item.getContent_1());
			}
		}
		if(item.getContent_2() != null) {
			v = view.findViewById(R.id.content_2);
			if (v instanceof TextView_custom){
				tc1 = (TextView_custom)v;
				tc1.setText(item.getContent_2());
			}
		}
		if(item.getContent_3() != null) {
			v = view.findViewById(R.id.content_3);
			if (v instanceof TextView_custom){
				tc1 = (TextView_custom)v;
				tc1.setText(item.getContent_3());
			}
		}
		if(item.getContent_4() != null) {
			v = view.findViewById(R.id.content_4);
			if (v instanceof TextView_custom){
				tc1 = (TextView_custom)v;
				tc1.setText(item.getContent_4());
			}
		}if(item.getContent_5() != null) {
			v = view.findViewById(R.id.content_5);
			if (v instanceof TextView_custom){
				tc1 = (TextView_custom)v;
				tc1.setText(item.getContent_5());
			}
		}
		if(item.getContent_6() != null) {
			v = view.findViewById(R.id.content_6);
			if (v instanceof TextView_custom){
				tc1 = (TextView_custom)v;
				tc1.setText(item.getContent_6());
			}
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
	
	/**
	 * 
	 * @param listener
	 */
	public void setClickListener(ClickListener listener) {
		this.mClickListener = listener;
	}
	
	/**
	 * 
	 */
	public void removeClickListener() {
		this.mClickListener = null;
	}

	public List<Map<String, Object>> get_dataList() {
		return _dataList;
	}

	public void set_dataList(List<Map<String, Object>> _dataList) {
		this._dataList = _dataList;
	}

	public int get_layoutid() {
		return _layoutid;
	}

	public void set_layoutid(int _layoutid) {
		this._layoutid = _layoutid;
	}

	/**
	 * 添加 查看 与 删除事件
	 */
	public class  MyOnclickListener implements OnClickListener{

		public MyOnclickListener(){}
		@Override
		public void onClick(View view) {

			int postion = (int) view.getTag();
			View v = null;
			switch (view.getId()){
				case  R.id.btn_edit:
					if(mClickListener != null){
						v = mListContainer.getChildAt(postion);
						mClickListener.onClick((Integer) v.getTag());
					}
					break;
				case R.id.btn_delte:
					BasicItem_List item = (BasicItem_List) mItemList.get(postion);
					String id = item.getId();
					if( item.getApprovalstatus() == 1 || item.getApprovalstatus() == 2 ){
						Toast.makeText(mContext,"单据在流程审批中不允许此动作！",Toast.LENGTH_SHORT).show();
						return;
					}else if( item.getEstatus() == 7 ){
						Toast.makeText(mContext,"单据在审核中不允许此动作！",Toast.LENGTH_SHORT).show();
						return;
					}

					String pars = "{\"userId\":\""+ AppUtils.app_username+"\",\"DATA_IDS\":\""+id+"\"}";
					execute(mContext,service_name,"deleteAll",pars,postion);
					break;
				default:
					break;
			}
		}
	}

	public boolean execute(Context context,String service,String method,String pars,int postion){

		Object res = ActivityController.getDataByPost(context,service,method,pars);
		if (res != null && !res.equals("")){
			Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
			if (resMap.get("state")!= null && resMap.get("state").toString().equals("success")){
				mItemList.remove(postion);
				mListContainer.removeViewAt(postion);
				Toast.makeText(context,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
				return  true;
			}else{
				Toast.makeText(context,resMap.get("msg").toString(),Toast.LENGTH_SHORT).show();
				return  false;
			}
		}
		return  false;
	}

}
