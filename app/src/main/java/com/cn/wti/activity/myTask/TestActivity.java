package com.cn.wti.activity.myTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import com.cn.wti.activity.base.BaseActivity;
import com.wticn.wyb.wtiapp.R;

public class TestActivity extends BaseActivity {


    private LayoutInflater inflater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_test);
        this.inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
