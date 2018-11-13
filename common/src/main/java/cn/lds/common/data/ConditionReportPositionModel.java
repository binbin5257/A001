package cn.lds.common.data;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by leadingsoft on 2017/11/30.
 * 车辆位置信息
 */

public class ConditionReportPositionModel extends BaseModel {


    /**
     * data : {"latitude":"string","longitude":"string"}
     * timestamp : 2018-02-02T08:46:22.548Z
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
         * latitude : double
         * longitude : double
         */

        private double latitude;
        private double longitude;

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
    }
}
