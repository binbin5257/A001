package cn.lds.ui;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import cn.lds.R;
import cn.lds.ui.adapter.MemberAdapter;
import cn.lds.common.base.BaseActivity;
import cn.lds.common.utils.ToolsHelper;
import cn.lds.databinding.ActivityDbTestBinding;
import cn.lds.common.table.Dog;
import cn.lds.common.table.People;
import cn.lds.common.table.base.DBManager;
import cn.lds.widget.dialog.CenterListDialog;
import cn.lds.widget.dialog.callback.OnDialogOnItemClickListener;
import io.realm.Realm;
import io.realm.RealmList;
public class DbTestActivity extends BaseActivity implements View.OnClickListener {
    ActivityDbTestBinding mBinding;
    Realm realm;
    List<People> list;
    MemberAdapter memberAdapter;
    CenterListDialog centerListDialog;
    private int mPostion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_db_test);
        realm = DBManager.getInstance().getRealm();
        initView();
        initListener();
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initView() {
        mBinding.addprime.setOnClickListener(this);
        mBinding.mulsend.setOnClickListener(this);
        Typeface typeFace5 = Typeface.createFromAsset(getAssets(), "DINCond-Bold.otf");
        mBinding.mulsend.setTypeface(typeFace5);

        list = realm.where(People.class).contains("name", "xuqm").findAll();
        memberAdapter = new MemberAdapter(mContext, list);
        mBinding.memberList.setAdapter(memberAdapter);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("删除");
        strings.add("更新");
        strings.add("删除全部");
        centerListDialog = new CenterListDialog(this, mContext, strings).setOnDialogOnItemClickListener(new OnDialogOnItemClickListener() {
            @Override
            public void onDialogItemClick(Dialog dialog, int position) {
                switch (position) {
                    case 0:
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                list.get(mPostion).deleteFromRealm();
                            }
                        });
                        updateAdapter();
                        break;
                    case 1:
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                People people = list.get(mPostion);
                                if (null != people) {
                                    people.setFavorateDog("ppp");
                                    RealmList<Dog> dogs = new RealmList<>();
                                    Dog dog = new Dog();
                                    dog.setAge(1);
                                    dog.setName("ppp");
                                    people.setDogs(dogs);
                                }
                            }
                        });
                        updateAdapter();
                        break;
                    case 2:
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.delete(People.class);
                            }
                        });
                        updateAdapter();
                        break;
                }
                centerListDialog.dismiss();
            }
        });

        mBinding.memberList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                centerListDialog.show();
                mPostion = i;
                return true;
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.mulsend == id) {
            delAll();
            addprime();
            updateAdapter();
        } else if (R.id.addprime == id) {
            addprime();
            updateAdapter();
        }

    }

    private void updateAdapter() {
        list = realm.where(People.class).contains("name", "xuqm").findAll();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        memberAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                list = realm.where(People.class).contains("name", "xuqm").findAll();
//            }
//        }, new Realm.Transaction.OnSuccess() {
//            @Override
//            public void onSuccess() {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                memberAdapter.notifyDataSetChanged();
//                            }
//                        });
//                    }
//                }).start();
//            }
//        }, new Realm.Transaction.OnError() {
//            @Override
//            public void onError(Throwable error) {
//
//            }
//        });

    }


    private void delAll() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(People.class);
            }
        });
    }


    private void addprime() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < 20; i++) {
                    People people = new People();
                    people.setId(list.size() + i);
                    people.setName("xuqm" + list.size() + i);
                    realm.copyToRealmOrUpdate(people);
                }
            }
        });
        ToolsHelper.showInfo(mContext, "增加20条数据");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
