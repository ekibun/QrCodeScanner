package com.inklin.qrcodescanner;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.google.zxing.Result;
import com.inklin.qrcodescanner.utils.ApplicationUtils;
import com.inklin.qrcodescanner.utils.ImageUtils;
import com.inklin.qrcodescanner.utils.ScreenCapture;
import com.inklin.qrcodescanner.zxing.Decoder;

import java.io.ByteArrayOutputStream;

import static java.lang.Thread.sleep;


public class CaptureService extends Service {
    ScreenCapture screenCapture;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.hasExtra("code") && intent.hasExtra("data")){
            screenCapture = new ScreenCapture(this, intent);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = null;
                    while(bmp == null){
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bmp = screenCapture.capture();
                    }
                    Looper.prepare();
                    scanBitmap(bmp);
                    finish();
                    Looper.loop();
                }
            }).start();
        }
        return Service.START_NOT_STICKY;
    }

    public void scanBitmap(Bitmap bitmap){
        Decoder.scanBitmap(bitmap, this);
    }

    public void finish(){
        screenCapture.destroy();
        stopSelf();
    }

}
