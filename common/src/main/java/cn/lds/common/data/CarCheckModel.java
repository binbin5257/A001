package cn.lds.common.data;

import java.util.List;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by sibinbin on 18-3-19.
 */

public class CarCheckModel extends BaseModel {

    private long timestamp;
    private DataBean data;
    public class DataBean{
        private int bodyControl;
        private int braking;
        private String dateTime;
        private int power;
        private int score;
        private int security;
        private int steering;

        public int getBodyControl() {
            return bodyControl;
        }

        public void setBodyControl( int bodyControl ) {
            this.bodyControl = bodyControl;
        }

        public int getBraking() {
            return braking;
        }

        public void setBraking( int braking ) {
            this.braking = braking;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime( String dateTime ) {
            this.dateTime = dateTime;
        }

        public int getPower() {
            return power;
        }

        public void setPower( int power ) {
            this.power = power;
        }

        public int getScore() {
            return score;
        }

        public void setScore( int score ) {
            this.score = score;
        }

        public int getSecurity() {
            return security;
        }

        public void setSecurity( int security ) {
            this.security = security;
        }

        public int getSteering() {
            return steering;
        }

        public void setSteering( int steering ) {
            this.steering = steering;
        }
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    public DataBean getData() {
        return data;
    }


    public void setData( DataBean data ) {
        this.data = data;
    }
}
