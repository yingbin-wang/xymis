package com.cn.wti.entity.view.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import static com.cn.wti.entity.view.pulltorefresh.PullToRefreshLayout.REFRESHING;

/**
 * Created by wyb on 2016/12/4.
 */

public class TestPullableScrollView extends ScrollView implements Pullable{

    private  int mLastMotionY,mLastMotionX;
    public TestPullableScrollView(Context context)
    {
        super(context);
    }

    public TestPullableScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TestPullableScrollView(Context context, AttributeSet attrs, int defStyle)
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        int x = (int) e.getRawX();
        boolean resume = false;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 发生down事件时,记录y坐标
                mLastMotionY = y;
                mLastMotionX = x;
                resume = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                int deltaY = y - mLastMotionY;
                int deleaX = x - mLastMotionX;

                if (Math.abs(deleaX) > Math.abs(deltaY)) {
                    resume = false;
                } else {
                    resume = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return resume;
    }

}
