package com.cn.wti.entity.view;

import java.util.Collections;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.view.View;

public class ChannelInfoBean implements Comparable<ChannelInfoBean>{

	private int id;  
    private String name;  
    private Drawable icon;  
    private String iconUrl;  
    private int iconID;  
    private String describtion;  
    private int type;
    private int order;  
    private onGridViewItemClickListener onClickListener;  
      
  
    public ChannelInfoBean(String name, int iconID, int order) {  
        super();  
        this.name = name;  
        this.iconID = iconID;  
        this.order = order;  
    }  
    public ChannelInfoBean(String name, int iconID, int order,onGridViewItemClickListener onClickListener) {  
        super();  
        this.name = name;  
        this.iconID = iconID;  
        this.order = order;  
        this.onClickListener=onClickListener;  
    }

    public ChannelInfoBean(int id, String name,int iconID, String iconUrl, int order) {
        super();
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.iconID = iconID;
        this.order = order;
    }

    public ChannelInfoBean(int id, String name, Drawable icon, String iconUrl, int iconID, int type, int order, String describtion) {  
        super();  
        this.id = id;  
        this.name = name;  
        this.icon = icon;  
        this.iconUrl = iconUrl;  
        this.iconID = iconID;  
        this.type = type;  
        this.order = order;  
        this.describtion = describtion;  
    }  
  
      
      
      
    public onGridViewItemClickListener getOnClickListener() {  
        return onClickListener;  
    }  
    public void setOnClickListener(onGridViewItemClickListener onClickListener) {  
        this.onClickListener = onClickListener;  
    }  
    public String getIconUrl() {  
        return iconUrl;  
    }  
  
    public void setIconUrl(String iconUrl) {  
        this.iconUrl = iconUrl;  
    }  
  
    public int getIconID() {  
        return iconID;  
    }  
  
    public void setIconID(int iconID) {  
        this.iconID = iconID;  
    }  
  
    public String getIconurl() {  
        return iconUrl;  
    }  
  
    public void setIconurl(String iconurl) {  
        this.iconUrl = iconurl;  
    }  
  
    public int getId() {  
        return id;  
    }  
  
    public void setId(int id) {  
        this.id = id;  
    }  
  
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public Drawable getIcon() {  
        return icon;  
    }  
  
    public void setIcon(Drawable icon) {  
        this.icon = icon;  
    }  
  
    public int getType() {  
        return type;  
    }  
  
    public void setType(int type) {  
        this.type = type;  
    }  
  
    public int getOrder() {  
        return order;  
    }  
  
    public void setOrder(int order) {  
        this.order = order;  
    }  
  
    public String getDescribtion() {  
        return describtion;  
    }  
  
    public void setDescribtion(String describtion) {  
        this.describtion = describtion;  
    }  
  
    @Override  
    public int compareTo(ChannelInfoBean info) {  
        if (info != null) {  
            if (this.getOrder() > info.getOrder()) {  
                return 1;  
            } else {  
                return -1;  
            }  
        } else {  
            return 0;  
        }  
    }  

    public static List<ChannelInfoBean> getOrderList(List<ChannelInfoBean> list) {  
        Collections.sort(list);  
        return list;  
    }  
      
      
      
    public interface onGridViewItemClickListener  
    {  
        public abstract void ongvItemClickListener(int position);  
    }  
      
}
