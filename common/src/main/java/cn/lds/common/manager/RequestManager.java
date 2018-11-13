package cn.lds.common.manager;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.lds.common.api.HttpApiKey;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.constants.Constants;
import cn.lds.common.enums.HttpType;
import cn.lds.common.http.HttpRequestErrorEvent;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.LogHelper;
import cn.lds.common.utils.NetWorkHelper;
import cn.lds.widget.ToastUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * api请求管理中心
 * Created by leadingsoft on 2017/6/28.
 */

public class RequestManager {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    //    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final String TAG = RequestManager.class.getSimpleName();
    private static final String BASE_URL = ConfigManager.getInstance().getBaseUrl();//请求接口根地址
    private static volatile RequestManager mInstance;//单利引用
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Request mRequest;
    private final String cacheName = "image_cache";

    public static boolean isLogin = false;


    /**
     * 初始化RequestManager
     */
    public RequestManager() {
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new LoggingInterceptor())
                .addInterceptor(new NetInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)//设置写入超时时间
                .cookieJar(cookieJar)
                .build();
    }

    public final class NetInterceptor implements Interceptor{

        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request request = chain.request();
            return chain.proceed(request);
        }
    }


    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept( Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.d(TAG,String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.d(TAG, String.format("Received response for %s in %.1fms%n%sconnection=%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers(), chain.connection()));

            return response;
        }
    }

    public void initFresco(Context context){

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryName(cacheName)
                .setBaseDirectoryPath(new File(Constants.SYS_CONFIG_FILE_PATH))
                .build();
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(context, mOkHttpClient)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(context, config);//初始化 fresco
    }

    //初始化Cookie管理器
    CookieJar cookieJar = new CookieJar() {
        //Cookie缓存区
        private final Map<String, List<Cookie>> cookiesMap = new HashMap<String, List<Cookie>>();

        @Override
        public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {

            if (null == arg1 || arg1.size() == 0)
                return;

            //移除相同的url的Cookie
            String host = arg0.host();
            List<Cookie> cookiesList = cookiesMap.get(host);
            if (cookiesList != null) {
                cookiesMap.remove(host);
            }
            //再重新天添加
            cookiesMap.put(host, arg1);

        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl arg0) {
            List<Cookie> cookiesList = cookiesMap.get(arg0.host());
            //注：这里不能返回null，否则会报NULLException的错误。
            //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
            return cookiesList != null ? cookiesList : new ArrayList<Cookie>();
        }
    };

    /**
     * 获取单例引用
     *
     * @return
     */
    public static RequestManager getInstance() {
        RequestManager inst = mInstance;
        if (inst == null) {
            synchronized (RequestManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new RequestManager();
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 统一为请求添加头信息
     *
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("content-type", "applicaion.json")
                .addHeader("appVersion", "3.2.0");
        return builder;
    }

    public void get(final String actionUrl, final String apiNo) {
        get(actionUrl, apiNo, null);
    }

    /**
     * okHttp get异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @return
     */
    public void get(final String actionUrl, final String apiNo, final Map<String, String> extras) {

        if(NetWorkHelper.isNetworkConnected()){
            try {
                final String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
                LogHelper.d(String.format("%s", requestUrl));
                final Request request = addHeaders().url(requestUrl).build();
                final Call call = mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        failureHandler(requestUrl, apiNo, extras, e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            successHandler(requestUrl, apiNo, extras, response);
                        } else {
                            failureHandler(requestUrl, apiNo, extras, response.toString(), response.code(), HttpType.GET);
                        }
                    }
                });
            } catch (Exception e) {
                LogHelper.e(TAG, e);
            }
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无网络连接");
        }

    }

    /**
     * okHttp post异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param params
     *         参数json串
     * @return
     */
    public void post(String actionUrl, String apiNo, String params) {
        post(actionUrl, apiNo, params, null);
    }

    /**
     * okHttp post异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @return
     */
    public void post(String actionUrl, String apiNo) {
        post(actionUrl, apiNo, "", null);
    }

    /**
     * okHttp post异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @param paramsMap
     *         key value 参数
     * @return
     */
    public void post(final String actionUrl, final String apiNo, Map<String, String> paramsMap, final Map<String, String> extras) {

        if(NetWorkHelper.isNetworkConnected()){
            StringBuilder tempParams = new StringBuilder();
            try {
                int pos = 0;
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }
                String params = tempParams.toString();
                post(actionUrl, apiNo, params, extras);
            } catch (Exception e) {
                LogHelper.e(TAG, e);
            }
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无网络连接");
        }

    }


    /**
     * okHttp post异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @param params
     *         参数json串
     * @return
     */
    public void post(final String actionUrl, final String apiNo, String params, final Map<String, String> extras) {
        if(NetWorkHelper.isNetworkConnected()){
            try {
                RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
                final String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
                LogHelper.d(String.format("%s\n%s", requestUrl, params));
                final Request request = addHeaders().url(requestUrl).post(body).build();
                if (HttpApiKey.login.equals(apiNo)) {
                    mRequest = request;
                }

                final Call call = mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        failureHandler(requestUrl, apiNo, extras, e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            successHandler(requestUrl, apiNo, extras, response);
                        } else {
                            failureHandler(requestUrl, apiNo, extras, response.toString(), response.code(), HttpType.POST);
                        }

                    }
                });
            } catch (Exception e) {
                LogHelper.e(TAG, e);
            }
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无网络连接");
        }


    }

    /**
     * okHttp put异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param params
     *         参数json串
     * @return
     */
    public void put(String actionUrl, String apiNo, String params) {
        if(NetWorkHelper.isNetworkConnected()){
            put(actionUrl, apiNo, params, null);
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无网络连接");
        }

    }

    /**
     * okHttp put异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @return
     */
    public void put(String actionUrl, String apiNo) {
        if(NetWorkHelper.isNetworkConnected()){
            put(actionUrl, apiNo, "", null);
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无网络连接");
        }
    }

    /**
     * okHttp put异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @param paramsMap
     *         参数key value
     * @return
     */
    public void put(final String actionUrl, final String apiNo, Map<String, String> paramsMap, final Map<String, String> extras) {

        if(NetWorkHelper.isNetworkConnected()){
            StringBuilder tempParams = new StringBuilder();
            try {
                int pos = 0;
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }
                String params = tempParams.toString();
                put(actionUrl, apiNo, params, extras);
            } catch (Exception e) {
                LogHelper.e(TAG, e);
            }
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无网络连接");
        }


    }

    /**
     * okHttp put异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @param params
     *         参数json串
     * @return
     */
    public void put(final String actionUrl, final String apiNo, String params, final Map<String, String> extras) {
        if(NetWorkHelper.isNetworkConnected()){
            try {
                RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
                final String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
                LogHelper.d(String.format("%s\n%s", requestUrl, params));
                final Request request = addHeaders().url(requestUrl).put(body).build();

                final Call call = mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        failureHandler(requestUrl, apiNo, extras, e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            successHandler(requestUrl, apiNo, extras, response);
                        } else {
                            failureHandler(requestUrl, apiNo, extras, response.toString(), response.code(), HttpType.PUT);
                        }
                    }
                });
            } catch (Exception e) {
                LogHelper.e(TAG, e);
            }
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无网络连接");
        }



    }

    /**
     * okHttp delete异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param params
     *         参数json串
     * @return
     */
    public void delete(String actionUrl, String apiNo, String params) {
        delete(actionUrl, apiNo, params, null);
    }

    /**
     * okHttp delete异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @return
     */
    public void delete(String actionUrl, String apiNo) {
        delete(actionUrl, apiNo, "", null);
    }

    /**
     * okHttp delete异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @param paramsMap
     *         参数key value
     * @return
     */
    public void delete(final String actionUrl, final String apiNo, Map<String, String> paramsMap, final Map<String, String> extras) {
        if(NetWorkHelper.isNetworkConnected()){
            StringBuilder tempParams = new StringBuilder();
            try {
                int pos = 0;
                for (String key : paramsMap.keySet()) {
                    if (pos > 0) {
                        tempParams.append("&");
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                    pos++;
                }
                String params = tempParams.toString();
                post(actionUrl, apiNo, params, extras);
            } catch (Exception e) {
                LogHelper.e(TAG, e);
            }
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无网络连接");
        }


    }

    /**
     * okHttp delete异步请求
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @param params
     *         参数json串
     * @return
     */
    public void delete(final String actionUrl, final String apiNo, String params, final Map<String, String> extras) {
        if(NetWorkHelper.isNetworkConnected()){
            try {
                RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
                final String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
                LogHelper.d(String.format("%s\n%s", requestUrl, params));
                final Request request = addHeaders().url(requestUrl).delete(body).build();

                final Call call = mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        failureHandler(requestUrl, apiNo, extras, e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            successHandler(requestUrl, apiNo, extras, response);
                        } else {
                            failureHandler(requestUrl, apiNo, extras, response.toString(), response.code(), HttpType.DELETE);
                        }
                    }
                });
            } catch (Exception e) {
                LogHelper.e(TAG, e);
            }
        }else{
            ToastUtil.showToast(BaseApplication.getInstance().getBaseContext(),"无网络连接");
        }


    }

    /**
     * 请求成功统一处理
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @param response
     *
     *         返回值
     * @return
     */
    private void successHandler(String actionUrl, String apiNo, Map<String, String> extras, Response response) {
        try {
            String result = response.body().string();
            LogHelper.d(String.format("%s\n%s", actionUrl, result));

            if (HttpApiKey.login.equals(apiNo)) {
                isLogin = true;
                Headers headers = response.headers();
                HttpUrl loginUrl = mRequest.url();
                //获取头部的Cookie,注意：可以通过Cooke.parseAll()来获取
                List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
                //防止header没有Cookie的情况
                if (cookies != null && cookies.size() > 0) {
                    //存储到Cookie管理器中
                    CacheHelper.setCookie(cookies.get(0).value());
                    mOkHttpClient.cookieJar().saveFromResponse(loginUrl, cookies);//这样就将Cookie存储到缓存中了
                }

            }

            HttpResult httpResult = new HttpResult(apiNo, actionUrl, result, extras);
            JSONObject jsonObject;
            jsonObject = new JSONObject(result);
            httpResult.setJsonResult(jsonObject);
            if ("failure".equals(jsonObject.optString("status"))) {
                httpResult.setError(true);
            }
            if (!httpResult.isError()) {
                EventBus.getDefault().post(new HttpRequestEvent(httpResult));
            } else {
                EventBus.getDefault().post(new HttpRequestErrorEvent(httpResult));
            }
        } catch (JSONException e) {
            LogHelper.d(TAG, e);
        } catch (IOException e) {
            LogHelper.d(TAG, e);
        }
    }

    /**
     * 请求失败统一处理
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @param error
     *         错误信息
     * @return
     */
    private void failureHandler(String actionUrl, String apiNo, Map<String, String> extras, String error) {
        LogHelper.e(String.format("%s\n%s", actionUrl, error.toString()));
        HttpResult httpResult = new HttpResult(apiNo, actionUrl, error, extras);
        httpResult.setError(true);
        HttpRequestErrorEvent httpRequestErrorEvent = new HttpRequestErrorEvent(httpResult);
        httpRequestErrorEvent.setException(new IOException(error));
        EventBus.getDefault().post(httpRequestErrorEvent);
    }

    /**
     * 请求失败统一处理
     *
     * @param actionUrl
     *         接口地址
     * @param apiNo
     *         标识
     * @param extras
     *         说明参数
     * @param error
     *         错误信息
     * @param code
     *         错误码
     * @return
     */
    private void failureHandler(String actionUrl, String apiNo, Map<String, String> extras, String error, int code, HttpType type) {
        failureHandler(actionUrl, apiNo, extras, error);
        if (401 == code || 403 == code) {
            if(isLogin){
                isLogin = false;
                BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
            }
//            final Activity topActivity = ActivityManager.getTopActivity();
//            BaseApplication.getInstance().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    VersionUpdateDialog updateDialog = new VersionUpdateDialog(topActivity).setOnDialogClickListener(new OnDialogClickListener() {
//                        @Override
//                        public void onDialogClick(Dialog dialog, String clickPosition) {
//                            dialog.dismiss();
//                            switch (clickPosition) {
//                                case ClickPosition.SUBMIT:
////                                    dialog.dismiss();
//                                    BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
//                                    break;
//                            }
//                        }
//                    });
//                    updateDialog.setMustUpdate(true);
//                    updateDialog.setUpdateContent("您的账号已在其他设备上登录");
//                    updateDialog.show();
//                }
//            });



//            if (HttpApiKey.login.equals(apiNo)) {
//            BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
//            } else {//自动登录
//                AccountManager.getInstance().autoLogin(actionUrl, apiNo, extras, type);
//            }
        }
    }

    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }

    public void setmOkHttpClient(OkHttpClient mOkHttpClient) {
        this.mOkHttpClient = mOkHttpClient;
    }
}
