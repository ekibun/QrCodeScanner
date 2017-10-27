package com.inklin.qrcodescanner;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.quicksettings.TileService;

import com.inklin.qrcodescanner.utils.ApplicationUtils;

import java.lang.reflect.Method;

@TargetApi(Build.VERSION_CODES.N)
public class QuickTileService extends TileService {
    @Override     // 点击
    public void onClick() {
        ApplicationUtils.collapseStatusBar(getBaseContext());

        Intent intent = new Intent(this, NavigateActivity.class);
        intent.putExtra("click", true);
        startActivity(intent);
    }
}

