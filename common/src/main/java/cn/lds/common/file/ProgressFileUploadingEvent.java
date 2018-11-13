package cn.lds.common.file;

/**
 * 文件上传进度
 */
public class ProgressFileUploadingEvent {


    /* 文件所属 */
    String owner;

    String filePath;

    int progress;

    public ProgressFileUploadingEvent(String owner, String filePath, int progress) {

        this.owner = owner;
        this.filePath = filePath;
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
