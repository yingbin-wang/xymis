package com.cn.wti.util.number;

import android.util.TypedValue;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.io.BufferedOutputStream;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileOutputStream;

import java.io.IOException;

import java.io.InputStream;

import java.io.OutputStream;

import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
/**
 * 功能： 1 、实现把指定文件夹下的所有文件压缩为指定文件夹下指定 zip 文件 2 、实现把指定文件夹下的 zip 文件解压到指定目录下
 *
 * @author ffshi
 *
 */

public class ZipUtils {

    public static void zip(String sourceDir, String zipFile) {

        OutputStream os;

        try {

            os = new FileOutputStream(zipFile);

            BufferedOutputStream bos = new BufferedOutputStream(os);

            ZipOutputStream zos = new ZipOutputStream(bos);



            File file = new File(sourceDir);



            String basePath = null ;

            if (file.isDirectory()) {

                basePath = file.getPath();

            } else {

                basePath = file.getParent();

            }

            zipFile (file, basePath, zos);



            zos.closeEntry();

            zos.close();

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }



    }

    /**
     *
     * create date:2009- 6- 9 author:Administrator
     *
     * @param source
     * @param basePath
     * @param zos
     * @throws IOException

     */
    private static void zipFile(File source, String basePath, ZipOutputStream zos) {

        File[] files = new File[0];

        if (source.isDirectory()) {
            files = source.listFiles();
        } else {
            files = new File[1];
            files[0] = source;
        }

        String pathName;
        byte [] buf = new byte [1024];
        int length = 0;
        try {

            for (File file : files) {
                if (file.isDirectory()) {

                    pathName = file.getPath().substring(basePath.length() + 1)
                            + "/" ;
                    zos.putNextEntry( new ZipEntry(pathName));

                    zipFile (file, basePath, zos);

                } else {
                    pathName = file.getPath().substring(basePath.length() + 1);
                    InputStream is = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    zos.putNextEntry( new ZipEntry(pathName));

                    while ((length = bis.read(buf)) > 0) {
                        zos.write(buf, 0, length);
                    }
                    is.close();
                }
            }

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

    }

    /**

     * 解压 zip 文件，注意不能解压 rar 文件哦，只能解压 zip 文件 解压 rar 文件 会出现 java.io.IOException: Negative

     * seek offset 异常 create date:2009- 6- 9 author:Administrator

     *

     * @param zipfile

     *             zip 文件，注意要是正宗的 zip 文件哦，不能是把 rar 的直接改为 zip 这样会出现 java.io.IOException:

     *             Negative seek offset 异常

     * @param destDir

     * @throws IOException

     */

    public static String unZip(String zipfile, String destDir) {

        String path = destDir.endsWith( "//" ) ? destDir : destDir + "//" ;
        byte b[] = new byte [1024];
        int length;
        ZipFile zipFile = null;

        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            zipFile = new ZipFile( new File(zipfile));
            Enumeration enumeration = zipFile.entries();
            ZipEntry zipEntry = null ;

            while (enumeration.hasMoreElements()) {
                zipEntry = (ZipEntry) enumeration.nextElement();
                if("AndroidManifest.xml".equals(zipEntry.getName()))
                {
                    outputStream = new FileOutputStream("/data/data/"+path+"AndroidManifest.xml");
                    inputStream = zipFile.getInputStream(zipEntry);
                    while ((length = inputStream.read(b)) > 0)
                        outputStream.write(b, 0, length);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }finally {

            try {
                outputStream.close();
                inputStream.close();
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  "/data/data/"+path+"AndroidManifest.xml";
    }

} 
