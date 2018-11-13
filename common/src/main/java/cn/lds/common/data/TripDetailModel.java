package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;

/**
 * 行程轨迹数据结构
 * Created by leadingsoft on 17/12/15.
 */

public class TripDetailModel extends BaseModel {

    /**
     * data : [{"direction":"string","height":"string","latitude":"string","longitude":"string","speed":"string","tripUuid":"string","vin":"string"}]
     * timestamp : 2018-01-04T07:39:38.387Z
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
         * direction (string, optional): 方向
         */

        private String direction;
        /**
         * height (string, optional): 高度（海拔）
         */
        private String height;
        /**
         * latitude (string, optional): 纬度
         */
        private double latitude;
        /**
         * longitude (string, optional): 经度
         */
        private double longitude;
        /**
         * speed (string, optional): 速度
         */
        private String speed;
        /**
         * tripUuid (string, optional): 行程ID
         */
        private String tripUuid;
        /**
         * vin (string, optional): 车架号
         */
        private String vin;

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
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

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getTripUuid() {
            return tripUuid;
        }

        public void setTripUuid(String tripUuid) {
            this.tripUuid = tripUuid;
        }

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }
    }
}
