package cn.lds.common.file;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 文件上传进度监听
 * Created by sibinbin on 17-12-21.
 */

public interface ProgressFileUploadListener {

    void onLoading(long totalSize, long currSize);

    void loadSuccess(Response response) throws IOException;

    void loadFaile(Call call, IOException e);
}
