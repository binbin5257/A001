package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;
import cn.lds.common.enums.MsgType;

/**
 * Created by leadingsoft on 2017/11/30.
 * 消息列表api 数据模型
 */

public class MessagesModel extends BaseModel {


    /**
     * data : [{"id":0,"content":"string","sendTime":"2018-01-10T05:06:43.186Z","messageType":"ABNORMAL_MOVEMENT"}]
     * pageable : {"totalElements":0,"numberOfElements":0,"totalPages":0,"number":0,"first":false,"last":false,"size":0,"fromNumber":0,"toNumber":0}
     * timestamp : 2018-01-10T05:06:43.186Z
     */

    private PageableBean pageable;
    private long timestamp;
    private List<DataBean> data;

    public PageableBean getPageable() {
        return pageable;
    }

    public void setPageable(PageableBean pageable) {
        this.pageable = pageable;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class PageableBean {
        /**
         * totalElements : 0
         * numberOfElements : 0
         * totalPages : 0
         * number : 0
         * first : false
         * last : false
         * size : 0
         * fromNumber : 0
         * toNumber : 0
         */

        private int totalElements;
        private int numberOfElements;
        private int totalPages;
        private int number;
        private boolean first;
        private boolean last;
        private int size;
        private int fromNumber;
        private int toNumber;

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public int getNumberOfElements() {
            return numberOfElements;
        }

        public void setNumberOfElements(int numberOfElements) {
            this.numberOfElements = numberOfElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getFromNumber() {
            return fromNumber;
        }

        public void setFromNumber(int fromNumber) {
            this.fromNumber = fromNumber;
        }

        public int getToNumber() {
            return toNumber;
        }

        public void setToNumber(int toNumber) {
            this.toNumber = toNumber;
        }
    }

    public static class DataBean {
        /**
         * id (integer, optional): 消息ID ,
         */
        private String id;

        /**
         * title (string, optional): 消息标题 ,
         */
        private String title;
        /**
         * content (string, optional): 消息内容 ,
         */
        private String content;
        /**
         * sendTime (string, optional): 发送时间 ,
         */
        private long sendTime;
        /**
         * messageType (string, optional): 消息类型 = ['ABNORMAL_MOVEMENT', 'REMOTE_FAULT', 'CARE_NOTIFACTION']
         */
        private MsgType messageType;
        /**
         * 是否已读
         */
        private boolean checked;

        public boolean isChecked() {
            return checked;
        }

        public void setChecked( boolean checked ) {
            this.checked = checked;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle( String title ) {
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getSendTime() {
            return sendTime;
        }

        public void setSendTime(long sendTime) {
            this.sendTime = sendTime;
        }

        public MsgType getMessageType() {
            return messageType;
        }

        public void setMessageType(MsgType messageType) {
            this.messageType = messageType;
        }
    }
}
