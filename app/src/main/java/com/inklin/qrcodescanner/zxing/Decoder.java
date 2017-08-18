package com.inklin.qrcodescanner.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.inklin.qrcodescanner.R;
import com.inklin.qrcodescanner.utils.ApplicationUtils;
import com.inklin.qrcodescanner.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by acaoa on 2017/8/17.
 */

public class Decoder {
    public static final int DECODE_SUCCESS = 1;
    public static final int DECODE_FAILED = 0;
    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final String BARCODE_RESULT = "barcode_result";
    public static final String BARCODE_ROTATE = "barcode_rotate";

    private static byte[] mRotatedData;
    public static byte[] rotate(byte[]data, int width, int height){
        if (null == mRotatedData) {
            mRotatedData = new byte[width * height];
        } else {
            if (mRotatedData.length < width * height) {
                mRotatedData = new byte[width * height];
            }
        }
        Arrays.fill(mRotatedData, (byte) 0);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                mRotatedData[x * height + height - y - 1] = data[x + y * width];
        return mRotatedData;
    }

    public static Result decode(byte[] data, int width, int height, Rect crop){
        Result result = null;
        try {
            Hashtable<DecodeHintType, Object>  hints = new Hashtable<>();
            hints.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);

            /*
            Collection<BarcodeFormat> barcodeFormats = new ArrayList<>();
            barcodeFormats.add(BarcodeFormat.QR_CODE);
            barcodeFormats.add(BarcodeFormat.CODE_39);
            barcodeFormats.add(BarcodeFormat.CODE_93);
            barcodeFormats.add(BarcodeFormat.CODE_128);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, barcodeFormats);*/

            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, crop.left, crop.top, crop.width(), crop.height(), false);
            //BinaryBitmap bitmap1 = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            result = new QRCodeReader().decode(bitmap1, hints);
        } catch (NotFoundException e) {
            //e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static void scanBitmap(Bitmap bitmap, Context context){
        if(bitmap == null)
            return;
        try {
            bitmap = ImageUtils.converBitmap(bitmap);
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            byte[] data = ImageUtils.getYUV420sp(bitmap);
            Result result = decode(data, w, h, new Rect(0, 0, w, h));
            if(result != null){
                Bundle bundle = new Bundle();
                //bundle.putInt(Decoder.BARCODE_ROTATE, 90);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                bundle.putByteArray(Decoder.BARCODE_BITMAP, stream.toByteArray());
                bundle.putString(Decoder.BARCODE_RESULT, Decoder.fixString(result.getText()));
                ApplicationUtils.handleResult(context, bundle);
            }else{
                Toast.makeText(context, context.getString(R.string.toast_no_result), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String fixString(String resultStr) {
        String UTF_Str = "";
        String GB_Str = "";
        boolean is_cN = false;
        try {
            UTF_Str = new String(resultStr.getBytes("ISO-8859-1"), "UTF-8");
            is_cN = isChineseCharacter(UTF_Str);
            //防止有人特意使用乱码来生成二维码来判断的情况
            boolean b = isSpecialCharacter(resultStr);
            if (b) is_cN = true;
            if (!is_cN)
                GB_Str = new String(resultStr.getBytes("ISO-8859-1"), "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (is_cN)
            return UTF_Str;
        else
            return GB_Str;
    }

    public static final boolean isChineseCharacter(String chineseStr) {
        char[] charArray = chineseStr.toCharArray();
        for (int i = 0; i < charArray.length; i++)
            //是否是Unicode编码,除了"�"这个字符.这个字符要另外处理
            if ((charArray[i] >= '\u0000' && charArray[i] < '\uFFFD') || ((charArray[i] > '\uFFFD' && charArray[i] < '\uFFFF')))
                continue;
            else
                return false;
        return true;
    }

    public static final boolean isSpecialCharacter(String str) {
        //是"�"这个特殊字符的乱码情况
        if (str.contains("ï¿½"))
            return true;
        return false;
    }
}
