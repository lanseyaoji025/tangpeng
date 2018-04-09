package com.example.android_bookreader2.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.base.BaseRVActivity;
import com.example.android_bookreader2.bean.BooksByTag;
import com.example.android_bookreader2.bean.SearchDetail;
import com.example.android_bookreader2.component.AppComponent;
import com.example.android_bookreader2.component.DaggerBookComponent;
import com.example.android_bookreader2.ui.contract.SearchByAuthorContract;
import com.example.android_bookreader2.ui.easyadapter.SearchAdapter;
import com.example.android_bookreader2.ui.presenter.SearchByAuthorPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by tangpeng on 2017/12/8.
 */

public class SearchByAuthorActivity  extends BaseRVActivity<SearchDetail.BooksBean> implements SearchByAuthorContract.View{

    public static final String INTENT_AUTHOR = "author";
    private String author;

    public static void startActivity(Context context, String author) {
        context.startActivity(new Intent(context, SearchByAuthorActivity.class)
                .putExtra(INTENT_AUTHOR, author));
    }
    @Inject
    SearchByAuthorPresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_recyclerview;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        author = getIntent().getStringExtra(INTENT_AUTHOR);
        mCommonToolbar.setTitle(author);
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        initAdapter(SearchAdapter.class, false, false);
    }

    @Override
    public void configViews() {
        mPresenter.attachView(this);
        mPresenter.getSearchResultList(author);
    }

    @Override
    public void showSearchResultList(List<BooksByTag.BooksBean> list) {
        List<SearchDetail.BooksBean> mList = new ArrayList<>();
        for (BooksByTag.BooksBean book : list) {
            mList.add(new SearchDetail.BooksBean(book.get_id(), book.getTitle(), book.getAuthor(), book.getCover(), book.getRetentionRatio(), book.getLatelyFollower()));
        }
        mAdapter.clear();
        mAdapter.addAll(mList);
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
    public void onItemClick(int position) {
        SearchDetail.BooksBean data = mAdapter.getItem(position);
        BookDetailActivity.startActivity(this, data.get_id());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
