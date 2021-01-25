package com.cn.wti.util.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.BaseViewHolder;

/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class MyGridAdapter extends BaseAdapter {
	private Context mContext;
	private View view;

	public String[] img_text = null;
	public int[] imgs = null;

	public MyGridAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public String[] getImg_text() {
		return img_text;
	}

	public void setImg_text(String[] img_text) {
		this.img_text = img_text;
	}

	public int[] getImgs() {
		return imgs;
	}

	public void setImgs(int[] imgs) {
		this.imgs = imgs;
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
					R.layout.grid_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
		ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
		if (imgs != null){
			iv.setBackgroundResource(imgs[position]);
		}

		tv.setText(img_text[position]);
		return convertView;
	}

}
