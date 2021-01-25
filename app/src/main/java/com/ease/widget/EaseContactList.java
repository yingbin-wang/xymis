package com.ease.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ease.adpter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import java.util.ArrayList;
import java.util.List;

public class EaseContactList extends com.hyphenate.easeui.widget.EaseContactList {


    public EaseContactList(Context context) {
        super(context);
    }

    public EaseContactList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseContactList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
    * init view
    */
    @Override
    public void init(List<EaseUser> contactList){
        this.contactList = contactList;
        adapter = new EaseContactAdapter(context, 0, new ArrayList<EaseUser>(contactList));
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                .setInitialLetterColor(initialLetterColor);
        listView.setAdapter(adapter);

        if(showSiderBar){
            sidebar.setListView(listView);
        }
    }

    /*
    * init view
    */
    public void init(List<EaseUser> contactList,int res){
        this.contactList = contactList;
        adapter = new EaseContactAdapter(context, res, new ArrayList<EaseUser>(contactList));
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                .setInitialLetterColor(initialLetterColor);
        listView.setAdapter(adapter);

        if(showSiderBar){
            sidebar.setListView(listView);
        }
    }
}
