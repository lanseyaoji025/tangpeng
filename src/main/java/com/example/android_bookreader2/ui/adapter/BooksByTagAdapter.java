package com.example.android_bookreader2.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.base.Constant;
import com.example.android_bookreader2.bean.BooksByTag;
import com.example.android_bookreader2.common.OnRvItemClickListener;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;
import com.yuyh.easyadapter.recyclerview.EasyRVHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/12/12.
 */

public class BooksByTagAdapter extends EasyRVAdapter<BooksByTag.BooksBean> {
    private OnRvItemClickListener itemClickListener;

    public BooksByTagAdapter(Context context, List<BooksByTag.BooksBean> list,
                             OnRvItemClickListener listener) {
        super(context, list, R.layout.item_tag_book_list);
        this.itemClickListener = listener;
    }

    @Override
    protected void onBindData(final EasyRVHolder holder, final int position, final BooksByTag.BooksBean item) {
        StringBuffer sbTags = new StringBuffer();
        for (String tag : item.getTags()) {
            if (!TextUtils.isEmpty(tag)) {
                sbTags.append(tag);
                sbTags.append(" | ");
            }
        }
        holder.setRoundImageUrl(R.id.ivBookCover, Constant.IMG_BASE_URL + item.getCover(), R.drawable.cover_default)
                .setText(R.id.tvBookListTitle, item.getTitle())
                .setText(R.id.tvShortIntro, item.getShortIntro())
                .setText(R.id.tvTags, (item.tags.size() == 0 ? "" : sbTags.substring(0, sbTags
                        .lastIndexOf(" | "))));

        holder.setOnItemViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(holder.getItemView(), position, item);
            }
        });
    }
}
