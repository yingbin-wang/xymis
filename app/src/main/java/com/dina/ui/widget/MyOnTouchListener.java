package com.dina.ui.widget;

import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by wyb on 2016/12/13.
 */

public class  MyOnTouchListener implements View.OnTouchListener {

    private HorizontalScrollView slideView;
    private View action;

    MyOnTouchListener(HorizontalScrollView slideView,View action){
        this.slideView = slideView;
        this.action = action;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (view != null) {
                    slideView.smoothScrollTo(0, 0);
                }
            case MotionEvent.ACTION_UP:
                // 获得HorizontalScrollView滑动的水平方向值.
                int scrollX = slideView.getScrollX();

                // 获得操作区域的长度
                int actionW = action.getWidth();

                // 注意使用smoothScrollTo,这样效果看起来比较圆滑,不生硬
                // 如果水平方向的移动值<操作区域的长度的一半,就复原
                if (scrollX < actionW / 2)
                {
                    slideView.smoothScrollTo(0, 0);
                }
                else// 否则的话显示操作区域
                {
                    slideView.smoothScrollTo(actionW, 0);
                }
                return true;
        }
        return false;
    }
}