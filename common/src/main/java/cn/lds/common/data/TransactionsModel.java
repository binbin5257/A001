package cn.lds.common.data;

import cn.lds.common.data.base.BaseModel;
import cn.lds.common.enums.TransactionsType;

/**
 * 远程控制车辆跟踪结果
 * Created by leadingsoft on 17/12/14.
 */

public class TransactionsModel extends BaseModel {


    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * 跟踪结果
         */

        private TransactionsType result;

        public TransactionsType getResult() {
            return result;
        }

        public void setResult(TransactionsType result) {
            this.result = result;
        }
    }
}
