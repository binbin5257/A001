package cn.lds.common.data.base;

import java.util.List;

/**
 * Created by leadingsoft on 17/12/11.
 */

public class BaseModel {


    /**
     * 状态
     */

    private String status;
    /**
     * 错误列表
     */
    private List<ErrorsBean> errors;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ErrorsBean> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorsBean> errors) {
        this.errors = errors;
    }

    public static class DataBean {

    }

    public static class ErrorsBean {
        /**
         * 错误码
         */

        private String errcode;
        /**
         * 错误消息
         */
        private String errmsg;
        /**
         * 字段名（字段校验类异常）
         */
        private String field;

        public String getErrcode() {
            return errcode;
        }

        public void setErrcode(String errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
