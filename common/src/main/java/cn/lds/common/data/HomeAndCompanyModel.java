package cn.lds.common.data;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by leadingsoft on 2017/11/30.
 * 家和公司数据模型
 */

public class HomeAndCompanyModel extends BaseModel {


    /**
     * data : {"company":{"address":"string","collectId":"string","desc":"string","latitude":"string","longitude":"string","name":"string","tel":"string","typeCode":"string"},"home":{"address":"string","collectId":"string","desc":"string","latitude":"string","longitude":"string","name":"string","tel":"string","typeCode":"string"}}
     * timestamp : 2017-12-28T05:22:39.580Z
     */

    private DataBean data;
    private String timestamp;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class DataBean {
        /**
         * company : {"address":"string","collectId":"string","desc":"string","latitude":"string","longitude":"string","name":"string","tel":"string","typeCode":"string"}
         * home : {"address":"string","collectId":"string","desc":"string","latitude":"string","longitude":"string","name":"string","tel":"string","typeCode":"string"}
         */

        private CollectionsModel.DataBean company;
        private CollectionsModel.DataBean home;

        public CollectionsModel.DataBean getCompany() {
            return company;
        }

        public void setCompany(CollectionsModel.DataBean company) {
            this.company = company;
        }

        public CollectionsModel.DataBean getHome() {
            return home;
        }

        public void setHome(CollectionsModel.DataBean home) {
            this.home = home;
        }
    }
}
