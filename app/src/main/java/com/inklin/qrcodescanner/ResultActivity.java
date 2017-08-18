package com.inklin.qrcodescanner;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inklin.qrcodescanner.utils.ImageUtils;
import com.inklin.qrcodescanner.zxing.DecodeThread;
import com.inklin.qrcodescanner.zxing.Decoder;

public class ResultActivity extends Activity {
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();

        ImageView mResultImage = (ImageView) findViewById(R.id.result_image);
        TextView mResultText = (TextView) findViewById(R.id.result_text);

        if (null != extras) {
            result = extras.getString(Decoder.BARCODE_RESULT);
            mResultText.setText(result);

            Bitmap barcode = null;
            byte[] compressedBitmap = extras.getByteArray(Decoder.BARCODE_BITMAP);
            if (compressedBitmap != null) {
                barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                // Mutable copy:
                int degree = extras.getInt(Decoder.BARCODE_ROTATE, 0);
                barcode = ImageUtils.rotate(barcode, degree);
            }
            if(barcode != null){
                mResultImage.setImageBitmap(barcode);
            }else
                mResultImage.setVisibility(View.GONE);
        }

        TextView tv = (TextView)findViewById(R.id.copy);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy();
            }
        });
        tv = (TextView)findViewById(R.id.share);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        tv = (TextView)findViewById(R.id.open);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });
    }

    private void copy(){
        ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("QrCode", result);
        cmb.setPrimaryClip(clip);
        Toast.makeText(this, String.format(getString(R.string.toast_copied), result), Toast.LENGTH_SHORT).show();
    }

    private void share(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, result);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, ""));
    }

    private void open(){
        try{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(result));
            startActivity(Intent.createChooser(intent, ""));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
