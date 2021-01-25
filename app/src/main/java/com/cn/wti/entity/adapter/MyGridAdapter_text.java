package com.cn.wti.entity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.wti.util.BaseViewHolder;
import com.wticn.wyb.wtiapp.R;

/**
 * Created by wyb on 2017/6/10.
 */

public class MyGridAdapter_text extends BaseAdapter {
    private Context mContext;
    private View view;

    public String[] img_text = null;

    public MyGridAdapter_text(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public String[] getImg_text() {
        return img_text;
    }

    public void setImg_text(String[] img_text) {
        this.img_text = img_text;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_item_text, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
        tv.setText(img_text[position]);
        return convertView;
    }

}
