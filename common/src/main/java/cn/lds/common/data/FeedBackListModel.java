package cn.lds.common.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by leadingsoft on 17/12/22.
 */

public class FeedBackListModel extends BaseModel {

    /**
     * data : [{"id":0,"content":"string","pictures":["string"],"createdDate":"2017-12-22T06:07:32.830Z"}]
     * pageable : {"totalElements":0,"numberOfElements":0,"totalPages":0,"number":0,"first":false,"last":false,"size":0,"fromNumber":0,"toNumber":0}
     * timestamp : 2017-12-22T06:07:32.830Z
     */

    private PageableBean pageable;
    private String timestamp;
    private List<DataBean> data;

    public PageableBean getPageable() {
        return pageable;
    }

    public void setPageable(PageableBean pageable) {
        this.pageable = pageable;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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

    public static class DataBean implements Parcelable {


        /**
         * 反馈ID
         */

        private int id;
        /**
         * 提交意见
         */
        private String content;
        /**
         * tsp反馈内容
         */
        private String tspContent;
        /**
         * 反馈时间
         */
        private long createdDate;
        /**
         * 反馈图片
         */
        private List<String> pictures;
        /**
         *反馈问题类型
         */
        private String opinionType;
        /**
         *反馈结果时间
         */
        private long lastModifiedDate;

        protected DataBean( Parcel in ) {
            id = in.readInt();
            content = in.readString();
            tspContent = in.readString();
            createdDate = in.readLong();
            pictures = in.createStringArrayList();
            opinionType = in.readString();
            lastModifiedDate = in.readLong();
        }

        @Override
        public void writeToParcel( Parcel dest, int flags ) {
            dest.writeInt(id);
            dest.writeString(content);
            dest.writeString(tspContent);
            dest.writeLong(createdDate);
            dest.writeStringList(pictures);
            dest.writeString(opinionType);
            dest.writeLong(lastModifiedDate);
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel( Parcel in ) {
                return new DataBean(in);
            }

            @Override
            public DataBean[] newArray( int size ) {
                return new DataBean[size];
            }
        };

        public long getLastModifiedDate() {
            return lastModifiedDate;
        }

        public void setLastModifiedDate( long lastModifiedDate ) {
            this.lastModifiedDate = lastModifiedDate;
        }

        public String getOpinionType() {
            return opinionType;
        }

        public void setOpinionType( String opinionType ) {
            this.opinionType = opinionType;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(long createdDate) {
            this.createdDate = createdDate;
        }

        public List<String> getPictures() {
            return pictures;
        }

        public void setPictures(List<String> pictures) {
            this.pictures = pictures;
        }

        public String getTspContent() {
            return tspContent;
        }

        public void setTspContent(String tspContent) {
            this.tspContent = tspContent;
        }

        @Override
        public int describeContents() {
            return 0;
        }
        public DataBean() {
        }


    }
}
