package com.cn.wti.entity.view.custom.AdpaterListView_custom;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.cn.wti.entity.adapter.viewHolder.Edit_ViewHolder;
import com.cn.wti.entity.adapter.viewHolder.Select_ViewHolder;
import com.cn.wti.entity.adapter.viewHolder.Text_ViewHolder;
import com.cn.wti.entity.view.custom.EditText_custom;
import com.cn.wti.entity.view.custom.textview.TextView_custom;
import com.wticn.wyb.wtiapp.R;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.WebServiceHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangz on 2016/10/28.
 */
public class ListViewAdapter_01 extends BaseAdapter {

    private List<Map<String,Object>> list = null;
    private Map<String,Object> dataMap = null;
    private Map<String,Object> selectMap = null;
    private List<View> views = null;
    private Context context;
    private String key;

    final int Type1 = 0,Type2 = 1,Type3 = 2,Type4 = 3;

    public ListViewAdapter_01(Context context,List<Map<String,Object>> list,String key,Map<String,Object>dataMap,Map<String,Object> selectMap,List<View> views){
        this.list = list;
        this.context = context;
        this.key = key;
        this.dataMap = dataMap;
        this.selectMap = selectMap;
        this.views = views;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Map<String,Object> map = list.get(position);
        if (!map.get("is_select").toString().equals("")){
            return Type4;
        }else if (map.get("type")!= null && map.get("type").toString().equals("date")){
            return Type3;
        }else if (map.get("is_write")!=null && !map.get("is_write").toString().equals("")){
            return Type2;
        }else if (map.get("is_visible")!=null && !map.get("is_visible").toString().equals("")){
            return Type1;
        }
        return  Type1;
    }

