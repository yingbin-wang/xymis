package com.cn.wti.util.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cn.wti.util.other.StringUtils;

public class FastJsonUtils {

	/**
	 * 功能描述：把JSON数据转换成普通字符串列表
	 * 
	 * @param jsonData
	 *            JSON数据
	 * @return
	 * @throws Exception
	 * @author myclover
	 */
	public static List<String> getStringList(String jsonData) throws Exception {
		return JSON.parseArray(jsonData, String.class);
	}

	/**
	 * 功能描述：把JSON数据转换成指定的java对象
	 * 
	 * @param jsonData
	 *            JSON数据
	 * @param clazz
	 *            指定的java对象
	 * @return
	 * @throws Exception
	 * @author myclover
	 */
	public static <T> T getSingleBean(String jsonData, Class<T> clazz)
			throws Exception {
		return JSON.parseObject(jsonData, clazz);
	}

	/**
	 * 功能描述：把JSON数据转换成指定的java对象列表
	 * 
	 * @param jsonData
	 *            JSON数据
	 * @param clazz
	 *            指定的java对象
	 * @return
	 * @throws Exception
	 * @author myclover
	 */
	public static <T> List<T> getBeanList(String jsonData, Class<T> clazz)
			throws Exception {
		return JSON.parseArray(jsonData, clazz);
	}

	/**
	 * 功能描述：把JSON数据转换成较为复杂的java对象列表
	 * 
	 * @param jsonData
	 *            JSON数据
	 * @return
	 * @throws Exception
	 * @author myclover
	 */
	public static List<Map<String, Object>> getBeanMapList(String jsonData)
			throws Exception {
		return JSON.parseObject(jsonData,
				new TypeReference<List<Map<String, Object>>>() {
				});
	}

	/**
	 * Json Array 转 listMaplink
	 * @param jay
	 * @return
	 * @throws Exception
     */
	public static List<Map<String, Object>> getLinkHashMapList(JSONArray jay)throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = null;
		for (Object jot:jay ) {
			map = strToMap(jot.toString()) ;
			list.add(map);
		}

