package cn.lds.common.data;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by sibinbin on 18-5-4.
 */

public class CollectionSuccess extends BaseModel {


    /**
     * data : {"collectionId":"B019B08T3H"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData( DataBean data ) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * collectionId : B019B08T3H
         */

        private String collectionId;

        public String getCollectionId() {
            return collectionId;
        }

        public void setCollectionId( String collectionId ) {
            this.collectionId = collectionId;
        }
    }
}
