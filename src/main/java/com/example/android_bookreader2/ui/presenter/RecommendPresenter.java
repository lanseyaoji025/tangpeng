package com.example.android_bookreader2.ui.presenter;

import android.content.Context;

import com.example.android_bookreader2.api.BookApi;
import com.example.android_bookreader2.base.RxPresenter;
import com.example.android_bookreader2.bean.BookMixAToc;
import com.example.android_bookreader2.bean.Recommend;
import com.example.android_bookreader2.manager.SettingManager;
import com.example.android_bookreader2.ui.contract.RecommendContract;
import com.example.android_bookreader2.utils.ACache;
import com.example.android_bookreader2.utils.LogUtils;
import com.example.android_bookreader2.utils.RxUtil;
import com.example.android_bookreader2.utils.StringUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tangpeng on 2017/11/15.
 */

public class RecommendPresenter extends RxPresenter<RecommendContract.View> implements
        RecommendContract.Presenter<RecommendContract.View> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public RecommendPresenter(Context mContext, BookApi bookApi) {
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    @Override
    public void getRecommendList() {
        String key = StringUtils.creatAcacheKey("recommend-list", SettingManager.getInstance().getUserChooseSex());
        Observable<Recommend> fromNetWork =bookApi.getRecommend(SettingManager.getInstance().getUserChooseSex())
                .compose(RxUtil.<Recommend>rxCacheListHelper(key));
        //依次检查disk、network
        Subscription subscribe = Observable.concat(RxUtil.rxCreateDiskObservable(key, Recommend.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Recommend>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getRecommendList", e.toString());
                        mView.showError();
                    }

                    @Override
                    public void onNext(Recommend recommend) {
                        if (recommend != null) {
                            List<Recommend.RecommendBooks> list = recommend.books;
                            if (list != null && !list.isEmpty() && mView != null) {
                                mView.showRecommendList(list);
                            }
                        }
                    }
                });
        addSubscrebe(subscribe);
    }

    public <T> Observable<T> applySchedulers(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void getTocList(final String bookId) {
        bookApi.getBookMixAToc(bookId, "chapters").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookMixAToc>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
                        mView.showError();
                    }

                    @Override
                    public void onNext(BookMixAToc data) {
                        ACache.get(mContext).put(bookId + "bookToc", data);
                        List<BookMixAToc.mixToc.Chapters> list = data.mixToc.chapters;
                        if (list != null && !list.isEmpty() && mView != null) {
                            mView.showBookToc(bookId, list);
                        }
                    }
                });
    }
}
