package cn.lds.common.file;

import java.util.List;

/**
 * Created by leadingsoft on 17/12/12.
 */

public class FileUploadComplete {
    List<FilesBean> filesBeanList;

    public FileUploadComplete(List<FilesBean> filesBeanList) {
        this.filesBeanList = filesBeanList;

    }

    public void setFilesBeanList(List<FilesBean> filesBeanList) {
        this.filesBeanList = filesBeanList;
    }

    public List<FilesBean> getFilesBeanList() {
        return filesBeanList;
    }
}
