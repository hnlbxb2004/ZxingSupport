package com.zxing.support.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xubingbing on 2017/9/19.
 */

public class ResultActivity extends AppCompatActivity {

    private TextView mResultText;
    private ImageView mResultImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mResultImage = (ImageView) findViewById(R.id.result_image);
        mResultText = (TextView) findViewById(R.id.result);

        Intent data = getIntent();

        String result = data.getStringExtra("result");
        mResultText.setText(result);
        Bitmap barcode = null;
        byte[] compressedBitmap = data.getByteArrayExtra("resultByte");
        if (compressedBitmap != null) {
            barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
            barcode = barcode.copy(Bitmap.Config.RGB_565, true);
            mResultImage.setImageBitmap(barcode);
        }
    }
}
