package com.inklin.qrcodescanner.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.nio.ByteBuffer;

/**
 * Created by acaoa on 2017/8/18.
 */


public class ScreenCapture {
    private int mResultCode;
    private Intent mResultData;

    private WindowManager wm;
    public int screenDensity;
    public int windowWidth;
    public int windowHeight;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;

    public ScreenCapture(Context context, Intent intent){
        mResultCode = intent.getIntExtra("code", -1);
        mResultData = intent.getParcelableExtra("data");

        wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        checkOrientation();
        int maxDist = Math.max(windowWidth,windowHeight);
        mImageReader = ImageReader.newInstance(maxDist, maxDist, 0x1, 2);

        mMediaProjection = ((MediaProjectionManager)context.getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(mResultCode, mResultData);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("capture_screen", windowWidth, windowHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
    }

    public void checkOrientation(){
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metric);

        windowWidth = metric.widthPixels;
        windowHeight = metric.heightPixels;
        screenDensity = metric.densityDpi;

        if(mVirtualDisplay!=null)
            mVirtualDisplay.resize(windowWidth,windowHeight,screenDensity);
    }

    public void destroy(){
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Nullable
    public Bitmap capture(){
        if(mImageReader==null)
            return null;
        Image image = mImageReader.acquireLatestImage();
        if(image==null)
            return null;

        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, windowWidth, windowHeight).copy(Bitmap.Config.RGB_565,true);
        image.close();
        return bitmap;
    }
}
