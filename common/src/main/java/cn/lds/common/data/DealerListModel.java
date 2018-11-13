package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;

/**
 * 经销商列表数据模型
 * Created by leadingsoft on 17/12/26.
 */

public class DealerListModel extends BaseModel {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData( List<DataBean> data ) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * dealerCode : CFA1382
         * dealerName : 大连振兴汽车销售有限公司CFA1382
         * longitude : 121.92566
         * latitude : 39.636949
         * address : 辽宁省瓦房店市岗店街道太阳沟
         * dealerPhone : 15998580611
         * activationDealer : false
         * subscriberDealer : false
         * distance : 9.51万
         * collected : false
         * collectionId : null
         * evaluateScore : 3.2
         * dealerImge : http://171.217.92.76:8144/dealer/ftpImage/leopaard/dealer/2018/04/28/201804281112489961d0a4a9e.jpg
         */

        private String dealerCode;
        private String dealerName;
        private double longitude;
        private double latitude;
        private String address;
        private String dealerPhone;
        private boolean activationDealer;
        private boolean subscriberDealer;
        private String distance;
        private boolean collected;
        private String collectionId;
        private String evaluateScore;
        private String dealerImge;

        public String getDealerCode() {
            return dealerCode;
        }

        public void setDealerCode( String dealerCode ) {
            this.dealerCode = dealerCode;
        }

        public String getDealerName() {
            return dealerName;
        }

        public void setDealerName( String dealerName ) {
            this.dealerName = dealerName;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude( double longitude ) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude( double latitude ) {
            this.latitude = latitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress( String address ) {
            this.address = address;
        }

        public String getDealerPhone() {
            return dealerPhone;
        }

        public void setDealerPhone( String dealerPhone ) {
            this.dealerPhone = dealerPhone;
        }

        public boolean isActivationDealer() {
            return activationDealer;
        }

        public void setActivationDealer( boolean activationDealer ) {
            this.activationDealer = activationDealer;
        }

        public boolean isSubscriberDealer() {
            return subscriberDealer;
        }

        public void setSubscriberDealer( boolean subscriberDealer ) {
            this.subscriberDealer = subscriberDealer;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance( String distance ) {
            this.distance = distance;
        }

        public boolean isCollected() {
            return collected;
        }

        public void setCollected( boolean collected ) {
            this.collected = collected;
        }

        public String getCollectionId() {
            return collectionId;
        }

        public void setCollectionId( String collectionId ) {
            this.collectionId = collectionId;
        }

        public String getEvaluateScore() {
            return evaluateScore;
        }

        public void setEvaluateScore( String evaluateScore ) {
            this.evaluateScore = evaluateScore;
        }

        public String getDealerImge() {
            return dealerImge;
        }

        public void setDealerImge( String dealerImge ) {
            this.dealerImge = dealerImge;
        }
    }
}
