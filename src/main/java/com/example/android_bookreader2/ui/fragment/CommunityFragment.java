package com.example.android_bookreader2.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.base.BaseFragment;
import com.example.android_bookreader2.bean.support.FindBean;
import com.example.android_bookreader2.common.OnRvItemClickListener;
import com.example.android_bookreader2.component.AppComponent;
import com.example.android_bookreader2.ui.activity.BookDiscussionActivity;
import com.example.android_bookreader2.ui.activity.BookReviewActivity;
import com.example.android_bookreader2.ui.adapter.FindAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by tangpeng on 2017/11/16.
 */

public class CommunityFragment extends BaseFragment implements OnRvItemClickListener<FindBean>  {

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private List<FindBean> mList = new ArrayList<>();
    private FindAdapter mAdapter;
    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        mList.clear();
        mList.add(new FindBean("综合讨论区", R.drawable.discuss_section));
        mList.add(new FindBean("书评区", R.drawable.comment_section));
        mList.add(new FindBean("书荒互助区", R.drawable.helper_section));
        mList.add(new FindBean("女生区", R.drawable.girl_section));
        mList.add(new FindBean("原创区",R.drawable.yuanchuang));
    }

    @Override
    public void configViews() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.addItemDecoration(new SupportDividerItemDecoration(mContext, LinearLayoutManager.VERTICAL, true));

        mAdapter = new FindAdapter(mContext, mList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_find;
    }

    @Override
    public void onItemClick(View view, int position, FindBean data) {
        switch (position) {
            case 0:
                BookDiscussionActivity.startActivity(activity,true);
                break;
            case 1:
                BookReviewActivity.startActivity(activity);
                break;
        }
    }
}
