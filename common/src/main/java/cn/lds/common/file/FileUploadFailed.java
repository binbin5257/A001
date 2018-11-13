package cn.lds.common.file;

/**
 * Created by leadingsoft on 17/12/12.
 */

public class FileUploadFailed {
    String url;
    String error;

    public FileUploadFailed(String url, String error) {
        this.url = url;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getUrl() {
        return url;
    }
}
