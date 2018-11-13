package cn.lds.common.base;

import java.util.List;

/**
 * 权限接口
 * Created by sibinbin on 17-12-14.
 */

public interface IPermission {

    void onGranted();

    void onDenied(List<String> deniedPermissions);
}
