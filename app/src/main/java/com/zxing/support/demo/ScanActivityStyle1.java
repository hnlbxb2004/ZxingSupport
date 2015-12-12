package com.zxing.support.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import com.zxing.support.library.QRCodeSupport;
import com.zxing.support.library.view.FinderViewStyle1;


/**
 * @author bingbing
 * @date 15/9/22
 */
public class ScanActivityStyle1 extends AppCompatActivity implements QRCodeSupport.OnScanResultListener {

    private SurfaceView mSurfaceView;
    private FinderViewStyle1 mFinderView;
    private QRCodeSupport mQRCodeSupport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mFinderView = (FinderViewStyle1) findViewById(R.id.viewfinder_view);
        mSurfaceView = (SurfaceView) findViewById(R.id.sufaceview);
        mQRCodeSupport = new QRCodeSupport(mSurfaceView,mFinderView);
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
    public void onScanResult(String notNullResult,byte[] comp) {
        Intent intent = new Intent();
        intent.putExtra("result",notNullResult);
        intent.putExtra("resultByte",comp);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
