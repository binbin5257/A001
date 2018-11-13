package cn.lds.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.amap.api.navi.model.NaviLatLng;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.lds.BuildConfig;
import cn.lds.R;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.base.IPermission;
import cn.lds.common.constants.Constants;
import cn.lds.common.data.FilesModel;
import cn.lds.common.data.WxPayModel;
import cn.lds.common.file.FilesBean;
import cn.lds.common.file.ProgressFileUploadListener;
import cn.lds.common.manager.FilesManager;
import cn.lds.common.manager.RequestManager;
import cn.lds.common.manager.UMengManager;
import cn.lds.common.manager.WeChatShare;
import cn.lds.common.manager.WxShareTo;
import cn.lds.common.manager.WxShareType;
import cn.lds.common.utils.BitmapUtils;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.FileHelper;
import cn.lds.common.utils.LogHelper;
import cn.lds.common.utils.NetWorkHelper;
import cn.lds.common.utils.PictureHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.databinding.ActivityWebviewBinding;
import cn.lds.ui.select_image.PictureSelectorConfig;
import cn.lds.widget.ToastUtil;
import cn.lds.widget.dialog.CameraOrAlbumBottomDialog;
import cn.lds.widget.dialog.CenterListDialog;
import cn.lds.widget.dialog.ConfirmDialog;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.callback.OnDialogClickListener;
import cn.lds.widget.dialog.callback.OnDialogOnItemClickListener;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 彩虹H5页面
 * Created by sibinbin on 18-1-31.
 */

@SuppressLint("Registered")
public class WebviewActivity extends BaseActivity {

    private IWXAPI iwxapi;
    private ActivityWebviewBinding mWebviewBinding;
    /**
     * 获取到的分享图片地址
     */
    private String imgUrl;
    private List<String> shareTypeList;
    private CenterListDialog shareDialog;
    private CameraOrAlbumBottomDialog cameraOrAlbumBottomDialog;
    protected static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    protected static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private String h5PhotoPath;
    private String enCodePic;
    private String loadUrl;
    List<LocalMedia> localMediaList = new ArrayList<>();
    ArrayList<String> sdPath = new ArrayList<>();
    ArrayList<String> picId = new ArrayList<>();
    public ValueCallback<Uri[]> mUploadMsgForAndroid5;
    private boolean isOnShowFileChooser = false;



