package com.cn.wti.util;

import java.util.Map;

/**
 * Created by ASUS on 2017/9/22.
 */

public class Constant {
    /*税率 税额 价税合计*/
    public  static String[]  taxrate_array =  new String[]{"hsdj","taxrate","taxje","taxtotal"};
    public  static String method_findById = "findById";

    /*文件类型*/
    public static final String DOC = "application/msword";
    public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String XLS = "application/vnd.ms-excel application/x-excel";
    public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String PPT = "application/vnd.ms-powerpoint";
    public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String PDF = "application/pdf";
    public static final String IMAGE = "image/*";
}
