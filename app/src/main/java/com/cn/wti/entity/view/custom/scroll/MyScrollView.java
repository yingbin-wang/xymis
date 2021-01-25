package com.cn.wti.entity.view.custom.scroll;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ScrollView;

import com.cn.wti.util.app.AppUtils;

/**
 * Created by wyb on 2017/6/16.
 */

public class MyScrollView extends ScrollView {
    private Context mContext;
    private int height;

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            //最大高度显示为屏幕内容高度的一半
            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            DisplayMetrics d = new DisplayMetrics();
            display.getMetrics(d);

            //此处是关键，设置控件高度不能超过屏幕高度一半（d.heightPixels / 2）（在此替换成自己需要的高度）
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void reshHeight(int height){
        setHeight(height);
        measure(AppUtils.getScreenWidth(mContext),height);
    }
}