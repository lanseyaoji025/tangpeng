package com.example.android_bookreader2.ui.contract;

import com.example.android_bookreader2.base.BaseContract;
import com.example.android_bookreader2.bean.SearchDetail;

import java.util.List;

/**
 * Created by tangpeng on 2017/11/29.
 */

public interface SearchContract {

    interface  View extends BaseContract.BaseView{
        void showHotWordList(List<String> list);

        void showAutoCompleteList(List<String> list);

        void showSearchResultList(List<SearchDetail.BooksBean> list);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void getHotWordList();

        void getAutoCompleteList(String query);

        void getSearchResultList(String query);
    }
}
