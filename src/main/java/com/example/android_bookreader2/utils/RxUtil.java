package com.example.android_bookreader2.utils;

import android.text.TextUtils;

import com.example.android_bookreader2.ReaderApplication2;
import com.example.android_bookreader2.bean.RankingList;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tangpeng on 2017/11/17.
 */

public class RxUtil {

    public static <T> Observable rxCreateDiskObservable(final String key, final Class<T> clazz) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                LogUtils.d("get data from disk: key==" + key);
                String json = ACache.get(ReaderApplication2.getsInstance()).getAsString(key);
                LogUtils.d("get data from disk finish , json==" + json);
                if (!TextUtils.isEmpty(json)) {
                    subscriber.onNext(json);
                }
                subscriber.onCompleted();
            }
        }).map(new Func1<String ,T>() {
            @Override
            public T call(String s) {
                return new Gson().fromJson(s, clazz);
            }
        }).subscribeOn(Schedulers.io());
    }

    public static  <T>Observable.Transformer<T, T> rxCacheListHelper(final String key) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io())
                        .doOnNext(new Action1<T>() {//指定doOnNext执行线程是新线程
                            @Override
                            public void call(T data) {
                                LogUtils.d("get data from network finish ,start cache...");
                                //通过反射获取List,再判空决定是否缓存
                                if (data == null){
                                    LogUtils.d("get data from null .......");
                                    return;
                                }
                                Class aClass = data.getClass();
                                Field[] fields = aClass.getFields();
                                for (Field field : fields) {
                                    String className = field.getType().getSimpleName();
                                    if (className.equalsIgnoreCase("List")) {
                                        try{
                                            List list = (List) field.get(data);
                                            LogUtils.d("list==" + list);
                                            if (list != null && !list.isEmpty()) {
                                                ACache.get(ReaderApplication2.getsInstance()).put(key,new Gson().toJson(data, aClass));
                                                LogUtils.d("cache finish");
                                            }
                                        }catch (IllegalAccessException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T>Observable.Transformer<T,T> rxCacheBeanHelper(final String key) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .doOnNext(new Action1<T>() {
                            @Override
                            public void call(final T data) {
                                Schedulers.io().createWorker().schedule(new Action0() {
                                    @Override
                                    public void call() {
                                        LogUtils.d("get data from network finish ,start cache...");
                                        ACache.get(ReaderApplication2.getsInstance())
                                                .put(key, new Gson().toJson(data, data.getClass()));
                                        LogUtils.d("cache finish");
                                    }
                                });
                            }
                        }).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
