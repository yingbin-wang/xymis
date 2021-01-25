package com.cn.wti.entity.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.activity.myTask.MyTask_ExamineActivity;
import com.cn.wti.entity.parms.ListParms;
import com.cn.wti.entity.view.custom.button.SlidingButtonView;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/5/18.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements SlidingButtonView.IonSlidingButtonListener,View.OnClickListener{
    public List<Map<String,Object>> datas = null;
    Map<String,Object> data_map = null,setMap;
    private int screenWidth= 0,layoutid= 0;
    private boolean[] mCheck;
    private boolean sfVisible = false;
    private Context mContext;
    private SlidingButtonView mMenu = null;
    private Resources resources;
    private  String way_type = "";
    private int tempPosition= -1;
    private View main_form;

    public String getWay_type() {
        return way_type;
    }

    //列表Activity
    public TaskAdapter(Context mContext, List<Map<String,Object>> datas, int screenWidth ,View main_form, int layoutid, boolean[]mCheck) {
        this.datas = datas;
        this.screenWidth = screenWidth;
        this.layoutid = layoutid;
        this.mCheck = mCheck;
        this.mContext = mContext;
        this.resources = mContext.getResources();
        this.main_form = main_form;
    }

    public void reshData() {
        notifyDataSetChanged();
    }

    public boolean[] getSelectItem(){
        return  mCheck;
    }

    public List<Map<String,Object>> getDatas(){
        return datas;
    };

    public int getCount(){
        return  datas.size();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutid,viewGroup,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int finalposition = position;
        data_map = datas.get(position);
        viewHolder.myTask_examine_lvItem_cb.setChecked(false);

        final Map<String, Object>  finalmap = data_map;
        way_type = data_map.get("way_type").toString();

        String assignee = "";

        if (data_map.get("copy_assignee") != null && !data_map.get("copy_assignee").toString().equals("")){
            assignee = data_map.get("copy_assignee").toString();
        }else if (data_map.get("assignee_select") != null  && !"".equals(data_map.get("assignee_select").toString())){
            assignee = data_map.get("assignee_select").toString();
            data_map.put("copy_assignee",data_map.get("assignee_select"));
        }else{
            if (data_map.get("assignee")!= null){
                assignee = data_map.get("assignee").toString();
                data_map.put("copy_assignee",data_map.get("assignee"));
            }
        }

        final String finalassignee = assignee.replaceAll(",","<dh>");

        final TextView_custom ev =  viewHolder.myTask_examine_lvItem_jbr;
        ev.setCode("assignee_name");

        viewHolder.myTask_examine_lvItem_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                finalmap.put("checked",isChecked);
                if (isChecked){
                    if (way_type== null || way_type.equals("")){
                        tempPosition = finalposition;
                    }
                }
            }
        });

        viewHolder.myTask_examine_lvItem_cb.setEnabled(true);
        if (getCount() == 1) {
            viewHolder.myTask_examine_lvItem_cb.setChecked(true);
            viewHolder.myTask_examine_lvItem_cb.setEnabled(false);
            mCheck[position] = true;
        }else if(way_type.equals("排它网关")  && position == 0 && getCount() == 1){
            viewHolder.myTask_examine_lvItem_cb.setChecked(true);
            viewHolder.myTask_examine_lvItem_cb.setEnabled(false);
            mCheck[position] = true;
        }else if(way_type.equals("排它网关")  && position == 0){
            viewHolder.myTask_examine_lvItem_cb.setChecked(true);
            viewHolder.myTask_examine_lvItem_cb.setEnabled(false);
            mCheck[position] = true;
        }else if(way_type.equals("并行网关") || way_type.equals("包容网关")){
            viewHolder.myTask_examine_lvItem_cb.setChecked(true);
            viewHolder.myTask_examine_lvItem_cb.setEnabled(false);
            mCheck[position] = true;
        }else if(position == 0){
            if (tempPosition == -1){
                viewHolder.myTask_examine_lvItem_cb.setChecked(true);
                mCheck[position] = true;
            }else{
                viewHolder.myTask_examine_lvItem_cb.setChecked(tempPosition == position);
                if (tempPosition == position){
                    mCheck[position] = true;
                }else{
                    mCheck[position] = false;
                }
            }

        }else{
            viewHolder.myTask_examine_lvItem_cb.setEnabled(true);
            viewHolder.myTask_examine_lvItem_cb.setChecked(tempPosition == position);
            if (tempPosition == position){
                mCheck[position] = true;
            }else{
                mCheck[position] = false;
            }
        }

        final String name = data_map.get("name").toString();
        if (name.equals("结束") || name.equals("不通过结束")) {
            ev.setVisibility(View.GONE);
            viewHolder.jbr_linear.setVisibility(View.GONE);
        }else{

            if (assignee.indexOf(",")>=0){

                viewHolder.jbr_linear.setVisibility(View.VISIBLE);
                viewHolder.myTask_examine_lvItem_jbrxz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String,Object> parmsMap = new HashMap<String, Object>();
                        parmsMap.put("assignee","USER_ID_");
                        parmsMap.put("assignee_name","USER_NAME_");
                        String pars = new ListParms("0","0", AppUtils.limit,"process","rycodes:"+finalassignee).getParms();
                        ActivityController.showDialog_OneSelect(mContext,parmsMap,"process","findUserByCodeslistPage",pars,"USER_NAME_",main_form,ev,finalmap);
                    }
                });

            }else{
                viewHolder.jbr_linear.setVisibility(View.GONE);
                if (data_map.get("assignee_name")!= null && !data_map.get("assignee_name").toString().equals("")){
                    viewHolder.jbr_linear.setVisibility(View.VISIBLE);
                    viewHolder.myTask_examine_lvItem_jbrxz.setVisibility(View.GONE);
                    viewHolder.tb.setVisibility(View.GONE);
                    ev.setText(data_map.get("assignee_name").toString());
                }
            }
        }
        viewHolder.myTask_examine_lvItem_name.setText(data_map.get("name").toString());

        viewHolder.myTask_examine_lvItem_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                finalmap.put("checked",isChecked);
                if (isChecked){
                    if (way_type== null || way_type.equals("")){
                        tempPosition = finalposition;
                        reshData();
                    }
                }
            }
        });

    }
    //获取数据的数量
    @Override
    public int getItemCount() {
          return datas.size();
    }

    public void setSfVisible(boolean sfVisible) {
        this.sfVisible = sfVisible;
    }


    /**
     * 删除菜单打开信息接收
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /**
     * 滑动或者点击了Item监听
     * @param slidingButtonView
     */
    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if(menuIsOpen()){
            if(mMenu != slidingButtonView){
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;

    }
    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if(mMenu != null){
            return true;
        }
        return false;
    }



    public interface IonSlidingViewClickListener {
        void onItemClick(View view, int position);
        void onDeleteBtnClilck(View view, int position);
        void onCuibanBtnClick(View view, int position);
    }

    @Override
    public void onClick(View v) {

        int n;
        if (setMap != null){
            Map copyMap = (Map<String,Object>)v.getTag();
            n = (int) copyMap.get("postion");
        }else{
            n = (int) v.getTag();
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends RecyclerView.ViewHolder{
        public CheckBox myTask_examine_lvItem_cb;
        public TextView myTask_examine_lvItem_name,myTask_examine_lvItem_jbrxz;
        public TextView_custom myTask_examine_lvItem_jbr;
        public LinearLayout jbr_linear;
        public ImageView tb;

        public ViewHolder(View view){
            super(view);
            myTask_examine_lvItem_cb = (CheckBox) view.findViewById(R.id.myTask_examine_lvItem_cb);
            myTask_examine_lvItem_name = (TextView) view.findViewById(R.id.myTask_examine_lvItem_name);
            myTask_examine_lvItem_jbr = (TextView_custom) view.findViewById(R.id.myTask_examine_lvItem_jbr);
            myTask_examine_lvItem_jbr.setCode("assignee_name");
            myTask_examine_lvItem_jbrxz = (TextView) view.findViewById(R.id.myTask_examine_lvItem_jbrxz);
            jbr_linear = (LinearLayout) view.findViewById(R.id.jbr_linear);
            myTask_examine_lvItem_cb.setButtonDrawable(R.drawable.selector_mytask_checkbox);
            tb = (ImageView) view.findViewById(R.id.tb);
        }
    }

    private void dispaly_none(View view,boolean isVisible){
        if (view != null){
            if (isVisible){
                view.setVisibility(View.VISIBLE);
            }else{
                view.setVisibility(View.GONE);
            }
        }
    }

}
