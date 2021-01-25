package com.dina.ui.model;

public class BasicItem_List extends BasicItem {

	//标题 1 2 3 4 5 6
	private String title_1,title_2,title_3,title_4,title_5,title_6;

	//内容 1 2 3 4 5 6
	private String content_1,
					content_2,
					content_3,
					content_4,
					content_5,
					content_6,
					content_7,
					content_8,
					content_9;

	public int getApprovalstatus() {
		return approvalstatus;
	}

	public void setApprovalstatus(int approvalstatus) {
		this.approvalstatus = approvalstatus;
	}

	private int approvalstatus; //审批状态
	private int estatus;//单据状态

	/**
	 *
	 * @param code 控件编号
	 * @param _title 控件标题
	 * @param _color 颜色
	 * @param _clickable 是否点击事件
     * @param type 控件类型
     */
	public BasicItem_List(String id,String code, String _title,String mSubtitle,
							   String content_1, String content_2, String content_3,
							   String content_4, String content_5, String content_6,
							   int _color, boolean _clickable, int type) {
		this.id = id;
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = mSubtitle;
		this.content_1 = content_1;
		this.content_2 = content_2;
		this.content_3 = content_3;
		this.content_4 = content_4;
		this.content_5 = content_5;
		this.content_6 = content_6;

		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
	}

	//审批状态
	public BasicItem_List(String id,String code, String _title,String mSubtitle,
						  String content_1, String content_2, String content_3,
						  String content_4, String content_5, String content_6,
						  int _color, boolean _clickable, int type,int approvalstatus,int estatus) {
		this.id = id;
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = mSubtitle;
		this.content_1 = content_1;
		this.content_2 = content_2;
		this.content_3 = content_3;
		this.content_4 = content_4;
		this.content_5 = content_5;
		this.content_6 = content_6;

		this.approvalstatus = approvalstatus;
		this.estatus = estatus;

		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
	}

	/**
	 * 五列内容
	 * @param id
	 * @param code
	 * @param _title
	 * @param mSubtitle
	 * @param content_1
	 * @param content_2
	 * @param content_3
	 * @param content_4
	 * @param _color
     * @param _clickable
     * @param type
     */
	public BasicItem_List(String id,String code,String _title,String mSubtitle,String content_1, String content_2, String content_3,
						  String content_4, int _color, boolean _clickable, int type) {
		this.id = id;
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = mSubtitle;
		this.content_1 = content_1;
		this.content_2 = content_2;
		this.content_3 = content_3;
		this.content_4 = content_4;
	/*	this.content_5 = content_5;
		this.content_6 = content_6;*/

		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
	}

	public BasicItem_List(String id,String code,String _title,String mSubtitle,int _color, boolean _clickable, int type) {
		this.id = id;
		this.code = code;
		this.mTitle = _title;
		this.mSubtitle = mSubtitle;
/*		this.content_1 = content_1;
		this.content_2 = content_2;
		this.content_3 = content_3;
		this.content_4 = content_4;
		this.content_5 = content_5;
		this.content_6 = content_6;*/

		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
	}

	//审批状态
	public BasicItem_List(String id,String code, String[] titles,String[] contents,
						  int _color, boolean _clickable, int type,int approvalstatus,int estatus) {
		this.id = id;
		this.code = code;

		if(titles != null){
			for (int i=0,n=titles.length;i<n;i++){
				switch (i+1){
					case 1:
						this.title_1 = titles[i];
						break;
					case 2:
						this.title_2 = titles[i];
						break;
					case 3:
						this.title_3 = titles[i];
						break;
					case 4:
						this.title_4 = titles[i];
						break;
					case 5:
						this.title_5 = titles[i];
						break;
					case 6:
						this.title_6 = titles[i];
						break;

					default:
						break;
				}
			}
		}

		if(contents != null){
			for (int i=0,n=contents.length;i<n;i++){
				switch (i+1){
					case 1:
						this.mTitle = contents[i];
						break;
					case 2:
						this.mSubtitle = contents[i];
						break;
					case 3:
						this.content_1 = contents[i];
						break;
					case 4:
						this.content_2 = contents[i];
						break;
					case 5:
						this.content_3 = contents[i];
						break;
					case 6:
						this.content_4 = contents[i];
						break;
					case 7:
						this.content_5 = contents[i];
						break;
					case 8:
						this.content_6 = contents[i];
						break;

					default:
						break;
				}
			}
		}

		this.approvalstatus = approvalstatus;
		this.estatus = estatus;

		this.mClickable = _clickable;
		this.type = type;
		this.mColor = _color;
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

	public String getContent_1() {
		return content_1;
	}

	public void setContent_1(String content_1) {
		this.content_1 = content_1;
	}

	public String getContent_2() {
		return content_2;
	}

	public void setContent_2(String content_2) {
		this.content_2 = content_2;
	}

	public String getContent_3() {
		return content_3;
	}

	public void setContent_3(String content_3) {
		this.content_3 = content_3;
	}

	public String getContent_4() {
		return content_4;
	}

	public void setContent_4(String content_4) {
		this.content_4 = content_4;
	}

	public String getContent_5() {
		return content_5;
	}

	public void setContent_5(String content_5) {
		this.content_5 = content_5;
	}

	public String getContent_6() {
		return content_6;
	}

	public void setContent_6(String content_6) {
		this.content_6 = content_6;
	}

	public int getEstatus() {
		return estatus;
	}

	public void setEstatus(int estatus) {
		this.estatus = estatus;
	}

	public String getTitle_1() {
		return title_1;
	}

	public void setTitle_1(String title_1) {
		this.title_1 = title_1;
	}

	public String getTitle_2() {
		return title_2;
	}

	public void setTitle_2(String title_2) {
		this.title_2 = title_2;
	}

	public String getTitle_3() {
		return title_3;
	}

	public void setTitle_3(String title_3) {
		this.title_3 = title_3;
	}

	public String getTitle_4() {
		return title_4;
	}

	public void setTitle_4(String title_4) {
		this.title_4 = title_4;
	}

	public String getTitle_5() {
		return title_5;
	}

	public void setTitle_5(String title_5) {
		this.title_5 = title_5;
	}

	public String getTitle_6() {
		return title_6;
	}

	public void setTitle_6(String title_6) {
		this.title_6 = title_6;
	}
}
