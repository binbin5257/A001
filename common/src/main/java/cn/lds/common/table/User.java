package cn.lds.common.table;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 用户表
 * Created by sibinbin on 18-4-24.
 */

public class User extends RealmObject {

    /**
     * 用户名
     */
    @PrimaryKey
    private String loginId;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否是最后一次登录用户  0是；1不是
     */
    private int lastLogin;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId( String loginId ) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public int getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin( int lastLogin ) {
        this.lastLogin = lastLogin;
    }
}
