package com.cn.wti.util.app;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by wyb on 2017/5/24.
 */

public class RecyclerViewUtils {

    /**
     * 设置显示高度 为 几行
     * @param mContext
     * @param recyclerView
     * @param hs
     */
    public static void setLayoutManagerHeight(Context mContext, RecyclerView recyclerView,int hs){
        final int finalhs = hs;

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
                if (getChildCount() > 0) {
                    View firstChildView = null;
                    try {
                        firstChildView = recycler.getViewForPosition(0);
                        measureChild(firstChildView, widthSpec, heightSpec);
                        setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), firstChildView.getMeasuredHeight()* finalhs);
                    }catch (Exception e){}
                    if (firstChildView == null){
                        super.onMeasure(recycler, state, widthSpec, heightSpec);
                    }

                } else {
                    super.onMeasure(recycler, state, widthSpec, heightSpec);
                }
            }
        });
    }




}
