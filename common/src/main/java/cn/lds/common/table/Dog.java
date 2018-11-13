package cn.lds.common.table;

import cn.lds.common.utils.json.GsonImplHelp;
import io.realm.RealmObject;

/**
 * Created by leadingsoft on 17/12/4.
 */

public class Dog extends RealmObject {
    private String name;
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return GsonImplHelp.get().toJson(this);
    }
}
