package com.inklin.qrcodescanner.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.inklin.qrcodescanner.NavigateActivity;
import com.inklin.qrcodescanner.PreferencesActivity;
import com.inklin.qrcodescanner.R;
import com.inklin.qrcodescanner.ResultActivity;
import com.inklin.qrcodescanner.zxing.Decoder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by acaoa on 2017/8/18.
 */

public class ApplicationUtils {

    public static void collapseStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;
            collapse = statusBarManager.getClass().getMethod("collapsePanels");
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public static void handleResult(Context context, Bundle bundle){
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("open_direct", false)){
            try{
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(bundle.getString(Decoder.BARCODE_RESULT)));
                context.startActivity(Intent.createChooser(intent, ""));
            }catch (Exception e){
                e.printStackTrace();
            }
        }else
            context.startActivity(new Intent(context, ResultActivity.class).putExtras(bundle));
    }

    public static void openSettings(Context context){
        Intent intent = new Intent(context, PreferencesActivity.class);
        context.startActivity(intent);
    }

    public static void openGallery(Context context){
        Intent intent = new Intent(context, NavigateActivity.class);
        intent.putExtra("action", 5);
        context.startActivity(intent);
    }

    public static boolean openWechat(Context context){
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.setFlags(335544320);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.toast_no_wechat), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    public static boolean openAlipay(Context context){
        try {
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.toast_no_alipay), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    public static boolean openAliPayment(Context context){
        try {
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.toast_no_alipay), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }
}
