package cn.lds.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.lds.R;
import cn.lds.databinding.ActivityWebHostBinding;


/**
 * webview 界面
 */

@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public class WebHostActivity extends BaseWebviewActivity implements
        DownloadListener {

    private static final int PROGRESS_SHOW = 0;
    private static final int PROGRESS_HIDE = 1;
    protected static final int FILECHOOSER_RESULTCODE = 20;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    // WEB视图
    private WebView mWebview;
    // 返回按钮
    private ImageView btnBack;
    // 刷新按钮
    private ProgressBar mProgressBar;
    // 标题
    private TextView mTitleTextView;

    // 标题
    private String mTitle;
    // 地址
    private String mMainUrl;

    private boolean enableZoom = false;
    private boolean ReceivedTitle = false;

    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_host);

        //this.clearWebViewCache();
        // 获取传递的参数
        mTitle = getIntent().getStringExtra("title");
        mMainUrl = getIntent().getStringExtra("url");
        enableZoom = getIntent().getBooleanExtra("enablezoom", false);
        ReceivedTitle = getIntent().getBooleanExtra("ReceivedTitle", false);
        // mid = getIntent().getIntExtra("id", 1);

        // 返回按钮
        btnBack = (ImageView) findViewById(R.id.top_back_iv);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebHostActivity.this.finish();
            }
        });

        // 标题
        mTitleTextView = (TextView) findViewById(R.id.top_title_tv);

        // 设置标题
        mTitleTextView.setText(mTitle);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        initWebView();
//        syncCookie();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mWebview.canGoBack()) {
                    mWebview.goBack();
                } else {
                    WebHostActivity.this.finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        menu.add(0, 0, 0, getText(R.string.common_refresh));
//        menu.add(0, 1, 0, getText(R.string.common_open));
//        menu.add(0, 2, 0, getText(R.string.common_quit));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case 0:
                mWebview.reload();
                mHandler.sendEmptyMessage(PROGRESS_SHOW);
                return true;
            case 1:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mMainUrl));
                startActivity(intent);
                return true;
            case 2:
                WebHostActivity.this.finish();
                return true;
        }
        return false;
    }

    // 初始化WebView
    private void initWebView() {
        mWebview = (WebView) findViewById(R.id.webview);
        mWebview.setInitialScale(1);

        mWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setLoadWithOverviewMode(true);

        // 设置不缓存
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 优先使用缓存
        //mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //mWebview.setScrollBarStyle(View.SCROLLBAR_POSITION_DEFAULT);
        mWebview.getSettings().setJavaScriptEnabled(true);
        // mWebview.getSettings().setPluginsEnabled(true);
        mWebview.getSettings().setPluginState(PluginState.ON);

        // 启用数据库
        mWebview.getSettings().setDatabaseEnabled(true);
        String dir = this.getApplicationContext()
                .getDir("database", Context.MODE_PRIVATE).getPath();

        // 启用地理定位
        mWebview.getSettings().setGeolocationEnabled(true);
        // 设置定位的数据库路径
        mWebview.getSettings().setGeolocationDatabasePath(dir);

        // 最重要的方法，一定要设置，这就是地图出不来的主要原因
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setBlockNetworkImage(false);
        mWebview.getSettings().setBlockNetworkLoads(false);

        // webView自适应及缩放
        if (enableZoom) {
            // 设置可以支持缩放
            mWebview.getSettings().setSupportZoom(true);
            // 设置默认缩放方式尺寸是far
            mWebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            // 设置出现缩放工具
            mWebview.getSettings().setBuiltInZoomControls(true);
            // 让网页自适应屏幕宽度
            // WebSettings webSettings = mWebview.getSettings();
            // webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        }

        mWebview.setWebViewClient(new MyWebViewClient());
        mWebview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                // TODO Auto-generated method stub
                super.onReceivedTitle(view, title);
                if (ReceivedTitle) {
                    mTitleTextView.setText(title);
                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {

                super.onReceivedIcon(view, icon);

            }


            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           Callback callback) {

                callback.invoke(origin, true, false);

                super.onGeolocationPermissionsShowPrompt(origin, callback);

            }

            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                if (progress == 100) {
                    mHandler.sendEmptyMessage(PROGRESS_HIDE);
                }
            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }
        });
        mWebview.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        // 清除缓存的有效方法
        // mWebview.loadDataWithBaseURL(null, "","text/html", "utf-8",null);
        // 设置下载监听
        mWebview.setDownloadListener(this);
        startLoadUrl(mMainUrl);

    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    public void startLoadUrl(final String url) {
        mHandler.sendEmptyMessage(PROGRESS_SHOW);

//        //List<Cookie> cookies = MyApplication.cookieStore.getCookies();
//        List<Cookie> cookies = ImtpService.cookieStore.getCookies();
//        if (!cookies.isEmpty()) {
//            for (int i = 0; i < cookies.size(); i++) {
//                cookie = cookies.get(i);
//            }
//        }
//
//        CookieSyncManager.createInstance(WebHostActivity.this);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();//移除
//        String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain="
//                + cookie.getDomain();
//        cookieManager.setCookie(url, cookieString);//cookies是在HttpClient中获得的cookie
//        CookieSyncManager.getInstance().sync();
        mWebview.loadUrl(url);
    }

    /**
     * 返回文件选择
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

    // WebView 客户端
    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebview.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

    public class JavaScriptInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        public void quit() {
            WebHostActivity.this.finish();
        }

    }

    // Handler线程
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case PROGRESS_SHOW:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case PROGRESS_HIDE:
                        mProgressBar.setVisibility(View.GONE);
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onDownloadStart(String url, String userAgent,
                                String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    /**
     * Sync Cookie
     */
    private void syncCookie() {
//        try {
//
//            CookieSyncManager.createInstance(getApplicationContext());
//
//            CookieManager cookieManager = CookieManager.getInstance();
//            cookieManager.setAcceptCookie(true);
////			cookieManager.removeSessionCookie();// 移除
////			cookieManager.removeAllCookie();
//            String oldCookie = cookieManager.getCookie(Constants.getCoreUrls().H5_SERVER_HOST());
//            if (oldCookie != null) {
//                System.out.println("-----oldCookie:" + oldCookie);
//            }
//            DefaultHttpClient dh = (DefaultHttpClient) HttpHelper.getHttpInstance().getHttpClient();
//            CookieStore cs = dh.getCookieStore();
//            List<Cookie> cookies = cs.getCookies();
//            String jsessionid = null;
//            for (int i = 0; i < cookies.size(); i++) {
//                if ("JSESSIONID".equals(cookies.get(i).getName())) {
//                    jsessionid = cookies.get(i).getValue();
//                    break;
//                }
//            }
//            StringBuilder sbCookie = new StringBuilder();
//            sbCookie.append(String.format("JSESSIONID=%s", jsessionid));
//            sbCookie.append(String.format(";domain=%s", Constants.getCoreUrls().H5_SERVER_DOMAIN()));
//            sbCookie.append(String.format(";path=%s", "/"));
//
//            String cookieValue = sbCookie.toString();
//            cookieManager.setCookie(Constants.getCoreUrls().H5_SERVER_HOST(), cookieValue);
//            CookieSyncManager.getInstance().sync();
//
//            String newCookie = cookieManager.getCookie(Constants.getCoreUrls().H5_SERVER_HOST());
//            if (newCookie != null) {
//                System.out.println("-----newCookie:" + newCookie);
//            }
//        } catch (Exception e) {
//            System.out.println("-----Exception:" + e.getMessage());
//        }
    }
}
