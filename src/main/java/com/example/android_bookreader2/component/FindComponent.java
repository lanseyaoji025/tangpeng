package com.example.android_bookreader2.component;

import com.example.android_bookreader2.ui.activity.SubCategoryListActivity;
import com.example.android_bookreader2.ui.activity.SubOtherHomeRankActivity;
import com.example.android_bookreader2.ui.activity.SubRankActivity;
import com.example.android_bookreader2.ui.activity.SubjectBookListActivity;
import com.example.android_bookreader2.ui.activity.SubjectBookListDetailActivity;
import com.example.android_bookreader2.ui.activity.TopCategoryListActivity;
import com.example.android_bookreader2.ui.activity.TopRankActivity;
import com.example.android_bookreader2.ui.fragment.SubCategoryFragment;
import com.example.android_bookreader2.ui.fragment.SubRankFragment;
import com.example.android_bookreader2.ui.fragment.SubjectFragment;

import dagger.Component;
import dagger.Module;

/**
 * Created by Administrator on 2017/12/15.
 */

@Component(dependencies = AppComponent.class)
public interface FindComponent {

    /** 主题书单 **/
    SubjectBookListDetailActivity inject(SubjectBookListDetailActivity subjectBookListActivity);

    /** 排行 **/
    TopRankActivity inject(TopRankActivity activity);

    SubRankActivity inject(SubRankActivity activity);

    SubOtherHomeRankActivity inject(SubOtherHomeRankActivity activity);

    TopCategoryListActivity inject(TopCategoryListActivity activity);

    SubCategoryListActivity inject(SubCategoryListActivity activity);

    SubjectBookListActivity inject(SubjectBookListActivity activity);

    SubRankFragment inject(SubRankFragment fragment);

    SubCategoryFragment inject(SubCategoryFragment fragment);

    SubjectFragment inject(SubjectFragment fragment);

}
