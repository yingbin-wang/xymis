package com.cn.wti.util.number;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wyb on 2017/4/14.
 */

public class IniFileUtils {

    private static BufferedReader bufferedReader = null;

    public IniFileUtils(InputStream inputStream) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
    }
    /**
     * 从ini配置文档中读取变量的值
     * @param section 要获取的变量所在段名称
     * @param variable 要获取的变量名称
     * @param defaultValue 变量名称不存在时的默认值
     * @return 变量的值
     * @throws IOException 抛出文档操作可能出现的io异常
     */
    public static String getProfileString(
            String section,
            String variable,
            String defaultValue)
            throws IOException {
        String strLine, value = "";
        boolean isInSection = false;
        try {
            while ((strLine = bufferedReader.readLine()) != null) {
                strLine = strLine.trim();
                Pattern p;
                Matcher m;
                p = Pattern.compile("]");
                m = p.matcher((strLine));
                if (m.matches()) {
                    p = Pattern.compile("]");
                    m = p.matcher(strLine);
                    if (m.matches()) {
                        isInSection = true;
                    } else {
                        isInSection = false;
                    }
                }
                if (isInSection == true) {
                    strLine = strLine.trim();
                    String[] strArray = strLine.split("=");
                    if (strArray.length == 1) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(variable)) {
                            value = "";
                            return value;
                        }
                    } else if (strArray.length == 2) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(variable)) {
                            value = strArray[1].trim();
                            return value;
                        }
                    } else if (strArray.length > 2) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(variable)) {
                            value = strLine.substring(strLine.indexOf("=") + 1).trim();
                            return value;
                        }
                    }
                }
            }
        } finally {
            bufferedReader.close();
        }
        return defaultValue;
    }
    /**
     * 修改ini配置文档中变量的值
     * @param file 配置文档的路径
     * @param section 要修改的变量所在段名称
     * @param variable 要修改的变量名称
     * @param value 变量的新值
     * @throws IOException 抛出文档操作可能出现的io异常
     */
    public static boolean setProfileString(
            String file,
            String section,
            String variable,
            String value)
            throws IOException {
        String fileContent, allLine,strLine, newLine, remarkStr;
        String getValue;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        boolean isInSection = false;
        fileContent = "";
        try {

            while ((allLine = bufferedReader.readLine()) != null) {
                allLine = allLine.trim();
                System.out.println("allLine == "+allLine);
                strLine = allLine;
                Pattern p;
                Matcher m;
                p = Pattern.compile("]");
                m = p.matcher((strLine));
                //System.out.println("+++++++ ");
                if (m.matches()) {
                    System.out.println("+++++++ ");
                    p = Pattern.compile("]");
                    m = p.matcher(strLine);
                    if (m.matches()) {
                        System.out.println("true ");
                        isInSection = true;
                    } else {
                        isInSection = false;
                        System.out.println("+++++++ ");
                    }
                }

                if (isInSection == true) {

                    strLine = strLine.trim();
                    String[] strArray = strLine.split("=");
                    getValue = strArray[0].trim();

                    if (getValue.equalsIgnoreCase(variable)) {
                        // newLine = getValue + " = " + value + " " + remarkStr;
                        newLine = getValue + " = " + value + " ";
                        fileContent += newLine + "\r\n";
                        while ((allLine = bufferedReader.readLine()) != null) {
                            fileContent += allLine + "\r\n";
                        }
                        bufferedReader.close();
                        BufferedWriter bufferedWriter =
                                new BufferedWriter(new FileWriter(file, false));
                        bufferedWriter.write(fileContent);
                        bufferedWriter.flush();
                        bufferedWriter.close();

                        return true;
                    }
                }
                fileContent += allLine + "\r\n";
            }
        }catch(IOException ex){
            throw ex;
        } finally {
            bufferedReader.close();
        }
        return false;
    }
    /*public static void main(String[] args) {
    //String value = Config.getProfileString("sysconfig.ini", "Option", "OracleDB", "default");
    //System.out.println(value);
        try {

            System.out.println("值已经改变!... "+ConfigurationFile.setProfileString("E:/web5/h2_test/gamewww.ini", "TestSect1", "10012", "111111"));

            System.out.println("值读取成功!... "+ConfigurationFile.getProfileString("E:/web5/h2_test/gamewww.ini", "TestSect1", "10012", ""));

        } catch (IOException e) {
            System.out.println("错误 ......"+e.toString());
        }
    }*/
}
