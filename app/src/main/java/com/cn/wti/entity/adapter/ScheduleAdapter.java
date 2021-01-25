package com.cn.wti.entity.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.view.custom.button.SlidingButtonView;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
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

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> implements SlidingButtonView.IonSlidingButtonListener,View.OnClickListener{
    public List<Map<String,Object>> datas = null;
    Map<String,Object> data_map = null,copyMap;
    private int screenWidth= 0,layoutid= 0;
    private String[] contents;
    private boolean[] mCheck;
    private Context mContext;

    private IonSlidingViewClickListener mIDeleteBtnClickListener;
    private SlidingButtonView mMenu = null;
    private Resources resources;

    public ScheduleAdapter(Context mContext, List<Map<String,Object>> datas, int screenWidth ,String[]contents, int layoutid, boolean[]mCheck) {
        this.datas = datas;
        this.screenWidth = screenWidth;
        this.contents = contents;
        this.layoutid = layoutid;
        this.mCheck = mCheck;
        this.mContext = mContext;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) mContext;
        this.resources = mContext.getResources();
    }

    public void refreshData(boolean[] flag)
    {
        if (datas != null)
        {
            if (datas.size() > flag.length){
                mCheck= new boolean[datas.size()];
                for(int x=0;x<flag.length;x++){
                    mCheck[x] = flag[x];
                }
                boolean[] array = new boolean[datas.size() - flag.length];
                for(int y=0;y<array.length;y++){
                    mCheck[flag.length+y]=array[y];
                }
            }else{
                mCheck = flag;
            }
            notifyDataSetChanged();
        }
    }

    public boolean[] getSelectItem(){
        return  mCheck;
    }

    public List<Map<String,Object>> getDatas(){
        return datas;
    };


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (layoutid == 0){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_middle3_03,viewGroup,false);
        }else{
            view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutid,viewGroup,false);
        }

        LinearLayout layoutContent = (LinearLayout) view.findViewById(R.id.itemContainer);
        ViewGroup.LayoutParams lp = layoutContent.getLayoutParams();
        lp.width = screenWidth;
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        data_map = datas.get(position);
        int i = 0;

        if (contents != null && contents.length>0){
            i = 0;
            List<String> values = createMain(data_map,contents);
            if (values==null || values.size() == 0){return;}
            String[] test_contents = (String[]) values.toArray(new String[values.size()]);

            for (String content:contents){

                i++;
                switch (i){
                    case 1:
                        viewHolder.title.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.title,true,test_contents[i-1]);
                        break;
                    case 2:
                        viewHolder.content.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.content,true,test_contents[i-1]);
                        break;
                    case 3:
                        viewHolder.content_1.setText(test_contents[i-1]);
                       dispaly_none(viewHolder.content_1,true,test_contents[i-1]);
                        break;
                    case 4:
                        viewHolder.content_2.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.content_2,true,test_contents[i-1]);
                        break;
                }
            }
            //判断状态
            String status = "";

            if (data_map.get("statusmx") != null){
                status = data_map.get("statusmx").toString();
                if (status.equals("")){
                    status = data_map.get("status").toString();
                }
            }else{
                status = data_map.get("status").toString();
            }

            if(status.equals("已完成")){
                viewHolder.status.setBackground(mContext.getResources().getDrawable(R.drawable.green01_dot_bg));
            }else {
                viewHolder.status.setBackground(mContext.getResources().getDrawable(R.drawable.red01_dot_bg));
            }

        }
        copyMap = new HashMap<String, Object>();
        //点击视图
        copyMap.put("index",position);
        copyMap.put("currentView",viewHolder.item_layoutContent);
        viewHolder.item_layoutContent.setTag(copyMap);
        viewHolder.item_layoutContent.setOnClickListener(this);

        viewHolder.schedulemore.setTag(copyMap);
        viewHolder.schedulemore.setOnClickListener(this);

    }
    //获取数据的数量
    @Override
    public int getItemCount() {
          return datas.size();
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
        void onShowMoreBtnClick(View view, int position);
    }

    @Override
    public void onClick(View v) {

        int n;
        if (copyMap != null){
            Map copyMap = (Map<String,Object>)v.getTag();
            n = (int) copyMap.get("index");
        }else{
            n = (int) v.getTag();
        }

        switch (v.getId()){
            case R.id.schedulemore:
                mIDeleteBtnClickListener.onShowMoreBtnClick(v, n);
                break;
            case R.id.itemContainer:
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                    //打开视图
                } else {
                    mIDeleteBtnClickListener.onItemClick(v, n);
                }
                break;
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView_custom title,content,content_1,content_2;
        public CheckBox checkBox;
        private ImageView schedulemore;
        public View view1;
        public int approvalstatus; //审批状态
        public int estatus;//单据状态*/
        public TextView btn_delte;//删除按钮
        public LinearLayout item_layoutContent;
        public TextView status;

        public ViewHolder(View view){
            super(view);
            //标题：
            title = (TextView_custom) view.findViewById(R.id.title);
            //内容：
            content = (TextView_custom) view.findViewById(R.id.subtitle);
            content_1 = (TextView_custom) view.findViewById(R.id.content_1);
            content_2 = (TextView_custom) view.findViewById(R.id.content_2);
            status = (TextView) view.findViewById(R.id.status);

            dispaly_none(title,false);

            dispaly_none(content,false);dispaly_none(content_1,false);dispaly_none(content_2,false);

            //更多
            schedulemore = (ImageView) view.findViewById(R.id.schedulemore);
            item_layoutContent = (LinearLayout) view.findViewById(R.id.itemContainer);
            if (itemView instanceof  SlidingButtonView){
                ((SlidingButtonView) itemView).setSlidingButtonListener(ScheduleAdapter.this);
            }
            view1 = view;
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

    private void dispaly_none(View view,boolean isVisible,String val){
        if (view != null){
            if (isVisible){
                view.setVisibility(View.VISIBLE);
            }else{
                view.setVisibility(View.GONE);
            }

            if (view.getId() == R.id.subtitle){
                if (val != null){
                    switch (val){
                        case "未完成":
                            view.setBackground(resources.getDrawable(R.drawable.right_title_02));
                            break;
                        case "未出库":
                            view.setBackground(resources.getDrawable(R.drawable.right_title_02));
                            break;
                        case "未支付":
                            view.setBackground(resources.getDrawable(R.drawable.right_title_02));
                            break;
                        case "未还":
                            view.setBackground(resources.getDrawable(R.drawable.right_title_02));
                            break;
                    }
                }
            }else if (view.getId() == R.id.content_1){
                if (val != null){
                    switch (val){
                        case "未完成":
                            view.setBackground(resources.getDrawable(R.drawable.right_title_02));
                            break;
                        case "未出库":
                            view.setBackground(resources.getDrawable(R.drawable.right_title_02));
                            break;
                        case "未支付":
                            view.setBackground(resources.getDrawable(R.drawable.right_title_02));
                            break;
                        case "未还":
                            view.setBackground(resources.getDrawable(R.drawable.right_title_02));
                            break;
                    }
                }
            }

        }
    }

    public List<String> createMain(Map<String,Object> data,String[] keys){
        List<String> resList = new ArrayList<String>();
        String key = "",val;
        boolean isDrqf = false;

        if (keys == null){
            Toast.makeText(mContext,"数据异常",Toast.LENGTH_SHORT).show();
            return resList;
        }

        for (int i=0,n=keys.length;i<n; i++) {
            key = keys[i];

            if(key.indexOf("(drq)")>=0){
                key = key.replace("(drq)","");
                isDrqf = true;
            }else{
                isDrqf = false;
            }

            if(isDrqf){
                val = data.get(key).toString();
                val = DateUtil.strFomatDate(val);
            }else{
                val = getVal(key,data);
            }

            if (i ==0 && key.equals("start")){
                if (data.get("allDay") != null && data.get("allDay").equals("true")){
                    val = "全天";
                }else{
                    val = DateUtil.getHourAndMin(val);
                }
            }else if(key.equals("start") || key.equals("end")){
                val = DateUtil.getHourAndMin(val);
            }

            resList.add(val);
        }

        return resList;

    }

    public String getVal(String key,Map<String,Object>map){
        String res="";
        if (key.equals("")){
            res = "";
        }else{
            if (map.get(key) != null){
                res = map.get(key).toString();
            }else{
                res = "";
            }

        }
        return  res;
    }

}
