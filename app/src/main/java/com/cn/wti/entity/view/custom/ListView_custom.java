package com.cn.wti.entity.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

/**
 * Created by wangz on 2016/10/28.
 */
public class ListView_custom extends ListView{

    private Map<String,Object> dataMap;
    private List<View> views;

    public ListView_custom(Context context) {
        super(context);
    }

    public ListView_custom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListView_custom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        super.setOnScrollListener(new MyOnScrollListener());
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }


    public class  MyOnScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState){
                case OnScrollListener.SCROLL_STATE_FLING:
                    Log.v("test","手指滑动");
                    break;
                case OnScrollListener.SCROLL_STATE_IDLE:
                    Log.v("test","停止滑动");
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    Log.v("test","正在滑动");
                    break;
                default:
                    break;
            }
            Log.v("Test",String.valueOf(scrollState));
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            /*ActivityController.edit_listView_MapData2(view,firstVisibleItem,visibleItemCount,dataMap,views);*/
        }
    }




}
