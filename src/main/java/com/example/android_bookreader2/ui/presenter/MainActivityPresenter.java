package com.example.android_bookreader2.ui.presenter;

import com.example.android_bookreader2.api.BookApi;
import com.example.android_bookreader2.base.RxPresenter;
import com.example.android_bookreader2.bean.BookMixAToc;
import com.example.android_bookreader2.bean.Recommend;
import com.example.android_bookreader2.bean.user.Login;
import com.example.android_bookreader2.manager.CollectionsManager;
import com.example.android_bookreader2.ui.contract.MainContract;
import com.example.android_bookreader2.utils.LogUtils;
import com.example.android_bookreader2.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tangpeng on 2017/11/15.
 */

public class MainActivityPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter<MainContract.View> {

    private BookApi bookApi;
    public static boolean isLastSyncUpdateed = false;

    @Inject
    public MainActivityPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }
    @Override
    public void login(String uid, String token, String platform) {
        Subscription rxSubscription =bookApi.login(uid, token, platform).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Login>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("login" + e.toString());
                    }

                    @Override
                    public void onNext(Login data) {
                        if (data != null && mView != null && data.ok) {
                            mView.loginSuccess();
                            LogUtils.e(data.user.toString());
                        }
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void syncBookShelf() {
        //从缓存中读取添加的小说列表
        List<Recommend.RecommendBooks> list = CollectionsManager.getInstance().getCollectionList();
        List<Observable<BookMixAToc.mixToc>> observables = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            LogUtils.d("Recommend.RecommendBooks的长度是:"+list.size());
            for (Recommend.RecommendBooks bean : list) {
                if (!bean.isFromSD){
                    Observable<BookMixAToc.mixToc> fromNetWork = bookApi.getBookMixAToc(bean._id, "chapters").map(new Func1<BookMixAToc, BookMixAToc.mixToc>() {
                        @Override
                        public BookMixAToc.mixToc call(BookMixAToc bookMixAToc) {
                            return bookMixAToc.mixToc;
                        }
                    });
                    observables.add(fromNetWork);
                }
            }
        }else {
            ToastUtils.showSingleToast("书架空空如也...");
            mView.syncBookShelfCompleted();
            return;
        }
        isLastSyncUpdateed = false;
        Subscription subscribe = Observable.mergeDelayError(observables)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookMixAToc.mixToc>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.d("mergeDelayError的onCompleted执行");
                        mView.syncBookShelfCompleted();
                        if(isLastSyncUpdateed){
                            ToastUtils.showSingleToast("小説已更新");
                        }else{
                            ToastUtils.showSingleToast("你追的小説沒有更新");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
                        mView.showError();
                    }

                    @Override
                    public void onNext(BookMixAToc.mixToc data) {
                        String lastChapter = data.chapters.get(data.chapters.size() - 1).title;
                        CollectionsManager.getInstance().setLastChapterAndLatelyUpdate(data.book, lastChapter, data.chaptersUpdated);
                    }
                });
        addSubscrebe(subscribe);
    }
}
