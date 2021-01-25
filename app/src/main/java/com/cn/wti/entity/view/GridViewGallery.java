package com.cn.wti.entity.view;

import java.util.ArrayList;
import java.util.List;

import com.cn.wti.entity.adapter.ViewPager_GV_ItemAdapter;
import com.cn.wti.entity.adapter.ViewPager_GridView_Adapter;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;



public class GridViewGallery extends LinearLayout {

    private Context context;
    /** 保存实体对象链表 */
    private List<ChannelInfoBean> list;
    private ViewPager viewPager;
    private LinearLayout ll_dot;
    private ImageView[] dots;
    /** ViewPager当前页 */
    private int currentIndex;
    /** ViewPager页数 */
    private int viewPager_size;
    /** 默认一页12个item */
    private int pageItemCount = 12;

    /** 保存每个页面的GridView视图 */
    private List<View> list_Views;
    public static final String TAG = "GridViewGallery";

    public GridViewGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e(TAG, "进入默认的构造方法");
        this.context = context;
        this.list = null;
        initView();
        Log.e(TAG, "initView()方法完成");
    }


    @SuppressWarnings("unchecked")
    public GridViewGallery(Context context, List<?> list) {
        super(context);
        Log.e(TAG, "进入list的构造方法");
        this.context = context;
        this.list = (List<ChannelInfoBean>) list;
        initView();
        initDots();
        setAdapter();
    }

    private void setAdapter() {
        list_Views = new ArrayList<View>();
        for (int i = 0; i < viewPager_size; i++) {
            list_Views.add(getViewPagerItem(i));
        }
        viewPager.setAdapter(new ViewPager_GridView_Adapter(list_Views));
    }

    private void initView() {
        Log.e(TAG, "初始化View");
        View view = LayoutInflater.from(context).inflate(R.layout.channel_activity, null);
        viewPager = (ViewPager) view.findViewById(R.id.vPager);
        ll_dot = (LinearLayout) view.findViewById(R.id.ll_channel_dots);
        Log.e(TAG, "添加View");
        addView(view);
    }

    // 初始化底部小圆点
    private void initDots() {

        // 根据屏幕宽度高度计算pageItemCount
        int width = AppUtils.getWindowWidth(context);
        int high = AppUtils.getWindowHeight(context);

        int col = (width / 300) > 2 ? (width /300) : 3;
        int row = (high/400) >4 ? (high/400):3;

        pageItemCount = col * row;  //每一页可装item
        int t=1;
        if(list.size() % pageItemCount==0){
            t=0;
        }
        viewPager_size = list.size() / pageItemCount + t;

        if (0 < viewPager_size) {
            ll_dot.removeAllViews();
            if (1 == viewPager_size) {
                ll_dot.setVisibility(View.GONE);
            } else if (1 < viewPager_size) {
                ll_dot.setVisibility(View.VISIBLE);
                for (int j = 0; j < viewPager_size; j++) {
                    ImageView image = new ImageView(context);
                    LayoutParams params = new LayoutParams(20, 20);  //dot的宽高
                    params.setMargins(3, 0, 3, 0);
                    image.setBackgroundResource(R.mipmap.play_hide);
                    ll_dot.addView(image, params);
                }
            }
        }
        if (viewPager_size != 1) {
            dots = new ImageView[viewPager_size];
            for (int i = 0; i < viewPager_size; i++) {
                //从布局中填充dots数组
                dots[i] = (ImageView) ll_dot.getChildAt(i);
                //dots[i].setEnabled(true);
                //dots[i].setTag(i);
            }
            currentIndex = 0;  //当前页
            //dots[currentIndex].setEnabled(false);
            dots[currentIndex].setBackgroundResource(R.mipmap.play_display);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    setCurDot(arg0);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

    /** 当前底部小圆点 */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > viewPager_size - 1 || currentIndex == positon) {
            return;
        }
        for(int i=0;i<dots.length;i++){
            dots[i].setBackgroundResource(R.mipmap.play_hide);
        }
        //dots[positon].setEnabled(false);
        // dots[currentIndex].setEnabled(true);
        dots[positon].setBackgroundResource(R.mipmap.play_display);
        currentIndex = positon;
    }

    //ViewPager中每个页面的GridView布局
    private View getViewPagerItem(int index) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.channel_viewpage_gridview, null);
        GridView gridView = (GridView) layout.findViewById(R.id.vp_gv);

        int width = AppUtils.getWindowWidth(context);
        int col = (width / 300) > 2 ? (width /300) : 3;

        gridView.setNumColumns(col);

        //每个页面GridView的Adpter
        ViewPager_GV_ItemAdapter adapter = new ViewPager_GV_ItemAdapter(context, list, index, pageItemCount);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (null != list.get(position + currentIndex * pageItemCount).getOnClickListener())
                    list.get(position + currentIndex * pageItemCount).getOnClickListener().ongvItemClickListener(position + currentIndex * pageItemCount);
            }
        });
        return gridView;
    }

    //宽
    public int getViewWidth(View view){
        view.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return view.getMeasuredWidth();
    }
    //高
    public int getViewHeight(View view){
        view.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return view.getMeasuredHeight();
    }

}
