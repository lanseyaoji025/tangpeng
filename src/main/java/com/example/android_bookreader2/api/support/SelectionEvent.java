package com.example.android_bookreader2.api.support;

import com.example.android_bookreader2.base.Constant;

/**
 * Created by Administrator on 2017/12/13.
 */

public class SelectionEvent {

    public String distillate;

    public String type;

    public String sort;

    public SelectionEvent(@Constant.Distillate String distillate,
                          @Constant.BookType String type,
                          @Constant.SortType String sort){
        this.distillate = distillate;
        this.type = type;
        this.sort = sort;
    }

    public SelectionEvent(@Constant.SortType String sort) {
        this.sort = sort;
    }
}
