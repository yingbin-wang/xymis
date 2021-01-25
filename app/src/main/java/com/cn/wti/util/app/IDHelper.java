package com.cn.wti.util.app;

import android.content.Context;

/**
 * Created by wyb on 2017/4/19.
 */

public class IDHelper {
    public static int getLayout(Context mContext, String layoutName) {
        return ResourceHelper.getInstance(mContext).getLayoutId(layoutName);
    }

    public static int getViewID(Context mContext, String IDName) {
        return ResourceHelper.getInstance(mContext).getId(IDName);
    }

    public static int getDrawable(Context context, String drawableName) {
        return ResourceHelper.getInstance(context).getDrawableId(drawableName);
    }

    public static int getAttr(Context context, String attrName) {
        return ResourceHelper.getInstance(context).getAttrId(attrName);
    }

    public static int getString(Context context, String stringName) {
        return ResourceHelper.getInstance(context).getStringId(stringName);
    }

}