package cn.lds.common.manager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import cn.lds.common.api.ModuleUrls;
import cn.lds.common.base.BaseApplication;
import cn.lds.common.constants.Constants;
import cn.lds.common.data.FilesModel;
import cn.lds.common.file.FileUploadComplete;
import cn.lds.common.file.ProgressFileUploadErrorEvent;
import cn.lds.common.file.FileUploadFailed;
import cn.lds.common.file.ProgressFileUploadCompleteEvent;
import cn.lds.common.file.ProgressFileUploadingEvent;
import cn.lds.common.file.OnDownloadListener;
import cn.lds.common.file.ProgressFileUploadListener;
import cn.lds.common.file.ProgressFileRequestBody;
import cn.lds.common.utils.CacheHelper;
import cn.lds.common.utils.LogHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 文件管理中心
 *
 * @author user
 */
public class FilesManager {
    private static FilesManager mInstance;//单利引用

    /* 下载包安装路径 */
    private static final String mSavePath = Constants.SYS_CONFIG_FILE_PATH + "file/";

    private static String mPortrait = "protrait";

    public static FilesManager getInstance() {
        FilesManager inst = mInstance;
        if (inst == null) {
            synchronized (FilesManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new FilesManager();
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    public FilesManager() {
    }


    /**
     * 下载
     *
     * @param fileNo
     *         文件id
     * @param listener
     *         下载监听
     */
    public void download(String fileNo, final OnDownloadListener listener) {
        final String url = String.format("%s/%s", ConfigManager.getInstance().getBaseUrl(), ModuleUrls.downloadFile).replace("{no}", fileNo);
        Request request = new Request.Builder().url(url).build();
        RequestManager.getInstance().getmOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(isExistDir(mSavePath + mPortrait), portraitName());
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 是否是存在路径
     *
     * @param saveDir
     * @return
     * @throws IOException
     *         判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * 获取文件名
     *
     * @param url
     * @return 从下载连接中解析出文件名
     */
    public String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 获取文件名
     *
     * @return 从下载连接中解析出文件名
     */
    public String portraitName() {
        return CacheHelper.getLoginId() + ".jpg";
    }

    /**
     * 上传
     *
     * @param filePath
     *         文件路径
     */
    public void upload(String filePath) {
        final String url = String.format("%s/%s", ConfigManager.getInstance().getBaseUrl(), ModuleUrls.uploadFile);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), new File(filePath));
        final RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", getNameFromUrl(url), fileBody)
                .build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        RequestManager.getInstance().getmOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException error) {
                LogHelper.e(String.format("%s\n%s", url, error.toString()));
                FileUploadFailed fileUploadFailed = new FileUploadFailed(url, error.toString());
                EventBus.getDefault().post(fileUploadFailed);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                LogHelper.d(String.format("%s\n%s", url, result));
                if (response.isSuccessful()) {
                    FilesModel filesModel = GsonImplHelp.get().toObject(result, FilesModel.class);
                    FileUploadComplete fileUploadComplete = new FileUploadComplete(filesModel.getData());
                    EventBus.getDefault().post(fileUploadComplete);
                } else {
                    int code = response.code();
                    if (401 == code || 403 == code) {
                        BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
                    } else {
                        FileUploadFailed fileUploadFailed = new FileUploadFailed(url, new IOException(requestBody.toString()).toString());
                        EventBus.getDefault().post(fileUploadFailed);
                    }
                }
            }
        });
    }

    /**
     * 带进度的上传文件（支持多文件异步同时上传）
     *
     * @param path
     *         上传文件路径
     * @param extras
     *         文件的标识
     */

    public void uploadProgress(String path, final Map<String, String> extras) {
        final String owner = extras.get("owner");
        final String filePath = extras.get("filePath");
        File file = new File(path);

        this.uploadProgress(ModuleUrls.fileUpload, file, new ProgressFileUploadListener() {
            @Override
            public void onLoading(long totalSize, long currSize) {
                if (currSize < totalSize) {
                    long n = totalSize / 100;
                    int p = (int) (currSize / n);
                    EventBus.getDefault().post(new ProgressFileUploadingEvent(owner, filePath, p));
                } else if (totalSize == currSize) {
                    EventBus.getDefault().post(new ProgressFileUploadingEvent(owner, filePath, 100));
                }
            }

            @Override
            public void loadSuccess(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ProgressFileUploadCompleteEvent progressFileUploadCompleteEvent = new ProgressFileUploadCompleteEvent(owner, filePath, response.body().string());
                    //发送事件总线
                    EventBus.getDefault().post(progressFileUploadCompleteEvent);
                } else {
                    int code = response.code();
                    if (401 == code || 403 == code) {
                        BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
                    } else {
                        ProgressFileUploadErrorEvent progressFileUploadErrorEvent = new ProgressFileUploadErrorEvent(owner, filePath, response.body().string());
                        //发送事件总线
                        EventBus.getDefault().post(progressFileUploadErrorEvent);
                    }
                }
            }

            @Override
            public void loadFaile(Call call, IOException e) {
                ProgressFileUploadErrorEvent progressFileUploadErrorEvent = new ProgressFileUploadErrorEvent(owner, filePath, e.toString());
                //发送事件总线
                EventBus.getDefault().post(progressFileUploadErrorEvent);
            }
        });
    }

    /**
     * 带进度的上传文件（支持多文件异步同时上传）
     *
     * @param actionUrl
     *         自定义上传路径
     * @param listener
     *         监听
     */


    public void uploadProgress(String actionUrl, File file, final ProgressFileUploadListener listener) {
        final String requestUrl = String.format("%s/%s", ConfigManager.getInstance().getBaseUrl(), actionUrl);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
        MultipartBody multipartBody = builder.build();
        ProgressFileRequestBody requestBody = new ProgressFileRequestBody(multipartBody, new ProgressFileRequestBody.ProgressListener() {
            @Override
            public void onProgress(long totalSize, long currSize) {
                listener.onLoading(totalSize, currSize);
            }
        });
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();
        RequestManager.getInstance().getmOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.loadFaile(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    listener.loadSuccess(response);
                } else {
                    int code = response.code();
                    if (401 == code || 403 == code) {
                        BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
                    } else {
                        listener.loadFaile(call, new IOException(response.body().string()));
                    }
                }
            }
        });
    }

    /**
     * 带进度的上传文件（支持多文件异步同时上传）
     *
     * @param filePath
     *         文件路径列
     * @param listener
     *         监听
     */
    public void uploadFile(String filePath, final ProgressFileUploadListener listener) {
        final String requestUrl = String.format("%s/%s", ConfigManager.getInstance().getBaseUrl(), ModuleUrls.uploadFile);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        File file = new File(filePath);
        builder.addFormDataPart("file", file.getName(),
                RequestBody.create(MediaType.parse("application/octet-stream"), file));
        MultipartBody multipartBody = builder.build();
        ProgressFileRequestBody requestBody = new ProgressFileRequestBody(multipartBody, new ProgressFileRequestBody.ProgressListener() {
            @Override
            public void onProgress(long totalSize, long currSize) {
                listener.onLoading(totalSize, currSize);
            }
        });
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();
        RequestManager.getInstance().getmOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.loadFaile(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    listener.loadSuccess(response);
                } else {
                    int code = response.code();
                    if (401 == code || 403 == code) {
                        BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
                    } else {
                        listener.loadFaile(call, new IOException(response.body().string()));
                    }
                }
            }
        });
    }

    /**
     * 带进度的上传文件（支持多文件异步同时上传）
     *
     * @param files
     *         文件路径列表
     * @param listener
     *         监听
     */
    public void uploadFiles(List<String> files, final ProgressFileUploadListener listener) {
        final String requestUrl = String.format("%s/%s", ConfigManager.getInstance().getBaseUrl(), ModuleUrls.uploadFile);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));
            builder.addFormDataPart("file", file.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }
        MultipartBody multipartBody = builder.build();
        ProgressFileRequestBody requestBody = new ProgressFileRequestBody(multipartBody, new ProgressFileRequestBody.ProgressListener() {
            @Override
            public void onProgress(long totalSize, long currSize) {
                listener.onLoading(totalSize, currSize);
            }
        });
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();
        RequestManager.getInstance().getmOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.loadFaile(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    listener.loadSuccess(response);
                } else {
                    int code = response.code();
                    if (401 == code || 403 == code) {
                        BaseApplication.getInstance().sendLogoutBroadcast(Constants.SYS_CONFIG_LOGOUT_FLITER, "用户认证失败");
                    } else {
                        listener.loadFaile(call, new IOException(response.body().string()));
                    }
                }
            }
        });
    }
}
