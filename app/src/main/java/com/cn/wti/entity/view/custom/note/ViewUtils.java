package com.cn.wti.entity.view.custom.note;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;

/**
 * Created by wyb on 2017/4/21.
 */

public class ViewUtils {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static float getLineSpacingExtra(Context context, TextView view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return view.getLineSpacingExtra();
        }
        else{
            return DisplayUtils.dip2px(context, 8);
        }
    }
}
