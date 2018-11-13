package cn.lds.common.http;

import java.io.IOException;

/**
 * REST请求失败事件
 * <p>
 * REST请求现在全部在 @see
 * cn.lds.chat.common.HttpHelper中管理，在各种请求（CURD）完成后，失败（HttpCode=4
 * xx，或业务定义的失败）会发出该事件
 * </p>
 *
 * @author suncf
 */
public class HttpRequestErrorEvent {
    /**
     * 请求结果
     */
    public final HttpResult httpResult;

    /**
     * 异常
     */
    public IOException exception;

    /**
     * 构造方法
     *
     * @param httpResult
     */
    public HttpRequestErrorEvent(HttpResult httpResult) {
        this.httpResult = httpResult;
    }

    /**
     * 获取请求结果
     *
     * @return
     */
    public HttpResult getResult() {
        return httpResult;
    }

    public IOException getException() {
        return exception;
    }

    public void setException(IOException exception) {
        this.exception = exception;
    }
}
