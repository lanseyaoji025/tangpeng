package com.example.android_bookreader2.ui.easyadapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.base.Constant;
import com.example.android_bookreader2.bean.SearchDetail;
import com.example.android_bookreader2.view.recyclerview.adapter.BaseViewHolder;
import com.example.android_bookreader2.view.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by tangpeng on 2017/12/1.
 */

public class SearchAdapter extends RecyclerArrayAdapter<SearchDetail.BooksBean> {

    public SearchAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<SearchDetail.BooksBean>(parent, R.layout.item_search_result_list) {
            @Override
            public void setData(SearchDetail.BooksBean item) {
                holder.setRoundImageUrl(R.id.ivBookCover, Constant.IMG_BASE_URL + item.getCover(), R.drawable.cover_default)
                        .setText(R.id.tvBookListTitle, item.getTitle())
                        .setText(R.id.tvLatelyFollower,String.format(mContext.getString(R.string.search_result_lately_follower), item.getLatelyFollower()))
                        .setText(R.id.tvRetentionRatio, (TextUtils.isEmpty(item.getRetentionRatio()) ? String.format(mContext.getString(R.string.search_result_retention_ratio), "0")
                                : String.format(mContext.getString(R.string.search_result_retention_ratio), item.getRetentionRatio())))
                        .setText(R.id.tvBookListAuthor, String.format(mContext.getString(R.string.search_result_author), item.getAuthor()));
            }
        };
    }
}
