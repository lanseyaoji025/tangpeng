package com.example.android_bookreader2.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.base.BaseFragment;
import com.example.android_bookreader2.bean.support.FindBean;
import com.example.android_bookreader2.common.OnRvItemClickListener;
import com.example.android_bookreader2.component.AppComponent;
import com.example.android_bookreader2.ui.activity.SubjectBookListActivity;
import com.example.android_bookreader2.ui.activity.TopCategoryListActivity;
import com.example.android_bookreader2.ui.activity.TopRankActivity;
import com.example.android_bookreader2.ui.adapter.FindAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by tangpeng on 2017/11/16.
 */

public class FindFragment extends BaseFragment implements OnRvItemClickListener<FindBean> {

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private FindAdapter mAdapter;
    private List<FindBean> mList = new ArrayList<>();
    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        mList.clear();
        mList.add(new FindBean("排行榜", R.drawable.home_find_rank));
        mList.add(new FindBean("主题书单", R.drawable.home_find_topic));
        mList.add(new FindBean("分类", R.drawable.home_find_category));
        mList.add(new FindBean("官方QQ群", R.drawable.home_find_listen));
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
        switch (position){
            case 0:
                TopRankActivity.startActivity(activity);
                break;
            case 1:
                SubjectBookListActivity.startActivity(activity);
                break;
            case 2:
                startActivity(new Intent(activity, TopCategoryListActivity.class));
                break;
            case 3:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://jq.qq.com/?_wv=1027&k=46qbql8")));
                break;
            default:
                break;
        }
    }
}
