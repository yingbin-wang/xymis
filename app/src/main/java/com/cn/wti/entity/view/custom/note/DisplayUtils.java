package com.cn.wti.entity.view.custom.note;

import android.content.Context;

/**
 * Created by wyb on 2017/4/21.
 */

public class DisplayUtils {
    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static float dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }
}
