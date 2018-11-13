package cn.lds.common.table;

import java.util.List;

import cn.lds.common.table.base.DBManager;
import cn.lds.common.utils.CacheHelper;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sibinbin on 18-4-24.
 */

public class TableHelper {

    private static TableHelper instance;
    private List<HomeCustomTable> tables;
    private ControlCarFailtureHistoryTable historyTable;
    private TableHelper(){}
    public static TableHelper getInstance(){
        if(instance == null){
            synchronized (TableHelper.class){
                if(instance == null){
                    instance = new TableHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 获取首页配置项目列表
     * @param loginId 用户id
     * @return
     */
    public List<HomeCustomTable> getHomeCustomByLoginId( final String loginId){
        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {

            @Override
            public void execute( Realm realm ) {
                RealmResults<HomeCustomTable> homeCustomTables = realm.where(HomeCustomTable.class).equalTo("loginId", loginId).findAll();
                tables = realm.copyFromRealm(homeCustomTables);

            }
        });

        return tables;

    }

    /**
     * 创建首页定制项目
     * @param loginId 用户id
     * @return
     */
    public void postHomeCustomByLoginId( final String loginId, final List<String> customNameList){
        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute( Realm realm ) {
                RealmResults<HomeCustomTable> homeCustomTables = realm.where(HomeCustomTable.class).equalTo("loginId", loginId).findAll();
                if(homeCustomTables != null && homeCustomTables.size() == 0){
                    for (int i = 0; i < customNameList.size(); i++) {
                        HomeCustomTable homeCustomTable = realm.createObject(HomeCustomTable.class);
                        homeCustomTable.setName(customNameList.get(i));
                        homeCustomTable.setLoginId(loginId);
                        if (i < 2) {
                            homeCustomTable.setGroup(0); //首页显示项目
                        }else{
                            homeCustomTable.setGroup(1); //其他更多项目
                        }
                    }
                }

            }
        });


    }
    /**
     * 创建首页定制项目
     * @param loginId 用户id
     * @return
     */
    public void postHomeCustomListByLoginId( final String loginId, final List<HomeCustomTable> customNameList){
        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute( Realm realm ) {
                RealmResults<HomeCustomTable> homeCustomTables = realm.where(HomeCustomTable.class).equalTo("loginId", loginId).findAll();
                if(homeCustomTables != null && homeCustomTables.size() > 0){
                    homeCustomTables.deleteAllFromRealm();
                }
                for (int i = 0; i < customNameList.size(); i++) {
                   realm.copyToRealm(customNameList.get(i));
                }
            }
        });


    }

    /**
     * 获取首页配置项目列表
     * @param loginId
     * @return
     */
    public List<HomeCustomTable> getHomeCustomFromGropByLoginId( final String loginId, final int group){
        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {

            @Override
            public void execute( Realm realm ) {
                RealmResults<HomeCustomTable> homeCustomTables = realm.where(HomeCustomTable.class).equalTo("loginId", loginId).equalTo("group",group).findAll();
                tables = realm.copyFromRealm(homeCustomTables);

            }
        });
        return tables;
    }

    /**
     * 获取控车失败历史记录第一条
     * @param vin
     * @return
     */
    public ControlCarFailtureHistoryTable findFirstControlCarFailHistoryByVin( final String vin){
        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute( Realm realm ) {
                RealmResults<ControlCarFailtureHistoryTable> tables = realm.where(ControlCarFailtureHistoryTable.class).equalTo("vin", vin).findAll();
                if(tables != null && tables.size() > 0){
                    historyTable = tables.sort("time", Sort.DESCENDING).first();
                }else{
                    historyTable = null;
                }
            }
        });
        return historyTable;
    }

    /**
     * 数据库增加一条控车失败记录
     * @return
     */
    public void insertControlCarFailHistory( final ControlCarFailtureHistoryTable table){
        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute( Realm realm ) {
                ControlCarFailtureHistoryTable historyTable = realm.where(ControlCarFailtureHistoryTable.class).equalTo("vin", table.getVin()).equalTo("type",table.getType()).findFirst();
                if(historyTable != null){
                    historyTable.deleteFromRealm();
                }
                realm.copyToRealm(table);
            }
        });

    }
    /**
     * 数据库删除一条控车失败记录
     */
    public void deleteOneControlCarFailHistory( final String vin, final String type){
        DBManager.getInstance().getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute( Realm realm ) {
                ControlCarFailtureHistoryTable historyTable = realm.where(ControlCarFailtureHistoryTable.class).equalTo("vin", vin).equalTo("type",type).findFirst();
                if(historyTable != null){
                    historyTable.deleteFromRealm();
                }
            }
        });
    }



}
