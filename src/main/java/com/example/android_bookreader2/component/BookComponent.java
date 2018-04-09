package com.example.android_bookreader2.component;

import com.example.android_bookreader2.ui.activity.BookDetailActivity;
import com.example.android_bookreader2.ui.activity.BooksByTagActivity;
import com.example.android_bookreader2.ui.activity.ReadActivity;
import com.example.android_bookreader2.ui.activity.SearchActivity;
import com.example.android_bookreader2.ui.activity.SearchByAuthorActivity;
import com.example.android_bookreader2.ui.fragment.BookDetailDiscussionFragment;
import com.example.android_bookreader2.ui.fragment.BookDetailReviewFragment;

import dagger.Component;

/**
 * Created by Administrator on 2017/11/29.
 */

@Component(dependencies = AppComponent.class)
public interface BookComponent {
    SearchActivity inject(SearchActivity activity);

    BookDetailActivity inject(BookDetailActivity activity);

    SearchByAuthorActivity inject(SearchByAuthorActivity activity);

    BooksByTagActivity inject(BooksByTagActivity activity);

    ReadActivity inject(ReadActivity activity);

    BookDetailDiscussionFragment inject(BookDetailDiscussionFragment fragment);

    BookDetailReviewFragment inject(BookDetailReviewFragment fragment);



}
