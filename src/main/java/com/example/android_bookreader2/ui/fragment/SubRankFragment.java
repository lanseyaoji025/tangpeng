package com.example.android_bookreader2.ui.fragment;

import android.os.Bundle;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.base.BaseRVFragment;
import com.example.android_bookreader2.bean.BooksByCats;
import com.example.android_bookreader2.component.AppComponent;
import com.example.android_bookreader2.component.DaggerFindComponent;
import com.example.android_bookreader2.ui.activity.BookDetailActivity;
import com.example.android_bookreader2.ui.contract.SubRankContract;
import com.example.android_bookreader2.ui.easyadapter.SubCategoryAdapter;
import com.example.android_bookreader2.ui.presenter.SubRankPresenter;

/**
 * Created by Administrator on 2017/12/26.
 */

public class SubRankFragment extends BaseRVFragment<SubRankPresenter, BooksByCats.BooksBean> implements SubRankContract.View  {

    public final static String BUNDLE_ID = "_id";

    public static SubRankFragment newInstance(String id) {
        SubRankFragment fragment = new SubRankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String id;

    @Override
    public void initDatas() {
        id = getArguments().getString(BUNDLE_ID);
    }

    @Override
    public void configViews() {
        initAdapter(SubCategoryAdapter.class, true, false);
        onRefresh();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerFindComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.common_easy_recyclerview;
    }

    @Override
    public void onItemClick(int position) {
        BookDetailActivity.startActivity(activity, mAdapter.getItem(position)._id);
    }

    @Override
    public void showRankList(BooksByCats data) {
        mAdapter.clear();
        mAdapter.addAll(data.books);
    }

    @Override
    public void showError() {
        loaddingError();
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getRankList(id);
    }
}
