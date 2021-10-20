package com.dina.ui.widget;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.res.ObbInfo;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.cn.wti.entity.adapter.MyAdapter2;
import com.cn.wti.entity.avalidations.EditTextValidator;
import com.cn.wti.entity.avalidations.ValidationModel;
import com.cn.wti.entity.avalidations.utils.DateValidation;
import com.cn.wti.entity.avalidations.utils.DecimalValidation;
import com.cn.wti.entity.avalidations.utils.GddhValidation;
import com.cn.wti.entity.avalidations.utils.GsValidation;
import com.cn.wti.entity.avalidations.utils.IntValidation;
import com.cn.wti.entity.avalidations.utils.NulllValidation;
import com.cn.wti.entity.avalidations.utils.OnlyGsValidation;
import com.cn.wti.entity.avalidations.utils.TelphoneValidation;
import com.cn.wti.entity.view.custom.CheckBox_custom;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.wticn.wyb.wtiapp.R;
import com.dina.ui.model.BasicItem;
import com.dina.ui.model.EditTextWatcher;
import com.dina.ui.model.IListItem;
import com.dina.ui.model.MyTextWatcher;
import com.dina.ui.model.ViewItem;

import static com.wticn.wyb.wtiapp.R.color.*;

public class UITableView extends LinearLayout {
	
	private int mIndexController = 0;
	private LayoutInflater mInflater;
	private LinearLayout mMainContainer;
	private LinearLayout mListContainer;
	private List<IListItem> mItemList;
	private ClickListener mClickListener;
	private EditTextValidator editTextValidator;
	private ImageButton button_ok;
	private Context context1;
	private String mxIndex;

	//当前明细数据
	private List<Map<String,Object>> _dataList;
	private List<Map<String,Object>> _gxdataList,_removeList;
	//关联主数据
	private Map<String,Object> _zhuData;
	//只读属性
	private boolean readOnly;

