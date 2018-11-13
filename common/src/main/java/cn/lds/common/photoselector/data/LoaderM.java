package cn.lds.common.photoselector.data;

import java.util.ArrayList;

import cn.lds.common.photoselector.entity.Folder;


/**
 * Created by dmcBig on 2017/7/20.
 */

public class LoaderM {

    public String getParent(String path) {
        String sp[]=path.split("/");
        return sp[sp.length-2];
    }

    public int hasDir(ArrayList<Folder> folders, String dirName){
        for(int i = 0; i< folders.size(); i++){
            Folder folder = folders.get(i);
            if( folder.name.equals(dirName)) {
                return i;
            }
        }
        return -1;
    }


}
