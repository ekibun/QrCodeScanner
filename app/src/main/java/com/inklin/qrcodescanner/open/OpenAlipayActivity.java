package com.inklin.qrcodescanner.open;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.inklin.qrcodescanner.NavigateActivity;

public class OpenAlipayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.finish();
        startActivity(new Intent(this, NavigateActivity.class).putExtra("action", 3));
    }
}
