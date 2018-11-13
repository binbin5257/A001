package cn.lds.common.table;

import java.io.Serializable;

import cn.lds.common.utils.json.GsonImplHelp;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by leadingsoft on 17/12/4.
 * 常用车辆
 */

public class CarsTable extends RealmObject implements Serializable{


    /**
     * 车架号
     */
    @PrimaryKey
    private String vin;
    /**
     * 车牌号
     */
    private String licensePlate;
    /**
     * 车型
     */
    private String mode;
    /**
     * 生产年份
     */
    private String year;
    /**
     * 颜色
     */
    private String color;
    /**
     * 燃油类型
     */

    private int fuelType;

    private String image;

    private String account;

    private String modelColor;

    public String getModelColor() {
        return modelColor;
    }

    public void setModelColor( String modelColor ) {
        this.modelColor = modelColor;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount( String account ) {
        this.account = account;
    }

    public String getImage() {
        return image;
    }

    public void setImage( String image ) {
        this.image = image;
    }

    @Override
    public String toString() {
        return GsonImplHelp.get().toJson(this);
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getFuelType() {
        return fuelType;
    }

    public void setFuelType(int fuelType) {
        this.fuelType = fuelType;
    }
}
