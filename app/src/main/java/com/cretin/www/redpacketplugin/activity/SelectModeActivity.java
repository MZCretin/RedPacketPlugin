package com.cretin.www.redpacketplugin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.model.ModeListModel;
import com.cretin.www.redpacketplugin.model.UserInfoModel;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class SelectModeActivity extends BaseActivity {
    @BindView( R.id.recyclerview )
    RecyclerView recyclerview;
    @BindView( R.id.tv_back )
    TextView tvBack;
    @BindView( R.id.tv_title_info )
    TextView tvTitleInfo;
    @BindView( R.id.tv_right )
    TextView tvRight;
    @BindView( R.id.iv_right )
    ImageView ivRight;
    @BindView( R.id.line_divider )
    View lineDivider;
    @BindView( R.id.ll_main_title )
    LinearLayout llMainTitle;
    private List<ModeListModel> list;
    //模式选择 0 未选择 1 自动抢 单聊 2 自动抢 群聊 3 自动抢 all 4 仅打开红包页面
    private String[] titles = {"全自动抢红包（私聊）", "全自动抢红包（群聊）", "全自动抢红包（私聊和群聊）",
            "仅打开红包页面（手动抢）"};
    private String[] des = {"请停留在聊天列表页面，一旦有私信红包，自动抢红包后返回首页，请不要手动干预"
            , "请停留在聊天列表页面，一旦有群红包，自动抢红包后返回首页，请不要手动干预"
            , "请停留在聊天列表页面，一旦有私信和群红包，自动抢红包后返回首页，请不要手动干预"
            , "当发现有新红包的时候，只会打开红包所在的页面，用户自己手动抢红包"};
    private Integer[] type = {1, 2, 3, 4};
    private Adapter adapter;

    private int currSelect = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        ButterKnife.bind(this);
        tvTitleInfo.setText("模式选择");
        initData();
    }


    private void initData() {
        list = new ArrayList<>();

        for ( int i = 0; i < titles.length; i++ ) {
            //组装数据
            ModeListModel modeListModel = new ModeListModel();
            modeListModel.setDes(des[i]);
            modeListModel.setTitle(titles[i]);
            modeListModel.setTypeValue(type[i]);
            list.add(modeListModel);
        }

        getData();

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(list);
        adapter.setNotDoAnimationCount(1);
        recyclerview.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                setInfo(position);
            }
        });
    }

    //获取数据
    private void getData() {
        showDialog();
        CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
        if ( cusUser != null ) {
            BmobQuery<UserInfoModel> query = new BmobQuery<>();
            query.getObject(cusUser.getUserInfoModel().getObjectId(), new QueryListener<UserInfoModel>() {
                @Override
                public void done(UserInfoModel object, BmobException e) {
                    stopDialog();
                    if ( e == null ) {
                        showData(object);
                    } else {
                        showToast("数据获取失败");
                    }
                }
            });
        }else{
            stopDialog();
        }
    }

    //展示数据
    private void showData(UserInfoModel object) {
        for ( int i = 0; i < list.size(); i++ ) {
            ModeListModel m = list.get(i);
            if ( m.getTypeValue() == object.getModeState() ) {
                currSelect = i;
                adapter.notifyDataSetChanged();
            }
        }
    }

    //设置数据
    private void setInfo(final int position) {
        showDialog();
        CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
        final ModeListModel modeListModel = list.get(position);
        if ( cusUser != null ) {
            UserInfoModel userInfoModel = cusUser.getUserInfoModel();
            if ( userInfoModel != null ) {

                BmobQuery<UserInfoModel> query = new BmobQuery<UserInfoModel>();
                query.getObject(userInfoModel.getObjectId(), new QueryListener<UserInfoModel>() {

                    @Override
                    public void done(final UserInfoModel object, BmobException e) {
                        if ( e == null ) {
                            object.setModeState(modeListModel.getTypeValue());
                            object.setModeStateValue(modeListModel.getTitle());
                            object.update(object.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    stopDialog();
                                    if ( e == null ) {
                                        currSelect = position;
                                        adapter.notifyDataSetChanged();
                                        CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
                                        if ( cusUser != null ) {
                                            cusUser.setUserInfoModel(object);
                                            KV.put(LocalStorageKeys.USER_INFO, cusUser);
                                        }
                                    } else {
                                        showToast("设置失败");
                                    }
                                }
                            });
                        } else {
                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
            }
        } else {
            stopDialog();
            Toast.makeText(this, "请求超时,请稍后再试", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick( R.id.tv_back )
    public void onViewClicked() {
        finish();
    }

    class Adapter extends BaseQuickAdapter<ModeListModel, BaseViewHolder> {

        public Adapter(@Nullable List<ModeListModel> data) {
            super(R.layout.item_select_mode_recycler, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ModeListModel item) {
            helper.setText(R.id.tv_title, item.getTitle());
            helper.setText(R.id.tv_des, item.getDes());
            if ( currSelect == helper.getPosition() ) {
                helper.setImageResource(R.id.iv_select, R.mipmap.select_done);
            } else {
                helper.setImageResource(R.id.iv_select, R.mipmap.select);
            }
        }
    }
}
