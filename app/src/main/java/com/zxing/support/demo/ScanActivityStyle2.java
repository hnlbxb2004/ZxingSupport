package com.zxing.support.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import com.zxing.support.library.QRCodeSupport;
import com.zxing.support.library.view.FinderViewStyle2;

/**
 * @author bingbing
 * @date 15/9/22
 */
public class ScanActivityStyle2 extends AppCompatActivity implements QRCodeSupport.OnScanResultListener {

    private SurfaceView mSurfaceView;
    private FinderViewStyle2 mFinderView;
    private QRCodeSupport mQRCodeSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_style2);

        mFinderView = (FinderViewStyle2) findViewById(R.id.capture_viewfinder_view);
        mSurfaceView = (SurfaceView) findViewById(R.id.sufaceview);
        mQRCodeSupport = new QRCodeSupport(mSurfaceView, mFinderView);
        mQRCodeSupport.setScanResultListener(this);
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
    public void onScanResult(String notNullResult) {
        Intent intent = new Intent();
        intent.putExtra("result", notNullResult);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
