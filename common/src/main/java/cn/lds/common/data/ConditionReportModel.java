package cn.lds.common.data;

import java.io.Serializable;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by leadingsoft on 2017/11/30.
 * 车辆详情数据模型
 */

public class ConditionReportModel extends BaseModel implements Serializable{


    /**
     * data : {"alarmStatus":"string","dateTime":"string","direction":"string","doorStatus":"string","gpsSpeed":"string","height":"string","instantaneFuleCon":0,"instrumentDisplayMode":"string","instrumentSpeed":0,"lampStatus":"string","latitude":"string","latitude_":"string","leftFrontTirePressure":0,"leftFrontTirePressureStatus":0,"leftFrontTirePressureStatusEffectiveFlag":0,"leftRearTirePressure":0,"leftRearTirePressureStatus":0,"leftRearTirePressureStatusEffectiveFlag":0,"locationValid":"string","longitude":"string","longitude_":"string","otherStatus":"string","positionStatus":"string","remianOil":0,"rightFrontTirePressure":0,"rightFrontTirePressureStatus":0,"rightFrontTirePressureStatusEffectiveFlag":0,"rightRearTirePressure":0,"rightRearTirePressureStatus":0,"rightRearTirePressureStatusEffectiveFlag":0,"rotateSpeed":0,"signalStrength":"string","totalMileage":0,"tripAAvergeFuleCon":0,"tripAEnduranceMileage":0,"tripBAvergeFuleCon":0,"tripBEnduranceMileage":0,"waterTemperature":"string"}
     * timestamp : 2017-12-28T05:22:39.671Z
     */

