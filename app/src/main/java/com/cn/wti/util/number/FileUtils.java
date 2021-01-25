package com.cn.wti.util.number;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by wyb on 2017/5/25.
 */

public class FileUtils {

    private static char szstr2bin[] = {
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,            0,1,2,3,4,5,6,7,8,9,10,0,0,0,0,0,
         0,10,11,12,13,14,15,0,0,0,0,0,0,0,0,0,        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
         0,10,11,12,13,14,15,0,0,0,0,0,0,0,0,0
     };

    public static byte[] str2byte(String str) {
         int length = str.length();
         if(length < 1)  return null;
         if(length%2 != 0)return null;
         byte[] result = new byte[str.length()/2];
         for (int i = 0;i<length;) {
             char H = szstr2bin[str.charAt(i++)&0xFF];
             char L = szstr2bin[str.charAt(i++)&0xFF];
             result[(i/2)-1] = (byte)(H*16 + L);
         }
        return result;
    }

    public static Bitmap readBitmapFromByteArray(byte[] data, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcHeight > height || srcWidth > width) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / height);
            } else {
                inSampleSize = Math.round(srcWidth / width);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /*Bitmap转Drawable*/
    public static Drawable bitmapToDrawable(Resources resources, Bitmap bm) {
        Drawable drawable = new BitmapDrawable(resources, bm);
        return drawable;
    }
    /*图片转二进制*/
    public byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    /*图片旋转角度*/
    private static Bitmap rotateBitmap(Bitmap b, float rotateDegree) {
        if (b == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
        return rotaBitmap;
    }

    /**
     * 利用BitmapShader绘制底部圆角图片
     *
     * @param bitmap
     *              待处理图片
     * @param edgeWidth
     *              正方形控件大小
     * @param radius
     *              圆角半径大小
     * @return
     *              结果图片
     */
    public static Bitmap circleBitmapByShader(Bitmap bitmap, int edgeWidth, int radius) {
        if(bitmap == null) {
            return bitmap;
        }

        float btWidth = bitmap.getWidth();
        float btHeight = bitmap.getHeight();
        // 水平方向开始裁剪的位置
        float btWidthCutSite = 0;
        // 竖直方向开始裁剪的位置
        float btHeightCutSite = 0;
        // 裁剪成正方形图片的边长，未拉伸缩放
        float squareWidth = 0f;
        if(btWidth > btHeight) { // 如果矩形宽度大于高度
            btWidthCutSite = (btWidth - btHeight) / 2f;
            squareWidth = btHeight;
        } else { // 如果矩形宽度不大于高度
            btHeightCutSite = (btHeight - btWidth) / 2f;
            squareWidth = btWidth;
        }

        // 设置拉伸缩放比
        float scale = edgeWidth * 1.0f / squareWidth;
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);

        // 将矩形图片裁剪成正方形并拉伸缩放到控件大小
        Bitmap squareBt = Bitmap.createBitmap(bitmap, (int)btWidthCutSite, (int)btHeightCutSite, (int)squareWidth, (int)squareWidth, matrix, true);

        // 初始化绘制纹理图
        BitmapShader bitmapShader = new BitmapShader(squareBt, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        // 初始化目标bitmap
        Bitmap targetBitmap = Bitmap.createBitmap(edgeWidth, edgeWidth, Bitmap.Config.ARGB_8888);

        // 初始化目标画布
        Canvas targetCanvas = new Canvas(targetBitmap);

        // 初始化画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);

        // 利用画笔绘制圆形图
        targetCanvas.drawRoundRect(new RectF(0, 0, edgeWidth, edgeWidth), radius, radius, paint);

        return targetBitmap;
    }

    public static void copyInputStreamToFile(InputStream fromFile, File toFile, Boolean rewrite )
    {

        try {

            FileInputStream fosfrom = (FileInputStream) fromFile;

            FileOutputStream fosto = new FileOutputStream(toFile);

            byte bt[] = new byte[1024];

            int c;

            while ((c = fosfrom.read(bt)) > 0) {

                fosto.write(bt, 0, c); //将内容写到新文件当中

            }

            fosfrom.close();

            fosto.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
