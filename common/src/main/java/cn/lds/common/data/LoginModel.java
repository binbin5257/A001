package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;

/**
 * 登录数据模型
 * Created by leadingsoft on 2017/11/30.
 */

public class LoginModel extends BaseModel {


    /**
     * data : {"appId":"","authenticated":true,"authorities":"","details":{"versionDescription":"","iosLatestVersion":"10","androidMinVersion":"0","onceToken":"MTI0NDg4ODE1MTMyMzQ2MzY=","iosMinVersion":"0","androidLatestVersion":"10","apkDownloadUrl":"http://appdb-leopaard.cu-sc.com:1081/app-release.apk"},"principal":"USER1NQYWL9BTQJM","vehicle":[{"color":"珠光白","fuelType":0,"licensePlate":"辽A88888","mode":"","vin":"LN86DCBF3WL173272","year":""},{"color":"","fuelType":0,"licensePlate":"京A88888","mode":"","vin":"LN86GCBF1GB140320","year":"2017"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * appId :
         */
        private String appId;
        private boolean authenticated;
        private String authorities;
        /**
         * 用户详情 :
         */
        private DetailsBean details;
        /**
         * userNo :
         */
        private String principal;

        /**
         * 车辆列表
         */
        private List<VehicleBean> vehicle;


        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public void setAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
        }

        public String getAuthorities() {
            return authorities;
        }

        public void setAuthorities(String authorities) {
            this.authorities = authorities;
        }

        public DetailsBean getDetails() {
            return details;
        }

        public void setDetails(DetailsBean details) {
            this.details = details;
        }

        public String getPrincipal() {
            return principal;
        }

        public void setPrincipal(String principal) {
            this.principal = principal;
        }

        public List<VehicleBean> getVehicle() {
            return vehicle;
        }

        public void setVehicle(List<VehicleBean> vehicle) {
            this.vehicle = vehicle;
        }

        public static class DetailsBean {
            /**
             * 版本升级描述
             */
            private String versionDescription;
            /**
             * 安卓最低版本号
             */
            private int androidMinVersion;
            /**
             * 一次性登录token
             */
            private String onceToken;
            /**
             * 安卓最新版本号
             */
            private int androidLatestVersion;
            /**
             * 安卓apk下载地址
             */
            private String apkDownloadUrl;
            /**
             * jpush tag分组
             */

            private String tagList;

            /**
             * 是否是demo账户
             */
            private boolean isDemo;

            public boolean isDemo() {
                return isDemo;
            }

            public void setDemo( boolean demo ) {
                isDemo = demo;
            }

            public String getVersionDescription() {
                return versionDescription;
            }

            public void setVersionDescription(String versionDescription) {
                this.versionDescription = versionDescription;
            }


            public int getAndroidMinVersion() {
                return androidMinVersion;
            }

            public void setAndroidMinVersion(int androidMinVersion) {
                this.androidMinVersion = androidMinVersion;
            }

            public String getOnceToken() {
                return onceToken;
            }

            public void setOnceToken(String onceToken) {
                this.onceToken = onceToken;
            }


            public int getAndroidLatestVersion() {
                return androidLatestVersion;
            }

            public void setAndroidLatestVersion(int androidLatestVersion) {
                this.androidLatestVersion = androidLatestVersion;
            }

            public String getApkDownloadUrl() {
                return apkDownloadUrl;
            }

            public void setApkDownloadUrl(String apkDownloadUrl) {
                this.apkDownloadUrl = apkDownloadUrl;
            }

            public String getTagList() {
                return tagList;
            }

            public void setTagList(String tagList) {
                this.tagList = tagList;
            }
        }

        public static class VehicleBean {

            /**
             * 颜色
             */
            private String color;
            /**
             * 燃油类型
             */
            private int fuelType;
            /**
             * 车牌号
             */
            private String licensePlate;
            /**
             * 车型
             */
            private String mode;
            /**
             * 车架号
             */
            private String vin;
            /**
             * 生产年份
             */
            private String year;
            /**
             * 车型颜色
             */
            private String modelColor;

            private String image;

            public String getModelColor() {
                return modelColor;
            }

            public void setModelColor( String modelColor ) {
                this.modelColor = modelColor;
            }

            public String getImage() {
                return image;
            }

            public void setImage( String image ) {
                this.image = image;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public int getFuelType() {
                return fuelType;
            }

            public void setFuelType(int fuelType) {
                this.fuelType = fuelType;
            }

            public String getLicensePlate() {
                return licensePlate;
            }

            public void setLicensePlate(String licensePlate) {
                this.licensePlate = licensePlate;
            }

            public String getMode() {
                return mode;
            }

            public void setMode(String mode) {
                this.mode = mode;
            }

            public String getVin() {
                return vin;
            }

            public void setVin(String vin) {
                this.vin = vin;
            }

            public String getYear() {
                return year;
            }

            public void setYear(String year) {
                this.year = year;
            }
        }
    }
}
