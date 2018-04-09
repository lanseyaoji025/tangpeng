package com.example.android_bookreader2.ui.contract;

import com.example.android_bookreader2.base.BaseContract;
import com.example.android_bookreader2.bean.BookDetail;
import com.example.android_bookreader2.bean.HotReview;
import com.example.android_bookreader2.bean.RecommendBookList;

import java.util.List;

/**
 * Created by tangpeng on 2017/12/4.
 */

public interface BookDetailContract {

    interface View extends BaseContract.BaseView {
        void showBookDetail(BookDetail data);

        void showHotReview(List<HotReview.Reviews> list);

        void showRecommendBookList(List<RecommendBookList.RecommendBook> list);

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getBookDetail(String bookId);

        void getHotReview(String book);

        void getRecommendBookList(String bookId, String limit);
    }
}
