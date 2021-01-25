package com.cn.wti.entity.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.view.custom.button.SlidingButtonView;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.ReflectHelper;
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

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.ViewHolder> implements SlidingButtonView.IonSlidingButtonListener,View.OnClickListener{
    public List<Map<String,Object>> datas = null;
    Map<String,Object> data_map = null,setMap,copyMap;
    private int screenWidth= 0,layoutid= 0;
    private String[] titles,contents;
    private boolean[] mCheck;
    private TextView count_view;
    private boolean sfVisible = false;
    private Context mContext;
    private Fragment mFragment;
    private IonSlidingViewClickListener mIDeleteBtnClickListener;
    private SlidingButtonView mMenu = null;
    private Resources resources;
    private  List<Map<String,Object>>gsList;

    private View parentView;

    //列表Activity
    public MyAdapter2(Context mContext, List<Map<String,Object>> datas, int screenWidth , String[] titles, String[]contents, int layoutid, boolean[]mCheck) {
        this.datas = datas;
        this.screenWidth = screenWidth;
        this.titles = titles;
        this.contents = contents;
        this.layoutid = layoutid;
        if (mCheck != null){
            this.mCheck = mCheck;
        }
        this.mContext = mContext;
        if (mContext instanceof  IonSlidingViewClickListener){
            mIDeleteBtnClickListener = (IonSlidingViewClickListener) mContext;
        }
        this.resources = mContext.getResources();
    }

    public MyAdapter2(Context mContext, List<Map<String,Object>> datas, int screenWidth , String[] titles, String[]contents, int layoutid) {
        this.datas = datas;
        this.screenWidth = screenWidth;
        this.titles = titles;
        this.contents = contents;
        this.layoutid = layoutid;
        this.mContext = mContext;
        this.resources = mContext.getResources();
    }

    //列表 Fragment
    public MyAdapter2(Fragment mFragment, List<Map<String,Object>> datas, int screenWidth , String[] titles, String[]contents, int layoutid, boolean[]mCheck) {
        this.datas = datas;
        this.screenWidth = screenWidth;
        this.titles = titles;
        this.contents = contents;
        this.layoutid = layoutid;
        this.mCheck = mCheck;
        this.mFragment = mFragment;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) mFragment;
        this.resources = mFragment.getResources();
    }

    public MyAdapter2(Context mContext, List<Map<String,Object>> datas, int screenWidth , String[] titles, String[]contents, int layoutid, boolean[]mCheck,Map<String,Object> setMap) {
        this.datas = datas;
        this.screenWidth = screenWidth;
        this.titles = titles;
        this.contents = contents;
        this.layoutid = layoutid;
        this.mCheck = mCheck;
        this.mContext = mContext;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) mContext;
        this.resources = mContext.getResources();
        this.setMap = setMap;
        this.gsList = (List<Map<String, Object>>) setMap.get("gs");
    }

    public void  refreshData(){
        notifyDataSetChanged();
    };
    public void refreshData(List<Map<String,Object>> objects,  boolean[] flag)
    {
        if (objects != null)
        {
            datas = objects;
            if (objects.size() > flag.length){
                mCheck= new boolean[objects.size()];
                for(int x=0;x<flag.length;x++){
                    mCheck[x] = flag[x];
                }
                boolean[] array = new boolean[objects.size() - flag.length];
                for(int y=0;y<array.length;y++){
                    mCheck[flag.length+y]=array[y];
                }
            }else{
                mCheck = flag;
            }
            notifyDataSetChanged();
        }
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

            //刷新视图
            if (mFragment != null){
                try {
                    ReflectHelper.callMethod2(mFragment,"reshView",null,String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean[] getSelectItem(){
        return  mCheck;
    }

    public List<Map<String,Object>> getDatas(){
        return datas;
    };

    public void  setViewCount(TextView count_view){
        this.count_view = count_view;
        count_view.setText("共 "+getItemCount()+" 条数据");
        this.count_view .setText("共 "+getItemCount()+" 条数据");
    }

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
        final int position_1 = position;
        int i = 0;
        if (titles != null && titles.length>0){
            for (String title:titles) {
                i++;
                switch (i){
                    case 1:
                        viewHolder.title_1.setText(titles[i-1]);
                        dispaly_none(viewHolder.title_1,true);
                        break;
                    case 2:
                        viewHolder.title_2.setText(titles[i-1]);
                        dispaly_none(viewHolder.title_2,true);
                        break;
                    case 3:
                        viewHolder.title_3.setText(titles[i-1]);
                        dispaly_none(viewHolder.title_3,true);
                        break;
                    case 4:
                        viewHolder.title_4.setText(titles[i-1]);
                        dispaly_none(viewHolder.title_4,true);
                        break;
                    case 5:
                        viewHolder.title_5.setText(titles[i-1]);
                        dispaly_none(viewHolder.title_5,true);
                        break;
                    case 6:
                        viewHolder.title_6.setText(titles[i-1]);
                        dispaly_none(viewHolder.title_6,true);
                        break;
                }
            }
        }

        if (contents != null && contents.length>0){
            i = 0;
            List<String> values = createMain(data_map,contents);
            if (values==null || values.size() == 0){return;}
            String[] test_contents = (String[]) values.toArray(new String[values.size()]);

            for (String content:contents){

                i++;
                switch (i){
                    case 1:
                        if (test_contents[i-1].equals("display_false")){
                            viewHolder.titile.setText("");
                            dispaly_none(viewHolder.titile,false,test_contents[i-1]);
                        }else{
                            viewHolder.titile.setText(test_contents[i-1]);
                            dispaly_none(viewHolder.titile,true,test_contents[i-1]);
                        }

                        break;
                    case 2:
                        if (test_contents[i-1].equals("display_false")){
                            viewHolder.content.setText("");
                            dispaly_none(viewHolder.content,false,test_contents[i-1]);
                        }else{
                            String contentStr = test_contents[i-1];
                            if(content.equals("is_purchase") || content.equals("storagestatus")){
                                if (contentStr.equals("") || contentStr.equals("false") || contentStr.equals("0")){
                                    contentStr = "否";
                                }else{
                                    contentStr = "是";
                                }
                            }
                            viewHolder.content.setText(contentStr);
                            dispaly_none(viewHolder.content,true,contentStr);
                        }
                        break;
                    case 3:
                        if (test_contents[i-1].equals("display_false")){
                            viewHolder.content_1.setText("");
                            dispaly_none(viewHolder.content_1,false,test_contents[i-1]);
                        }else{
                            viewHolder.content_1.setText(test_contents[i-1]);
                            dispaly_none(viewHolder.content_1,true,test_contents[i-1]);
                        }

                        break;
                    case 4:
                        if (test_contents[i-1].equals("display_false")){
                            viewHolder.content_2.setText("");
                            dispaly_none(viewHolder.content_2,false,test_contents[i-1]);
                        }else{
                            viewHolder.content_2.setText(test_contents[i-1]);
                            dispaly_none(viewHolder.content_2,true,test_contents[i-1]);
                        }
                        break;
                    case 5:
                        if (test_contents[i-1].equals("display_false")){
                            viewHolder.content_3.setText("");
                            dispaly_none(viewHolder.content_3,false,test_contents[i-1]);
                        }else{
                            viewHolder.content_3.setText(test_contents[i-1]);
                            dispaly_none(viewHolder.content_3,true,test_contents[i-1]);
                        }
                        break;
                    case 6:
                        if (test_contents[i-1].equals("display_false")){
                            viewHolder.content_4.setText("");
                            dispaly_none(viewHolder.content_4,false,test_contents[i-1]);
                        }else{
                            viewHolder.content_4.setText(test_contents[i-1]);
                            dispaly_none(viewHolder.content_4,true,test_contents[i-1]);
                        }
                        break;
                    case 7:
                        if (test_contents[i-1].equals("display_false")){
                            viewHolder.content_5.setText("");
                            dispaly_none(viewHolder.content_5,false,test_contents[i-1]);
                        }else{
                            viewHolder.content_5.setText(test_contents[i-1]);
                            dispaly_none(viewHolder.content_5,true,test_contents[i-1]);
                        }
                        break;
                    case 8:
                        if (test_contents[i-1].equals("display_false")){
                            viewHolder.content_6.setText("");
                            dispaly_none(viewHolder.content_6,false,test_contents[i-1]);
                        }else{
                            viewHolder.content_6.setText(test_contents[i-1]);
                            dispaly_none(viewHolder.content_6,true,test_contents[i-1]);
                        }
                        break;
                }
            }
        }

        if (viewHolder.checkBox != null){
            viewHolder.checkBox.setButtonDrawable(R.drawable.selector_checkbox1);
            if (sfVisible){
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            }else{
                viewHolder.checkBox.setVisibility(View.GONE);
            }

            if (position >= mCheck.length){
                mCheck[position] = false;
                viewHolder.checkBox.setChecked(mCheck[position]);
            }else{
                viewHolder.checkBox.setChecked(mCheck[position]);
            }
        }

        /*if (count_view != null){
            count_view.setText("共 "+mCheck.length+" 条数据");
        }*/

        if (viewHolder.btn_delte != null){
            //删除动作
            if (setMap != null){
                copyMap = new HashMap<String, Object>();
                copyMap.putAll(setMap);
                copyMap.put("postion",position);
                viewHolder.btn_delte.setTag(copyMap);
            }else{
                viewHolder.btn_delte.setTag(position);
            }
            viewHolder.btn_delte.setOnClickListener(this);
        }

        //点击视图
        if (setMap != null){
            copyMap = new HashMap<String, Object>();
            copyMap.putAll(setMap);
            copyMap.put("postion",position);
            viewHolder.item_layoutContent.setTag(copyMap);

        }else{
            viewHolder.item_layoutContent.setTag(position);
        }
        viewHolder.item_layoutContent.setOnClickListener(this);

        if (viewHolder.btn_delte != null){
            //删除动作
            if (setMap != null){
                viewHolder.btn_delte.setTag(copyMap);
            }else{
                viewHolder.btn_delte.setTag(position);
            }
            viewHolder.btn_delte.setOnClickListener(this);
        }

        if (viewHolder.btn_cuiban != null){
            if (setMap != null){
                viewHolder.btn_cuiban.setTag(copyMap);
            }else{
                viewHolder.btn_cuiban.setTag(position);
            }
            viewHolder.btn_cuiban.setOnClickListener(this);
        }

        if (viewHolder.btn_zuofei!= null){
            if (setMap != null){
                viewHolder.btn_zuofei.setTag(copyMap);
            }else{
                viewHolder.btn_zuofei.setTag(position);
            }
            viewHolder.btn_zuofei.setOnClickListener(this);
        }

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
        void onZuofeiBtnClick(View view,int postion);
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

        switch (v.getId()){
            case R.id.btn_cuiban:
                mIDeleteBtnClickListener.onCuibanBtnClick(v, n);
                break;
            case R.id.btn_zuofei:
                mIDeleteBtnClickListener.onZuofeiBtnClick(v, n);
                break;
            case R.id.btn_delte:
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                    mIDeleteBtnClickListener.onDeleteBtnClilck(v, n);
                } else {
                    mIDeleteBtnClickListener.onDeleteBtnClilck(v, n);
                }

                break;
            case R.id.itemContainer:
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                    //打开视图
                } else {
                    if (mIDeleteBtnClickListener != null){
                        mIDeleteBtnClickListener.onItemClick(v, n);
                    }
                }
                break;
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView_custom titile,title_1,title_2,title_3,title_4,title_5,title_6;
        public TextView_custom content,content_1,content_2,content_3,content_4,content_5,content_6,content_7,content_8;
        public CheckBox checkBox;
        public View view1;
        public int approvalstatus; //审批状态
        public int estatus;//单据状态*/
        public TextView btn_delte,/*删除按钮*/btn_cuiban /*催办按钮*/,btn_zuofei/*作废按钮*/;
        public LinearLayout item_layoutContent;

        public ViewHolder(View view){
            super(view);
            //标题：
            titile = (TextView_custom) view.findViewById(R.id.title);
            title_1 = (TextView_custom) view.findViewById(R.id.title_1);
            title_2 = (TextView_custom) view.findViewById(R.id.title_2);
            title_3 = (TextView_custom) view.findViewById(R.id.title_3);
            title_4 = (TextView_custom) view.findViewById(R.id.title_4);
            title_5 = (TextView_custom) view.findViewById(R.id.title_5);
            title_6 = (TextView_custom) view.findViewById(R.id.title_6);

            //内容：
            content = (TextView_custom) view.findViewById(R.id.subtitle);
            content_1 = (TextView_custom) view.findViewById(R.id.content_1);
            content_2 = (TextView_custom) view.findViewById(R.id.content_2);
            content_3 = (TextView_custom) view.findViewById(R.id.content_3);
            content_4 = (TextView_custom) view.findViewById(R.id.content_4);
            content_5 = (TextView_custom) view.findViewById(R.id.content_5);
            content_6 = (TextView_custom) view.findViewById(R.id.content_6);
            content_7 = (TextView_custom) view.findViewById(R.id.content_7);
            content_8 = (TextView_custom) view.findViewById(R.id.content_8);

            dispaly_none(titile,false);dispaly_none(title_1,false);dispaly_none(title_2,false);
            dispaly_none(title_3,false);dispaly_none(title_4,false);dispaly_none(title_5,false);
            dispaly_none(title_6,false);

            dispaly_none(content,false);dispaly_none(content_1,false);
            dispaly_none(content_2,false);dispaly_none(content_3,false);dispaly_none(content_4,false);
            dispaly_none(content_5,false);dispaly_none(content_6,false);
            dispaly_none(content_7,false);dispaly_none(content_8,false);

            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            //删除动作
            btn_delte = (TextView) view.findViewById(R.id.btn_delte);
            //催办动作
            btn_cuiban = (TextView) view.findViewById(R.id.btn_cuiban);
            //作废动作
            btn_zuofei = (TextView) view.findViewById(R.id.btn_zuofei);
            item_layoutContent = (LinearLayout) view.findViewById(R.id.itemContainer);
            if (itemView instanceof  SlidingButtonView){
                ((SlidingButtonView) itemView).setSlidingButtonListener(MyAdapter2.this);
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
                    if (val.equals("已完成") || val.equals("已出库") || val.equals("已支付") || val.equals("已还") || val.equals("已发")){
                        view.setBackground(resources.getDrawable(R.drawable.right_title));
                    }else if(val.equals("未完成") || val.equals("未出库") || val.equals("未支付") || val.equals("未还") || val.equals("未发")){
                        view.setBackground(resources.getDrawable(R.drawable.right_title_02));
                    }
                }
               /* switch (val){
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
                }*/
            }else if (view.getId() == R.id.content_1){
                if (val != null){
                    if (val.equals("已完成") || val.equals("已出库") || val.equals("已支付") || val.equals("已还") || val.equals("已发")){
                        view.setBackground(resources.getDrawable(R.drawable.right_title));
                    }else if(val.equals("未完成") || val.equals("未出库") || val.equals("未支付") || val.equals("未还") || val.equals("未发")){
                        view.setBackground(resources.getDrawable(R.drawable.right_title_02));
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

        for (int i=0,n=keys.length;i<n; i++){
            key = keys[i];
            /*if (key.indexOf("[")>=0 && key.indexOf("]")>=0){
                key = key.replace("[","").replace("]","");
            }*/

            if (gsList != null){
                val = ActivityController.returnString(data,key,gsList);
            }else{
                if(key.indexOf("(drq)")>=0){
                    key = key.replace("(drq)","");
                    isDrqf = true;
                }else{
                    isDrqf = false;
                }

                if (key.equals("")){
                    val = "display_false";
                }else if ( data.get(key)!= null){
                    if (key.equals("iscg")){
                        val = data.get(key).toString();
                        if (val.equals("0")){
                            val = "未完成";
                        }else{
                            val = "已完成";
                        }
                    }else if(key.equals("sfyck")){
                        val = data.get(key).toString();
                        if (val.equals("否")){
                            val = "未出库";
                        }else{
                            val = "已出库";
                        }
                    }else if(key.equals("isyh")){
                        val = data.get(key).toString();
                        if (val.equals("否")){
                            val = "未还";
                        }else{
                            val = "已还";
                        }
                    }else if(key.equals("estatus")){
                        val = data.get(key).toString();
                        if (val.equals("7")){
                            val = "已发";
                        }else{
                            val = "未发";
                        }
                    }
                    else if(key.equals("haspaid")){
                        //val = data.get("haspaid").toString();
                        if (data.get("haspaid").toString().equals(data.get("costapplymoney").toString())){
                            val = "已支付";
                        }else{
                            val = "未支付";
                        }
                    }else if(isDrqf){
                        val = data.get(key).toString();
                        val = DateUtil.strFomatDate(val);
                    }else{
                        val = data.get(key).toString();
                    }

                    if (StringUtils.isNumeric(val)){
                        if (val.indexOf(".")>=0){
                            val = val.substring(0,val.indexOf("."));
                        }
                    }

                    if (DateUtil.isValidDate(val)){
                        if (val.indexOf(".")>=0){
                            val = val.substring(0,val.indexOf("."));
                        }
                    }

                }else{

                    if (key.equals("iscg")){
                        val = "未完成";
                    }else if(key.equals("sfyck")){
                        val = "未出库";
                    }else if(key.equals("isyh")){
                        val = "未还";
                    }else if(key.equals("estatus")){
                        val = "已发";
                    }else if(key.equals("haspaid")){
                        val = "未支付";
                    }else{
                        val = "";
                    }
                }
            }

            if (val == null){
                val="";
            }

            resList.add(val);
        }

        return  resList;
    }

}
