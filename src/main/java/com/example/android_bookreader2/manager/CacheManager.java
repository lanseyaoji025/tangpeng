package com.example.android_bookreader2.manager;

import android.text.TextUtils;

import com.example.android_bookreader2.ReaderApplication2;
import com.example.android_bookreader2.base.Constant;
import com.example.android_bookreader2.bean.BookLists;
import com.example.android_bookreader2.bean.ChapterRead;
import com.example.android_bookreader2.utils.ACache;
import com.example.android_bookreader2.utils.AppUtils;
import com.example.android_bookreader2.utils.FileUtils;
import com.example.android_bookreader2.utils.LogUtils;
import com.example.android_bookreader2.utils.SharedPreferencesUtil;
import com.example.android_bookreader2.utils.StringUtils;
import com.example.android_bookreader2.utils.ToastUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/1.
 */

public class CacheManager {

    private static CacheManager manager;

    public static CacheManager getInstance() {
        return manager == null ? (manager = new CacheManager()) : manager;
    }

    private String getSearchHistoryKey() {
        return "searchHistory";
    }

    public List<String> getSearchHistory() {
        return SharedPreferencesUtil.getInstance().getObject(getSearchHistoryKey(), List.class);
    }

    public void saveSearchHistory(Object obj) {
        SharedPreferencesUtil.getInstance().putObject(getSearchHistoryKey(), obj);
    }
    private String getCollectionKey() {
        return "my_book_lists";
    }
    /**
     * 获取我收藏的书单列表
     *
     * @return
     */
    public List<BookLists.BookListsBean> getCollectionList() {
        List<BookLists.BookListsBean> list = (ArrayList<BookLists.BookListsBean>) ACache.get(
                ReaderApplication2.getsInstance()).getAsObject(getCollectionKey());
        return list == null ? null : list;
    }

    public void addCollection(BookLists.BookListsBean bean) {
        List<BookLists.BookListsBean> list = getCollectionList();
        if (list == null) {
            list = new ArrayList<>();
        }
        for (BookLists.BookListsBean data : list) {
            if (data != null) {
                if (TextUtils.equals(data._id, bean._id)) {
                    ToastUtils.showToast("已经收藏过啦");
                    return;
                }
            }
        }
        list.add(bean);
        ACache.get(ReaderApplication2.getsInstance()).put(getCollectionKey(), (Serializable) list);
        ToastUtils.showToast("收藏成功");
    }

    public File getChapterFile(String bookId, int chapter) {
        File file = FileUtils.getChapterFile(bookId, chapter);
        if (file != null && file.length() > 50)
            return file;
        return null;
    }

    public void saveChapterFile(String bookId, int chapter, ChapterRead.Chapter data) {
        File file = FileUtils.getChapterFile(bookId, chapter);
        FileUtils.writeFile(file.getAbsolutePath(), StringUtils.formatContent(data.body), false);
    }

    /**
     * 获取缓存大小
     *
     * @return
     */
    public synchronized String getCacheSize() {
        long cacheSize = 0;

        try{
            String cacheDir = Constant.BASE_PATH;
            cacheSize += FileUtils.getFolderSize(cacheDir);
            if (FileUtils.isSdCardAvailable()) {
                String extCacheDir = AppUtils.getAppContext().getExternalCacheDir().getPath();
                cacheSize += FileUtils.getFolderSize(extCacheDir);
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }

        return FileUtils.formatFileSizeToString(cacheSize);
    }

    /**
     * 清除缓存
     *
     * @param clearReadPos 是否删除阅读记录
     */
    public synchronized void clearCache(boolean clearReadPos, boolean clearCollect) {
        try {
            // 删除内存缓存
            String cacheDir = AppUtils.getAppContext().getCacheDir().getPath();
            FileUtils.deleteFileOrDirectory(new File(cacheDir));
            if (FileUtils.isSdCardAvailable()) {
                // 删除SD书籍缓存
                FileUtils.deleteFileOrDirectory(new File(Constant.PATH_DATA));
            }
            // 删除阅读记录（SharePreference）
            if (clearReadPos) {
                //防止再次弹出性别选择框，sp要重写入保存的性别
                String chooseSex = SettingManager.getInstance().getUserChooseSex();
                SharedPreferencesUtil.getInstance().removeAll();
                SettingManager.getInstance().saveUserChooseSex(chooseSex);
            }
            // 清空书架
            if (clearCollect) {
                CollectionsManager.getInstance().clear();
            }
            // 清除其他缓存
            ACache.get(AppUtils.getAppContext()).clear();
        }catch (Exception e){
            LogUtils.e(e.toString());
        }
    }
}
