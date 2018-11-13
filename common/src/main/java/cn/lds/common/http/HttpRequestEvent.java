package cn.lds.common.http;

/**
 * REST请求成功事件
 * <p>
 * REST请求现在全部在 @see cn.lds.chat.common.HttpHelper中管理，在各种请求（CURD）完成后，成功会发出该事件
 * </p>
 * 
 * @author suncf
 * 
 */
public class HttpRequestEvent {

	/** 请求结果 */
	public final HttpResult httpResult;
	/**
	 * 构造方法
	 * @param httpResult
	 */
	public HttpRequestEvent(HttpResult httpResult) {
		this.httpResult = httpResult;
	}

	/**
	 * 获取请求结果
	 * @return
	 */
	public HttpResult getResult() {
		return httpResult;
	}


}
