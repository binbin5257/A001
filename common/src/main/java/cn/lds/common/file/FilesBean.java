package cn.lds.common.file;

/**
 * Created by leadingsoft on 17/12/12.
 * 文件基本信息
 */

public class FilesBean {


    /**
     * no : 11
     * objectType :
     * filePath :
     * fileName :
     * fileSize :
     * thumbnailFilePath :
     * originalFilePath :
     * duration :
     */

    private String no;
    private String objectType;
    private String filePath;
    private String fileName;
    private String fileSize;
    private String thumbnailFilePath;
    private String originalFilePath;
    private String duration;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getThumbnailFilePath() {
        return thumbnailFilePath;
    }

    public void setThumbnailFilePath(String thumbnailFilePath) {
        this.thumbnailFilePath = thumbnailFilePath;
    }

    public String getOriginalFilePath() {
        return originalFilePath;
    }

    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
