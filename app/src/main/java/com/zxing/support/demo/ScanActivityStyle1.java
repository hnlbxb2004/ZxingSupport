package com.zxing.support.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zxing.support.demo.view.FinderViewStyle1;
import com.zxing.support.library.QRCodeSupport;


/**
 * @author bingbing
 * @date 15/9/22
 */
public class ScanActivityStyle1 extends AppCompatActivity implements QRCodeSupport.OnScanResultListener, QRCodeSupport.OnTestScanRectListener {

    private SurfaceView mSurfaceView;
    private FinderViewStyle1 mFinderView;
    private QRCodeSupport mQRCodeSupport;
    private ImageView mTestView;

    private static final int SCAN_W = 800;
    private static final int SCAN_H = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();


        QRCodeSupport.Builder builder = new QRCodeSupport.Builder();

        //demo 设置居中显示,宽500,高500
        builder.setScanRect((width - SCAN_W)/2, (height - SCAN_H)/2, SCAN_W, SCAN_H);

        mFinderView = (FinderViewStyle1) findViewById(R.id.viewfinder_view);
        mTestView = (ImageView) findViewById(R.id.test);
        mSurfaceView = (SurfaceView) findViewById(R.id.sufaceview);
        mQRCodeSupport = new QRCodeSupport(builder, mSurfaceView, mFinderView);
        mQRCodeSupport.setScanResultListener(this);
        mQRCodeSupport.setOnTestScanRectListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mQRCodeSupport.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQRCodeSupport.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQRCodeSupport.onDestory();
    }


    @Override
    public void onScanResult(String notNullResult, byte[] resultBytes) {
        Intent intent = new Intent();
        intent.putExtra("result", notNullResult);
        intent.putExtra("resultByte", resultBytes);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScanRect(byte[] rectByte) {
        Bitmap barcode = BitmapFactory.decodeByteArray(rectByte, 0, rectByte.length, null);
        barcode = barcode.copy(Bitmap.Config.RGB_565, true);
        mTestView.setImageBitmap(barcode);
    }

    public void openFlashLight(View view) {
        mQRCodeSupport.toggleFlashLight();
    }
}
