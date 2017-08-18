package com.inklin.qrcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.zxing.Result;
import com.inklin.qrcodescanner.utils.ApplicationUtils;
import com.inklin.qrcodescanner.zxing.DecodeThread;
import com.inklin.qrcodescanner.zxing.Decoder;

public class CameraActivity extends Activity implements Camera.PreviewCallback {

    private CameraView cameraView;
    public CameraActivity context;
    Rect crop = new Rect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_camera);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        cameraView = (CameraView)findViewById(R.id.camera);
        initView();
    }

    boolean flashlight = false;
    private FloatingActionButton initFab(){
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        fab.setBackgroundTintList(ColorStateList.valueOf(getColor(
                flashlight? R.color.colorPrimary : android.R.color.black)));
        return fab;
    }
    private void initView(){
        initFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraView != null){
                    flashlight = !flashlight;
                    cameraView.setFlashLight(flashlight);;
                    v.setBackgroundTintList(ColorStateList.valueOf(getColor(
                            flashlight? R.color.colorPrimary : android.R.color.black)));
                }
            }
        });

        int statusHeight = ApplicationUtils.getStatusBarHeight(this);

        View view = findViewById(R.id.run_wechat);
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams)view.getLayoutParams();
        llp.topMargin = -statusHeight;
        view.setLayoutParams(llp);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ApplicationUtils.openWechat(context))
                    context.finish();
            }
        });
        view = findViewById(R.id.run_alipay);
        llp = (LinearLayout.LayoutParams)view.getLayoutParams();
        llp.topMargin = -statusHeight;
        view.setLayoutParams(llp);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ApplicationUtils.openAlipay(context))
                    context.finish();
            }
        });
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        view = findViewById(R.id.scan_frame);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = rect.width() * 4 / 7;
        lp.height = lp.width;
        view.setLayoutParams(lp);
        view = findViewById(R.id.mask_status);
        lp = view.getLayoutParams();
        lp.height = statusHeight;
        view.setLayoutParams(lp);
        view = findViewById(R.id.gallery);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationUtils.openGallery(context);
                context.finish();
            }
        });
        view = findViewById(R.id.settings);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationUtils.openSettings(context);
            }
        });
    }

    private void initCrop(Camera.Size size) {
        int cameraWidth = size.height;
        int cameraHeight = size.width;

        int[] location = new int[2];
        Rect loc = new Rect();
        View view  = findViewById(R.id.scan_frame);
        view.getGlobalVisibleRect(loc);
        view.getLocationInWindow(location);
        int containerWidth = cameraView.getWidth();
        int containerHeight = cameraView.getHeight();

        int x = loc.left * cameraWidth / containerWidth;
        int y = loc.top * cameraHeight / containerHeight;
        int width = loc.width() * cameraWidth / containerWidth;
        int height = loc.height() * cameraHeight / containerHeight;

        crop = new Rect(y, x, height + y, width + x);
    }

    public void requestFrame(){
        if(cameraView != null && cameraView.camera != null)
            cameraView.camera.setOneShotPreviewCallback(this);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Size cameraResolution = camera.getParameters().getPreviewSize();
        if (crop.width() * crop.height() == 0)
            initCrop(cameraResolution);
        if (cameraView.camera != null && crop.width() * crop.height() != 0) {
            new DecodeThread(handler, data, cameraResolution.width, cameraResolution.height, crop).start();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case Decoder.DECODE_SUCCESS:
                    ApplicationUtils.handleResult(context, (Bundle) message.obj);
                    break;
                case Decoder.DECODE_FAILED:
                    requestFrame();
                    break;
            }
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        cameraView.loadCamera();
        flashlight = false;
        initFab();
        requestFrame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.releaseCamera();              // release the camera immediately on pause event
    }
}
