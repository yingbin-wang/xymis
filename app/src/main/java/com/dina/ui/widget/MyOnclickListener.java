package com.dina.ui.widget;

/**
 * Created by wyb on 2016/12/13.
 */

import android.view.View;
import android.widget.LinearLayout;

import com.wticn.wyb.wtiapp.R;
import com.dina.ui.model.BasicItem_List;
import com.dina.ui.model.IListItem;

import java.util.List;
import java.util.Map;

/**
 * 添加 查看 与 删除事件
 */
public class  MyOnclickListener implements View.OnClickListener {

    private ClickListener mClickListener;
    private LinearLayout mListContainer;
    private List<IListItem> mItemList;
    private List<Map<String,Object>> mxDataList;
    private UITableMxView tableMxView;

    public MyOnclickListener(ClickListener mClickListener,LinearLayout mListContainer,List<IListItem> mItemList,List<Map<String,Object>> mxDataList,UITableMxView tableMxView){
        this.mClickListener = mClickListener;
        this.mListContainer = mListContainer;
        this.mItemList = mItemList;
        this.mxDataList  = mxDataList;
        this.tableMxView = tableMxView;
    }
    @Override
    public void onClick(View view) {

        int postion = (int) view.getTag();
        View v = null;
        switch (view.getId()){
            case  R.id.btn_edit:
                if(mClickListener != null){
                    v = mListContainer.getChildAt(postion);
                    mClickListener.onClick((Integer) v.getTag(),view);
                }
                break;
            case R.id.btn_delte:
                BasicItem_List item = (BasicItem_List) mItemList.get(postion);
                String id = item.getId();
                mItemList.remove(postion);
                mListContainer.removeViewAt(postion);
                if(mxDataList != null && mxDataList.size() >0){
                    mxDataList.remove(postion);
                    tableMxView.clear2();
                    tableMxView.commit();
                }
                break;
            default:
                break;
        }
    }
}