    @Override
    public int getViewTypeCount() {
        return  4;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Map<String,Object> testmap = null;
        final Map<String,Object> map;
        map = (Map<String,Object>)getItem(position);
        //获取数据
        Object text = dataMap.get(map.get("code").toString());
        String type = map.get("type").toString();
        final String name = map.get("name").toString();
        text = AppUtils.returnVal(type,text);
        final TextView_custom tv1,tv2;
        final EditText_custom ed2;
        ImageButton ib2;

        Select_ViewHolder select_viewHolder= null;
        Edit_ViewHolder edit_viewHolder = null;
        Edit_ViewHolder edit_viewHolder2 = null;
        Text_ViewHolder text_viewHolder = null;

        int testType = getItemViewType(position);
        if (view == null) {
            switch (testType) {
                case Type1:
                    view = LayoutInflater.from(context).inflate(R.layout.activity_mytask_edit_item01,null);
                    text_viewHolder = new Text_ViewHolder(view);
                    tv1 =  (TextView_custom) view.findViewById(R.id.item01_title);
                    tv2 = (TextView_custom) view.findViewById(R.id.item01_content);
                    text_viewHolder.title = tv1;
                    text_viewHolder.content = tv2;
                    view.setTag(text_viewHolder);
                    break;
                case  Type2:
                    view = LayoutInflater.from(context).inflate(R.layout.activity_mytask_edit_item02,null);
                    edit_viewHolder = new Edit_ViewHolder(view);
                    tv1 =  (TextView_custom) view.findViewById(R.id.item02_title);
                    ed2 = (EditText_custom) view.findViewById(R.id.item02_content);
                    edit_viewHolder.title = tv1;
                    edit_viewHolder.content = ed2;
                    view.setTag(edit_viewHolder);
                    break;
                case  Type3:
                    view = LayoutInflater.from(context).inflate(R.layout.activity_mytask_edit_item02,null);
                    edit_viewHolder2 = new Edit_ViewHolder(view);
                    tv1 =  (TextView_custom) view.findViewById(R.id.item02_title);
                    ed2 = (EditText_custom) view.findViewById(R.id.item02_content);
                    edit_viewHolder2.title = tv1;
                    edit_viewHolder2.content = ed2;
                    view.setTag(edit_viewHolder2);
                    break;
                case Type4:
                    view = LayoutInflater.from(context).inflate(R.layout.activity_mytask_edit_item03,null);
                    select_viewHolder= new Select_ViewHolder(view,context);
                    tv1 =  (TextView_custom) view.findViewById(R.id.item03_title);
                    ed2 = (EditText_custom) view.findViewById(R.id.item03_content);
                    ImageButton ib1 = (ImageButton) view.findViewById(R.id.item03_select);
                    select_viewHolder.title = tv1;
                    select_viewHolder.content = ed2;
                    select_viewHolder.btnxz = ib1;
                    view.setTag(select_viewHolder);
                    break;
            }
        }else{
            switch (testType) {
                case Type1:
                    text_viewHolder = (Text_ViewHolder) view.getTag();
                    break;
                case  Type2:
                    edit_viewHolder = (Edit_ViewHolder) view.getTag();
                    break;
                case  Type3:
                    edit_viewHolder2 = (Edit_ViewHolder) view.getTag();
                    break;
                case Type4:
                    select_viewHolder = (Select_ViewHolder) view.getTag();
                    break;
            }
        }

        switch (testType){
            case Type1:
                text_viewHolder.title.setTextSize(14);
                text_viewHolder.title.setText(name);
                text_viewHolder.content.setText(text.toString());
                text_viewHolder.content.setTextSize(14);
                break;
            case  Type2:
                edit_viewHolder.title.setTextSize(14);
                edit_viewHolder.title.setText(name);
                edit_viewHolder.content.setText(text.toString());
                edit_viewHolder.content.setTextSize(14);
                break;
            case  Type3:
                final Date date = DateUtil.fomatDate(text.toString());
                edit_viewHolder2.title.setTextSize(14);
                edit_viewHolder2.title.setText(name);
                edit_viewHolder2.content.setText(text.toString());
                edit_viewHolder2.content.setTextSize(14);

                final Edit_ViewHolder finaleditViewHolder2 = edit_viewHolder2;
                finaleditViewHolder2.content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                finaleditViewHolder2.content.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, date.getYear()+1900, date.getMonth(), Integer.parseInt(DateUtil.getDay(date)));

                        datePickerDialog.show();
                    }
                });
                break;
            case Type4:

                select_viewHolder.title.setTextSize(14);
                select_viewHolder.title.setText(name);
                select_viewHolder.content.setText(text.toString());
                select_viewHolder.content.setTextSize(14);

                select_viewHolder.btnxz.setBackgroundResource(R.mipmap.u22);
                final Select_ViewHolder finalViewHolder = select_viewHolder;
                finalViewHolder.btnxz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(map.get("gldn") != null) {
                            String[] items = null;

                            final String gldn = map.get("gldn").toString();
                            if(gldn.equals("")){return;}
                            else if(gldn.contains(",")){
                                items = gldn.split(",");
                            }else{
                                String[] gldns = gldn.split("~");

                                if(selectMap.get(gldns[0])!= null){
                                    items = FastJsonUtils.ListMapToListStr((List<Map<String,Object>>) selectMap.get(gldns[0]),"name");
                                }else{
                                    Map<String,Object> testMap = new HashMap<String, Object>();
                                    testMap.put("serviceName",gldns[0].toLowerCase());
                                    testMap.put("methodName","list");
                                    testMap.put("parameters","{pageIndex:0,start:0,limit:10}");
                                    Object res = WebServiceHelper.getResult(AppUtils.app_address,"execute",testMap);
                                    if (res != null){
                                        Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
                                        if(resMap.get("state").toString().equals("success")){
                                            List<Map<String,Object>> dataList = (List<Map<String,Object>>) (((Map<String,Object>)resMap.get("data")).get("rows"));
                                            selectMap.put(gldns[0],dataList);
                                            items = FastJsonUtils.ListMapToListStr((List<Map<String,Object>>) selectMap.get(gldns[0]),"name");
                                        }
                                    }
                                }
                            }

                            final String[] finalItems = items;

                            new AlertDialog.Builder(context).setTitle(name).setIcon(
                                    android.R.drawable.ic_dialog_info).setSingleChoiceItems(items, 0,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(gldn.contains(",")){
                                                finalViewHolder.content.setText(finalItems[which]);
                                            }else{
                                                List<String> testList = FastJsonUtils.ListPdTOListStr(list,"gldn",gldn,"code");
                                                for (String aa:testList) {
                                                    View v =  AppUtils.findViewByCode(views,aa);
                                                    if (v instanceof TextView_custom){
                                                        List<Map<String,Object>> dataList = (List<Map<String,Object>>)selectMap.get(gldn.split("~")[0]);
                                                        TextView_custom tvs = (TextView_custom)v;
                                                        tvs.setText(dataList.get(which).get("id").toString());
                                                    }else if(v instanceof EditText_custom){
                                                        finalViewHolder.content.setText(finalItems[which]);
                                                    }
                                                }
                                            }

                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton("取消", null).show();
                        }
                    }
                });
                break;
        }

        return view;
    }
}