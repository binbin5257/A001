package cn.lds.common.file;

import org.json.JSONObject;

/**
 * 文件上传完成事件
 * <p>
 * 发送文件类型的消息时，都会先上传文件，将文件的URL地址返回后，在包装成消息体，发送出去
 * </p>
 *
 * @author suncf
 */
public class ProgressFileUploadCompleteEvent {
    /* 本地文件路径 */
    private String filePath;
    /* 文件所属者（如：messageId） */
    private String owner;
    /*原始JSON字符串*/
    private String result;
    /*原始JSON字符串*/
    private JSONObject jsonResult;

    public ProgressFileUploadCompleteEvent(String owner, String filePath, String result) {
        this.owner = owner;
        this.filePath = filePath;
        this.result = result;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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
}
