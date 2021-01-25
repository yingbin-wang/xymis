package com.cn.wti.activity.tab;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by wyb on 2016/11/20.
 */

public class TabListener<T extends Fragment>
        implements ActionBar.TabListener{
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    private Bundle bundle;
    public TabListener(Activity activity, String tag, Class<T> clz,Bundle bundle) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        this.bundle=bundle;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
        if(mFragment==null){
            mFragment=Fragment.instantiate(mActivity, mClass.getName());
            mFragment.setArguments(bundle);//向Fragment传递参数
            ft.add(android.R.id.content,mFragment, mTag);
        }else{
            ft.attach(mFragment);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

}