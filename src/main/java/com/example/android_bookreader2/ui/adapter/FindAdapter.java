package com.example.android_bookreader2.ui.adapter;

import android.content.Context;
import android.view.View;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.bean.support.FindBean;
import com.example.android_bookreader2.common.OnRvItemClickListener;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;
import com.yuyh.easyadapter.recyclerview.EasyRVHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/11/21.
 */

public class FindAdapter extends EasyRVAdapter<FindBean> {

    private OnRvItemClickListener itemClickListener;

    public FindAdapter(Context context, List<FindBean> list, OnRvItemClickListener
            listener) {
        super(context, list, R.layout.item_find);
        this.itemClickListener = listener;
    }

    @Override
    protected void onBindData(final EasyRVHolder holder, final int position, final FindBean item) {
        holder.setText(R.id.tvTitle, item.getTitle());
        holder.setImageResource(R.id.ivIcon,item.getIconResId());

        holder.setOnItemViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(holder.getItemView(), position, item);
            }
        });
    }
}
