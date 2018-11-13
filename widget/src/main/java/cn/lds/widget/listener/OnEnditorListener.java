package cn.lds.widget.listener;

import android.widget.EditText;

/**
 * Created by sibinbin on 17-12-14.
 */

public interface OnEnditorListener {

    void startEditor(String content, EditText contentEt);

    void endEditor(String content, EditText contentEt);

}
