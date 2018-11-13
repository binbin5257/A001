package cn.lds.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;

import cn.lds.widget.R;
import cn.lds.widget.dialog.annotation.ClickPosition;
import cn.lds.widget.dialog.base.Bottom2TopDialog;

/**
 * 获取图片从相机或者相册，在屏幕底部弹出(仿照ios鼎信效果)
 * Created by leading123 on 17-12-4.
 */

public class CameraOrAlbumBottomDialog extends Bottom2TopDialog<CameraOrAlbumBottomDialog> {

    private Activity activity;
    public CameraOrAlbumBottomDialog(Context context) {
        super(context,R.style.MyDialogStyle);
        this.activity = (Activity) context;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_camera_album;
    }

    @Override
    public void onCreateData() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setOnCilckListener(R.id.take_photo,R.id.take_abml,R.id.cancel);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onClick(View v, int id) {
        if (id == R.id.take_photo) {
            onDialogClickListener(ClickPosition.TAKE_PHOTO);
        } else if (id == R.id.take_abml) {
            onDialogClickListener(ClickPosition.TAKE_ALBUM);
        } else if(id == R.id.cancel){
            onDialogClickListener(ClickPosition.CANCEL);
        }
    }

}
