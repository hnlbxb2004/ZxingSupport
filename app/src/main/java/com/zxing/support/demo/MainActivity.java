package com.zxing.support.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxing.support.library.qrcode.QRCodeEncode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int  ACTIVITY_RESULT_SCAN = 1;

    private Button mEnCodeButton;
    private EditText mInputText;
    private TextView mResultTextView;
    private ImageView mQRCodeImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mEnCodeButton = (Button) findViewById(R.id.encode);
        mInputText = (EditText) findViewById(R.id.input);
        mResultTextView = (TextView) findViewById(R.id.result);
        mQRCodeImage = (ImageView) findViewById(R.id.qrcode);

        mEnCodeButton.setOnClickListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "点击右上角的菜单进入扫码", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_scan:

                Intent intent = new Intent(this,ScanActivityStyle1.class);
                startActivityForResult(intent, ACTIVITY_RESULT_SCAN,null);

                return true;
            case R.id.action_scan2:
                Intent intent2 = new Intent(this,ScanActivityStyle2.class);
                startActivityForResult(intent2, ACTIVITY_RESULT_SCAN,null);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String input = mInputText.getText().toString();
        if (TextUtils.isEmpty(input)){
            Snackbar.make(v, "请输入要生成二维码的字符串", Snackbar.LENGTH_SHORT).show();
        }else {
            QRCodeEncode.Builder builder = new QRCodeEncode.Builder();
            builder.setBackgroundColor(0xffffff)
                    .setOutputBitmapHeight(800)
                    .setOutputBitmapWidth(800)
                    .setOutputBitmapPadding(10);
            mQRCodeImage.setImageBitmap(builder.build().encode(input));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_SCAN){
            if (data != null){
                String result = data.getStringExtra("result");
                mResultTextView.setText(result);
                Bitmap barcode = null;
                byte[] compressedBitmap = data.getByteArrayExtra("resultByte");
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                    barcode = barcode.copy(Bitmap.Config.RGB_565, true);
                    mQRCodeImage.setImageBitmap(barcode);
                }

            }else {
                mResultTextView.setText("没有结果！！！");
            }
        }
    }
}
