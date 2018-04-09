package com.example.android_bookreader2.ui.presenter;

import com.example.android_bookreader2.api.BookApi;
import com.example.android_bookreader2.base.RxPresenter;
import com.example.android_bookreader2.bean.BooksByTag;
import com.example.android_bookreader2.ui.contract.SearchByAuthorContract;
import com.example.android_bookreader2.utils.LogUtils;
import com.example.android_bookreader2.utils.RxUtil;
import com.example.android_bookreader2.utils.StringUtils;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by tangpeng on 2017/12/8.
 * 根据作者查看
 */

public class SearchByAuthorPresenter extends RxPresenter<SearchByAuthorContract.View> implements SearchByAuthorContract.Presenter {

    private BookApi bookApi;

    @Inject
    public SearchByAuthorPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getSearchResultList(String author) {
        String key = StringUtils.creatAcacheKey("search-by-author", author);
        Observable<BooksByTag> fromNetWork = bookApi.searchBooksByAuthor(author)
                .compose(RxUtil.<BooksByTag>rxCacheListHelper(key));

        //依次检查disk、network
        Subscription rxSubscription = Observable.concat(RxUtil.rxCreateDiskObservable(key, BooksByTag.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BooksByTag>() {
                    @Override
                    public void onNext(BooksByTag booksByTag) {
                        if (mView != null)
                            mView.showSearchResultList(booksByTag.getBooks());
                    }

                    @Override
                    public void onCompleted() {
                        LogUtils.i("complete");
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getSearchResultList:" + e.toString());
                        if (mView != null)
                            mView.showError();
                    }
                });
        addSubscrebe(rxSubscription);
    }
}
