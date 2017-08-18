package com.inklin.qrcodescanner.zxing;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.Result;
import com.inklin.qrcodescanner.R;
import com.inklin.qrcodescanner.utils.CameraUtils;

import java.util.concurrent.CountDownLatch;

/**
 * Created by acaoa on 2017/8/18.
 */

public class DecodeThread extends Thread {

    private final Handler handler;
    private byte[] data;
    private int width, height;
    private Rect crop;

    public DecodeThread(Handler handler, byte[] data, int width, int height, Rect crop) {
        this.handler = handler;
        this.data = data;
        this.width = width;
        this.height = height;
        this.crop = crop;
    }

    @Override
    public void run() {
        Result result = Decoder.decode(data, width, height, crop);
        if(result != null){
            Bundle bundle = new Bundle();
            bundle.putInt(Decoder.BARCODE_ROTATE, 90);
            bundle.putByteArray(Decoder.BARCODE_BITMAP, CameraUtils.getBitmap(data, width, height, crop));
            bundle.putString(Decoder.BARCODE_RESULT, Decoder.fixString(result.getText()));
            Message.obtain(handler, Decoder.DECODE_SUCCESS, bundle).sendToTarget();
        }else{
            Message.obtain(handler, Decoder.DECODE_FAILED).sendToTarget();
        }
    }
}
