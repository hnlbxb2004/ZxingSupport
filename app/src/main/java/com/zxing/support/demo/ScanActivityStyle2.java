package com.zxing.support.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.zxing.support.library.QRCodeSupport;
import com.zxing.support.library.view.FinderViewStyle2;

/**
 * @author bingbing
 * @date 15/9/22
 */
public class ScanActivityStyle2 extends AppCompatActivity implements QRCodeSupport.OnScanResultListener, QRCodeSupport.OnTestScanRectListener {

    private static final int scan_w = 400;
    private static final int scan_h = 400;

    private SurfaceView mSurfaceView;
    private FinderViewStyle2 mFinderView;
    private QRCodeSupport mQRCodeSupport;
    private ImageView mTestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_style2);

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int w = defaultDisplay.getWidth();
        int h = defaultDisplay.getHeight();

        QRCodeSupport.Builder builder = new QRCodeSupport.Builder();
        builder.setScanRect((w-scan_w)/2 ,(h- scan_h)/2,scan_w,scan_h);
        mFinderView = (FinderViewStyle2) findViewById(R.id.capture_viewfinder_view);
        mSurfaceView = (SurfaceView) findViewById(R.id.sufaceview);
        mTestView = (ImageView) findViewById(R.id.test);
        mQRCodeSupport = new QRCodeSupport(builder,mSurfaceView, mFinderView);
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
    public void onScanResult(String notNullResult,byte[] resultBytes) {
        Intent intent = new Intent();
        intent.putExtra("result", notNullResult);
        intent.putExtra("resultByte",resultBytes);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScanRect(byte[] rectByte) {
        Bitmap barcode = BitmapFactory.decodeByteArray(rectByte, 0, rectByte.length, null);
        barcode = barcode.copy(Bitmap.Config.RGB_565, true);
        mTestView.setImageBitmap(barcode);
    }
}
