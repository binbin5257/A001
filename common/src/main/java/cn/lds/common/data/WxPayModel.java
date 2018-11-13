package cn.lds.common.data;

/**
 * Created by sibinbin on 18-5-28.
 */

public class WxPayModel {

    /**
     * id : null
     * appId : wxf2897f553372ccdd
     * sign : 57FF3DE0693EE0441A41A39234031FB4
     * nonceStr : fvNaYB2ygbzWYE7G
     * prePayId : wx28145154843921cc20f105dc4198731842
     * partnerId : 1501454741
     * timestamp : 1527490262
     */

    private Object id;
    private String appId;
    private String sign;
    private String nonceStr;
    private String prePayId;
    private String partnerId;
    private String timestamp;

    public Object getId() {
        return id;
    }

    public void setId( Object id ) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId( String appId ) {
        this.appId = appId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign( String sign ) {
        this.sign = sign;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr( String nonceStr ) {
        this.nonceStr = nonceStr;
    }

    public String getPrePayId() {
        return prePayId;
    }

    public void setPrePayId( String prePayId ) {
        this.prePayId = prePayId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId( String partnerId ) {
        this.partnerId = partnerId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( String timestamp ) {
        this.timestamp = timestamp;
    }
}
