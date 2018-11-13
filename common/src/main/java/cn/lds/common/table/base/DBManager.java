package cn.lds.common.table.base;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.exceptions.RealmMigrationNeededException;


/**
 * Created by leadingsoft on 2017/11/30.
 */

public class DBManager {
    private static DBManager mInstance;//单利引用
    private Context mContext;
    RealmConfiguration config;


    public static DBManager getInstance() {
        DBManager inst = mInstance;
        if (inst == null) {
            synchronized (DBManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new DBManager();
                    mInstance = inst;
                }
            }
        }
        return inst;
    }


    /*
    *初始化 realm，application
    * */
    public void initDB(Context mContext, String dataName, long schemaVersion, RealmMigration migration) {
        this.mContext = mContext;
        Realm.init(mContext);//数据库初始化
        if (null == config || config.getSchemaVersion() < schemaVersion) {//如果版本升级，更新config
            try {
                config = new RealmConfiguration.Builder()
                        .name(dataName)
                        .schemaVersion(schemaVersion) // Must be bumped when the schema changes 如果版本变更，必须增加版本号
                        .migration(migration) // Migration to run instead of throwing an exception
                        .build();
            } catch (RealmMigrationNeededException exception) {
                exception.printStackTrace();
            }
        }
    }

    public Realm getRealm() {
        return Realm.getInstance(config);
    }
}

