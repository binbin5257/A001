package cn.lds.common.table;

import java.io.Serializable;

import cn.lds.common.utils.json.GsonImplHelp;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * poiid 收藏夹表，
 */

public class PoiIdTable extends RealmObject implements Serializable {


    public PoiIdTable() {
    }

    /**
     * poiId
     */
    @PrimaryKey
    private String poiId;

    public PoiIdTable(String poiId) {
        this.poiId = poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getPoiId() {
        return poiId;
    }

    @Override
    public String toString() {
        return GsonImplHelp.get().toJson(this);
    }

}
