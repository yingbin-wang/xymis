package com.ease.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import com.ease.ui.EaseGaodeMapActivity;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;

/**
 * Created by wyb on 2017/7/28.
 */

public class EaseChatRowLocation extends com.hyphenate.easeui.widget.chatrow.EaseChatRowLocation {

    public EaseChatRowLocation(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onBubbleClick() {
        Intent intent = new Intent(context, EaseGaodeMapActivity.class);
        intent.putExtra("latitude", locBody.getLatitude());
        intent.putExtra("longitude", locBody.getLongitude());
        intent.putExtra("address", locBody.getAddress());
        activity.startActivity(intent);
    }
}
