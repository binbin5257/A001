package cn.lds.common.data;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by sibinbin on 18-4-13.
 */

public class TokenModel extends BaseModel {

    private String data;

    public String getData() {
        return data == null ? "" : data;
    }

    public void setData( String data ) {
        this.data = data;
    }
}
