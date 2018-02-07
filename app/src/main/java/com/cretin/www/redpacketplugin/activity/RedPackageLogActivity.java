package com.cretin.www.redpacketplugin.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.model.RedPackageInfoModel;
import com.cretin.www.redpacketplugin.utils.ItemButtomDecoration;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class RedPackageLogActivity extends BaseActivity {

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
    @BindView( R.id.recyclerview )
    RecyclerView recyclerview;
    @BindView( R.id.swip_refresh )
    SwipeRefreshLayout swipRefresh;
    private List<RedPackageInfoModel> list;
    private ListAdapter adapter;
    private int currPage = 0;
    private CusUser cusUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_package_log);
        ButterKnife.bind(this);

        cusUser = KV.get(LocalStorageKeys.USER_INFO);
        tvTitleInfo.setText("红包记录");

        initData();
    }

    private void addData(final int page) {
        currPage = page + 1;

        BmobQuery<RedPackageInfoModel> query = new BmobQuery<RedPackageInfoModel>();
        query.addWhereEqualTo("authorUserId", cusUser.getObjectId());
        query.addWhereGreaterThan("money", 0);
        query.setLimit(10);
        query.setSkip(page * 10);
        query.order("-createdAt");
        query.findObjects(new FindListener<RedPackageInfoModel>() {
            @Override
            public void done(List<RedPackageInfoModel> o, BmobException e) {
                swipRefresh.setRefreshing(false);
                stopDialog();
                if ( e == null ) {
                    if ( page == 0 ) {
                        list.clear();
                    }
                    list.addAll(o);
                    adapter.notifyDataSetChanged();

                    if ( o.isEmpty() ) {
                        adapter.loadMoreEnd();
                    } else {
                        if ( o.size() < 10 ) {
                            adapter.loadMoreEnd();
                        } else {
                            adapter.loadMoreComplete();
                        }
                    }
                } else {
                    showToast("请求超时,请稍后再试");
                }
            }
        });
    }

    private void initData() {
        list = new ArrayList<>();
        adapter = new ListAdapter(list);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new ItemButtomDecoration(this, 1));
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.isFirstOnly(true);
        adapter.setNotDoAnimationCount(3);
        swipRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addData(0);
            }
        });
        recyclerview.setAdapter(adapter);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                addData(currPage);
            }
        }, recyclerview);
        adapter.setEmptyView(R.layout.empty_view);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        showDialog();
        addData(currPage);
    }

    @OnClick( R.id.tv_back )
    public void onViewClicked() {
        finish();
    }

    class ListAdapter extends BaseQuickAdapter<RedPackageInfoModel, BaseViewHolder> {

        public ListAdapter(List list) {
            super(R.layout.item_red_package_log, list);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final RedPackageInfoModel item) {
            String title = "";
            if ( item.getType() == 0 ) {
                //私信红包
                title = "私信红包(" + item.getOrigin() + ")";
            } else if ( item.getType() == 1 ) {
                //微信群普通红包
                title = "普通群红包(" + item.getOrigin() + ")";
            } else if ( item.getType() == 2 ) {
                //微信群手气
                title = "拼手气群红包(" + item.getOrigin() + ")";
            }
            helper.setText(R.id.tv_title, title);

            helper.setText(R.id.tv_time, item.getCreatedAt());

            helper.setText(R.id.tv_money, "+" + item.getMoney());
        }
    }
}
