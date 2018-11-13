package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by sibinbin on 18-5-9.
 */

public class AdvertisementModel extends BaseModel {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData( List<DataBean> data ) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * pictureUrl : 123.125.218.29:1081/demo.jpg
         * url : xxxx
         */

        private int id;
        private String pictureUrl;
        private String url;

        public int getId() {
            return id;
        }

        public void setId( int id ) {
            this.id = id;
        }

        public String getPictureUrl() {
            return pictureUrl;
        }

        public void setPictureUrl( String pictureUrl ) {
            this.pictureUrl = pictureUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( String url ) {
            this.url = url;
        }
    }
}
