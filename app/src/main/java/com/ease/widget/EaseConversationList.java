package com.ease.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.ease.adpter.EaseConversationAdapter;
import com.hyphenate.chat.EMConversation;
import java.util.List;

public class EaseConversationList extends com.hyphenate.easeui.widget.EaseConversationList {

    public EaseConversationList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(List<EMConversation> conversationList){
        this.init(conversationList, null);
    }

    @Override
    public void init(List<EMConversation> conversationList, EaseConversationListHelper helper){
        conversations = conversationList;
        if(helper != null){
            this.conversationListHelper = helper;
        }
        adapter = new EaseConversationAdapter(context, 0, conversationList);
        adapter.setCvsListHelper(conversationListHelper);
        adapter.setPrimaryColor(primaryColor);
        adapter.setPrimarySize(primarySize);
        adapter.setSecondaryColor(secondaryColor);
        adapter.setSecondarySize(secondarySize);
        adapter.setTimeColor(timeColor);
        adapter.setTimeSize(timeSize);
        setAdapter(adapter);
    }

}
