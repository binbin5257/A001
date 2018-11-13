package cn.lds.widget.viewpager.bean;

import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */

public class PageBean {

    public View bottomLayout;
    public View openview;
    public List<View> viewList;
    public List<Object> datas;
    public PageBean(Builder builder) {
        this.bottomLayout = builder.bottomLayout;
        this.openview = builder.openview;
        this.datas = builder.datas;
        this.viewList = builder.viewList;

    }



    public static class Builder<T>{
        View bottomLayout;
        View openview;
        List<View> viewList;
        List<T> datas;
        public Builder setIndicator(View bottomLayout){
            this.bottomLayout = bottomLayout;
            return this;
        }
        public Builder setOpenView(View openView){
            this.openview = openView;
            return this;
        }

        public Builder setDataObjects(List<T> datas){
            this.datas = datas;
            return this;
        }
        public Builder setInToView( List<View> mViewList ) {
            viewList = mViewList;
            return this;
        }


        public PageBean builder(){
            return new PageBean(this);
        }


    }

}
