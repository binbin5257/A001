package cn.lds.common.table;

import java.io.Serializable;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 电车配置首页定制表
 * Created by sibinbin on 18-4-24.
 */

public class HomeCustomTable extends RealmObject implements Serializable {

//    @PrimaryKey
//    private String id = UUID.randomUUID().toString();

    private String name;

    private String loginId;

    /**
     * 0,首页展示;
     * 1,跟多状态；
     */
    private int group;

//    public String getId() {
//        return id;
//    }
//
//    public void setId( String id ) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId( String loginId ) {
        this.loginId = loginId;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup( int group ) {
        this.group = group;
    }
}
