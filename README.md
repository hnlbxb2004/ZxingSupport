#ZxingSupport

    简介：这个是对Zxing 库的二次封装，使用起来相当简单。

![图片描述](./tools/device-2015-09-23-142035.png)


[ ![Download](https://api.bintray.com/packages/hnlbxb2004/maven/zxing-support/images/download.svg) ](https://bintray.com/hnlbxb2004/maven/zxing-support/_latestVersion)

##Gradle 依赖配置

      compile 'com.github.hnlbxb2004:zxingsupport:0.6'

##扫码
    //初始化
    1.QRCodeSupport mQRCodeSupport = new QRCodeSupport(mSurfaceView,mFinderView);


    //设计监听
    2.mQRCodeSupport.setScanResultListener(new QRCodeSupport.OnScanResultListener(){
                                                @Override
                                                public void onScanResult(String notNullResult,byte[] resultBytes) {

                                                }
                                           };);

    3.在activity 或者 fragment  onResume 和  onPause   调用次QRCodeSupport 的方法。
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


##二维码生成

    QRCodeEncode.Builder builder = new QRCodeEncode.Builder();
    builder.setBackgroundColor(0xffffff)
           .setOutputBitmapHeight(800)
           .setOutputBitmapWidth(800)
           .setOutputBitmapPadding(10);
    Bitmap qrCodeBitmap = builder.build().encode("www.baidu.com");



##Demo 下载地址


[点击下载](https://raw.githubusercontent.com/hnlbxb2004/ZxingSupport/master/tools/scan_demo.apk)



## 更新:
    2015.12.12
       1.修复手机适配问题
       2.扫描回调中,返回扫描识别到的图片的byte 数组,可以将byte 自行转成bitmap


##说明:
    本库借鉴了很多前辈的代码,但是由于历史原因,之前未加借鉴出处,现在也找到不了,如果你认为我是借鉴你代码,请联系我.
    感谢:
    1.适配屏幕的代码,借鉴了此库,在此感谢.
    https://github.com/SkillCollege/ZXingProject/tree/master/ZXingProj/src/com/dtr/zxing