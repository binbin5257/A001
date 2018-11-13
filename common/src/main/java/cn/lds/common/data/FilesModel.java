package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;
import cn.lds.common.file.FilesBean;

/**
 * 文件列表数据模型
 * Created by leadingsoft on 2017/11/30.
 */

public class FilesModel extends BaseModel {


    private List<FilesBean> data;

    public List<FilesBean> getData() {
        return data;
    }

    public void setData(List<FilesBean> data) {
        this.data = data;
    }
}
