package com.example.android_bookreader2.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.ReaderApplication2;
import com.example.android_bookreader2.api.BookApi;
import com.example.android_bookreader2.api.support.DownloadMessage;
import com.example.android_bookreader2.api.support.DownloadProgress;
import com.example.android_bookreader2.api.support.DownloadQueue;
import com.example.android_bookreader2.api.support.Logger;
import com.example.android_bookreader2.api.support.LoggingInterceptor;
import com.example.android_bookreader2.bean.BookMixAToc;
import com.example.android_bookreader2.bean.ChapterRead;
import com.example.android_bookreader2.manager.CacheManager;
import com.example.android_bookreader2.utils.AppUtils;
import com.example.android_bookreader2.utils.LogUtils;
import com.example.android_bookreader2.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tangpeng on 2017/11/15.
 */

public class DownloadBookService extends Service {

    public BookApi bookApi;
    protected CompositeSubscription mCompositeSubscription;
    public static List<DownloadQueue> downloadQueues = new ArrayList<>();

    public boolean isBusy = false; // 当前是否有下载任务在进行

    public static boolean canceled = false;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        LoggingInterceptor logging = new LoggingInterceptor(new Logger());
        logging.setLevel(LoggingInterceptor.Level.BODY);
        bookApi = ReaderApplication2.getsInstance().getAppComponent().getReaderApi();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unSubscribe();
        EventBus.getDefault().unregister(this);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

    protected void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    public static void cancel() {
        canceled = true;
    }

    public static void post(DownloadQueue downloadQueue) {
        EventBus.getDefault().post(downloadQueue);
    }

    private void post(DownloadMessage message) {
        EventBus.getDefault().post(message);
    }
    public void post(DownloadProgress progress) {
        EventBus.getDefault().post(progress);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void addToDownloadQueue(DownloadQueue queue) {
        if (!TextUtils.isEmpty(queue.bookId)) {
            boolean exists = false;
            // 判断当前书籍缓存任务是否存在
            for (int i = 0; i < downloadQueues.size(); i++) {
                if (downloadQueues.get(i).bookId.equals(queue.bookId)) {
                    LogUtils.e("addToDownloadQueue:exists");
                    exists = true;
                    break;
                }
            }
            if (exists) {
                post(new DownloadMessage(queue.bookId, "当前缓存任务已存在", false));
                return;
            }
            // 添加到下载队列
            downloadQueues.add(queue);
            LogUtils.e("addToDownloadQueue:" + queue.bookId);
            post(new DownloadMessage(queue.bookId, "成功加入缓存队列", false));
        }

        // 从队列顺序取出第一条下载
        if (downloadQueues.size() > 0 && !isBusy) {
            isBusy = true;
            downloadBook(downloadQueues.get(0));
        }
    }

    public synchronized void downloadBook(final DownloadQueue downloadQueue) {
        AsyncTask<Integer, Integer, Integer> downloadTask=new AsyncTask<Integer, Integer, Integer>() {

            List<BookMixAToc.mixToc.Chapters> list = downloadQueue.list;
            String bookId = downloadQueue.bookId;
            int start = downloadQueue.start; // 起始章节
            int end = downloadQueue.end; // 结束章节
            @Override
            protected Integer doInBackground(Integer... params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                int failureCount = 0;
                for (int i = start; i <= end && i <= list.size(); i++) {
                    if (canceled) {
                        break;
                    }
                    // 网络异常，取消下载
                    if (!NetworkUtils.isAvailable(AppUtils.getAppContext())) {
                        downloadQueue.isCancel = true;
                        post(new DownloadMessage(bookId, getString(R.string.book_read_download_error), true));
                        failureCount = -1;
                        break;
                    }
                    if (!downloadQueue.isFinish && !downloadQueue.isCancel) {
                        // 章节文件不存在,则下载，否则跳过
                        BookMixAToc.mixToc.Chapters chapters = list.get(i - 1);
                        String url = chapters.link;
                        int ret = download(url, bookId, chapters.title, i, list.size());
                        if (ret != 1) {
                            failureCount++;
                        }else {
                            post(new DownloadProgress(bookId, String.format(
                                    getString(R.string.book_read_alreday_download), list.get(i - 1).title, i, list.size()),
                                    true));
                        }
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                return failureCount;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
            }
        };
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private int download(String url, final String bookId, final String title, final int chapter, final int chapterSize){
        final int[] result = {-1};
        Subscription subscription =bookApi.getChapterRead(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChapterRead>() {
                    @Override
                    public void onCompleted() {
                        result[0] = 1;
                    }

                    @Override
                    public void onError(Throwable e) {
                        result[0] = 0;
                    }

                    @Override
                    public void onNext(ChapterRead data) {
                        post(new DownloadProgress(bookId, String.format(
                                getString(R.string.book_read_download_progress), title, chapter, chapterSize),
                                true));
                        CacheManager.getInstance().saveChapterFile(bookId, chapter, data.chapter);
                        result[0] = 1;
                    }
                });
        addSubscrebe(subscription);

        while (result[0] == -1) {
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result[0];
    }
}
