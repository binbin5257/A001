package cn.lds.common.data;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by sibinbin on 18-3-1.
 */

public class CollectionStateModel extends BaseModel {
    private CollectionStateModel.DataBean data;
    private long timestamp;

    public CollectionStateModel.DataBean getData() {
        return data;
    }

    public void setData(CollectionStateModel.DataBean data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class DataBean {


        private boolean exist;

        public boolean isExist() {
            return exist;
        }

        public void setExist( boolean exist ) {
            this.exist = exist;
        }
    }
}
