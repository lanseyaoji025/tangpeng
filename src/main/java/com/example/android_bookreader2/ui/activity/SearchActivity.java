package com.example.android_bookreader2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.base.BaseRVActivity;
import com.example.android_bookreader2.bean.SearchDetail;
import com.example.android_bookreader2.component.AppComponent;
import com.example.android_bookreader2.component.DaggerBookComponent;
import com.example.android_bookreader2.manager.CacheManager;
import com.example.android_bookreader2.ui.adapter.AutoCompleteAdapter;
import com.example.android_bookreader2.ui.adapter.SearchHistoryAdapter;
import com.example.android_bookreader2.ui.contract.SearchContract;
import com.example.android_bookreader2.ui.easyadapter.SearchAdapter;
import com.example.android_bookreader2.ui.presenter.SearchPresenter;
import com.example.android_bookreader2.utils.LogUtils;
import com.example.android_bookreader2.utils.ToastUtils;
import com.example.android_bookreader2.view.TagColor;
import com.example.android_bookreader2.view.TagGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by tangpeng on 2017/11/29.
 * 搜索界面
 */

public class SearchActivity extends BaseRVActivity<SearchDetail.BooksBean> implements SearchContract.View  {

    public static final String INTENT_QUERY = "query";
    private String key;
    private List<String> mAutoList = new ArrayList<>();
    private SearchHistoryAdapter mHisAdapter;
    private List<String> mHisList = new ArrayList<>();
    private List<String> tagList = new ArrayList<>();
    private AutoCompleteAdapter mAutoAdapter;
    private int times = 0;
    @Bind(R.id.tvChangeWords)
    TextView mTvChangeWords;
    @Bind(R.id.tag_group)
    TagGroup mTagGroup;
    @Bind(R.id.rootLayout)
    LinearLayout mRootLayout;
    @Bind(R.id.layoutHotWord)
    RelativeLayout mLayoutHotWord;
    @Bind(R.id.rlHistory)
    RelativeLayout rlHistory;
    @Bind(R.id.tvClear)
    TextView tvClear;
    @Bind(R.id.lvSearchHistory)
    ListView lvSearchHistory;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    private ListPopupWindow mListPopupWindow;
    int hotIndex = 0;

    @Inject
    SearchPresenter mPresenter;


    @OnClick(R.id.tvClear)
    public void clearSearchHistory() {
        CacheManager.getInstance().saveSearchHistory(null);
        initSearchHistory();
    }

