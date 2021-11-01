package com.cn.wti.util.number;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    String aPath =  cursor.getString(column_index);
                    if (aPath == null){
                        aPath = uri.getPath();
                    }
                    return aPath;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static File uri2File(Context context ,Uri uri) {
        String img_path;

        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor actualimagecursor = context.getContentResolver().query(uri, proj, null,

                null, null);

        if (actualimagecursor == null) {
            img_path = uri.getPath();

        } else {
            int actual_image_column_index = actualimagecursor

                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            actualimagecursor.moveToFirst();

            img_path = actualimagecursor

                    .getString(actual_image_column_index);

        }

        File file = new File(img_path);

        return file;

    }

    public static String uriToBase64(Context mContext,Uri uri){
        File file1 = new File(getPath(mContext,uri));
        return Base64.encodeToString(File2Bytes(file1),Base64.DEFAULT);
    }

    public static byte[] File2Bytes(File file) {
        int byte_size = 1024;
        byte[] b = new byte[byte_size];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                    byte_size);
            for (int length; (length = fileInputStream.read(b)) != -1;) {
                outputStream.write(b, 0, length);
            }
            fileInputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这里我们生成了一个Pic文件夹，在下面放了我们质量压缩后的图片，用于和原图对比
     * 压缩图片使用Bitmap.compress()，这里是质量压缩
     */
    public static Uri bitmapCompress2(ContentResolver resolver, Uri uriClipUri,String fileName) {
        Bitmap photoBitmap = null;
        try {
            //裁剪后的图像转成BitMap
            //photoBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriClipUri));
            photoBitmap = MediaStore.Images.Media.getBitmap(resolver, uriClipUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建路径
        String path = Environment.getExternalStorageDirectory()
                .getPath() + File.separator;
        //获取外部储存目录
        File file = new File(path);
        //创建新目录, 创建此抽象路径名指定的目录，包括创建必需但不存在的父目录。
        file.mkdirs();
        //以当前时间重新命名文件
        long i = System.currentTimeMillis();
        //生成新的文件
        file = new File(file.toString() +File.separator +fileName);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //压缩文件，返回结果，参数分别是压缩的格式，压缩质量的百分比，输出流
        boolean bCompress = photoBitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
        return Uri.fromFile(file);
    }

    public void deleteTempPhotoFile(String fileName) {
        File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }


    /**
     * 压缩图片使用Bitmap.compress()，这里是质量压缩
     * @param resolver
     * @param uriClipUri
     * @param fileName
     * @return
     */
    public static File bitmapCompress(ContentResolver resolver, Uri uriClipUri,String fileName) {
        File sdFile = Environment.getExternalStorageDirectory();
        Bitmap bitmap = null;
        try {
            //裁剪后的图像转成BitMap
            bitmap = MediaStore.Images.Media.getBitmap(resolver, uriClipUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //设置缩放比
        int radio = 8;
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth() / radio, bitmap.getHeight() / radio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        RectF rectF = new RectF(0, 0, bitmap.getWidth() / radio, bitmap.getHeight() / radio);
        //将原图画在缩放之后的矩形上
        canvas.drawBitmap(bitmap, null, rectF, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        File cropFile = new File(sdFile, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(cropFile);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cropFile;
    }

}
