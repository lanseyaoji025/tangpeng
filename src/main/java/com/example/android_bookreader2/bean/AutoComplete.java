package com.example.android_bookreader2.bean;

import com.example.android_bookreader2.bean.base.Base;

import java.util.List;

/**
 * Created by tangpeng on 2017/11/29.
 */

public class AutoComplete extends Base{

    private List<String> keywords;

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
