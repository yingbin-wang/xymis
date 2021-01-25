package com.cn.wti.util.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.other.Cn2SpellUtils;
import com.cn.wti.util.other.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangz on 2016/9/20.
 */
public class DatabaseUtils extends SQLiteOpenHelper{

    private static  DatabaseUtils db = null;
    private static  SQLiteDatabase database = null;

    public static synchronized DatabaseUtils getInstance(Context context,String name) {
        if (db == null) {
            db = new DatabaseUtils(context,name);
            //database = SQLiteDatabase.openDatabase("wtmis.db", null, Context.MODE_PRIVATE);
        }
        return db;
    }

    public static synchronized SQLiteDatabase getInstance(String name) {
        if (database == null) {
            database = SQLiteDatabase.openDatabase(name, null, Context.MODE_PRIVATE);
        }
        return database;
    }

    public DatabaseUtils(Context context, String name,
                         SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseUtils(Context context, String name, int version){
        this(context,name,null,version);
    }

    public DatabaseUtils(Context context, String name){
        this(context,name,1);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    /**
     * 判断某张表是否存在
     * @param tabName 表名
     * @return
     */
    public boolean tabIsExist(String tabName){
        boolean result = false;
        if(tabName == null){
            return false;
        }

        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"'" ;
            cursor = db.getReadableDatabase().rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }
            cursor.close();
            /*db.close();*/

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }

    /**
     * 将数据库查询结果 封装成 List map
     * @param table_name 表名
     * @param colums     查询字段名
     * @param where_parms      where 条件
     * @param where_values    where 条件对应的 值
     * @param group      分组方式
     * @param having     having条件
     * @param ordrer     排序方式
     * @return
     */
    public List<Map<String,Object>> exec_select(String table_name,String[] colums,String where_parms,String[] where_values,String group,String having,String ordrer){
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        try{
            Cursor cursor = db.getReadableDatabase().query(table_name,colums, where_parms, where_values, group, having, ordrer);
            Map<String,Object> map = null;
            String val = "";

            while(cursor.moveToNext()){
                map = new HashMap<String,Object>();
                for (String column : colums) {
                    if (!column.equals("")){
                        val = cursor.getString(cursor.getColumnIndex(column));
                        map.put(column,val);
                    }
                }
                list.add(map);
            }
            cursor.close();
            //关闭数据库
            db.close();
        }catch (Exception e){

        }
        return  list;
    }

    /**
     * 添加数据到 数据库中
     * @param table_name
     * @param cv
     * @return
     */
    public Long exec_insert(String table_name,ContentValues cv){

        long updates;
        //调用insert方法，将数据插入数据库
        try{
            updates = db.getWritableDatabase().insert(table_name, null, cv);
        }catch (Exception e){
            e.printStackTrace();
            updates = 0;
        }
        //关闭数据库
        db.close();
        return updates;
    }

    public void execSql(String sql){
        db.getWritableDatabase().execSQL(sql);
        db.close();
    }

    /**
     * 验证数据重复性
     * @param tabName 表名
     * @return
     */
    public boolean columsIsExist(String tabName,String columns ,String vals){
        boolean result = false;
        if(tabName == null){
            return false;
        }

        Cursor cursor = null;
        String[] columns_array,values_array;
        String column;
        try {
            String sql = "select count(*) as c from "+tabName+" where " ;
            if (!columns.equals("")){
                columns_array = columns.split(",");
                values_array = vals.split(",");
                for (int i=0,n = columns_array.length;i<n;i++) {
                    column = columns_array[i];
                    if (i == n-1){
                        sql += column+" ='"+values_array[i];
                    }else {
                        sql += column + " ='" + values_array[i] + "' and ";
                    }
                }
            }

            cursor = db.getReadableDatabase().rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }
            cursor.close();

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }

    /**
     * 添加数据到 数据库中
     * @param table_name
     * @param cv
     * @return
     */
    public Long exec_insert(String table_name,ContentValues cv,String colums,String vals){

        long updates;
        if(!columsIsExist(table_name,colums,vals)){
            //调用insert方法，将数据插入数据库
            updates = db.getWritableDatabase().insert(table_name, null, cv);
            //关闭数据库
            db.close();
        }else{
            updates = 0;
        }

        return updates;
    }

    /**
     * //参数1 是要更新的表名
     //参数2 是一个ContentValeus对象
     //参数3 是where子句
     * @param columns
     * @param vals
     * @param where
     * @param whereArgs_str
     * @return
     */
    public  int exec_update(String tab_name,String columns,String vals,String where,String whereArgs_str) {

        String[] columns_array, values_array;
        String column;

        if (!columns.equals("")) {
            ContentValues cv = new ContentValues();
            columns_array = columns.split(",");
            values_array = vals.split(",");
            for (int i = 0, n = columns_array.length; i < n; i++) {
                column = columns_array[i];
                cv.put(column, values_array[i]);
            }

            //where 子句 "?"是占位符号，对应后面的"1", String whereClause="id=?";
            String[] whereArgs = null;
            if (!where.equals("")) {
                whereArgs = whereArgs_str.split(",");
            }
            return db.getWritableDatabase().update(tab_name, cv, where, whereArgs);
        }
        return  0;
    }

    /**
     * 删除数据
     * @param tab_name
     * @param where
     * @param whereArgs_str
     * @return
     */
    public  int exec_delete(String tab_name,String where,String whereArgs_str) {

        String[] whereArgs = null;
        if (!where.equals("")) {
            whereArgs = whereArgs_str.split(",");
            int i = 0;
            for (String one:whereArgs) {
                if (one.indexOf("<dh>")>=0){
                    whereArgs[i] = one.replace("<dh>",",");
                }
                i++;
            }

        }

        int count = db.getWritableDatabase().delete(tab_name, where, whereArgs);
        db.close();
        return count;
    }

    public static boolean  deleteDatabase(Context context,String data_name) {
        return context.deleteDatabase(data_name);
    }

    public  boolean execSql1(String table_name, List<Map<String, Object>> list, String columns){

        String[] colSplit = columns.split(",");
        String sql,col = "",values = "";
        sql = "replace into "+table_name;

        for (int i=0,n = colSplit.length;i<n ;i++){
            if (i==n-1){
                col += colSplit[i];
            }else{
                col += colSplit[i]+",";
            }
        }

        sql += "("+col+")values（";
        Object val = null;
        int j = 0,jn = 0;
        jn = list.size();

        for (Map<String,Object> map:list) {
            for (int i=0,n = colSplit.length;i<n ;i++){
                if( map.get(colSplit[i])==null){
                    val = "null";
                }else{
                    val =  map.get(colSplit[i]);
                }

                if (!StringUtils.isNumeric(val.toString()) && !val.equals("null")){
                    val = "'"+val+"'";
                }else{
                    val =  map.get(colSplit[i]).toString();
                }
                if (i==n-1) {
                    values += val;
                }else{
                    values += val + ",";
                }
            }

            if (j == jn-1){
                sql+= values;
            }else{
                sql+= values+",";
            }
            j++;
        }
        db.getWritableDatabase().execSQL(sql+")");


        /*replace into student( _id , name ,age ) VALUES ( 1,'zz7zz7zz',25)*/
        return  false;
    }


    public  boolean execSql1(String table_name, List<Map<String, Object>> list,List<String> selectedList,String column,String app_column){

        String[] colSplit = column.split(",");
        String[] app_cloumnSlit = app_column.split(",");

        String sql,col = "",values = "",temp1="",package1="";
        temp1 = "replace into "+table_name;

        for (int i=0,n = app_cloumnSlit.length;i<n ;i++){
            if (i==n-1){
                col += app_cloumnSlit[i];
            }else{
                col += app_cloumnSlit[i]+",";
            }
        }

        temp1 += "("+col+")values(";


        List<Map<String,Object>> itemsList = null;

        for (Map<String,Object> map:list) {
            exec_one(colSplit,app_cloumnSlit,temp1,map,selectedList,"");
            if(map.get("items")!= null){
                if (map.get("items") instanceof  String){
                    itemsList = FastJsonUtils.strToListMap(map.get("items").toString());
                }else{
                    itemsList = (List<Map<String,Object>>)map.get("items");
                }
                for (Map<String,Object> map2:itemsList) {;
                    package1 = map.get("code").toString();
                    package1 = package1.toLowerCase();
                    exec_one(colSplit,app_cloumnSlit,temp1,map2,selectedList,package1);
                }
            }
        }
        return  false;
    }

    /**
     * 执行insert 或 update 方法
     * @param table_name
     * @param list
     * @param column
     * @param app_column
     * @return
     */
    public  boolean execSql2(String table_name, List<Map<String, Object>> list,String column,String app_column){

        String[] colSplit = column.split(",");
        String[] app_cloumnSlit = app_column.split(",");

        String sql,col = "",values = "",temp1="",package1="";
        temp1 = "replace into "+table_name;

        for (int i=0,n = app_cloumnSlit.length;i<n ;i++){
            if (i==n-1){
                col += app_cloumnSlit[i];
            }else{
                col += app_cloumnSlit[i]+",";
            }
        }

        temp1 += "("+col+")values(";

        List<Map<String,Object>> itemsList = null;
        if(list == null) return false;
        for (Map<String,Object> map:list) {
            exec_two(colSplit,app_cloumnSlit,temp1,map);
        }
        return  false;
    }

    private void exec_one(String[] colSplit,String[] app_cloumnSlit,String temp1,Map<String,Object> map,List<String> selectedList,String package1){

        String sql,col = "",values = "",test1 = "";
        Object val = null;
        boolean flage = false;
        values = "";
        for (int i=0,n = colSplit.length;i<n ;i++){
            if( map.get(colSplit[i])==null){
                val = "null";
            }else{
                val =  map.get(colSplit[i]);
            }

            if (colSplit[i].equals("text")){

                if (selectedList.contains(val)){
                    flage = true;
                }else{
                    flage = false;
                }
            }else if(colSplit[i].equals("code") && !package1.equals("")){
                test1 = val.toString();
                val = test1.toString();
            }

            if (!StringUtils.isNumeric(val.toString()) && !val.equals("null")){
                val = "'"+val+"'";
            }

            if (i==n-1) {
                values += val;
                if (flage){
                    values += ",'"+ AppUtils.app_username+"',1,'"+package1+"','"+AppUtils.app_address+"')";
                }else {
                    values += ",'"+ AppUtils.app_username+ "',0,'"+package1+"','"+AppUtils.app_address+"')";
                }
            }else{
                values += val + ",";
            }
        }

        sql = temp1 + values;
        db.getWritableDatabase().execSQL(sql);
    }

    private void exec_two(String[] colSplit,String[] app_cloumnSlit,String temp1,Map<String,Object> map){

        String sql,col = "",values = "",test1 = "";
        Object val = null;
        values = "";
        for (int i=0,n = colSplit.length;i<n ;i++){
            if ( map.get(colSplit[i])==null && colSplit[i].equals(AppUtils.app_address)){
                val =  colSplit[i];
            }else if("8594584".equals(colSplit[i])){
                val = Cn2SpellUtils.getInstance().getSelling(map.get("name").toString());
            }else if( map.get(colSplit[i])==null){
                val = "null";
            }else{
                val =  map.get(colSplit[i]);
            }

            if (!StringUtils.isNumeric(val.toString()) && !val.equals("null")){
                val = "'"+val+"'";
            }

            if (i==n-1) {
                values += val;
                values +=")";
            }else{
                values += val + ",";
            }
        }

        sql = temp1 + values;
        db.getWritableDatabase().execSQL(sql);
    }

    /**
     * 编码转换
     * @param text
     * @param dqbm
     * @param tobm
     * @return
     */
    public String DqbmToBM(String text,String dqbm,String tobm){

        try {
            byte[] val = text.getBytes(dqbm);
            text=new String(val,tobm);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

}