package com.cn.wti.entity.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.view.custom.button.SlidingButtonView;
import com.cn.wti.entity.view.custom.textview.TextView_TableRow_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.number.SizheTool;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.other.StringUtils;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/5/18.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> implements SlidingButtonView.IonSlidingButtonListener,View.OnClickListener{
    public List<Map<String,Object>> datas = null;
    private List<Map<String,Object>>columns_list=null;
    Map<String,Object> data_map = null,setMap,copyMap;
    private int screenWidth= 0,layoutid= 0,count=0;
    private String[] titles,contents,hjcolumns;
    private boolean[] mCheck;
    private TextView count_view;
    private boolean sfVisible = false;
    private Context mContext;
    private Fragment mFragment;
    private IonSlidingViewClickListener mIDeleteBtnClickListener;
    private SlidingButtonView mMenu = null;
    private Resources resources;
    private  List<Map<String,Object>>gsList;
    private double hj= 0,hj_1 = 0,hj_2=0,hj_3=0;
    private Handler mHandler;
    private Map<String,Object> hjMap = new LinkedHashMap<String, Object>();

    //列表Activity
    public ReportAdapter(Context mContext, List<Map<String,Object>> datas, int screenWidth , String[] titles, String[]contents, int layoutid, boolean[]mCheck, Handler mHandler,String[] hjcolumns) {
        this.datas = datas;
        this.screenWidth = screenWidth;
        this.titles = titles;
        this.contents = contents;
        this.layoutid = layoutid;
        this.mCheck = mCheck;
        this.mContext = mContext;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) mContext;
        this.resources = mContext.getResources();
        this.mHandler = mHandler;

        //初始化合计列
        this.hjcolumns = hjcolumns;
        if (hjcolumns != null  && hjcolumns.length>0){
            initHj(datas,hjcolumns);
        }
    }

    //列表 Fragment
    public ReportAdapter(Fragment mFragment, List<Map<String,Object>> datas, int screenWidth , String[] titles, String[]contents, int layoutid, boolean[]mCheck, Handler mHandler,String[] hjcolumns) {
        this.datas = datas;
        this.screenWidth = screenWidth;
        this.titles = titles;
        this.contents = contents;
        this.layoutid = layoutid;
        this.mCheck = mCheck;
        this.mFragment = mFragment;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) mFragment;
        this.resources = mFragment.getResources();
        this.mHandler = mHandler;

        //初始化合计列
        this.hjcolumns = hjcolumns;
        if (hjcolumns != null && hjcolumns.length>0){
            initHj(datas,hjcolumns);
        }
    }

    public ReportAdapter(Context mContext, List<Map<String,Object>> datas, int screenWidth , String[] titles, String[]contents, int layoutid, boolean[]mCheck, Map<String,Object> setMap) {
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

    public void initHj(List<Map<String,Object>> datas,String[] hjcolumns){
        if (datas != null && hjcolumns != null/*&& datas.size()>0*/){
            int index= 11111;
            for (String col:hjcolumns) {

                try{
                    index =  Integer.parseInt(col);
                    String a = contents[Integer.parseInt(col)];
                }catch (Exception e){index= 11111;}

                if (index != 11111){
                    if (datas.size() == 0){
                        hjMap.put(col,0);
                    }else{
                        for (Map<String,Object> data:datas) {
                            if (data.get(contents[Integer.parseInt(col)]) == null){
                                hj = 0;
                            }else{
                                hj = Double.parseDouble(data.get(contents[Integer.parseInt(col)]).toString());
                            }

                            if (hjMap.get(col) != null){
                                hj_1 = Double.parseDouble(hjMap.get(col).toString());
                            }else{
                                hj_1 = 0;
                            }
                            hjMap.put(col,SizheTool.numberFormat(hj_1+hj,2));
                        }
                    }
                }
            }
            count = datas.size();
            mHandler.sendEmptyMessageDelayed(1,10);
        }
    }

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
                    //加合计列数据
                    if (count < getItemCount()){
                        for (String col:hjcolumns) {
                            Map<String,Object> data = datas.get(count+y);

                            hj = Double.parseDouble(data.get(contents[Integer.parseInt(col)]).toString());
                            if (hjMap.get(col) != null){
                                hj_1 = Double.parseDouble(hjMap.get(col).toString());
                            }else{
                                hj_1 = 0;
                            }
                            hjMap.put(col,hj_1+hj);

                        }

                    }
                }
                if (array.length >0){
                    count = datas.size();
                    mHandler.sendEmptyMessageDelayed(1,10);
                }
            }else{
                mCheck = flag;
                hjMap.clear();
                //初始化合计列
                initHj(datas,hjcolumns);
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
                    //加合计列数据
                    if (count < getItemCount()){
                        for (String col:hjcolumns) {
                            Map<String,Object> data = datas.get(count+y);

                            hj = Double.parseDouble(data.get(contents[Integer.parseInt(col)]).toString());
                            if (hjMap.get(col) != null){
                                hj_1 = Double.parseDouble(hjMap.get(col).toString());
                            }else{
                                hj_1 = 0;
                            }
                            hjMap.put(col,hj_1+hj);

                        }
                    }
                }
                if (array.length >0){
                    count = datas.size();
                    mHandler.sendEmptyMessageDelayed(1,10);
                }
            }else{
                mCheck = flag;
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 计算无刷新合计
     */
    public void refreshData()
    {
        if (datas != null )
        {
            notifyDataSetChanged();
            hjMap.clear();
            initHj(datas,hjcolumns);
            mHandler.sendEmptyMessageDelayed(1,10);
        }
    }

    public boolean[] getSelectItem(){
        return  mCheck;
    }

    public List<Map<String,Object>> getDatas(){
        return datas;
    };

    public Map<String,Object> getHj(){
        return hjMap;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (layoutid == 0){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_tablerow_1,viewGroup,false);
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

        if (titles != null && titles.length >0){
            for (String title:titles){

                i++;
                switch (i){
                    case 1:
                        dispaly_none(viewHolder.title_1_1,true,title);
                        break;
                    case 2:
                        dispaly_none(viewHolder.title_1_2,true,title);
                        break;
                    case 3:
                        dispaly_none(viewHolder.title_1_3,true,title);
                        break;
                    case 4:
                        dispaly_none(viewHolder.title_1_4,true,title);
                        break;
                    case 5:
                        dispaly_none(viewHolder.title_1_5,true,title);
                        break;
                    case 6:
                        dispaly_none(viewHolder.title_1_6,true,title);
                        break;
                }
            }
        }

        //内容

        if (contents != null && contents.length>0){
            i = 0;
            List<String> values = createMain(data_map,contents);
            if (values==null || values.size() == 0){return;}
            String[] test_contents = (String[]) values.toArray(new String[values.size()]);

            for (String content:contents){

                i++;
                switch (i){
                    case 1:
                        viewHolder.list_1_1.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.layout_1_1,true,test_contents[i-1]);
                        break;
                    case 2:
                        viewHolder.list_1_2.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.layout_1_2,true,test_contents[i-1]);
                        break;
                    case 3:
                        viewHolder.list_1_3.setText(test_contents[i-1]);
                       dispaly_none(viewHolder.layout_1_3,true,test_contents[i-1]);
                        break;
                    case 4:
                        viewHolder.list_1_4.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.layout_1_4,true,test_contents[i-1]);
                        break;
                    case 5:
                        viewHolder.list_1_5.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.layout_1_5,true,test_contents[i-1]);
                        break;
                    case 6:
                        viewHolder.list_1_6.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.layout_1_6,true,test_contents[i-1]);
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

        //设置背景颜色
        if (position % 2 == 0){
            viewHolder.item_layoutContent.setBackgroundColor(resources.getColor(R.color.color_249_249_249));
        }else{
            viewHolder.item_layoutContent.setBackgroundColor(resources.getColor(R.color.colorWhite));
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

    public List<Map<String, Object>> getColumns_list() {
        return columns_list;
    }

    public void setColumns_list(List<Map<String, Object>> columns_list) {
        this.columns_list = columns_list;
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

        switch (v.getId()){
            case R.id.btn_cuiban:
                mIDeleteBtnClickListener.onCuibanBtnClick(v, n);
                break;
            case R.id.btn_delte:
                mIDeleteBtnClickListener.onDeleteBtnClilck(v, n);
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
        public TextView title_1_1,title_1_2,title_1_3,title_1_4,title_1_5,title_1_6;
        public TextView list_1_1,list_1_2,list_1_3,list_1_4,list_1_5,list_1_6;
        public CheckBox checkBox;
        public View view1;
        public int approvalstatus; //审批状态
        public int estatus;//单据状态*/
        public TextView btn_delte;//删除按钮
        public LinearLayout item_layoutContent,layout_1_1,layout_1_2,layout_1_3,layout_1_4,layout_1_5,layout_1_6;

        public ViewHolder(View view){
            super(view);
            //标题：
            title_1_1 = (TextView) view.findViewById(R.id.title_1_1);
            title_1_2 = (TextView) view.findViewById(R.id.title_1_2);
            title_1_3 = (TextView) view.findViewById(R.id.title_1_3);
            title_1_4 = (TextView) view.findViewById(R.id.title_1_4);
            title_1_5 = (TextView) view.findViewById(R.id.title_1_5);
            title_1_6 = (TextView) view.findViewById(R.id.title_1_6);

            //内容
            list_1_1 = (TextView) view.findViewById(R.id.list_1_1);
            list_1_2 = (TextView) view.findViewById(R.id.list_1_2);
            list_1_3 = (TextView) view.findViewById(R.id.list_1_3);
            list_1_4 = (TextView) view.findViewById(R.id.list_1_4);
            list_1_5 = (TextView) view.findViewById(R.id.list_1_5);
            list_1_6 = (TextView) view.findViewById(R.id.list_1_6);

            layout_1_1 = (LinearLayout) view.findViewById(R.id.layout_1_1);
            layout_1_2 = (LinearLayout) view.findViewById(R.id.layout_1_2);
            layout_1_3 = (LinearLayout) view.findViewById(R.id.layout_1_3);
            layout_1_4 = (LinearLayout) view.findViewById(R.id.layout_1_4);
            layout_1_5 = (LinearLayout) view.findViewById(R.id.layout_1_5);
            layout_1_6 = (LinearLayout) view.findViewById(R.id.layout_1_6);

            //dispaly_none(list_1_1,false);dispaly_none(list_1_2,false);dispaly_none(list_1_3,false);dispaly_none(list_1_4,false);
            dispaly_none(layout_1_1,false);dispaly_none(layout_1_2,false);dispaly_none(layout_1_3,false);dispaly_none(layout_1_4,false);
            //隐藏标题
            dispaly_none(title_1_1,false);dispaly_none(title_1_2,false);dispaly_none(title_1_3,false);dispaly_none(title_1_4,false);dispaly_none(title_1_5,false);dispaly_none(title_1_6,false);

            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            //删除动作
            btn_delte = (TextView) view.findViewById(R.id.btn_delte);
            item_layoutContent = (LinearLayout) view.findViewById(R.id.itemContainer);
            if (itemView instanceof  SlidingButtonView){
                ((SlidingButtonView) itemView).setSlidingButtonListener(ReportAdapter.this);
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
                if (view instanceof TextView){
                    TextView view1 = (TextView) view;
                    view1.setText(val);
                }
            }else{
                view.setVisibility(View.GONE);
            }

        }
    }

    private String getVal(int i,String val){

        if(StringUtils.isNumeric(val)) {
            double val_ = Double.parseDouble(val);

            if (Math.abs(val_) > 10000) {
                val = SizheTool.jq2wxs(String.valueOf(val_ / 10000),"2") + "万";
            }/* else if (Math.abs(val_) > 1000) {
                val = SizheTool.jq2wxs(String.valueOf(val_ / 1000)) + "千";
            }*/
        }
        return val;
    }

    public List<String> createMain(Map<String,Object> data,String[] keys){
        List<String> resList = new ArrayList<String>();
        String key = "",val;
        boolean isDrqf = false;

        if (keys == null){
            Toast.makeText(mContext,"数据异常",Toast.LENGTH_SHORT).show();
            return resList;
        }

        int csl = 0;

        for (int i=0,n=keys.length;i<n; i++){
            key = keys[i];
            if (TextUtils.isEmpty(key)){
                continue;
            }
            if(key.indexOf("(drq)")>=0){
                key = key.replace("(drq)","");
                isDrqf = true;
            }else{
                isDrqf = false;
            }

            if ( data.get(key)!= null){
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
                }
                else if(key.equals("isyh")){
                    val = data.get(key).toString();
                    if (val.equals("否")){
                        val = "未还";
                    }else{
                        val = "已还";
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

                /*if (StringUtils.isNumeric(val)){
                    csl = i;
                }*/

            }else{
                val = "";
            }

            if (columns_list!= null && columns_list.size()>0){
               Map<String,Object> colMap =  FastJsonUtils.findMapToListByKey(columns_list,"field",key);
                if (colMap != null && colMap.get("type")!= null && colMap.get("type").toString().equals("int") ){
                    val = getVal(i,val);
                }
            }else{
                val = getVal(i,val);
            }

            resList.add(val);
        }

        return  resList;
    }

}
