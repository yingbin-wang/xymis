package com.cn.wti.entity.view.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.dina.ui.widget.UIListTableView;

import org.w3c.dom.ls.LSOutput;

/**
 * Created by wyb on 2016/12/4.
 */

public class PullableScrollView extends ScrollView implements Pullable{

    public PullableScrollView(Context context)
    {
        super(context);
    }

    public PullableScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullableScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown()
    {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean canPullUp()
    {
        if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
            return true;
        else
            return false;
    }

}
