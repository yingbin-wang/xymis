package com.ease.adpter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.wti.util.app.AppUtils;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.List;

public class EaseContactAdapter extends com.hyphenate.easeui.adapter.EaseContactAdapter{
    private static final String TAG = "ContactAdapter";

    public EaseContactAdapter(Context context, int resource, List<EaseUser> objects) {
        super(context, resource, objects);
    }
    
    private static class ViewHolder {
        ImageView avatar;
        TextView nameView;
        TextView headerView;
        TextView countView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            if(res == 0)
                convertView = layoutInflater.inflate(R.layout.ease_row_contact, parent, false);
            else
                convertView = layoutInflater.inflate(res, null);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.nameView = (TextView) convertView.findViewById(R.id.name);
            holder.headerView = (TextView) convertView.findViewById(R.id.header);

            //人数
            holder.countView = (TextView) convertView.findViewById(R.id.unread_msg_count);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        EaseUser user = getItem(position);
        if(user == null)
            Log.d("ContactAdapter", position + "");
        String username = user.getUsername();
        String userNick = user.getNick();
        String header = user.getInitialLetter();

        if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                holder.headerView.setVisibility(View.GONE);
            } else {
                holder.headerView.setVisibility(View.VISIBLE);
                holder.headerView.setText(header);
            }
        } else {
            holder.headerView.setVisibility(View.GONE);
        }

        if (holder.countView  != null && user.getCount() != 0){
            holder.countView.setText(String.valueOf(user.getCount()));
            holder.countView.setVisibility(View.VISIBLE);
        }

        if (user.getAvatar() != null){
            holder.avatar.setImageBitmap(AppUtils.convertStringToIconBase64(user.getAvatar()));
            EaseUser user1 = EaseUserUtils.getUserInfo(username);
            user1.setAvatar(user.getAvatar());
        }

        EaseUserUtils.setUserNick(userNick, holder.nameView);
        EaseUserUtils.setUserAvatar(getContext(), username, holder.avatar);

        if(primaryColor != 0)
            holder.nameView.setTextColor(primaryColor);
        if(primarySize != 0)
            holder.nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if(initialLetterBg != null)
            holder.headerView.setBackgroundDrawable(initialLetterBg);
        if(initialLetterColor != 0)
            holder.headerView.setTextColor(initialLetterColor);

        return convertView;
    }
    
}
