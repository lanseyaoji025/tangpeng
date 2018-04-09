package com.example.android_bookreader2.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.component.AppComponent;
import com.example.android_bookreader2.ui.fragment.BookDiscussionFragment;
import com.example.android_bookreader2.view.SelectionLayout;

import java.util.List;

import butterknife.Bind;

/**
 * Created by tangpeng on 2018/1/8.
 */

public class BookDiscussionActivity extends BaseCommuniteActivity{


    private static final String INTENT_DIS = "isDis";

    public static void startActivity(Context context, boolean isDiscussion) {
        context.startActivity(new Intent(context, BookDiscussionActivity.class)
                .putExtra(INTENT_DIS, isDiscussion));
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_community_book_discussion;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }
    private boolean mIsDiscussion;

    @Bind(R.id.slOverall)
    SelectionLayout slOverall;
    @Override
    public void initToolBar() {
        mIsDiscussion = getIntent().getBooleanExtra(INTENT_DIS, false);
        if (mIsDiscussion) {
            mCommonToolbar.setTitle("综合讨论区");
        } else {
            mCommonToolbar.setTitle("原创区");
        }
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        super.initDatas();
    }

    @Override
    public void configViews() {
        BookDiscussionFragment fragment = BookDiscussionFragment.newInstance(mIsDiscussion ? "ramble" : "original");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentCO, fragment).commit();
    }

    @Override
    protected List<List<String>> getTabList() {
        return list1;
    }
}
