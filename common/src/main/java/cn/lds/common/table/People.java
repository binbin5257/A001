package cn.lds.common.table;

import cn.lds.common.utils.json.GsonImplHelp;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by leadingsoft on 17/12/4.
 */

public class People extends RealmObject {

    @PrimaryKey
    private long id;
    private String name;
    private RealmList<Dog> dogs;
    private String favorateDog;

    public String getFavorateDog() {
        return favorateDog;
    }

    public void setFavorateDog(String favorateDog) {
        this.favorateDog = favorateDog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(RealmList<Dog> dogs) {
        this.dogs = dogs;
    }

    @Override
    public String toString() {
        return GsonImplHelp.get().toJson(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
