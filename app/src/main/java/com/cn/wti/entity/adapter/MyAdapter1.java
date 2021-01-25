package com.cn.wti.entity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.cn.wti.util.other.DateUtil;
import com.wticn.wyb.wtiapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2017/5/18.
 */

public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.ViewHolder> implements View.OnClickListener{
    public List<Map<String,Object>> datas = null;
    Map<String,Object> data_map = null;
    private int screenWidth= 0,layoutid= 0;
    private String[] titles,contents;
    private boolean[] mCheck;
    private TextView count_view;
    private boolean sfVisible = false;

    public MyAdapter1(List<Map<String,Object>> datas,int screenWidth ,String[] titles,String[]contents,int layoutid,boolean[]mCheck) {
        this.datas = datas;
        this.screenWidth = screenWidth;
        this.titles = titles;
        this.contents = contents;
        this.layoutid = layoutid;
        this.mCheck = mCheck;
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
        }
    }

    public boolean[] getSelectItem(){
        return  mCheck;
    }

    public void  setViewCount(TextView count_view){
        this.count_view = count_view;
        count_view.setText("共 "+mCheck.length+" 条数据");
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (layoutid == 0){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_middle3_01,viewGroup,false);
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
                        break;
                    case 2:
                        viewHolder.title_2.setText(titles[i-1]);
                        break;
                    case 3:
                        viewHolder.title_3.setText(titles[i-1]);
                        break;
                    case 4:
                        viewHolder.title_4.setText(titles[i-1]);
                        break;
                    case 5:
                        viewHolder.title_5.setText(titles[i-1]);
                        break;
                    case 6:
                        viewHolder.title_6.setText(titles[i-1]);
                        break;
                }
            }
        }

        if (contents != null && contents.length>0){
            i = 0;
            for (String content:contents){

                List<String> values = createMain(data_map,contents);
                if (values==null || values.size() == 0){return;}
                String[] test_contents = (String[]) values.toArray(new String[values.size()]);
                i++;
                switch (i){
                    case 1:
                        viewHolder.titile.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.titile,true);
                        break;
                    case 2:
                        viewHolder.content.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.content,true);
                        break;
                    case 3:
                        viewHolder.content_1.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.content_1,true);
                        break;
                    case 4:
                        viewHolder.content_2.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.content_2,true);
                        break;
                    case 5:
                        viewHolder.content_3.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.content_3,true);
                        break;
                    case 6:
                        viewHolder.content_4.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.content_4,true);
                        break;
                    case 7:
                        viewHolder.content_5.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.content_5,true);
                        break;
                    case 8:
                        viewHolder.content_6.setText(test_contents[i-1]);
                        dispaly_none(viewHolder.content_6,true);
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

        if (count_view != null){
            count_view.setText("共 "+mCheck.length+" 条数据");
        }

        //删除动作
        viewHolder.btn_delte.setTag(position);
        viewHolder.btn_delte.setOnClickListener(this);

    }
    //获取数据的数量
    @Override
    public int getItemCount() {
          return datas.size();
    }

    public void setSfVisible(boolean sfVisible) {
        this.sfVisible = sfVisible;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_delte:
                int index = (int) v.getTag();
                data_map = datas.get(index);
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
        public TextView btn_delte;//删除按钮

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

            dispaly_none(titile,false);dispaly_none(content,false);dispaly_none(content_1,false);
            dispaly_none(content_2,false);dispaly_none(content_3,false);dispaly_none(content_4,false);
            dispaly_none(content_5,false);dispaly_none(content_6,false);
            dispaly_none(content_7,false);dispaly_none(content_8,false);

            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            //删除动作
            btn_delte = (TextView) view.findViewById(R.id.btn_delte);
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

    public List<String> createMain(Map<String,Object> data,String[] keys){
        List<String> resList = new ArrayList<String>();
        String key = "",val;
        boolean isDrqf = false;

        for (int i=0,n=keys.length;i<n; i++){
            key = keys[i];

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
                }else if(isDrqf){
                    val = data.get(key).toString();
                    val = DateUtil.strFomatDate(val);
                }else{
                    val = data.get(key).toString();
                }

            }else{
                val = "";
            }
            resList.add(val);
        }

        return  resList;
    }

}
