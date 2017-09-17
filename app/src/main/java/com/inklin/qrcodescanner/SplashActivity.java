package com.inklin.qrcodescanner;

import android.app.Activity;
import android.os.Bundle;

import com.inklin.qrcodescanner.utils.ApplicationUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationUtils.openSettings(this);
        this.finish();
    }

    @Override
    public void onStop(){
        super.onStop();
        this.finish();
    }
}
