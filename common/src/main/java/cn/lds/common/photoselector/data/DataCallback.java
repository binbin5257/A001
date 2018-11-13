package cn.lds.common.photoselector.data;

import java.util.ArrayList;

import cn.lds.common.photoselector.entity.Folder;


/**
 * Created by dmcBig on 2017/7/3.
 */

public interface DataCallback {


     void onData(ArrayList<Folder> list);

}