	public UITableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mItemList = new ArrayList<IListItem>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainContainer = (LinearLayout)  mInflater.inflate(R.layout.list_container, null);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		addView(mMainContainer, params);				
		mListContainer = (LinearLayout) mMainContainer.findViewById(R.id.buttonsContainer);		
	}
	
	/**
	 * 
	 * @param title
	 * @param
	 */
	public void addBasicItem(String title) {
		mItemList.add(new BasicItem(title));
	}
	
	/**
	 * 
	 * @param title
	 * @param summary
	 */
	public void addBasicItem(String title, String summary) {
		mItemList.add(new BasicItem(title, summary));
	}
	
	/**
	 * 
	 * @param title
	 * @param summary
	 * @param color
	 */
	public void addBasicItem(String title, String summary, int color) {
		mItemList.add(new BasicItem(title, summary, color));
	}
	
	/**
	 * 
	 * @param drawable
	 * @param title
	 * @param summary
	 */
	public void addBasicItem(int drawable, String title, String summary) {
		mItemList.add(new BasicItem(drawable, title, summary));
	}
	
	/**
	 * 
	 * @param drawable
	 * @param title
	 * @param summary
	 */
	public void addBasicItem(int drawable, String title, String summary, int color) {
		mItemList.add(new BasicItem(drawable, title, summary, color));
	}

	public void addBasicItem(String title, String summary, int color, boolean _clickable,int type) {
		mItemList.add(new BasicItem(title, summary, color,_clickable,type));
	}

	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type));
	}

	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,String dataType) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,dataType));
	}

	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,String dataType,Object is_required) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,dataType,is_required));
	}

	/**
	 * 带公式 的 输入项
	 * @param code
	 * @param title
	 * @param summary
	 * @param color
	 * @param _clickable
	 * @param type
     * @param gs_cols
     * @param gs
     */
	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,String[] gs_cols,String gs) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,gs_cols,gs));
	}

	/**
	 * 只有验证
	 * @param code
	 * @param title
	 * @param summary
	 * @param color
	 * @param _clickable
	 * @param type
	 * @param rule
     * @param dataType
     */
	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,String rule,String dataType) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,rule,dataType));
	}

	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,String rule,String dataType,Object is_required) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,rule,dataType));
	}

	/**
	 * 只有公式
	 * @param code
	 * @param title
	 * @param summary
	 * @param color
	 * @param _clickable
	 * @param type
	 * @param gs_cols
     * @param gs
     * @param dataType
     */
	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,String[] gs_cols,String gs,String dataType) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,gs_cols,gs,dataType));
	}

	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,String[] gs_cols,String gs,String dataType,Object is_required) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,gs_cols,gs,dataType,is_required));
	}

	/**
	 * 既有公式 又有 验证
	 * @param code
	 * @param title
	 * @param summary
	 * @param color
	 * @param _clickable
	 * @param type
	 * @param gs_cols
	 * @param gs
     * @param rules
     * @param dataType
     */
	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,String[] gs_cols,String gs,String rules,String dataType) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,gs_cols,gs,rules,dataType));
	}

	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,String[] gs_cols,String gs,String rules,String dataType,Object is_required) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,gs_cols,gs,rules,dataType,is_required));
	}

	public void addBasicItem(String code,String title, String summary, int color, boolean _clickable,int type,Map<String,Object> actionMap,String gs,String rules,String dataType,Object is_required) {
		mItemList.add(new BasicItem(code,title, summary, color,_clickable,type,actionMap,gs,rules,dataType,is_required));
	}
	
	/**
	 * 
	 * @param item
	 */
	public void addBasicItem(BasicItem item) {
		mItemList.add(item);
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
		
		if(mItemList.size() > 1) {
			//when the list has more than one item
			BasicItem item ;
			for(IListItem obj : mItemList) {

				View tempItemView = null;
				item = (BasicItem)obj;

				if(item.getType() == 1 || item.getType() == 0 || item.getType() == 5){
					tempItemView = mInflater.inflate(R.layout.edit_item_middle2_01, null);
				}else if(item.getType() == 2){
					tempItemView = mInflater.inflate(R.layout.edit_item_middle_writable, null);
				}else if(item.getType() == 3 || item.getType() == 4 || item.getType() == 8 || item.getType() == 9){
					tempItemView = mInflater.inflate(R.layout.edit_item_middle2_02, null);
				}else if( item.getType() == 6 ){
					tempItemView = mInflater.inflate(R.layout.edit_item_middle_selectandwrite, null);
				}else if(item.getType() == 7 ){
					tempItemView = mInflater.inflate(R.layout.edit_item_middle2_03, null);
				}else if (item.getType() == 10){
					tempItemView = mInflater.inflate(R.layout.edit_item_middle2_upload, null);
				}

				if(item.getType() == 0){
					tempItemView.setVisibility(GONE);
				}
				setupItem(tempItemView, obj, mIndexController);
				tempItemView.setClickable(obj.isClickable());
				mListContainer.addView(tempItemView);
				mIndexController++;
			}
		}
		else if(mItemList.size() == 1) {
			//when the list has only one item
			View tempItemView = mInflater.inflate(R.layout.list_item_single, null);
			IListItem obj = mItemList.get(0);
			setupItem(tempItemView, obj, mIndexController);
			tempItemView.setClickable(obj.isClickable());
			mListContainer.addView(tempItemView);
		}
	}
	
	private void setupItem(View view, IListItem item, int index) {
		if(item instanceof BasicItem) {
			BasicItem tempItem = (BasicItem) item;
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
	private void setupBasicItem(View view, BasicItem item, int index) {

		final View v;
		TextView_custom tc1 = null;
		EditText_custom ec1 = null;

		if(item.getDrawable() > -1) {
			((ImageView) view.findViewById(R.id.image)).setBackgroundResource(item.getDrawable());
		}
		if(item.getSubtitle() != null) {

			if (item.getIs_required() != null && !item.getIs_required().equals("")){
				if (item.getDataType().equals("int")){
					item.setVal(String.valueOf(item.getColor()+Integer.parseInt(item.getIs_required().toString())));
				}
			}

			v = view.findViewById(R.id.subtitle);
			if (v instanceof TextView_custom){
				tc1 = (TextView_custom)v;
				tc1.setText(item.getSubtitle());
				if (item.getType() == 5){
					LinearLayout test= (LinearLayout)tc1.getParent();
					test.setBackgroundColor(getResources().getColor(shiqing));
					LinearLayout right_lint= (LinearLayout) view.findViewById(R.id.right_lint);
					right_lint.setVisibility(GONE);
				}

				tc1.setCode(item.getCode());
				tc1.setTitle(item.getTitle());

			}else if (v instanceof EditText_custom){
				ec1 = (EditText_custom)v;
				ec1.setText(item.getSubtitle());
				ec1.setCode(item.getCode());
				ec1.setTitle(item.getTitle());

				if (item.getType() == 3 || item.getType() == 4 || item.getType() == 9 || item.getType() == 10){
					ec1.setEnabled(false);
					ec1.setHint("点击选择");
					TextView select_item = (TextView) view.findViewById(R.id.select_item);
					select_item.setVisibility(View.VISIBLE);
					if(item.isClickable()) {
						if (item.getType() == 3 || item.getType() == 4 || item.getType() == 6 || item.getType() == 8 || item.getType() == 9 || item.getType() == 10){
							if (item.getType() != 10){
								((ImageView) view.findViewById(R.id.chevron)).setVisibility(View.VISIBLE);
							}else{
								Button upBtn = view.findViewById(R.id.chevron);
								upBtn.setTag(index);
								upBtn.setOnClickListener( new OnClickListener() {

									@Override
									public void onClick(View view) {
										if(mClickListener != null)
											mClickListener.onClick((Integer) view.getTag(),view);
									}
								});
							}
							select_item.setTag(index);
							select_item.setOnClickListener( new OnClickListener() {

								@Override
								public void onClick(View view) {
									if(mClickListener != null)
										mClickListener.onClick((Integer) view.getTag(),view);
								}
							});
						}else{
							((ImageView) view.findViewById(R.id.chevron)).setVisibility(View.GONE);
						}
					}

				}else{
					ec1.setEnabled(true);
					ec1.setHint("请输入");
					//设置键盘只显示数字键
					if (item.getDataType().equals("int") || item.getDataType().equals("decimal") || item.getDataType().equals("double")){
						ec1.setInputType(EditorInfo.TYPE_CLASS_PHONE);
					}

					if (item.getRule() != null && item.getRule().equals("phone")){
						ec1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
					}

					if ((item.getType() == 2 || item.getType() == 6)){
						//编辑字段 更新
						ec1.clearFocus();
						ec1.addTextChangedListener(new EditTextWatcher(item.getCode(),this));
					}
					//20171211 wang 添加 获取焦点事件
					if (item.getGs() != null &&!item.getGs().equals("")){
						final MyTextWatcher textWatcher = new MyTextWatcher(item.getCode(),item.getActionMap(),item.getGs(),this);
						ec1.addTextChangedListener(textWatcher);
						ec1.setOnFocusChangeListener(new OnFocusChangeListener() {
							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								textWatcher.setEdit(hasFocus);
							}
						});
					}
				}

			}else if (v instanceof CheckBox_custom){
				final CheckBox_custom cbx = (CheckBox_custom) v;
				cbx.setTag(item);
				cbx.setHint("请选择");
				final BasicItem finalitem = item;

				cbx.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CheckBox_custom cbx1 = (CheckBox_custom) v;
						if(cbx1.isChecked()){
							finalitem.setSubtitle("1");
						}else{
							finalitem.setSubtitle("0");
						}

						if (cbx.getTag()!= null){
							BasicItem biCbx = (BasicItem) cbx.getTag();
							if(biCbx.getGs()!= null){
								Map<String,Object> gsMap1 = FastJsonUtils.strToMap(biCbx.getGs());
								if (gsMap1 != null && gsMap1.get("action")!= null){
									Map<String,Object> actionMap = (Map<String, Object>) gsMap1.get("action");
									if(actionMap != null && actionMap.get("update")!= null){
										Map<String,Object> updateMap = (Map<String, Object>) actionMap.get("update");
										List<Map<String,Object>> mxData= null;
										if (updateMap != null){
											Set<String> updateSets = updateMap.keySet();
											for (String updateKey:updateSets) {
												String updatemxName = updateKey;
												if (!updatemxName.equals("")){
													Map<String,Object> itemUpdateMap = (Map<String, Object>) updateMap.get(updateKey);
													if (itemUpdateMap != null){
														if (itemUpdateMap.get("type") != null){
															String type = itemUpdateMap.get("type").toString();
															Object colObject = itemUpdateMap.get("column");
															//执行重新计算明细动作
															if (type.equals("updatemx")){
																try {
																	mxData = ActivityController.getMxData(getTag(),updatemxName);
																} catch (NoSuchFieldException e) {
																	e.printStackTrace();
																} catch (IllegalAccessException e) {
																	e.printStackTrace();
																}

																if (mxData != null){
																	Map<String,Object> columnMap = null;
																	if (colObject != null){
																		columnMap = (Map<String, Object>) colObject;
																		List<String> sortList = new ArrayList<String>();
																		if(columnMap.get("sort")!= null){
																			String[] sorts = columnMap.get("sort").toString().split(",");
																			sortList = Arrays.asList(sorts);
																			sortList.remove("sort");
																		}else{
																			sortList.addAll(columnMap.keySet()) ;
																			sortList.remove("sort");
																		}

																		Map<String,Object> itemMap = null;
																		for (Map<String,Object> onedata:mxData) {
																			for (String colKey:sortList) {

																				if (columnMap.get(colKey) instanceof JSONObject){
																					itemMap = (Map<String, Object>) columnMap.get(colKey);
																				}

																				if (itemMap != null && itemMap.get("type")!= null && itemMap.get("type").equals("calculate")){
																					String res = ActivityController.computeExpressionByMap(itemMap, (Context) getTag(),onedata);
																					if (!res.equals("")){
																						onedata.put(colKey,res);
																					}
																				}
																			}
																		}
																	}
																	String name = ActivityController.getMxName(updatemxName),mxAdpaterName="";
																	if (!name.equals("")){
																		String index1 = "";
																		index1 = name.replace("mx","").replace("_data","");
																		mxAdpaterName = "myAdapter"+index1;
																		try {
																			MyAdapter2 adapter = (MyAdapter2) ReflectHelper.getValueByFieldName(getTag(),mxAdpaterName);
																			if (adapter != null){
																				adapter.notifyDataSetChanged();
																			}
																		} catch (NoSuchFieldException e) {
																			e.printStackTrace();
																		} catch (IllegalAccessException e) {
																			e.printStackTrace();
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				});

				if (item.getSubtitle().equals("1")){
					cbx.setChecked(true);
				}else{
					cbx.setChecked(false);
					item.setSubtitle("0");
				}

				if(item.getColor()== 1){
					cbx.setEnabled(true);
				}else{
					cbx.setEnabled(false);
				}
			}
			//添加验证规则
			if ((item.getRule()!=null || (item.getIs_required()!= null && item.getIs_required().equals("是")) ) && editTextValidator != null){
				editTextValidator.setButton(button_ok);
				String rules = "";
				if (item.getRule()!=null){
					rules = item.getRule().toString();
				}
				//空验证
				if (rules.indexOf("empty")>=0 || (item.getIs_required()!= null && item.getIs_required().equals("是"))){
					editTextValidator.add(new ValidationModel(v,new NulllValidation()));
				}
				//整数验证
				if (rules.indexOf("int")>=0){
					editTextValidator.add(new ValidationModel(v,new IntValidation()));
				}
				//日期验证
				if (rules.indexOf("date")>=0){
					editTextValidator.add(new ValidationModel(v,new DateValidation()));
				}
				//浮点数公式验证
				if (rules.indexOf("decimal")>=0 && rules.indexOf("gs")>=0){
					ec1.setRules(rules);
					ec1.setTag(this);
					editTextValidator.add(new ValidationModel(v,new GsValidation()));
				}else if(rules.indexOf("gs")>=0){
					if (ec1 == null){
						tc1.setRules(rules);
						tc1.setTag(this);
						editTextValidator.add(new ValidationModel(v,new OnlyGsValidation()));
					}else{
						ec1.setRules(rules);
						ec1.setTag(this);
						editTextValidator.add(new ValidationModel(v,new OnlyGsValidation()));
					}
					
				}else if(rules.indexOf("decimal")>=0){
					editTextValidator.add(new ValidationModel(v,new DecimalValidation()));
				}

				//固定电话
				if (rules.indexOf("gddh")>=0){
					editTextValidator.add(new ValidationModel(v,new GddhValidation()));
				}
				//手机
				if (rules.indexOf("telphone")>=0){
					editTextValidator.add(new ValidationModel(v,new TelphoneValidation()));
				}

				editTextValidator.execute();
			}

		}else {
			((TextView_custom) view.findViewById(R.id.subtitle)).setVisibility(View.GONE);
		}
		((TextView) view.findViewById(R.id.title)).setText(item.getTitle());
		/*if(item.getColor() > -1) {
			((TextView) view.findViewById(R.id.title)).setTextColor(item.getColor());
		}*/
		view.setTag(index);
		if(item.isClickable()) {
			ImageView imageView  = null;
			if (/*item.getType() == 3 || item.getType() == 4 || */item.getType() == 6 || item.getType() == 8){
				imageView = ((ImageView) view.findViewById(R.id.chevron));
				imageView.setVisibility(View.VISIBLE);
				TextView item_select = (TextView) view.findViewById(R.id.itemCount);
				item_select.setTag(index);
				item_select.setOnClickListener( new OnClickListener() {

					@Override
					public void onClick(View view) {
						if(mClickListener != null)
							mClickListener.onClick((Integer) view.getTag(),view);
					}

				});
			}/*else{
				((ImageView) view.findViewById(R.id.chevron)).setVisibility(View.GONE);
			}*/
		}
		/*else {
			if (item.getType() == 3  || item.getType() == 4 || item.getType() == 6){
				((ImageView) view.findViewById(R.id.chevron)).setVisibility(View.VISIBLE);
			}else{
				((ImageView) view.findViewById(R.id.chevron)).setVisibility(View.GONE);
			}
		}*/
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
	 * 获取当前视图
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
	public void setupBasicItemValue(View view, BasicItem item, int index) {

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
			}else if (v instanceof EditText_custom){
				ec1 = (EditText_custom)v;
				ec1.setCode(item.getCode());
				if (!TextUtils.isEmpty(item.getVal())){
					ec1.setText(item.getVal());
				}else{
					ec1.setText(item.getSubtitle());
				}
				ec1.setRules(item.getRule());
				ec1.setTag(this);
				if (editTextValidator != null){
					for (ValidationModel validationModel :editTextValidator.getValidationModels()){
						if (validationModel.getEditText() != null && validationModel.getEditText() instanceof EditText_custom){
							EditText_custom eCm = (EditText_custom) validationModel.getEditText();
							if (eCm.getCode().equals(ec1.getCode())){
								eCm.setText(item.getSubtitle());
							}
						}
					}
				}
			}else if (v instanceof CheckBox_custom){
				CheckBox_custom cbx = (CheckBox_custom)v;
				cbx.setCode(item.getCode());
				if (item.getSubtitle().equals("1") ){
					cbx.setChecked(true);
				}else{
					cbx.setChecked(false);
				}
				cbx.setTag(this);
			}

			/*//执行验证方法
			if (item.getRule()!=null && editTextValidator != null){
				editTextValidator.validate(item.getCode());
			}*/

		}
	}

	/**
	 * 刷新主视图
	 * @param mainData
	 */
	public void reshView(Map<String,Object> mainData,List<Map<String,Object>> maings_list){
		if (mainData == null){return;}
		try {
			BasicItem item = null;
			String code,val,method,resid="";
			List<IListItem> list = getIListItem();
			for (int i=0,n = list.size();i< n ;i++){
				item = (BasicItem) list.get(i);
				code = item.getCode();
				if (mainData.get(code)!= null){
					String value = "";
					if (maings_list != null){
						value = ActivityController.returnString(mainData,code,maings_list).replace(code,"");
					}else{
						value = mainData.get(code).toString();
					}
					item.setSubtitle(value);
				}
			}
			this.clear2();
			this.commit();
		}catch (Exception e){
			e.printStackTrace();
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
			}else if (v instanceof EditText_custom){
				ec1 = (EditText_custom)v;
				res = ec1.getText().toString();
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
		/*mItemList.clear();*/
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

	public ImageButton getButton_ok() {
		return button_ok;
	}

	public void setButton_ok(ImageButton button_ok) {
		this.button_ok = button_ok;
	}

	public EditTextValidator getEditTextValidator() {
		return editTextValidator;
	}

	public void setEditTextValidator(EditTextValidator editTextValidator) {
		this.editTextValidator = editTextValidator;
	}

	public List<Map<String, Object>> get_dataList() {
		return _dataList;
	}

	public void set_dataList(List<Map<String, Object>> _dataList) {
		this._dataList = _dataList;
	}

	public Map<String, Object> get_zhuData() {
		return _zhuData;
	}

	public void set_zhuData(Map<String, Object> _zhuData) {
		this._zhuData = _zhuData;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Context getContext1() {
		return context1;
	}

	public void setContext1(Context context1) {
		this.context1 = context1;
	}

	public List<Map<String, Object>> get_gxdataList() {
		return _gxdataList;
	}

	public void set_gxdataList(List<Map<String, Object>> _gxdataList) {
		this._gxdataList = _gxdataList;
	}

	public List<Map<String, Object>> get_removeList() {
		return _removeList;
	}

	public void set_removeList(List<Map<String, Object>> _removeList) {
		this._removeList = _removeList;
	}

	public String getMxIndex() {
		return mxIndex;
	}

	public void setMxIndex(String mxIndex) {
		this.mxIndex = mxIndex;
	}
}
