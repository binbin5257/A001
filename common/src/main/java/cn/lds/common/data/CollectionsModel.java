package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by leadingsoft on 2017/11/30.
 * 收藏列表 返回
 */

public class CollectionsModel extends BaseModel {


    /**
     * data : [{"address":"string","collectId":"string","desc":"string","latitude":"string","longitude":"string","name":"string","tel":"string","typeCode":"string"}]
     * timestamp : 2017-12-25T01:37:09.388Z
     */

    private long timestamp;
    private List<DataBean> data;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * 地址
         */
        private String address;
        /**
         * 收藏ID
         */
        private String collectId;
        /**
         * 描述
         */
        private String desc;
        /**
         * 纬度
         */
        private double latitude;
        /**
         * 经度
         */
        private double longitude;
        /**
         * 名称
         */
        private String name;
        /**
         * 电话
         */
        private String tel;
        /**
         * 类型
         */
        private String typeCode;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCollectId() {
            return collectId;
        }

        public void setCollectId(String collectId) {
            this.collectId = collectId;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }
    }
}
