package cn.lds.common.table;

import android.text.TextUtils;

import cn.lds.common.api.HttpApiKey;
import io.realm.RealmObject;

/**
 * 控车操作失败历史记录
 * Created by sibinbin on 18-4-25.
 */

public class ControlCarFailtureHistoryTable extends RealmObject{

    private String vin;

    private String type;

    private long time;

    private int timeOut;

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut( int timeOut ) {
        this.timeOut = timeOut;
    }

    public String getVin() {
        return vin;
    }

    public void setVin( String vin ) {
        this.vin = vin;
    }

    public String getType() {


        return type;
    }

    public void setType( String type ) {

        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime( long time ) {
        this.time = time;
    }

    public String getContent(){
        String convertType = "";
        if(TextUtils.isEmpty(type)){
            return "";
        }
        if(timeOut == 0){
            switch (type){
                case HttpApiKey.unlock:
                    convertType = "开锁超时";
                    break;
                case HttpApiKey.lock:
                    convertType = "关锁超时";
                    break;
                case HttpApiKey.flashLightWhistle:
                    convertType = "闪灯鸣笛超时";
                    break;
                case HttpApiKey.airConditionTurnOff:
                    convertType = "关闭空调超时";
                    break;
                case HttpApiKey.airConditionRefrigerate:
                    convertType = "空调制冷启动超时";
                    break;
                case HttpApiKey.airConditionHeat:
                    convertType = "空调制热启动超时";
                    break;
            }
        }else if(timeOut ==1){
            switch (type){
                case HttpApiKey.unlock:
                    convertType = "开锁失败";
                    break;
                case HttpApiKey.lock:
                    convertType = "关锁失败";
                    break;
                case HttpApiKey.flashLightWhistle:
                    convertType = "闪灯鸣笛失败";
                    break;
                case HttpApiKey.airConditionTurnOff:
                    convertType = "关闭空调失败";
                    break;
                case HttpApiKey.airConditionRefrigerate:
                    convertType = "空调制冷启动失败";
                    break;
                case HttpApiKey.airConditionHeat:
                    convertType = "空调制热启动失败";
                    break;
            }
        }

        return convertType;
    }
}
