package com.inklin.qrcodescanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.widget.Toast;

import com.inklin.qrcodescanner.utils.ApplicationUtils;
import com.inklin.qrcodescanner.utils.ImageUtils;
import com.inklin.qrcodescanner.zxing.Decoder;

import java.util.ArrayList;
import java.util.Arrays;


public class NavigateActivity extends Activity {
    //public static final int REQUEST_CAMERA_PERMISSIONS = 1;
    public static final int REQUEST_CAPTURE_CODE = 2;
    public static final int REQUEST_PHOTO_GALLERY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent.hasExtra("action")){
            runAction(intent.getIntExtra("action", 0));
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //单击
        if(intent.hasExtra("click")){
            runAction(Integer.parseInt(sp.getString("action_click", "0")));
        }
        //长按
        if("android.service.quicksettings.action.QS_TILE_PREFERENCES".equals(intent.getAction())){
            runAction(Integer.parseInt(sp.getString("action_longclick", "1")));
        }
    }

    boolean flag_result = false;
    public void runAction(int action){
        switch (action){
            case 0:
                if(openCamera())
                    this.finish();
                break;
            case 1:
                flag_result = true;
                openScreenShot();
                break;
            case 2:
                ApplicationUtils.openWechat(this);
                this.finish();
                break;
            case 3:
                ApplicationUtils.openAlipay(this);
                this.finish();
                break;
            case 6:
                ApplicationUtils.openSettings(this);
                this.finish();
                break;
            case 5:
                flag_result = true;
                openGallery();
                break;
            case 4:
                ApplicationUtils.openAliPayment(this);
                this.finish();
                break;
            case 7:
                openMenu();
                break;
        }
    }

    private void openMenu(){
        ArrayList<String> al = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.pref_action)));
        al.remove(al.size() - 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(al.toArray(new String[1]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                runAction(which);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finishMe();
            }
        });
        builder.show();
    }

    private void finishMe(){
        if(!flag_result)
            this.finish();
    }

    private void openGallery(){
        Intent gallery=new Intent(Intent.ACTION_PICK);//ACTION_OPEN_DOCUMENT
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUEST_PHOTO_GALLERY);
    }

    private void openScreenShot() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent, REQUEST_CAPTURE_CODE);
    }

    private boolean openCamera(){
        //if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, CameraActivity.class);
            this.startActivity(intent);
            return true;
       //}else
        //    ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, REQUEST_CAMERA_PERMISSIONS);
        //return false;
    }

/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //重新运行
                openCamera();
            }else{
                Toast.makeText(this, R.string.toast_no_permit, Toast.LENGTH_SHORT).show();
            }
        }
        this.finish();
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {
                // 获得权限，启动Service开始录制
                Intent service = new Intent(this, CaptureService.class);
                service.putExtra("code", resultCode);
                service.putExtra("data", data);
                startService(service);
            } else {
                Toast.makeText(this, R.string.toast_no_permit, Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == REQUEST_PHOTO_GALLERY && data != null) {
            Uri uri = data.getData();
            Decoder.scanBitmap(ImageUtils.getBitmapFromUri(uri, this), this);
        }
        this.finish();
    }
}
