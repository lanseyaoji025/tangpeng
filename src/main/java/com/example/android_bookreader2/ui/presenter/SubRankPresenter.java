package com.example.android_bookreader2.ui.presenter;

import com.example.android_bookreader2.api.BookApi;
import com.example.android_bookreader2.base.RxPresenter;
import com.example.android_bookreader2.bean.BooksByCats;
import com.example.android_bookreader2.bean.Rankings;
import com.example.android_bookreader2.ui.contract.SubRankContract;
import com.example.android_bookreader2.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/26.
 */

public class SubRankPresenter extends RxPresenter<SubRankContract.View> implements SubRankContract.Presenter<SubRankContract.View> {

    private BookApi bookApi;

    @Inject
    public SubRankPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }


    @Override
    public void getRankList(String id) {
        Subscription rxSubscription = bookApi.getRanking(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Rankings>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getRankList:" + e.toString());
                        mView.showError();
                    }

                    @Override
                    public void onNext(Rankings ranking) {
                        List<Rankings.RankingBean.BooksBean> books = ranking.ranking.books;
                        BooksByCats cats = new BooksByCats();
                        cats.books = new ArrayList<>();
                        for (Rankings.RankingBean.BooksBean bean : books) {
                            cats.books.add(new BooksByCats.BooksBean(bean._id, bean.cover, bean.title, bean.author, bean.cat, bean.shortIntro, bean.latelyFollower, bean.retentionRatio));
                        }
                        mView.showRankList(cats);
                    }
                });
        addSubscrebe(rxSubscription);
    }
}