		return list;
	}


	/**
	 * 将map转化为string
	 * @param m
	 * @return
	 */
	public static String mapToString(Map m) {
		String s = JSONObject.toJSONString(m);
		return s;
	}

	public static String mapToStringByfor(Map m) {

		String s = "";
		Set<String> sets =  m.keySet();
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (String key:sets) {
			if (m.get(key) instanceof  JSONArray){
				m.put(key,m.get(key).toString());
			}
		}

		return JSONObject.toJSONString(m);
	}

	/**
	 * 单个map
	 * @param m
	 * @return
     */
	public static String mapToString2(Map m) {
		String s = "";
		Set<String> sets =  m.keySet();
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (String key:sets) {
			sb.append(key+":"+m.get(key).toString().replace(",","<dh>")+",");
		}

		if (sb.length()>2){
			sb = new StringBuilder(sb.substring(0,sb.length()-1).toString()) ;
			sb.append("}");
			s = sb.toString();
		}else{
			s = "{}";
		}

		return s;
	}

	public  static  JSONObject strToJson (String json){
		if(!json.equals("") && !json.equals("(abcdef)")){
			return  JSONObject.parseObject(json);
		}
		return null;
	}

	public  static  Map<String, Object> strToMap (String json){
		try{
			if(!json.equals("") && !json.contains("(abcdef)")){
				return  JSONObject.parseObject(json,LinkedHashMap.class);
			}
		}catch (Exception e){
			return null;
		}

		return null;
	}

	/**
	 * 将网络请求下来的数据用fastjson处理空的情况，并将时间戳转化为标准时间格式
	 * @param result
	 * @return
	 */
	public static String dealResponseResult(String result) {
		result = JSONObject.toJSONString(result,
				SerializerFeature.WriteClassName,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullBooleanAsFalse,
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteNullNumberAsZero,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteDateUseDateFormat,
				SerializerFeature.WriteEnumUsingToString,
				SerializerFeature.WriteSlashAsSpecial,
				SerializerFeature.WriteTabAsSpecial);
		return result;
	}

	/**
	 * 将 指定Key map 值 转成 list String
	 * @param list
	 * @param key
     * @return
     */
	public  static  List<String> MapToListByKey(List<Map<String,Object>> list,String key){

		List<String> resList  = new ArrayList<String>();

		if(list != null && list.size() >0){
			Map<String,Object> map = null;
			String val = "";
			for (int i = 0; i < list.size(); i++) {
				map = list.get(i);
				if(map.get(key)!= null){
					val = map.get(key).toString();
					resList.add(val);
				}
			}
		}
		return  resList;
	}

	/**
	 * 查询Map
	 * @param list
	 * @param key
	 * @param val
     * @return
     */
	public  static  Map<String,Object> findMapToListByKey(List<Map<String,Object>> list,String key,String val){

		if(list != null && list.size() >0){
			Map<String,Object> map = null;
			for (int i = 0; i < list.size(); i++) {
				map = list.get(i);
				if(map.get(key)!= null && map.get(key).toString().equals(val)){
					return  map;
				}
			}
		}
		return  null;
	}

	public static String[] ListMapToListStr (List<Map<String,Object>> list,String key) {

		String [] res_str = new String[list.size()],keys = null;
		int i = 0;

		for (Map<String,Object> map : list) {

			if(key.indexOf("~")>=0){
				keys = key.split("~");
			}

			if (keys != null){
				String contents = "";
				for (String col_:keys) {
					if( map.get(col_) != null){
						if (contents.equals("")){
							contents += map.get(col_).toString();
						}else{
							contents += " " +map.get(col_).toString();
						}
					}
				}

				res_str[i] = contents;

			}else{
				if( map.get(key) == null){
					res_str[i] = null;
				}else{
					res_str[i] = map.get(key).toString();
				}
			}
			i++;
		}
		return res_str;
	}

	/**
	 * 将 listMap 转字符串
	 * @param list
	 * @return
	 */
	public static String ListMapToListStr (List<Map<String,Object>> list) {

		if(list == null){
			return "[]";
		}
		String str = "",strs = "";
		int i = 0,n = list.size();
		for (Map<String,Object> map : list) {
			str = mapToString(map);
			if(i  == n -1){
				strs += str;
			}else{
				strs += str+",";
			}

			i++;
		}
		return "["+strs+"]";
	}

	/**
	 * 将指定key值 与 val 值 转换成 Map
	 * @param list
	 * @param key
	 * @param val
	 * @return
	 */
	public static Map<String, Object> ListPdTOmap (List<Map<String,Object>> list,String key,String val) {
		Map<String, Object> map = new HashMap<String, Object>();

		for (Map<String,Object> pd : list) {

			map.put(pd.get(key).toString(), pd.get(val));
		}
		return map;
	}

	/**
	 * 通过 key值 查询指定值 封装到 list string 中
	 * @param list
	 * @param key
	 * @param val
     * @return
     */
	public static List<String> ListPdTOListStr (List<Map<String,Object>> list,String key,String val,String resKey) {
		List<String> res = new ArrayList<String>();

		for (Map<String,Object> pd : list) {
			if(pd.get(key) != null && pd.get(key).toString().equals(val)){
				res.add(pd.get(resKey).toString());
			}
		}
		return res;
	}

	/**
	 * 通过 key 和 val 查询 符合结果的list
	 * @param list
	 * @param key
	 * @param val
     * @return
     */
	public static List<Map<String, Object>> findListPdByKey_and_Val (List<Map<String,Object>> list,String key,String val) {
		List<Map<String, Object>> _dataList = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = null;

		for (Map<String,Object> pd : list) {
			if(pd.get(key) != null && pd.get(key).toString().equals(val)) {
				_dataList.add(pd);
			}
		}
		return _dataList;
	}

	/**
	 * 通过 key1 和 val 查询 符合结果的list key2
	 * @param list
	 * @param key1
	 * @param key2
	 * @param val
	 * @return
	 */
	public static List<String> findListStrByKey_and_Val (List<Map<String,Object>> list,String key1,String key2,String val) {
		List<String> _dataList = new ArrayList<String>();
		Map<String,Object> map = null;

		for (Map<String,Object> pd : list) {
			if(pd.get(key1) != null && pd.get(key1).toString().equals(val)) {
				_dataList.add(pd.get(key2).toString());
			}
		}
		return _dataList;
	}

	/**
	 * 向 int 数组指定位置 插入元素
	 * @param a
	 * @param c
	 * @param val
     * @return
     */
	public int[] insertArray(int[]a,int c,int val){
		int[] b=new int[a.length +1];
		for (int i = 0; i < a.length; i++) {
			b[i]=a[i];
		}    for (int i =c; i < b.length-1; i++) {    b[i+1]=a[i];
		}   b[c]=val;
		a=b;
		return  a;
	}

	/**
	 * 向字符型数组 插入 指定元素
	 * @param a
	 * @param c
	 * @param val
     * @return
     */
	public String[] insertStrArray(String[]a,int c,String val){
		String[] b=new String[a.length +1];
		for (int i = 0; i < a.length; i++) {
			b[i]=a[i];
		}    for (int i =c; i < b.length-1; i++) {    b[i+1]=a[i];
		}   b[c]=val;
		a=b;
		return  a;
	}


	public static String[] insertStrArrayToArray(String[]a,int c,String[] vals){
		String[] b=new String[a.length +vals.length];
		for (int i = 0; i < c; i++) {
			b[i]=a[i];
		}

		int test = c;

		for (int i =c; i < b.length; i++) {
			if(i - c >= vals.length){
				b[i] = a[test++];
			}else{
				b[i]=vals[i - c];
			}
		}
		a=b;
		return  a;
	}

	public static String[] insertStrArrayToArray(String[]a,int c,String[] vals,String[] titles){
		String[] b=new String[a.length +vals.length];
		for (int i = 0; i < c; i++) {
			b[i]=a[i];
		}

		int test = c;

		for (int i =c; i < b.length; i++) {
			if(i - c >= vals.length){
				b[i] = a[test++];
			}else{
				b[i]=vals[i - c];
			}
		}
		a=b;
		return  a;
	}

	/**
	 * 得到返回的List 结果
	 * @param resMap
	 * @return
	 */
	public static List<Map<String,Object>> getResultList(Map<String, Object> resMap){
		if(resMap.get("state").toString().equals("success")){
			Object o = resMap.get("data");
			if(o instanceof JSONArray){
				return  (List<Map<String,Object>>)o;
			}
			else {
				return  (List<Map<String, Object>>) (((Map<String, Object>) o).get("rows"));
			}
		}
		return  null;
	}

	/**
	 * 得到返回的 结果
	 * @param resMap
	 * @return
	 */
	public static Map<String,Object> getResultMap(Map<String,Object> resMap){
		if(resMap.get("state").toString().equals("success")) {
			return (Map<String, Object>) resMap.get("data");
		}
		return  null;
	}

	/**
	 * 得到返回的 结果
	 * @param res
	 * @return
	 */
	public static Map<String,Object> getResultStrTOMap(String res){
		Map<String,Object> resMap = FastJsonUtils.strToMap(res.toString());
		if(resMap.get("state").toString().equals("success")) {
			return (Map<String, Object>) resMap.get("data");
		}
		return  null;
	}

	/**
	 * list 字符 转 数组
	 * @param _list
	 * @return
	 */
	public static String[] listStrToArray(List<String> _list){
		return  _list.toArray(new String[_list.size()]);
	}

	public static int[] listIntToArray(List<Integer> _list){

		if (_list == null){return new int[]{};}

		int[] logs = new int[_list.size()];

		for (int i = 0,n=_list.size();i<n;i++) {
			logs[i] = _list.get(i);
		}
		return logs;
	}

	/**
	 * 数组 转 list 字符
	 * @param strs
	 * @return
	 */
	public static List<String> arrayToListStr(String[] strs){
		return  new ArrayList(Arrays.asList(strs));
	}

	public static List<Integer> arrayToListInt(int[] strs){

		for (int a:strs){

		}

		return  new ArrayList(Arrays.asList(strs));
	}


	/**
	 * 删除 int 型数组 通过下表
	 * @param arr
	 * @param index
     * @return
     */
	public static int[] removeIntByIndex(int[] arr,int index){
		int[] ary=arr;
		System.arraycopy(arr, index+1,ary,index,arr.length-index-1);
		ary= Arrays.copyOf(ary, ary.length-1);
		return ary;
	}



	/**
	 * 将sets 转换成 数组
	 * @param sets
	 * @return
     */
	public static String[] setStrToArray(Set<String> sets){
		List<String> _list = new ArrayList<String>();
		_list.addAll(sets);
		return  _list.toArray(new String[_list.size()]);
	}

	public static String getVal(Map<String,Object> data,String key){
		String val="";
		if (data != null){
			if (data.get(key) != null){
				val = data.get(key).toString();
			}else{
				val ="";
			}
		}else{
			val = "";
		}
		return  val;
	}

	/**
	 * 判断是否存在
	 * @param datas
	 * @param key
	 * @param val
     * @return
     */
	public static boolean isExitVal(List<Map<String,Object>> datas,String key,String val){
		for (Map<String,Object> dataMap:datas) {
			if (val.equals(dataMap.get(key).toString())){
				return  true;
			}
		}

		return  false;
	}

	public static Map<String,Object> getMapExitVal(List<Map<String,Object>> datas,String key,String val){
		int i = 0;
		for (Map<String,Object> dataMap:datas) {
			if (val.equals(dataMap.get(key).toString())){
				dataMap.put("index",i);
				return  dataMap;
			}
			i++;
		}

		return  null;
	}

	/**
	 * 根据 字段值 与 value 查询 返回list
	 * @param copyList
	 * @param cxcolumns
	 * @param cs
     * @return
     */
	public static List<Map<String,Object>> findListBycolAndval(List<Map<String,Object>> copyList,String[] cxcolumns,String cs){

		List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
		if (copyList != null && copyList.size()>0){
			Map<String,Object> firstMap = (Map<String, Object>) copyList.get(0);
			String cxs="";
			for (String col:cxcolumns) {
				if (firstMap.get(col)!= null){
					if (cxs.equals("")){
						cxs+=col;
					}else{
						cxs+=","+col;
					}
				}
			}

			String[] columns = cxs.split(",");
			Map<String,Object> resMap = null;

			for (int i=0,n=copyList.size();i<n;i++) {
				resMap = (Map<String, Object>) copyList.get(i);
				for (String col:columns) {
					if(resMap.get(col).toString().indexOf(cs)>=0 ){
						resList.add(resMap);
						break;
					}
				}
			}

			/*if (resList.size() >0){
				mList.clear();
				mList.addAll(resList);
				mSingleChoicAdapter.notifyDataSetChanged();
			}*/

		}
		return  resList;
	}

	/**
	 *
	 * @param list
	 * @param keys
	 * @param val
	 * @return
	 */
	public static List<Map<String, Object>> ListPdTOListByKeys (List<Map<String,Object>> list,String[] keys,String val) {
		List<Map<String, Object>> resList = new ArrayList<Map<String,Object>>();

		for (Map<String,Object> pd : list) {
			String key_one = "";
			for (String key : keys) {
				if (key_one.equals("")){
					key_one += pd.get(key).toString();
				}else{
					key_one += ","+pd.get(key).toString();
				}
			}

			if (key_one.equals(val)){
				resList.add(pd);
			}
		}
		return resList;
	}


	/**
	 * 将List String 转换成 ListMap
	 * @param mList
	 * @return
     */
	public static List<Map<String,Object>> listStringToListMap(List<String> mList){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if (mList != null && mList.size() >0){

			for (String val:mList) {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name",val);
				list.add(map);
			}
		}
		return  list;
	}

	/**
	 * 将list strs 转换成 str
	 * @param list
	 * @return
     */
	public static String listStrTOStr(List<String> list){
		String strs = "";
		if (list == null){return "";}
		for (String str:list) {
			if (strs.equals("")){
				strs += str;
			}else {
				strs += ","+str;
			}
		}
		return  strs;
	}

	public static String listStrTOStr(List<String> list,String split){
		String strs = "";
		if (list == null){return "";}
		for (String str:list) {
			if (strs.equals("")){
				strs += str;
			}else {
				strs += split+str;
			}
		}
		return  strs;
	}

	public static String getListsByVal(List<String> classList, String name) {
		for (String className1:classList) {

			if (!className1.equals("") && className1.substring(className1.lastIndexOf(".")+1).toLowerCase().equals(name.toLowerCase())){
				return className1;
			}
		}
		return "";
	}

	/**
	 * 从 list map 中得到 指定key 值的 字符串
	 * @param list
	 * @param key
	 * @param split
     * @return
     */
	public static String ListMapToStr (List<Map<String,Object>> list,String key,String split) {

		int i = 0;
		String val = "";
		String contents = "";

		for (Map<String,Object> map : list) {

			if( map.get(key) != null){
				val = map.get(key).toString();
				if (StringUtils.isNumeric(val)){
					if (contents.equals("")){
						contents += val;
					}else{
						contents += split +val;
					}

				}else{
					if (contents.equals("")){
						contents += "'"+val+"'";
					}else{
						contents += split+"'"+val+"'";
					}
				}
			}
		}
		return contents;
	}

	public static List<String> mergeList(List<String> list1,List<String> list2){
		List<String> resList = new ArrayList<String>();
		if (list1 != null && list1.size()>0 && list2 != null && list2.size()>0){
			int i = 0;
			for (String str1:list1) {
				resList.add(str1+","+list2.get(i));
				i++;
			}
		}
		return  resList;
	}
	/**
	 * 按商品 ID 合计 数量
	 * @param records
	 * @param colname
	 * @param val
	 */
	public static List<Map<String,Object>> mergSumRecordsByKeyAndVal(List<Map<String,Object>>records,String colname,String val){
		List<Map<String,Object>> copy_records = new ArrayList<Map<String,Object>>();
		Map<String,Object>copy_record = new HashMap<String, Object>(),record2,record;
		String key="";
		Object val1;
		String[] colnames;

		if(records != null && records.size() >0){
			for(int j=0;j<records.size();j++){
				record = records.get(j);
				if(colname.indexOf(',')>=0){
					colnames = colname.split(",");
					for(int i = 0,n = colnames.length;i<n;i++){
						colname = colnames[i];
						if(key.equals("")){
							key += record.get(colname).toString();
						}else{
							key += "~" +record.get(colname).toString();
						}
					}
				}else{
					key = record.get(colname).toString();
				}

				val1 = copy_record.get(key);
				if(val1 != null){
					copy_record.put(key,Integer.parseInt(val1.toString()) + Integer.parseInt(record.get(val).toString()));
					record2 = findRecordByKeyAndVal(copy_records,colname,key);
					record2.put(val,copy_record.get(key));
				}else{
					copy_record.put(key,record.get(val));
					record2 =  new HashMap<String, Object>();
					record2.putAll(record);
					copy_records.add(record2);
				}
			}
		}
		return copy_records;
	}

	/**
	 * 通过 键值对 返回 一个对象
	 * @param records
	 * @param colname
	 * @param val
	 * @returns
	 */
	public static Map<String, Object> findRecordByKeyAndVal (List<Map<String,Object>>records,String colname,String val){
		String[] colnames;
		String key="";
		if(records != null && records.size() >0){
			for(int j=0;j<records.size();j++){
				Map<String,Object> record = records.get(j);
				if(colname.indexOf(',')>=0){
					colnames = colname.split(",");
					for(int i = 0,n = colnames.length;i<n;i++){
						colname = colnames[i];
						if(key.equals("")){
							key += record.get(colname).toString();
						}else{
							key += "~" +record.get(colname).toString();
						}
					}
				}else{
					key = record.get(colname).toString();
				}
				if(record.get(key).toString().equals(val)){
					return record;
				}
			}
		}
		return null;
	}

	/**
	 * 查找指定列 指定值 的集合
	 * @param records
	 * @param colname
	 * @param val
	 */
	 public static  List<Map<String,Object>> findRecordsByKeyAndValNot(List<Map<String,Object>>records,String colname,Object val){
		 List<Map<String,Object>> records_11 = new ArrayList<Map<String,Object>>();
		if(records != null && records.size() > 0){
			for(int j=0;j<records.size();j++){
				Map<String,Object> record = records.get(j);
				if(record.get(colname).toString() != val){
					records_11.add(record);
				}
			}
		}
		return records_11;
	}

	/**
	 * 字符串转
	 * @param jsonData
	 * @return
	 */
	public static List<Map<String, Object>> strToListMap(String jsonData){
		return JSON.parseObject(jsonData.replace("\\",""),new TypeReference<List<Map<String, Object>>>() {});
	}

	public static JSONObject mapToJson(Map<String,Object> map){
		JSONObject jot = new JSONObject();
		if (map != null);{
			jot.putAll(map);
		}
		return jot;
	}

	public static Map<String,String> mapTOmapStr(Map<String,Object> map){
		Set<String> keys = map.keySet();
		Map<String,String> resMap = new HashMap<>();
		for (String key1 : keys){
			resMap.put(key1,map.get(key1).toString());
		}
		return resMap;
	}

	/**
	 * 根据参数更新主数据
	 * @param m
	 * @param m2
	 * @param params
	 * @return
	 */
	public static String mapTOmapByParams(Map m,Map m2,Map<String,Object> params) {

		String s = "";
		Set<String> sets =  params.keySet();
		for (String key:sets) {
			Object o = m2.get(params.get(key));
			if (o == null){
				o = "";
			}
			m.put(key,o);
		}

		return JSONObject.toJSONString(m);
	}
}
