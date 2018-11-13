package cn.lds.common.data;

import java.io.Serializable;

import cn.lds.common.data.base.BaseModel;

/**
 * Created by sibinbin on 17-12-15.
 */

public class UserInfoModel extends BaseModel implements Serializable {

    /**
     * data : {"address":"string","avatarFileRecordNo":"string","firstEmLinkerMobile":"string","firstEmLinkerName":"string","gender":"string","mobile":"string","name":"string","nickname":"string","secEmLinkerMobile":"string","secEmLinkerName":"string"}
     * timestamp : 2017-12-15T02:06:26.365Z
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
         * address : string
         * avatarFileRecordNo : string
         * firstEmLinkerMobile : string
         * firstEmLinkerName : string
         * gender : string
         * mobile : string
         * name : string
         * nickname : string
         * secEmLinkerMobile : string
         * secEmLinkerName : string
         */

        private String address;
        private String avatarFileRecordNo;
        private String firstEmLinkerMobile;
        private String firstEmLinkerName;
        private String gender;
        private String mobile;
        private String name;
        private String nickname;
        private String secEmLinkerMobile;
        private String secEmLinkerName;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAvatarFileRecordNo() {
            return avatarFileRecordNo;
        }

        public void setAvatarFileRecordNo(String avatarFileRecordNo) {
            this.avatarFileRecordNo = avatarFileRecordNo;
        }

        public String getFirstEmLinkerMobile() {
            return firstEmLinkerMobile;
        }

        public void setFirstEmLinkerMobile(String firstEmLinkerMobile) {
            this.firstEmLinkerMobile = firstEmLinkerMobile;
        }

        public String getFirstEmLinkerName() {
            return firstEmLinkerName;
        }

        public void setFirstEmLinkerName(String firstEmLinkerName) {
            this.firstEmLinkerName = firstEmLinkerName;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getSecEmLinkerMobile() {
            return secEmLinkerMobile;
        }

        public void setSecEmLinkerMobile(String secEmLinkerMobile) {
            this.secEmLinkerMobile = secEmLinkerMobile;
        }

        public String getSecEmLinkerName() {
            return secEmLinkerName;
        }

        public void setSecEmLinkerName(String secEmLinkerName) {
            this.secEmLinkerName = secEmLinkerName;
        }
    }
}