    private DataBean data;
    private String timestamp;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class DataBean implements Serializable{
        /**
         * 报警状态
         */
        private String alarmStatus;
        /**
         * 定位时间
         */
        private String dateTime;
        /**
         * 方向
         */
        private String direction;
        /**
         * 车门状态
         */
        private String doorStatus;
        /**
         * 速度
         */
        private String gpsSpeed;
        /**
         * 高度（海拔）
         */
        private String height;
        /**
         * 瞬时油耗
         */
        private double instantaneFuleCon;
        /**
         * 仪表显示模式
         */
        private String instrumentDisplayMode;
        /**
         * 速度
         */
        private int instrumentSpeed;
        /**
         * 车灯状态
         */
        private String lampStatus;
        /**
         * 纬度
         */
        private double latitude;
        /**
         * 转义后纬度
         */
        private String latitude_;
        /**
         * 左前轮胎压
         */
        private int leftFrontTirePressure;
        /**
         * 左前轮胎压状态
         */
        private int leftFrontTirePressureStatus;
        /**
         * 左前轮胎压状态有效位
         */
        private int leftFrontTirePressureStatusEffectiveFlag;
        /**
         * 左后轮胎压
         */
        private int leftRearTirePressure;
        /**
         * 左后轮胎压状态
         */
        private int leftRearTirePressureStatus;
        /**
         * 左后轮胎压状态有效位
         */
        private int leftRearTirePressureStatusEffectiveFlag;
        /**
         * 定位是否有效
         */
        private String locationValid;
        /**
         * 经度
         */
        private double longitude;
        /**
         * 转义后经度
         */
        private String longitude_;
        /**
         * 车其它状态
         */
        private String otherStatus;
        /**
         * 档位状态
         */
        private String positionStatus;
        /**
         * 剩余油耗
         */
        private int remianOil;
        /**
         * 右前轮胎压
         */
        private int rightFrontTirePressure;
        /**
         * 右前轮胎压状态
         */
        private int rightFrontTirePressureStatus;
        /**
         * 右前轮胎压状态有效位
         */
        private int rightFrontTirePressureStatusEffectiveFlag;
        /**
         * 右后轮胎压
         */
        private int rightRearTirePressure;
        /**
         * 右后轮胎压状态
         */
        private int rightRearTirePressureStatus;
        /**
         * 右后轮胎压状态有效位
         */
        private int rightRearTirePressureStatusEffectiveFlag;
        /**
         * 转速
         */
        private int rotateSpeed;
        /**
         * 通讯模块信号强度
         */
        private String signalStrength;
        /**
         * 总里程
         */
        private double totalMileage;
        /**
         * tripA平均油耗
         */
        private double tripAAvergeFuleCon;
        /**
         * tripA续航里程
         */
        private double tripAEnduranceMileage;
        /**
         * tripB平均油耗
         */
        private double tripBAvergeFuleCon;
        /**
         * tripB续航里程
         */
        private double tripBEnduranceMileage;
        /**
         * 冷却液温度(水温)
         */
        private String waterTemperature;

        /**
         * Ev车续航里程
         */
        private int enduranceMileage;
        /**
         * Ev车剩余电量
         */
        private int soc;
        private String chargingStatus;
        private double instantanePower;
        private double averagePower;
        private double avergeFuleCon;
        private String speed;
        private String windowsStatus;
        private String carLockStatus;
        private String carAdress;
        private boolean airConditionerStatus;
        private boolean doorClosed;
        private boolean carLocked;
        /**
         * 瞬时油耗单位
         */
        private String  instantaneFuleConUnit;

        private String modelColor;

        public String getModelColor() {
            return modelColor;
        }

        public void setModelColor( String modelColor ) {
            this.modelColor = modelColor;
        }


        public double getAvergeFuleCon() {
            return avergeFuleCon;
        }

        public void setAvergeFuleCon( double avergeFuleCon ) {
            this.avergeFuleCon = avergeFuleCon;
        }

        public boolean isAirConditionerStatus() {
            return airConditionerStatus;
        }

        public void setAirConditionerStatus( boolean airConditionerStatus ) {
            this.airConditionerStatus = airConditionerStatus;
        }

        public boolean isDoorClosed() {
            return doorClosed;
        }

        public void setDoorClosed( boolean doorClosed ) {
            this.doorClosed = doorClosed;
        }

        public boolean isCarLocked() {
            return carLocked;
        }

        public void setCarLocked( boolean carLocked ) {
            this.carLocked = carLocked;
        }

        public String getInstantaneFuleConUnit() {
            return instantaneFuleConUnit;
        }

        public void setInstantaneFuleConUnit( String instantaneFuleConUnit ) {
            this.instantaneFuleConUnit = instantaneFuleConUnit;
        }

        public void setCarAdress( String carAdress) {
            this.carAdress = carAdress;
        }

        public String getCarAdress() {
            return carAdress;
        }

        public int getEnduranceMileage() {
            return enduranceMileage;
        }

        public void setEnduranceMileage(int enduranceMileage) {
            this.enduranceMileage = enduranceMileage;
        }

        public int getSoc() {
            return soc;
        }

        public void setSoc(int soc) {
            this.soc = soc;
        }

        public String getChargingStatus() {
            return chargingStatus;
        }

        public void setChargingStatus(String chargingStatus) {
            this.chargingStatus = chargingStatus;
        }

        public double getInstantanePower() {
            return instantanePower;
        }

        public void setInstantanePower(double instantanePower) {
            this.instantanePower = instantanePower;
        }

        public double getAveragePower() {
            return averagePower;
        }

        public void setAveragePower(double averagePower) {
            this.averagePower = averagePower;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getWindowsStatus() {
            return windowsStatus;
        }

        public void setWindowsStatus(String windowsStatus) {
            this.windowsStatus = windowsStatus;
        }

        public String getCarLockStatus() {
            return carLockStatus;
        }

        public void setCarLockStatus(String carLockStatus) {
            this.carLockStatus = carLockStatus;
        }

        public String getAlarmStatus() {
            return alarmStatus;
        }

        public void setAlarmStatus(String alarmStatus) {
            this.alarmStatus = alarmStatus;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getDoorStatus() {
            return doorStatus;
        }

        public void setDoorStatus(String doorStatus) {
            this.doorStatus = doorStatus;
        }

        public String getGpsSpeed() {
            return gpsSpeed;
        }

        public void setGpsSpeed(String gpsSpeed) {
            this.gpsSpeed = gpsSpeed;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public double getInstantaneFuleCon() {
            return instantaneFuleCon;
        }

        public void setInstantaneFuleCon(double instantaneFuleCon) {
            this.instantaneFuleCon = instantaneFuleCon;
        }

        public String getInstrumentDisplayMode() {
            return instrumentDisplayMode;
        }

        public void setInstrumentDisplayMode(String instrumentDisplayMode) {
            this.instrumentDisplayMode = instrumentDisplayMode;
        }

        public int getInstrumentSpeed() {
            return instrumentSpeed;
        }

        public void setInstrumentSpeed(int instrumentSpeed) {
            this.instrumentSpeed = instrumentSpeed;
        }

        public String getLampStatus() {
            return lampStatus;
        }

        public void setLampStatus(String lampStatus) {
            this.lampStatus = lampStatus;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getLatitude_() {
            return latitude_;
        }

        public void setLatitude_(String latitude_) {
            this.latitude_ = latitude_;
        }

        public int getLeftFrontTirePressure() {
            return leftFrontTirePressure;
        }

        public void setLeftFrontTirePressure(int leftFrontTirePressure) {
            this.leftFrontTirePressure = leftFrontTirePressure;
        }

        public int getLeftFrontTirePressureStatus() {
            return leftFrontTirePressureStatus;
        }

        public void setLeftFrontTirePressureStatus(int leftFrontTirePressureStatus) {
            this.leftFrontTirePressureStatus = leftFrontTirePressureStatus;
        }

        public int getLeftFrontTirePressureStatusEffectiveFlag() {
            return leftFrontTirePressureStatusEffectiveFlag;
        }

        public void setLeftFrontTirePressureStatusEffectiveFlag(int leftFrontTirePressureStatusEffectiveFlag) {
            this.leftFrontTirePressureStatusEffectiveFlag = leftFrontTirePressureStatusEffectiveFlag;
        }

        public int getLeftRearTirePressure() {
            return leftRearTirePressure;
        }

        public void setLeftRearTirePressure(int leftRearTirePressure) {
            this.leftRearTirePressure = leftRearTirePressure;
        }

        public int getLeftRearTirePressureStatus() {
            return leftRearTirePressureStatus;
        }

        public void setLeftRearTirePressureStatus(int leftRearTirePressureStatus) {
            this.leftRearTirePressureStatus = leftRearTirePressureStatus;
        }

        public int getLeftRearTirePressureStatusEffectiveFlag() {
            return leftRearTirePressureStatusEffectiveFlag;
        }

        public void setLeftRearTirePressureStatusEffectiveFlag(int leftRearTirePressureStatusEffectiveFlag) {
            this.leftRearTirePressureStatusEffectiveFlag = leftRearTirePressureStatusEffectiveFlag;
        }

        public String getLocationValid() {
            return locationValid;
        }

        public void setLocationValid(String locationValid) {
            this.locationValid = locationValid;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getLongitude_() {
            return longitude_;
        }

        public void setLongitude_(String longitude_) {
            this.longitude_ = longitude_;
        }

        public String getOtherStatus() {
            return otherStatus;
        }

        public void setOtherStatus(String otherStatus) {
            this.otherStatus = otherStatus;
        }

        public String getPositionStatus() {
            return positionStatus;
        }

        public void setPositionStatus(String positionStatus) {
            this.positionStatus = positionStatus;
        }

        public int getRemianOil() {
            return remianOil;
        }

        public void setRemianOil(int remianOil) {
            this.remianOil = remianOil;
        }

        public int getRightFrontTirePressure() {
            return rightFrontTirePressure;
        }

        public void setRightFrontTirePressure(int rightFrontTirePressure) {
            this.rightFrontTirePressure = rightFrontTirePressure;
        }

        public int getRightFrontTirePressureStatus() {
            return rightFrontTirePressureStatus;
        }

        public void setRightFrontTirePressureStatus(int rightFrontTirePressureStatus) {
            this.rightFrontTirePressureStatus = rightFrontTirePressureStatus;
        }

        public int getRightFrontTirePressureStatusEffectiveFlag() {
            return rightFrontTirePressureStatusEffectiveFlag;
        }

        public void setRightFrontTirePressureStatusEffectiveFlag(int rightFrontTirePressureStatusEffectiveFlag) {
            this.rightFrontTirePressureStatusEffectiveFlag = rightFrontTirePressureStatusEffectiveFlag;
        }

        public int getRightRearTirePressure() {
            return rightRearTirePressure;
        }

        public void setRightRearTirePressure(int rightRearTirePressure) {
            this.rightRearTirePressure = rightRearTirePressure;
        }

        public int getRightRearTirePressureStatus() {
            return rightRearTirePressureStatus;
        }

        public void setRightRearTirePressureStatus(int rightRearTirePressureStatus) {
            this.rightRearTirePressureStatus = rightRearTirePressureStatus;
        }

        public int getRightRearTirePressureStatusEffectiveFlag() {
            return rightRearTirePressureStatusEffectiveFlag;
        }

        public void setRightRearTirePressureStatusEffectiveFlag(int rightRearTirePressureStatusEffectiveFlag) {
            this.rightRearTirePressureStatusEffectiveFlag = rightRearTirePressureStatusEffectiveFlag;
        }

        public int getRotateSpeed() {
            return rotateSpeed;
        }

        public void setRotateSpeed(int rotateSpeed) {
            this.rotateSpeed = rotateSpeed;
        }

        public String getSignalStrength() {
            return signalStrength;
        }

        public void setSignalStrength(String signalStrength) {
            this.signalStrength = signalStrength;
        }

        public double getTotalMileage() {
            return totalMileage;
        }

        public void setTotalMileage(double totalMileage) {
            this.totalMileage = totalMileage;
        }

        public double getTripAAvergeFuleCon() {
            return tripAAvergeFuleCon;
        }

        public void setTripAAvergeFuleCon(double tripAAvergeFuleCon) {
            this.tripAAvergeFuleCon = tripAAvergeFuleCon;
        }

        public double getTripAEnduranceMileage() {
            return tripAEnduranceMileage;
        }

        public void setTripAEnduranceMileage(double tripAEnduranceMileage) {
            this.tripAEnduranceMileage = tripAEnduranceMileage;
        }

        public double getTripBAvergeFuleCon() {
            return tripBAvergeFuleCon;
        }

        public void setTripBAvergeFuleCon(double tripBAvergeFuleCon) {
            this.tripBAvergeFuleCon = tripBAvergeFuleCon;
        }

        public double getTripBEnduranceMileage() {
            return tripBEnduranceMileage;
        }

        public void setTripBEnduranceMileage(double tripBEnduranceMileage) {
            this.tripBEnduranceMileage = tripBEnduranceMileage;
        }

        public String getWaterTemperature() {
            return waterTemperature;
        }

        public void setWaterTemperature(String waterTemperature) {
            this.waterTemperature = waterTemperature;
        }
    }
}
