package com.hyphenate.easeui.model;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.adapter.EMAGroup;

/**
 * Created by wyb on 2017/6/8.
 */

public class EaseGroup extends EMGroup{

    private String name;

    public EaseGroup(EMAGroup emaGroup) {
        super(emaGroup);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
