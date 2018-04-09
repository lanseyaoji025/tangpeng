package com.example.android_bookreader2.ui.contract;

import com.example.android_bookreader2.base.BaseContract;
import com.example.android_bookreader2.bean.BookMixAToc;
import com.example.android_bookreader2.bean.Recommend;

import java.util.List;

/**
 * Created by tangpeng on 2017/11/15.
 */

public interface RecommendContract {

    interface View extends BaseContract.BaseView {
        void showRecommendList(List<Recommend.RecommendBooks> list);

        void showBookToc(String bookId, List<BookMixAToc.mixToc.Chapters> list);
    }
    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        void getRecommendList();
    }
}
