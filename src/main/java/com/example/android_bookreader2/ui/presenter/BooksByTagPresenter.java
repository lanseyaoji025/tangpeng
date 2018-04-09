package com.example.android_bookreader2.ui.presenter;

import com.example.android_bookreader2.api.BookApi;
import com.example.android_bookreader2.base.RxPresenter;
import com.example.android_bookreader2.bean.BooksByTag;
import com.example.android_bookreader2.ui.contract.BooksByTagContract;
import com.example.android_bookreader2.utils.LogUtils;
import com.example.android_bookreader2.utils.RxUtil;
import com.example.android_bookreader2.utils.StringUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2017/12/11.
 */

public class BooksByTagPresenter extends RxPresenter<BooksByTagContract.View> implements BooksByTagContract.Presenter<BooksByTagContract.View>   {

    private BookApi bookApi;

    private boolean isLoading = false;

    @Inject
    public BooksByTagPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }
    @Override
    public void getBooksByTag(String tags, final String start, String limit) {
        if (!isLoading) {
            isLoading = true;
            String key = StringUtils.creatAcacheKey("books-by-tag", tags, start, limit);
            Observable<BooksByTag> fromNetWork = bookApi.getBooksByTag(tags, start, limit)
                    .compose(RxUtil.<BooksByTag>rxCacheListHelper(key));

            //依次检查disk、network
            Subscription rxSubscription = Observable.concat(RxUtil.rxCreateDiskObservable(key, BooksByTag.class), fromNetWork)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BooksByTag>() {
                        @Override
                        public void onNext(BooksByTag data) {
                            if (data != null) {
                                List<BooksByTag.BooksBean> list = data.getBooks();
                                if (list != null && !list.isEmpty() && mView != null) {
                                    boolean isRefresh = start.equals("0") ? true : false;
                                    mView.showBooksByTag(list, isRefresh);
                                }
                            }
                        }

                        @Override
                        public void onCompleted() {
                            isLoading = false;
                            mView.onLoadComplete(true, "");
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.e(e.toString());
                            isLoading = false;
                            mView.onLoadComplete(false, e.toString());
                        }
                    });
            addSubscrebe(rxSubscription);
        }
    }
}
