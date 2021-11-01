package com.cn.wti.activity.dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cn.wti.entity.view.custom.badger.BadgeView;
import com.wticn.wyb.wtiapp.R;

/**
 * 项目名称：translate
 * 实现功能： 翻译详情界面，分享弹出窗口
 * 类名称：PopWinShare
 * 类描述：(该类的主要功能)
 * @version
 */
public class PopWinShare extends PopupWindow{
    private View mainView;
    private LinearLayout layoutPass, layoutNopass, layoutbohui, layoutWeituo, layoutFujian;
    private TextView badgeView = null;

    public PopWinShare(Activity paramActivity, View.OnClickListener paramOnClickListener, int paramInt1, int paramInt2){
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(R.layout.popwin_sp, null);
        //通过
        layoutPass = ((LinearLayout)mainView.findViewById(R.id.layout_pass));
        //不通过
        layoutNopass = (LinearLayout)mainView.findViewById(R.id.layout_nopass);
        //驳回
        layoutbohui = (LinearLayout)mainView.findViewById(R.id.layout_bohui);
        //委托
        layoutWeituo = (LinearLayout)mainView.findViewById(R.id.layout_weituo);
        //附件
        layoutFujian = (LinearLayout)mainView.findViewById(R.id.layout_fujian);
        badgeView = mainView.findViewById(R.id.badgeText);

        //设置每个子布局的事件监听器
        if (paramOnClickListener != null){
            layoutPass.setOnClickListener(paramOnClickListener);
            layoutNopass.setOnClickListener(paramOnClickListener);
            layoutbohui.setOnClickListener(paramOnClickListener);
            layoutWeituo.setOnClickListener(paramOnClickListener);
            layoutFujian.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);
        //设置显示隐藏动画
        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));

    }

    public void setBadgeText(int val){
        if (String.valueOf(val).equals("") || String.valueOf(val).equals("0")){
            badgeView.setVisibility(View.GONE);
        }else{
            badgeView.setText(String.valueOf(val));
            badgeView.setVisibility(View.VISIBLE);
        }

    }
}