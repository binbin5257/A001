package cn.lds.common.data;

import java.io.Serializable;

/**
 * 车辆信息显示配置model
 * Created by sibinbin on 18-3-26.
 */

public class CarInfoShowConfigModel implements Serializable {

    private String name;

    private String value;

    private boolean select;

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect( boolean select ) {
        this.select = select;
    }
}
