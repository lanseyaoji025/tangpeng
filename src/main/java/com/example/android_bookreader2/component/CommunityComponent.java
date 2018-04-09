package com.example.android_bookreader2.component;

import com.example.android_bookreader2.ui.activity.BookDiscussionDetailActivity;
import com.example.android_bookreader2.ui.fragment.BookDiscussionFragment;
import com.example.android_bookreader2.ui.fragment.BookReviewFragment;

import dagger.Component;

/**
 * Created by Administrator on 2017/12/15.
 */

@Component(dependencies = AppComponent.class)
public interface CommunityComponent {

    BookDiscussionDetailActivity inject(BookDiscussionDetailActivity activity);

    BookDiscussionFragment inject(BookDiscussionFragment fragment);

    BookReviewFragment inject(BookReviewFragment fragment);
}
