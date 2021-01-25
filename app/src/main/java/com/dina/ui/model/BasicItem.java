package com.dina.ui.model;

import com.dina.ui.widget.UITableMxView;

import java.util.Map;

public class BasicItem implements IListItem {
	
	protected boolean mClickable = true;
	private int mDrawable = -1;
	protected  String id;
	protected String code;
	private String name;
	protected String mTitle;
	protected String mSubtitle;
	protected int mColor = -1;
	protected int type;
	protected int index;
	private String dataType;
	private Object is_required;
	private String val;
	private Map<String,Object> actionMap;


	private String gs;
	private  String rule;
	private String[] gs_cols;
	private String[] items;

	private UITableMxView uiTableMxView;
	

	public BasicItem(String _title) {
		this.mTitle = _title;
	}
	
	public BasicItem(String _title, String _subtitle) {
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
	}
	
	public BasicItem(String _title, String _subtitle,int _color) {
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mColor = _color;
	}

	public BasicItem(String _title, String _subtitle,int _color, boolean _clickable,int type) {
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
	}

	/**
	 *
	 * @param code 控件编号
	 * @param _title 控件标题
	 * @param _subtitle 控件内容
	 * @param _color 颜色
	 * @param _clickable 是否点击事件
     * @param type 控件类型
     */
	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
	}

	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type,String dataType) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.dataType = dataType;
	}

	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type,String dataType,Object is_required) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.dataType = dataType;
		this.is_required = is_required;
	}

	/**
	 *
	 * @param code 控件编号
	 * @param _title 控件标题
	 * @param _subtitle 控件内容
	 * @param _color 颜色
	 * @param _clickable 是否点击事件
	 * @param type 控件类型
	 */
	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type,String[] gs_cols,String gs) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.gs_cols = gs_cols;
		this.gs = gs;
	}

	/**
	 * 只带公式
	 * @param code
	 * @param _title
	 * @param _subtitle
	 * @param _color
	 * @param _clickable
	 * @param type
	 * @param gs_cols
     * @param gs
     * @param dataType
     */
	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type,String[] gs_cols,String gs,String dataType) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.gs_cols = gs_cols;
		this.gs = gs;
		this.dataType = dataType;
	}

	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type,String[] gs_cols,String gs,String dataType,Object is_required) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.gs_cols = gs_cols;
		this.gs = gs;
		this.dataType = dataType;
		this.is_required = is_required;
	}

	/**
	 * 只带验证规则
	 * @param code
	 * @param _title
	 * @param _subtitle
	 * @param _color
	 * @param _clickable
	 * @param type
	 * @param rule
     * @param dataType
     */
	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type,String rule,String dataType) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.rule = rule;
		this.dataType = dataType;
	}

	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type,String rule,String dataType,Object is_required) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.rule = rule;
		this.dataType = dataType;
		this.is_required = is_required;
	}

	/**
	 * 带验证表单功能
	 * @param code 字段编号
	 * @param _title 字段名称
	 * @param _subtitle 字段值
	 * @param _color 颜色
	 * @param _clickable 是否点击
	 * @param type 类型
	 * @param gs_cols 规则字段
	 * @param gs 公式
     * @param rules 规则
     * @param dataType
     */
	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type,String[] gs_cols,String gs,String rules,String dataType) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.gs_cols = gs_cols;
		this.gs = gs;
		this.rule = rules;
		this.dataType = dataType;
	}

	public BasicItem(String code,String _title, String _subtitle,int _color, boolean _clickable,int type,String[] gs_cols,String gs,String rules,String dataType,Object is_required) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.gs_cols = gs_cols;
		this.gs = gs;
		this.rule = rules;
		this.dataType = dataType;
		this.is_required = is_required;
	}

	public BasicItem(String code, String _title, String _subtitle, int _color, boolean _clickable, int type, Map<String,Object> actionMap, String gs, String rules, String dataType, Object is_required) {
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
		this.actionMap = actionMap;
		this.gs = gs;
		this.rule = rules;
		this.dataType = dataType;
		this.is_required = is_required;
	}


	
	public BasicItem(String _title, String _subtitle, boolean _clickable) {
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mClickable = _clickable;
	}	
	
	public BasicItem(int _drawable, String _title, String _subtitle) {
		this.mDrawable = _drawable;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
	}
	
	public BasicItem(int _drawable, String _title, String _subtitle, int _color) {
		this.mDrawable = _drawable;
		this.mTitle = _title;
		this.mSubtitle = _subtitle;
		this.mColor = _color;
	}

	public BasicItem() {
	}

	public int getDrawable() {
		return mDrawable;
	}

	public void setDrawable(int drawable) {
		this.mDrawable = drawable;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getSubtitle() {
		return mSubtitle;
	}

	public void setSubtitle(String summary) {
		this.mSubtitle = summary;
	}

	public int getColor() {
		return mColor;
	}

	public void setColor(int mColor) {
		this.mColor = mColor;
	}

	@Override
	public boolean isClickable() {
		return mClickable;
	}

	@Override
	public void setClickable(boolean clickable) {
		mClickable = clickable;			
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

	public String[] getGs_cols() {
		return gs_cols;
	}

	public void setGs_cols(String[] gs_cols) {
		this.gs_cols = gs_cols;
	}

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		this.items = items;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public UITableMxView getUiTableMxView() {
		return uiTableMxView;
	}

	public void setUiTableMxView(UITableMxView uiTableMxView) {
		this.uiTableMxView = uiTableMxView;
	}

	public Object getIs_required() {
		return is_required;
	}

	public void setIs_required(Object is_required) {
		this.is_required = is_required;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public Map<String, Object> getActionMap() {
		return actionMap;
	}

	public void setActionMap(Map<String, Object> actionMap) {
		this.actionMap = actionMap;
	}
}
