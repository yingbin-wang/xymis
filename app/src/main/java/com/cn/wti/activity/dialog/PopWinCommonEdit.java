package com.cn.wti.activity.dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wticn.wyb.wtiapp.R;

import java.util.List;
import java.util.Map;

/**
 * 项目名称：translate
 * 实现功能： 翻译详情界面，分享弹出窗口
 * 类名称：PopWinShare
 * 类描述：(该类的主要功能)
 * @version
 */
public class PopWinCommonEdit extends PopupWindow{
    private View mainView;
    private LinearLayout layoutAdd, layoutSave, layoutDelete, layoutCheck, layoutUncheck, layoutFujian;
    private TextView badgeView = null;
    private List<String> qxList = null;

    public PopWinCommonEdit(Activity paramActivity, View.OnClickListener paramOnClickListener, int paramInt1, int paramInt2, List<String> qxList){
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(R.layout.popwin_commonedit, null);
        //新增
        layoutAdd = ((LinearLayout)mainView.findViewById(R.id.layout_add));
        //保存
        layoutSave = (LinearLayout)mainView.findViewById(R.id.layout_save);
        //删除
        layoutDelete = (LinearLayout)mainView.findViewById(R.id.layout_delete);
        //审核
        layoutCheck = (LinearLayout)mainView.findViewById(R.id.layout_check);
        //撤审
        layoutUncheck = (LinearLayout)mainView.findViewById(R.id.layout_uncheck);
        //附件
        layoutFujian = (LinearLayout)mainView.findViewById(R.id.layout_fujian);
        badgeView = mainView.findViewById(R.id.badgeText);

        this.qxList = qxList;

        //设置每个子布局的事件监听器
        if (paramOnClickListener != null){
            layoutAdd.setOnClickListener(paramOnClickListener);
            layoutSave.setOnClickListener(paramOnClickListener);
            layoutDelete.setOnClickListener(paramOnClickListener);
            layoutCheck.setOnClickListener(paramOnClickListener);
            layoutUncheck.setOnClickListener(paramOnClickListener);
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

    public void setEstatus(Map<String,Object> main_data,boolean isUpdate){

        int estatus = 0,approvalstatus=0;
        if (main_data.get("estatus") != null && !main_data.get("estatus") .equals("")){
            estatus = Integer.parseInt(main_data.get("estatus").toString());
        }else{
            estatus =1;
        }

        if(main_data.get("approvalstatus") != null){
            approvalstatus = Integer.parseInt(main_data.get("approvalstatus").toString());
        }else{
            approvalstatus = 0;
        }
        setView(R.id.layout_fujian,true);
        setView(R.id.layout_add,true);
        if(main_data.get("estatus") != null && estatus == 7){
            setView(R.id.layout_save,true);
            setView(R.id.layout_uncheck,true);
            //隐藏
            setView(R.id.layout_delete,false);
            setView(R.id.layout_check,false);
            //setHeight(240);

        }else if(main_data.get("estatus") != null && estatus == 1 && main_data.get("approvalstatus")!= null && approvalstatus == 1){
            //隐藏
            setView(R.id.layout_save,false);
            setView(R.id.layout_delete,false);
            setView(R.id.layout_check,false);
            setView(R.id.layout_uncheck,false);
            //setHeight(192);

        }else if(main_data.get("estatus") != null && estatus == 1){
            setView(R.id.layout_save,true);
            setView(R.id.layout_delete,true);
            if (isUpdate){
                setView(R.id.layout_check,false);
                //setHeight(320);
            }else{
                setView(R.id.layout_check,true);
                //setHeight(400);
            }
            //隐藏
            setView(R.id.layout_uncheck,false);

        }else if(main_data.get("estatus") == null){
            setView(R.id.layout_save,true);
            //隐藏
            setView(R.id.layout_delete,false);
            setView(R.id.layout_check,false);
            setView(R.id.layout_uncheck,false);
            setView(R.id.layout_fujian,false);
            //setHeight(160);
        }
    }

    private void setView(int id,boolean state){
        if (R.id.layout_save == id && qxList.contains("save") && state){
            mainView.findViewById(id).setVisibility(View.VISIBLE);
        }else if (R.id.layout_delete == id && qxList.contains("delete") && state){
            mainView.findViewById(id).setVisibility(View.VISIBLE);
        }else if (R.id.layout_check == id && qxList.contains("check") && state){
            mainView.findViewById(id).setVisibility(View.VISIBLE);
        }else if (R.id.layout_uncheck == id && qxList.contains("uncheck") && state){
            mainView.findViewById(id).setVisibility(View.VISIBLE);
        }else if (R.id.layout_add == id && qxList.contains("add") && state){
            mainView.findViewById(id).setVisibility(View.VISIBLE);
        }else if (R.id.layout_fujian == id && qxList.contains("fj") && state){
            mainView.findViewById(id).setVisibility(View.VISIBLE);
        }else{
            mainView.findViewById(id).setVisibility(View.GONE);
        }

    }
}