    public static void startActivity(Context context, String query) {
        context.startActivity(new Intent(context, SearchActivity.class)
                .putExtra(INTENT_QUERY, query));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LogUtils.d("onCreateOptionsMenu执行了");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        searchMenuItem = menu.findItem(R.id.action_search);//在菜单中找到对应控件的item
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                key = query;
                mPresenter.getSearchResultList(query);
                saveSearchHistory(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LogUtils.d("onQueryTextChange");
                if (TextUtils.isEmpty(newText)) {
                    if (mListPopupWindow.isShowing())
                        mListPopupWindow.dismiss();
                    initTagGroup();
                } else {
                    mPresenter.getAutoCompleteList(newText);
                }
                return false;
            }
        });
        search(key); // 外部调用搜索，则打开页面立即进行搜索
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
               ToastUtils.showLongToast("展开");
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ToastUtils.showLongToast("收缩");
                initTagGroup();
                return true;
            }
        });
        return true;
    }

    /**
     * 隐藏搜索的结果，显示页面。
     */
    private void initTagGroup() {
        visible(mTagGroup, mLayoutHotWord, rlHistory);
        gone(mRecyclerView);
        if (mListPopupWindow.isShowing())
            mListPopupWindow.dismiss();
    }

    /**
     * 保存搜索历史
     * @param query
     */
    private void saveSearchHistory(String query) {
        List<String> list = CacheManager.getInstance().getSearchHistory();
        if (list == null) {
            list = new ArrayList<>();
            list.add(query);
        } else {
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                String item = iterator.next();
                if (TextUtils.equals(query, item)) {
                    iterator.remove();
                }
            }
            list.add(0, query);
        }
        int size = list.size();
        if (size > 20) { // 最多保存20条
            for (int i = size - 1; i >= 20; i--) {
                list.remove(i);
            }
        }
        CacheManager.getInstance().saveSearchHistory(list);
        initSearchHistory();
    }

    @Override
    public void initDatas() {
        key = getIntent().getStringExtra(INTENT_QUERY);
        mHisAdapter = new SearchHistoryAdapter(this, mHisList);
        lvSearchHistory.setAdapter(mHisAdapter);
        lvSearchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search(mHisList.get(position));
            }
        });
        initSearchHistory();
    }

    @Override
    public void configViews() {
        initAdapter(SearchAdapter.class, false, false);

        initAutoList();

        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                search(tag);
            }
        });

        mTvChangeWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHotWord();
            }
        });
        mPresenter.attachView(this);
        //得到热门搜索
        mPresenter.getHotWordList();
    }

    /**
     * 每次显示8个热搜词
     */
    private synchronized void showHotWord() {
        int tagSize = 8;
        String[] tags = new String[tagSize];
        for (int j = 0; j < tagSize && j < tagList.size(); hotIndex++, j++) {
            tags[j] = tagList.get(hotIndex % tagList.size());
        }
        List<TagColor> colors = TagColor.getRandomColors(tagSize);
        mTagGroup.setTags(colors, tags);
    }

    private void initAutoList() {
        mAutoAdapter = new AutoCompleteAdapter(this, mAutoList);
        mListPopupWindow = new ListPopupWindow(this);
        mListPopupWindow.setAdapter(mAutoAdapter);
        mListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mListPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPopupWindow.setAnchorView(mCommonToolbar);
        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListPopupWindow.dismiss();
                TextView tv = (TextView) view.findViewById(R.id.tvAutoCompleteItem);
                String str = tv.getText().toString();
                search(str);
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        SearchDetail.BooksBean data = mAdapter.getItem(position);
        BookDetailActivity.startActivity(this, data.get_id());
    }

    @Override
    public void showHotWordList(List<String> list) {
        visible(mTvChangeWords);
        tagList.clear();
        tagList.addAll(list);
        times = 0;
        showHotWord();
    }

    @Override
    public void showAutoCompleteList(List<String> list) {
        mAutoList.clear();
        mAutoList.addAll(list);

        if (!mListPopupWindow.isShowing()) {
            mListPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            mListPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mListPopupWindow.show();
        }
        mAutoAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSearchResultList(List<SearchDetail.BooksBean> list) {
        mAdapter.clear();
        mAdapter.addAll(list);
        mAdapter.notifyDataSetChanged();
        initSearchResult();
    }
    /**
     * 显示搜索结果，隐藏其他页面
     */
    private void initSearchResult() {
        gone(mTagGroup, mLayoutHotWord, rlHistory);
        visible(mRecyclerView);
        if (mListPopupWindow.isShowing())
            mListPopupWindow.dismiss();
    }

    @Override
    public void showError() {
        loaddingError();
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    /**
     * 展开SearchView进行查询
     *
     * @param key
     */
    private void search(String key) {
        MenuItemCompat.expandActionView(searchMenuItem);
        if (!TextUtils.isEmpty(key)) {
            searchView.setQuery(key, true);
            saveSearchHistory(key);
        }
    }

    /**
     * 初始化搜索历史
     */
    private void initSearchHistory() {
        List<String> list = CacheManager.getInstance().getSearchHistory();
        mHisAdapter.clear();
        if (list != null && list.size() > 0) {
            tvClear.setEnabled(true);
            mHisAdapter.addAll(list);
        } else {
            tvClear.setEnabled(false);
        }
        mHisAdapter.notifyDataSetChanged();
    }
}
