package com.cretin.www.redpacketplugin.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.model.UserExplainModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class UserExplainActivity extends BaseActivity {

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
    private List<UserExplainModel> list;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_explain);
        ButterKnife.bind(this);

        tvTitleInfo.setText("使用说明");

        initData();
    }

    @OnClick( R.id.tv_back )
    public void onViewClicked() {
        finish();
    }

    private void initData() {
        list = new ArrayList<>();
        adapter = new ListAdapter(list);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
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
                addData();
            }
        });
        recyclerview.setAdapter(adapter);
        showDialog();
        addData();
    }

    private void addData() {
        BmobQuery<UserExplainModel> query = new BmobQuery<UserExplainModel>();
        query.setLimit(50);
        query.order("step");
        query.findObjects(new FindListener<UserExplainModel>() {
            @Override
            public void done(List<UserExplainModel> o, BmobException e) {
                swipRefresh.setRefreshing(false);
                stopDialog();
                if ( e == null ) {
                    list.clear();
                    list.addAll(o);
                    adapter.notifyDataSetChanged();
                    adapter.loadMoreEnd();
                } else {
                    showToast("请求超时,请稍后再试");
                }
            }
        });
    }

    class ListAdapter extends BaseQuickAdapter<UserExplainModel, BaseViewHolder> {

        public ListAdapter(List list) {
            super(R.layout.item_recycler_user_explain, list);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final UserExplainModel item) {
            final ImageView iv = helper.getView(R.id.iv_pic);
            if ( TextUtils.isEmpty(item.getPicUrl()) ) {
                iv.setVisibility(View.GONE);
            } else {
                iv.setVisibility(View.VISIBLE);
                Picasso.with(UserExplainActivity.this).load(item.getPicUrl()).into(iv);
                ViewTreeObserver vto = iv.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi( Build.VERSION_CODES.KITKAT )
                    @Override
                    public void onGlobalLayout() {
                        iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        int width = iv.getMeasuredWidth();
                        int height = ( int ) ((( float ) item.getHeight() * width) / item.getWidth());
                        ViewGroup.LayoutParams layoutParams =
                                iv.getLayoutParams();
                        layoutParams.height = height;
                        layoutParams.width = width;
                        iv.setLayoutParams(layoutParams);
                    }
                });
            }
            String res = item.getDesc().replaceAll("\\\\n", "\n");
            helper.setText(R.id.tv_des, res);
        }
    }
}