    public static void enterWebviewActivity( Context context, String url) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("URL", url);
        context.startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebviewBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        initShareDialog();
        initCameraDialog();
        initView();
        initListener();
    }

    /**
     * 初始化相机对话框
     */
    private void initCameraDialog() {
        cameraOrAlbumBottomDialog = getCameraOrAlbumBottomDialog();
        cameraOrAlbumBottomDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClick(Dialog dialog, String clickPosition) {
                dialog.dismiss();
                switch (clickPosition){
                    case ClickPosition.TAKE_PHOTO:
                        requesTakePhotoPermission();
                        break;
                    case ClickPosition.TAKE_ALBUM:
                        requestWriteStoragePermission();
                        break;
                    case ClickPosition.CANCEL:
                        if(isOnShowFileChooser){
                            restoreUploadMsg();
                        }
                        break;

                }
            }
        });
    }

    @NonNull
    private CameraOrAlbumBottomDialog getCameraOrAlbumBottomDialog() {
        return new CameraOrAlbumBottomDialog(this);
    }

    /**
     * 初始化分享对话框
     */
    private void initShareDialog() {
        shareTypeList = new ArrayList<>();
        shareTypeList.add("分享微信好友");
        shareTypeList.add("分享朋友圈");
        shareDialog = new CenterListDialog(this,this,shareTypeList).setOnDialogOnItemClickListener(new OnDialogOnItemClickListener() {
            @Override
            public void onDialogItemClick(Dialog dialog, final int position) {
                dialog.dismiss();

                //JS调用APP提供的分享方法，唤起微信，将图片分享给微信好友或者朋友圈，呈现为图片缩略图形式。
//                分享方法名：wechatShare(imgUrl, type)
                 mWebviewBinding.webview.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebviewBinding.webview.loadUrl("javascript:wechatShare('" + imgUrl +',' + position + "')");
                    }
                });
            }
        });
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public void initView() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            loadUrl = bundle.getString("URL");
            if(!TextUtils.isEmpty(loadUrl) && loadUrl.contains("maintance")){
                UMengManager.getInstance().onClick("VehicleService");
                UMengManager.getInstance().onResumePage("VehicleService");
            }else if(!TextUtils.isEmpty(loadUrl) && loadUrl.contains("activity-leopaard/#/?token=")){
                UMengManager.getInstance().onResumePage("ubi");
                UMengManager.getInstance().onClick("ubi");
            }else if(!TextUtils.isEmpty(loadUrl) && loadUrl.contains("myOrder")){
                UMengManager.getInstance().onResumePage("myOrder");
                UMengManager.getInstance().onClick("myOrder");
            }else if(!TextUtils.isEmpty(loadUrl) && loadUrl.contains("rescue")){
                UMengManager.getInstance().onResumePage("rescue");
                UMengManager.getInstance().onClick("rescue");
            }else if(loadUrl.equals(CacheHelper.getVersionContent())){
                LinearLayout topBar = findViewById(R.id.top_bar);
                TextView title = findViewById(R.id.top_title_tv);
                ImageView backIv = findViewById(R.id.top_back_iv);
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        finish();
                    }
                });
                title.setText("帮助说明");
                topBar.setVisibility(View.VISIBLE);
            }
        }

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        //有测试发现，清空cookie的操作会造成，cookie偶然性失效的现象
        cookieManager.removeSessionCookie();// 移除
        cookieManager.removeAllCookie();
        String jessionid = CacheHelper.getCookie();
        if(!TextUtils.isEmpty(loadUrl)){
            cookieManager.setCookie(loadUrl, "SESSION="+jessionid);

        }
        CookieSyncManager.getInstance().sync();

        WebSettings settings = mWebviewBinding.webview.getSettings();
        // 缓存(cache)
        settings.setAppCacheEnabled(true);      // 默认值 false
        settings.setAppCachePath(getCacheDir().getAbsolutePath());
        // 存储(storage)
        settings.setDomStorageEnabled(true);    // 默认值 false
        settings.setDatabaseEnabled(true);      // 默认值 false
        // 是否支持viewport属性，默认值 false
        // 页面通过`<meta name="viewport" ... />`自适应手机屏幕
        settings.setUseWideViewPort(true);
        // 是否使用overview mode加载页面，默认值 false
        // 当页面宽度大于WebView宽度时，缩小使页面宽度等于WebView宽度
        settings.setLoadWithOverviewMode(true);
        // 是否支持Javascript，默认值false
        settings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (NetWorkHelper.isNetworkConnected()) {
            // 根据cache-control决定是否从网络上取数据
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            // 没网，离线加载，优先加载缓存(即使已经过期)
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
//        String loadUrl = getLoadUrl();
//        loadUrl = "file:///android_asset/testH5.html";
        if(!TextUtils.isEmpty(loadUrl)){
            mWebviewBinding.webview.loadUrl(loadUrl);
        }
        //参数2：Java对象名
        mWebviewBinding.webview.addJavascriptInterface(new AndroidtoJs(), "WebView");//AndroidtoJS类对象映射到js的app对象


    }

    @Override
    public void initListener() {
        mWebviewBinding.webview.setWebChromeClient(new MyWebChromeClient());
        mWebviewBinding.webview.setWebViewClient(new MyWebViewClient());
    }
    /**
     * 需求说明：
     *  APP通过URL（query参数）携带用户唯一标识（如userId）给H5页面，同时猎豹方面提供获取用户信息的外部接口，通过该接口以userId作为参数获取用户其他必需信息（附下表）。
     *  示例：
     *  H5页面URL: http://xxxxx/demo.html?userid=xxx
     */
    public String getLoadUrl() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            //url: http://xxxxx/demo.html?userid=xxx
            String url = bundle.getString("url");
            return url;
        }
        return "";
    }

    /**
     * WebViewClient 主要提供网页加载各个阶段的通知，比如网页开始加载onPageStarted，网页结束加载onPageFinished等
     */
    public class MyWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            BaseApplication.getInstance().runOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    mWebviewBinding.rlLoading.setVisibility(View.GONE);
                }
            },1000);
        }
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
           if(errorCode == 401 || errorCode == 403){
               RequestManager.isLogin = false;
               BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");

           }
        }
    }

    public class MyWebChromeClient extends WebChromeClient{
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            mUploadMsgForAndroid5 = filePathCallback;
//            isOnShowFileChooser = true;
//            cameraOrAlbumBottomDialog.show();
            return true;
        }
    }

    /**
     * 打开相册或者照相机选择凭证图片，最多5张
     *
     * @param maxTotal 最多选择的图片的数量
     */
    private void selectPic(int maxTotal) {
        PictureSelectorConfig.initMultiConfig(this, maxTotal,localMediaList);
    }

    // 继承自Object类
    public class AndroidtoJs extends Object {
        @JavascriptInterface
        public void startPayWx( final String orderInfo) {

            mWebviewBinding.webview.post(new Runnable() {
                @Override
                public void run() {
                    goPayWx(orderInfo);

                }
            });
        }
        @JavascriptInterface
        public void startPayAli( final String orderInfo) {

            mWebviewBinding.webview.post(new Runnable() {
                @Override
                public void run() {
                    goPayAli(orderInfo);

                }
            });
        }
        @JavascriptInterface
        public void relocate() {

            mWebviewBinding.webview.post(new Runnable() {
                @Override
                public void run() {
                    mWebviewBinding.webview.loadUrl("javascript:relocateResult('" +  CacheHelper.getCity() +',' + CacheHelper.getCityAdCode() + "')");

                }
            });
        }
        @JavascriptInterface
        public void navigate( final double fromLongitude, final double fromLatitude, final double toLongitude, final double toLatitude) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NaviLatLng s = new NaviLatLng(fromLatitude,fromLongitude);
                    NaviLatLng e = new NaviLatLng(toLatitude,toLongitude);
                    Intent intent = new Intent(WebviewActivity.this,NaviActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("FLAG","Webview");
                    bundle.putParcelable("startNavi",s);
                    bundle.putParcelable("endNavi",e);
                    bundle.putDouble("distance",3000);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        @JavascriptInterface
        public void makeCall( final String mobile) {
            //显示分享对话框
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConfirmDialog dialog = new ConfirmDialog(mContext).setOnDialogClickListener(new OnDialogClickListener() {
                        @Override
                        public void onDialogClick( Dialog dialog, String clickPosition ) {
                            dialog.dismiss();
                            if(clickPosition.equals(ClickPosition.SUBMIT)){
                                callService(mobile);
                            }else if(clickPosition.equals(ClickPosition.CANCEL)){
                            }
                        }
                    });
                    dialog.setRightButtonText("呼叫");
                    dialog.setContent(mobile);
                    dialog.setTitle("提示");
                    dialog.setTitleVisibilty(View.GONE);
                    dialog.show();
                }
            });
        }

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void getImageUrl(String url) {
            imgUrl = url;
            //显示分享对话框
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    shareDialog.show();
                }
            });
        }
        @JavascriptInterface
        public void takePhoto() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isOnShowFileChooser = false;
                    cameraOrAlbumBottomDialog.show();
                }
            });
        }
        @JavascriptInterface
        public void exitApp() {
            RequestManager.isLogin = false;
            BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
        }
        @JavascriptInterface
        public void closeAppWebview() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WebviewActivity.this.finish();
                }
            });
        }
        @JavascriptInterface
        public void addPicture( final int count) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    requestRunTimePermission(new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, new IPermission() {
                        @Override
                        public void onGranted() {
                            selectPic(count);                        }

                        @Override
                        public void onDenied(List<String> deniedPermissions) {
                            for (String deniedPermission : deniedPermissions) {
//                                ToastUtil.showToast(mContext, "被拒绝的权限是"+deniedPermission);
                            }
                        }
                    });

                }
            });
        }
        @JavascriptInterface
        public void wechatShare(String imageUrl,int type){
            Bitmap bitmap = BitmapUtils.getBitmap(imageUrl);
            if(type == 0){
                //分享朋友圈
                WeChatShare.regToWx(WebviewActivity.this)// 注册APP到微信
                        .setWhere(WxShareTo.share_timeline)// 设置分享朋友圈
                        .setType(WxShareType.type_image) // 设置分享的类型
                        .addImage(bitmap) // 文本分享添加要分享的文本
                        .share(); // 发起分享请求
            }else {

                //分享好友
                WeChatShare.regToWx(WebviewActivity.this)// 注册APP到微信
                        .setWhere(WxShareTo.share_session)// 设置分享到好友
                        .setType(WxShareType.type_image) // 设置分享的类型
                        .addImage(bitmap)// 文本分享添加要分享的文本
                        .share(); // 发起分享请求

            }
        }

    }

    /**
     * 请求读写内存卡权限
     */
    private void requestWriteStoragePermission() {
        requestRunTimePermission(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, new IPermission() {
            @Override
            public void onGranted() {
                PictureHelper.enterAlbum(WebviewActivity.this,PHOTO_REQUEST_GALLERY);
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
                for (String deniedPermission : deniedPermissions) {
//                    ToastUtil.showToast(mContext, "被拒绝的权限是"+deniedPermission);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!TextUtils.isEmpty(loadUrl) && loadUrl.contains("maintance")){
            UMengManager.getInstance().onPausePage("VehicleService");
        }else if(!TextUtils.isEmpty(loadUrl) && loadUrl.contains("activity-leopaard/#/?token=")){
            UMengManager.getInstance().onPausePage("UBI保险");
        }else if(!TextUtils.isEmpty(loadUrl) && loadUrl.contains("myOrder")){
            UMengManager.getInstance().onPausePage("我的订单");
        }
    }

    /**
     * 请求照相权限
     */
    private void requesTakePhotoPermission() {
        BaseActivity.requestRunTimePermission(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, new IPermission() {

            @Override
            public void onGranted() {
                //启动相机
                // 获取SD卡路径
                h5PhotoPath = FileHelper.getTakeCarPath();
                PictureHelper.takePhoto(WebviewActivity.this,PHOTO_REQUEST_CAMERA, h5PhotoPath);


            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
//                for (String deniedPermission : deniedPermissions) {
//                    restoreUploadMsg();
//                    ToastUtil.showToast(mContext, "被拒绝的权限是"+deniedPermission);
//                }
            }
        });
    }

    private void restoreUploadMsg() {
        if (mUploadMsgForAndroid5 != null) {
            mUploadMsgForAndroid5.onReceiveValue(null);
            mUploadMsgForAndroid5 = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK && isOnShowFileChooser) {
            if (mUploadMsgForAndroid5 != null) {         // for android 5.0+
                mUploadMsgForAndroid5.onReceiveValue(null);
            }
            return;
        }
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_CAMERA:
                    if(!isOnShowFileChooser){
                        enCodePic = PictureHelper.compressImageAndBase64EnCode(h5PhotoPath,60);
                        postPicEnCode();
                    }else{
                        String sourcePath = PictureHelper.compressPic(h5PhotoPath,60);
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUploadMsgForAndroid5.onReceiveValue(new Uri[]{uri});

                    }

                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (data != null) {
                        // 得到图片的全路径
                        String path;
                        Uri uri = data.getData();
                        if(uri.getPath().contains("/external")){
                             path = PictureHelper.getRealPathFromUri(this,uri);
                        }else{
                             path = uri.getPath();
                        }

                        File file = new File(path);
                        if(isOnShowFileChooser){
                            mUploadMsgForAndroid5.onReceiveValue(new Uri[]{uri});

                        }

                        enCodePic = PictureHelper.compressImageAndBase64EnCode(path,100);
                    }
                    postPicEnCode();
                    break;
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> result = (List)data.getSerializableExtra("extra_result_media");
                    localMediaList.clear();
                    localMediaList.addAll(result);
                    if(localMediaList.size() > 0){
                        processSelectedPicture(PictureSelector.obtainMultipleResult(data));
                    }

                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    break;
            }

        }
    }

    // 处理选择的照片的地址
    private void processSelectedPicture( final List<LocalMedia> picList) {
        for (LocalMedia localMedia : picList) {
            //被压缩后的图片路径
            if (localMedia.isCompressed()) {
                String compressPath = localMedia.getCompressPath(); //压缩后的图片路径
                sdPath.add(compressPath);
            }

        }
        picId.clear();
        uploadPic(0);


    }

    private void uploadPic(final int i) {
        FilesManager.getInstance().uploadFile(sdPath.get(i), new ProgressFileUploadListener() {
            @Override
            public void onLoading(long totalSize, long currSize) {

            }

            @Override
            public void loadSuccess( Response response) throws IOException {
                String s = response.body().string();
                LogHelper.d(s);
                FilesModel model = GsonImplHelp.get().toObject(s, FilesModel.class);
                List<FilesBean> filesBeanList = model.getData();
                for (int i = 0; i < filesBeanList.size(); i++) {
                    picId.add(filesBeanList.get(i).getNo());
                }
                if (picId.size() == sdPath.size()) {

                    //图片id 集合返回给h5
                    postPicIdToH5(picId);
                } else {
                    int j = i + 1;
                    uploadPic(j);//上传下一张图片
                }
            }

            @Override
            public void loadFaile( Call call, IOException e) {
            }
        });
    }

    private void postPicIdToH5( final List<String> picidList){
        mWebviewBinding.webview.post(new Runnable() {
            @Override
            public void run() {
                String json = GsonImplHelp.get().toJson(picidList);
                sdPath.clear();
                mWebviewBinding.webview.loadUrl("javascript:addPictureResult('"+ json + "')");

            }
        });
    }

    private void postPicEnCode() {
        mWebviewBinding.webview.post(new Runnable() {
            @Override
            public void run() {
                mWebviewBinding.webview.loadUrl("javascript:getBase64ByApp('"+ enCodePic + "')");

            }
        });
    }

    /**
     * 给客服打电话
     * @param mobile
     */
    private void callService( final String mobile ) {
        requestRunTimePermission(new String[]{Manifest.permission.CALL_PHONE}, new IPermission() {
            @SuppressLint("MissingPermission")
            @Override
            public void onGranted() {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + mobile);
                intent.setData(data);
                startActivity(intent);
            }

            @Override
            public void onDenied( List<String> deniedPermissions ) {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage( Message msg ) {

            Object obj = msg.obj;
        }
    };


    private void goPayWx( String orderInfo ) {
        WxPayModel payModel = GsonImplHelp.get().toObject(orderInfo, WxPayModel.class);
        PayReq req = new PayReq();
        req.appId = payModel.getAppId();
        req.partnerId = payModel.getPartnerId();
        req.prepayId = payModel.getPrePayId();
        req.nonceStr = payModel.getNonceStr();
        req.timeStamp = payModel.getTimestamp();
        req.packageValue = "Sign=WXPay";
        req.sign = payModel.getSign();
        req.extData = "app data"; // optional
//                    ToolsHelper.showInfo(mContext, "支付");
//                    api.registerApp(Constants.appId);
        getIwxapi(payModel.getAppId()).sendReq(req);
    }
    public IWXAPI getIwxapi(String appId) {
        if (null == iwxapi)
            iwxapi = WXAPIFactory.createWXAPI(this,appId);
        return iwxapi;
    }

//    String orderInfo = "alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2016091100488277&biz_content=%7B%22out_trade_no%22%3A%22201805091534491560269341329%22%2C%22total_amount%22%3A%221%22%2C%22subject%22%3A%22test%22%2C%22body%22%3A%22%E7%8C%8E%E8%B1%B9%E6%94%AF%E4%BB%98%22%2C%22timeout_express%22%3A%222h%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F123.125.218.30%3A60005%2Fliebaoqiche_interface%2FpayNotify%2FalipayBackNotify&sign=TXSroXCNjRQkY5Hgt9OdME0iAIiOPZDa09qJIne%2BRWacSrTTCqqELZ09FPmtAFCPufffgk0Y3Z0RSnZNeyIdnghoTOu4IC94xSnaS1ZZuRcX57ymTUi6LeCSzJW4cGvrwprqxQYZoG8P4tZbIQyxLWgTFQQKRbXLmrDjElMEO%2BZhTr6AwM9CjVx4UktePOiYE6Xa2WeCUM6et%2BTAwAtK3wWQ8%2Fg1bUf%2BSyHmHCdIiookpqw8dYh4GMPg%2FkRzFSAj872SpC6fbGOtjKYTpzT7TW32pt%2FxQEZXacqSrWPitqf8z6LI7PNKMe5MiYWBbrrbnMrm7wVwvxElcy3enPbxBw%3D%3D&sign_type=RSA2&timestamp=2018-05-09+15%3A34%3A03&version=1.0";
    private void goPayAli( final String orderInfo ) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(WebviewActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo,true);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
