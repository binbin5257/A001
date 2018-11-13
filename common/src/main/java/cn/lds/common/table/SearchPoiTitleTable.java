package cn.lds.common.table;

import java.io.Serializable;

import cn.lds.common.utils.json.GsonImplHelp;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * poi搜索记录
 */

public class SearchPoiTitleTable extends RealmObject implements Serializable {


    public SearchPoiTitleTable() {
    }

    /**
     * title
     */
    @PrimaryKey
    private String title;
    /**
     * snippet 街区
     */
    private String snippet;
    /**
     * 存储事件
     */
    private long time;

    /**
     * loginid
     * @return
     */
    private String loginId;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId( String loginId ) {
        this.loginId = loginId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public SearchPoiTitleTable(String title, String snippet, long l,String loginId) {
        this.loginId = loginId;
        this.title = title;
        this.snippet = snippet;
        this.time = l;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return GsonImplHelp.get().toJson(this);
    }

}
