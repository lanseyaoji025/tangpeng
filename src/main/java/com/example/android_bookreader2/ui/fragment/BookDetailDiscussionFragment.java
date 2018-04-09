package com.example.android_bookreader2.ui.fragment;

import android.os.Bundle;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.api.support.SelectionEvent;
import com.example.android_bookreader2.base.BaseRVFragment;
import com.example.android_bookreader2.base.Constant;
import com.example.android_bookreader2.bean.DiscussionList;
import com.example.android_bookreader2.component.AppComponent;
import com.example.android_bookreader2.component.DaggerBookComponent;
import com.example.android_bookreader2.ui.contract.BookDetailDiscussionContract;
import com.example.android_bookreader2.ui.easyadapter.BookDiscussionAdapter;
import com.example.android_bookreader2.ui.presenter.BookDetailDiscussionPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by Administrator on 2017/12/13.
 */

public class BookDetailDiscussionFragment extends BaseRVFragment<BookDetailDiscussionPresenter, DiscussionList.PostsBean> implements BookDetailDiscussionContract.View{

    public final static String BUNDLE_ID = "bookId";
    private String bookId;
    public static BookDetailDiscussionFragment newInstance(String id) {
        BookDetailDiscussionFragment fragment = new BookDetailDiscussionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }
    private String sort = Constant.SortType.DEFAULT;

    @Override
    public void showError() {
        loaddingError();
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        bookId = getArguments().getString(BUNDLE_ID);
    }

    @Override
    public void configViews() {
        initAdapter(BookDiscussionAdapter.class, true, true);
        onRefresh();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.common_easy_recyclerview;
    }

    @Override
    public void showBookDetailDiscussionList(List<DiscussionList.PostsBean> list, boolean isRefresh) {
        if (isRefresh) {
            mAdapter.clear();
            start = 0;
        }
        mAdapter.addAll(list);
        start = start + list.size();
    }

    @Override
    public void onItemClick(int position) {

    }

    @Subscribe(threadMode =ThreadMode.MAIN)
    public void initCategoryList(SelectionEvent event) {
        if (getUserVisibleHint()) {
            mRecyclerView.setRefreshing(true);
            sort = event.sort;
            onRefresh();
        }
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getBookDetailDiscussionList(bookId, sort, 0, limit);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getBookDetailDiscussionList(bookId, sort, start, limit);
    }
}
