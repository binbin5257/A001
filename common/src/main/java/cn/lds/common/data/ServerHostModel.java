package cn.lds.common.data;

import java.util.List;

/**
 * 服务器配置模型
 * Created by leadingsoft on 2017/11/30.
 */

public class ServerHostModel {

    private List<DataEntity> data;

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public static class DataEntity {
        /**
         * 服务器名称
         */
        private String name;
        /**
         * 服务器地址
         */
        private String url;

        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}
