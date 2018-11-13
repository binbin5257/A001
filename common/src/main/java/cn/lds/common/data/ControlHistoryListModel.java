package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;
import cn.lds.common.enums.CarControlType;
import cn.lds.common.enums.TransactionsType;

/**
 * tsplog 记录数据结构
 * Created by leadingsoft on 17/12/15.
 */

public class ControlHistoryListModel extends BaseModel {


    /**
     * data : [{"commandResult":"string","commandType":"string","lastUpdateTime":"2017-12-18T01:19:12.386Z","transactionId":"string"}]
     * pageable : {"totalElements":0,"numberOfElements":0,"totalPages":0,"number":0,"first":false,"last":false,"size":0,"fromNumber":0,"toNumber":0}
     * timestamp : 2017-12-18T01:19:12.386Z
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
         * 总条数
         */

        private int totalElements;
        /**
         * 返回条数
         */
        private int numberOfElements;
        /**
         * 总页数
         */
        private int totalPages;
        /**
         * 当前页码（从0开始）
         */
        private int number;
        /**
         * 是否第一页
         */
        private boolean first;
        /**
         * 是否最后页
         */
        private boolean last;
        /**
         * 页SIZE
         */
        private int size;
        /**
         * 本页开始行（相对于总记录数，从1开始计数
         */
        private int fromNumber;
        /**
         * 本页结束行
         */
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
         * 远控命令结果
         */

        private TransactionsType commandResult;
        /**
         * 远控命令
         */

        private CarControlType commandType;
        /**
         * 最后一次状态变化的时间
         */

        private long lastUpdateTime;
        /**
         * 控车ID
         */
        private String transactionId;

        public TransactionsType getCommandResult() {
            return commandResult;
        }

        public void setCommandResult(TransactionsType commandResult) {
            this.commandResult = commandResult;
        }

        public CarControlType getCommandType() {
            return commandType;
        }

        public void setCommandType(CarControlType commandType) {
            this.commandType = commandType;
        }

        public long getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(long lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }
    }
}
