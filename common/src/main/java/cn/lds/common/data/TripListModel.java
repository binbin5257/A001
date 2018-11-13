package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;

/**
 * 行程列表数据结构
 * Created by leadingsoft on 17/12/15.
 */

public class TripListModel extends BaseModel {


    /**
     * data : [{"createTime":"2017-12-15T06:58:53.987Z","endLatitude":"string","endLongitude":"string","ipStr":"string","modifyTime":"2017-12-15T06:58:53.987Z","startLatitude":"string","startLongitude":"string","uuid":"string","vin":"string"}]
     * timestamp : 2017-12-15T06:58:53.987Z
     */

    private String timestamp;
    private List<DataBean> data;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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
         * 起始时间
         */
        private long createTime;
        /**
         * 熄火纬度
         */
        private double endLatitude;
        /**
         * 起始时间
         */
        private double endLongitude;
        /**
         * ip地址
         */
        private String ipStr;
        /**
         * 结束时间
         */
        private long modifyTime;
        /**
         * 点火纬度
         */
        private double startLatitude;
        /**
         * 点火经度
         */
        private double startLongitude;
        /**
         * 行程ID
         */
        private String uuid;
        /**
         * vin码
         */
        private String vin;
        /**
         * 根据坐标，通过高德地图转换得到的地址
         */
        private String startAddress;
        /**
         * 根据坐标，通过高德地图转换得到的地址
         */
        private String endAddress;

        public String getEndAddress() {
            return endAddress;
        }

        public String getStartAddress() {
            return startAddress;
        }

        public void setEndAddress(String endAddress) {
            this.endAddress = endAddress;
        }

        public void setStartAddress(String startAddress) {
            this.startAddress = startAddress;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public double getEndLatitude() {
            return endLatitude;
        }

        public void setEndLatitude(double endLatitude) {
            this.endLatitude = endLatitude;
        }

        public double getEndLongitude() {
            return endLongitude;
        }

        public void setEndLongitude(double endLongitude) {
            this.endLongitude = endLongitude;
        }

        public String getIpStr() {
            return ipStr;
        }

        public void setIpStr(String ipStr) {
            this.ipStr = ipStr;
        }

        public long getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(long modifyTime) {
            this.modifyTime = modifyTime;
        }

        public double getStartLatitude() {
            return startLatitude;
        }

        public void setStartLatitude(long startLatitude) {
            this.startLatitude = startLatitude;
        }

        public double getStartLongitude() {
            return startLongitude;
        }

        public void setStartLongitude(long startLongitude) {
            this.startLongitude = startLongitude;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }
    }
}
