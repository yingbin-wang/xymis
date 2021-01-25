package com.cn.wti.activity.tab;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerTabActivity extends Activity{

    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private RadioButton tab1;
    private RadioButton tab2;
    private RadioButton tab3;
    private List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewPager();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.tab1:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.tab2:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.tab3:
                        viewPager.setCurrentItem(2);
                        break;
                }
            }
        });
    }

    private void initViewPager() {
        viewList = new ArrayList<View>();
        viewList.add(LayoutInflater.from(this).inflate(R.layout.page_layout,null));
        viewList.add(LayoutInflater.from(this).inflate(R.layout.page_layout,
                null));
        viewList.add(LayoutInflater.from(this).inflate(R.layout.page_layout,
                null));
        viewPager.setAdapter(new MyViewPagerAdapter());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageSelected(int arg0) {
                switch (arg0) {
                    case 0:
                        tab1.setChecked(true);
                        tab2.setChecked(false);
                        tab3.setChecked(false);
                        break;
                    case 1:
                        tab1.setChecked(false);
                        tab2.setChecked(true);
                        tab3.setChecked(false);
                        break;
                    case 2:
                        tab1.setChecked(false);
                        tab2.setChecked(false);
                        tab3.setChecked(true);
                        break;
                }
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private class MyViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = viewList.get(position);
            container.addView(view);
            TextView textView = (TextView) view
                    .findViewById(R.id.page_textview);
            textView.setText("第" + position + "页的数据");
            return view;
        }

    }

}
