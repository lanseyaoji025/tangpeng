package com.example.android_bookreader2.ui.contract;

import com.example.android_bookreader2.base.BaseContract;

/**
 * Created by tangpeng on 2017/11/15.
 */

public interface MainContract {

    interface View extends BaseContract.BaseView {
        void loginSuccess();

        void syncBookShelfCompleted();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        void login(String uid, String token, String platform);

        void syncBookShelf();
    }
}
