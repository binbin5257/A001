package cn.lds.common.http;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import cn.lds.common.utils.LogHelper;
import okhttp3.Response;


/**
 * REST请求结果封装
 */
public class HttpResult {

    /**
     * API编码
     */
    private String apiNo;
    /**
     * 原始请求地址
     */
    private String url;
    /**
     * 原始JSON字符串
     */
    private String result;
    /**
     * 原始JSON字符串
     */
    private JSONObject jsonResult;
    /**
     * 传递跟踪信息
     */
    private Map<String, String> extras;
    /**
     * API请求结果
     */
    private boolean error = false;

    public HttpResult(String apiNo, String url, String result) {
        super();
        this.apiNo = apiNo;
        this.url = url;
        this.result = result;
        try {
            JSONObject jsonObject = new JSONObject(result);
            this.setJsonResult(jsonObject);
        } catch (JSONException e) {
            LogHelper.e(this.getClass().getSimpleName(), e);
        }
    }

    public HttpResult(String apiNo, String url, String result, Map<String, String> extras) {
        super();
        this.apiNo = apiNo;
        this.url = url;
        this.result = result;
        this.extras = extras;
    }

    public String getApiNo() {
        return apiNo;
    }

    public void setApiNo(String apiNo) {
        this.apiNo = apiNo;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean isError) {
        this.error = isError;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public JSONObject getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(JSONObject jsonResult) {
        this.jsonResult = jsonResult;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(apiNo);
        sb.append("|");
        sb.append(url);
        sb.append("|");
        sb.append(result);
        sb.append("|");
        sb.append(error);
        return sb.toString();
    }



}
