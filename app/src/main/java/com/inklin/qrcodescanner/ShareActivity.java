package com.inklin.qrcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.inklin.qrcodescanner.utils.ImageUtils;
import com.inklin.qrcodescanner.zxing.Decoder;

public class ShareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri uri = null;
        if(Intent.ACTION_SEND.equals(intent.getAction()))
            uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(Intent.ACTION_VIEW.equals(intent.getAction()))
            uri = intent.getData();
        if(uri != null)
            Decoder.scanBitmap(ImageUtils.getBitmapFromUri(uri, this), this);
        this.finish();
    }
}
