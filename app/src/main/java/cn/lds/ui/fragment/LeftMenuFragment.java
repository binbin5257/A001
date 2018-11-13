package cn.lds.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.lds.R;
import cn.lds.common.api.HttpApiKey;
import cn.lds.common.api.ModuleUrls;
import cn.lds.common.data.UserInfoModel;
import cn.lds.common.http.HttpRequestEvent;
import cn.lds.common.http.HttpResult;
import cn.lds.common.manager.AccountManager;
import cn.lds.common.utils.LogHelper;
import cn.lds.common.utils.json.GsonImplHelp;
import cn.lds.ui.CarListActivity;
import cn.lds.ui.ControlHistoryListActivity;
import cn.lds.ui.MapActivity;
import cn.lds.ui.ProfileActivity;
import cn.lds.ui.SettingActivity;
import cn.lds.ui.TripListActivity;
import cn.lds.ui.VoiceListActivity;
import cn.lds.ui.adapter.LeftMenuAdapter;


/**
 * 左侧滑菜单碎片
 * Created by leading123
 */

public class LeftMenuFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView menuLv;
    private Context mContext;
    private LeftMenuAdapter adapter;
    private UserInfoModel model;
    private TextView nameTv;
    private SimpleDraweeView avatarIv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_left_menu, null);
        menuLv = view.findViewById(R.id.menu_list);
        avatarIv = view.findViewById(R.id.bg_avatar);
        nameTv = view.findViewById(R.id.name);
        LinearLayout persionInfoLayout = view.findViewById(R.id.person_info);
        persionInfoLayout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMenu();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
            AccountManager.getInstance().getPesionInfo();
        } catch (Exception e) {
            LogHelper.e("注销EvenBus", e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            LogHelper.e("注销EvenBus", e);
        }
    }

    private void initMenu() {
        adapter = new LeftMenuAdapter(getActivity());
        menuLv.setAdapter(adapter);
        menuLv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ToastUtil.showToast(getActivity(), adapter.getMenuListData().get(position).getName());
        switch (position) {
            case 0:
                getActivity().startActivity(new Intent(mContext, CarListActivity.class));
                break;
            case 1:
                getActivity().startActivity(new Intent(mContext, TripListActivity.class));
                break;
            case 2:
                getActivity().startActivity(new Intent(mContext, ControlHistoryListActivity.class));
                break;
            case 3:
                getActivity().startActivity(new Intent(mContext, MapActivity.class));
                break;
            case 4:
                getActivity().startActivity(new Intent(mContext, VoiceListActivity.class));
                break;
            case 6:
                getActivity().startActivity(new Intent(mContext, SettingActivity.class));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_info:
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("USERINFO", model.getData());
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void persionInfo(HttpRequestEvent event) {
        HttpResult httpResult = event.getResult();
        String apiNo = httpResult.getApiNo();
        if (!HttpApiKey.getPersonalInfo.equals(apiNo))
            return;
        model = GsonImplHelp.get().toObject(httpResult.getResult(), UserInfoModel.class);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initPersonInfo(model);
            }
        });
    }

    /**
     * 初始化个人信息
     *
     * @param model
     */
    private void initPersonInfo(UserInfoModel model) {
        UserInfoModel.DataBean bean = model.getData();
        if (!TextUtils.isEmpty(bean.getAvatarFileRecordNo())) {
            //根据id加载图片
            avatarIv.setImageURI(ModuleUrls.displayFile + bean.getAvatarFileRecordNo());
        } else {
            avatarIv.setImageResource(R.drawable.bg_headavatar);
        }
        if (!TextUtils.isEmpty(bean.getName())) {
            nameTv.setText(bean.getName());
        } else {
            if (!TextUtils.isEmpty(bean.getMobile())) {
                nameTv.setText(bean.getMobile());
            } else {
                nameTv.setText("");
            }
        }
    }

}
