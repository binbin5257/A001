package cn.lds.common.data;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by leadingsoft on 2017/11/30.
 * 系统配置 数据类型
 */

public class SystemConfigModel extends BaseModel {


    /**
     * data : {"interval":0,"serviceCall":"string"}
     * timestamp : 2017-12-19T06:20:46.030Z
     */

    private DataBean data;
    private long timestamp;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class DataBean {
        /**
         * 短信发送最短间隔（秒）
         */

        private int interval;
        /**
         * 400服务电话
         */
        private String serviceCall;

        private String verInfo;
        private String userAgreementUrl;
        private String helpContent;

        /**
         * 更新版本信息
         */
        private String newerVersionInfo;
        /**
         * 版本号
         */
        private int newerVersionNo;

        /**
         * 强制更新
         */
        private boolean forceUpdate;

        /**
         * 下载地址
         */
        private String downloadUrl;

        public String getNewerVersionInfo() {
            return newerVersionInfo;
        }

        public void setNewerVersionInfo( String newerVersionInfo ) {
            this.newerVersionInfo = newerVersionInfo;
        }

        public int getNewerVersionNo() {
            return newerVersionNo;
        }

        public void setNewerVersionNo( int newerVersionNo ) {
            this.newerVersionNo = newerVersionNo;
        }

        public boolean isForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate( boolean forceUpdate ) {
            this.forceUpdate = forceUpdate;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl( String downloadUrl ) {
            this.downloadUrl = downloadUrl;
        }

        public String getVerInfo() {
            return verInfo;
        }

        public void setVerInfo( String verInfo ) {
            this.verInfo = verInfo;
        }

        public String getUserAgreementUrl() {
            return userAgreementUrl;
        }

        public void setUserAgreementUrl( String userAgreementUrl ) {
            this.userAgreementUrl = userAgreementUrl;
        }

        public String getHelpContent() {
            return helpContent;
        }

        public void setHelpContent( String helpContent ) {
            this.helpContent = helpContent;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public String getServiceCall() {
            return serviceCall;
        }

        public void setServiceCall(String serviceCall) {
            this.serviceCall = serviceCall;
        }
    }
}
