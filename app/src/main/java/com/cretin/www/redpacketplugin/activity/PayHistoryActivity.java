package com.cretin.www.redpacketplugin.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.model.PayDetailModel;
import com.cretin.www.redpacketplugin.utils.AlertDialog;
import com.cretin.www.redpacketplugin.utils.ItemButtomDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PayHistoryActivity extends BaseActivity {

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
    private List<PayDetailModel> list;
    private ListAdapter adapter;
    private int currPage = 0;
    private ClipboardManager cmb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_history);
        ButterKnife.bind(this);

        tvTitleInfo.setText("历史续费记录");

        initData();
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
                PayDetailModel payDetailModel = list.get(position);
                if ( payDetailModel.getState() == 1 ) {
                    //审核中
                    showCusDialog(payDetailModel.getStateValue() + "\n可联系mxnzp_cretin@163.com加速审核", true);
                } else if ( payDetailModel.getState() == 2 ) {
                    //已处理
                    showCusDialog(payDetailModel.getStateValue() + "，感谢使用", false);
                } else {
                    //有错误
                    showCusDialog(payDetailModel.getStateValue() + "，请联系mxnzp_cretin@163.com确认", true);
                }
            }
        });
        showDialog();
        addData(currPage);
    }

    private void showCusDialog(String msg, boolean show) {
        final AlertDialog dialog = new AlertDialog(this);
        dialog.setOnClickListener(new AlertDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClickListener(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setMessage(msg);
        if ( show ) {
            dialog.setLeftButton("复制邮箱");
            dialog.setOnNegativeListener(new AlertDialog.OnNegativeClickListener() {
                @Override
                public void onNegativeClickListener(View v) {
                    cmb = ( ClipboardManager ) getSystemService(Context.CLIPBOARD_SERVICE);
                    String sina = "mxnzp_cretin@163.com";
                    cmb.setText(sina);
                    showToast("邮箱已复制，快去申请加速审核吧！");
                }
            });
        }
    }

    private void addData(final int page) {
        currPage = page + 1;
        BmobQuery<PayDetailModel> query = new BmobQuery<PayDetailModel>();
        query.include("payTypeModel");
        query.setLimit(10);
        query.setSkip(page);
        query.order("-createdAt");
        query.findObjects(new FindListener<PayDetailModel>() {
            @Override
            public void done(List<PayDetailModel> o, BmobException e) {
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


    @OnClick( R.id.tv_back )
    public void onViewClicked() {
        finish();
    }

    class ListAdapter extends BaseQuickAdapter<PayDetailModel, BaseViewHolder> {

        public ListAdapter(List list) {
            super(R.layout.item_recycler_pay_history, list);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final PayDetailModel item) {
            helper.setText(R.id.tv_time, format(item.getCreatedAt()));
            if ( item.getPayTypeModel() != null )
                helper.setText(R.id.tv_des, item.getPayTypeModel().getComboTypeValue());
            helper.setText(R.id.tv_state, item.getStateValue());
            if ( item.getState() == 1 ) {
                helper.setTextColor(R.id.tv_state, getResources().getColor(R.color.green_status));
            } else if ( item.getState() == 2 ) {
                helper.setTextColor(R.id.tv_state, getResources().getColor(R.color.font_black4));
            } else {
                helper.setTextColor(R.id.tv_state, getResources().getColor(R.color.red_money_out));
            }
        }
    }

    private String format(String time) {
        return time.replace(" ", "\n");
    }
}
