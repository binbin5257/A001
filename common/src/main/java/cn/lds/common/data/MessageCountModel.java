package cn.lds.common.data;

/**
 * Created by sibinbin on 18-5-28.
 */

public class MessageCountModel {

    /**
     * status : success
     * data : 0
     */

    private String status;
    private int data;

    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public int getData() {
        return data;
    }

    public void setData( int data ) {
        this.data = data;
    }
}
