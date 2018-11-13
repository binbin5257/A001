package cn.lds.common.data;

/**
 * Created by sibinbin on 18-5-10.
 */

public class ControlCarSuccessEvent {

    private String mType;

    public ControlCarSuccessEvent(String type){
        this.mType = type;
    }

    public String getmType() {
        return mType;
    }

    public void setmType( String mType ) {
        this.mType = mType;
    }
}
