#ZxingSupport

    简介：这个是对Zxing 库的二次封装，使用起来相当简单,支持横竖屏。

![图片描述](./tools/device-2015-09-23-142035.png)

##当前最新版本
[ ![Download](https://api.bintray.com/packages/hnlbxb2004/maven/zxing-support/images/download.svg) ](https://bintray.com/hnlbxb2004/maven/zxing-support/_latestVersion)

##Gradle 依赖配置

```gradle
      compile 'com.github.hnlbxb2004:zxingsupport:1.1.0'
```

##扫码

```java
    //初始化
      QRCodeSupport.Builder builder = new QRCodeSupport.Builder();
       //四个参数分别为：
       1、扫描区域距离屏幕左边的距离，
       2、扫描区域距离屏幕顶部的距离
       3、扫描区域的宽度
       4、扫描区域的长度
      builder.setScanRect(140,500,300,300);
    1.QRCodeSupport mQRCodeSupport = new QRCodeSupport(builder，mSurfaceView,mFinderView);


    //设计监听
    2.mQRCodeSupport.setScanResultListener(new QRCodeSupport.OnScanResultListener(){
                                                @Override
                                                public void onScanResult(String notNullResult,byte[] resultBytes) {
                                                    //此处监听只要扫到就会一直回掉，比如1秒扫了2次，那么回掉会执行2次，需要注意。
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

          @Override
          protected void onDestroy() {
              super.onDestroy();
              mQRCodeSupport.onDestory();
           }
```

##扫码闪光灯打开关闭

```java

    mQrcodeSupport.toggleFlashLight();
```


##二维码生成

```java
    QRCodeEncode.Builder builder = new QRCodeEncode.Builder();
    builder.setBackgroundColor(0xffffff)
           .setOutputBitmapHeight(800)
           .setOutputBitmapWidth(800)
           .setOutputBitmapPadding(10);
    Bitmap qrCodeBitmap = builder.build().encode("www.baidu.com");
```

##Demo 下载地址


[点击下载](https://raw.githubusercontent.com/hnlbxb2004/ZxingSupport/master/tools/scan_demo.apk)



## 更新:
    2017.04.24
       增加闪光灯切换
    2016.12.30
       将FindView 从sdk 中删除，放到Demo 工程中，方便自定义。
    2016.11.05
       之前因为我的项目用的竖屏，一直没用横屏，今天早上再使用横屏的时候，发现横屏出现异常。因此修复以下。
       1、修复横屏扫码异常的问题。
       2、增加设置扫码区域，可以自定义调节。

    2015.12.12
       1.修复手机适配问题
       2.扫描回调中,返回扫描识别到的图片的byte 数组,可以将byte 自行转成bitmap

