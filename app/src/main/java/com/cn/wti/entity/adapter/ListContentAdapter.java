package com.cn.wti.entity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cn.wti.activity.base.BaseListActivity;
import com.cn.wti.entity.view.custom.Menutbl;
import com.wticn.wyb.wtiapp.R;

import java.util.List;

/**
 * Created by wyb on 2016/11/16.
 */

public class ListContentAdapter extends BaseAdapter {

    private List<Menutbl> menus;
    private Context context;

    public ListContentAdapter(Context context) {
        this.context = context;
    }

    public List<Menutbl> getMenus() {
        return menus;
    }

    public void setMenus(List<Menutbl> menus) {
        this.menus = menus;
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Object getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            LayoutInflater inflater = LayoutInflater
                    .from(context);
            view = inflater.inflate(R.layout.pop_list_item, null);

            ViewHolder holder = new ViewHolder();
            holder.txtName = (TextView) view.findViewById(R.id.txt_name);
            holder.btnDel = (ImageButton) view.findViewById(R.id.btnDel);

            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.txtName.setText("" + menus.get(position).getName() + "");

        return view;
    }

    class ViewHolder {
        public TextView txtName;
        public ImageButton btnDel;
    }
}

