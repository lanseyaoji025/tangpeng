package com.example.android_bookreader2.component;

import android.content.Context;

import com.example.android_bookreader2.api.BookApi;
import com.example.android_bookreader2.module.AppModule;
import com.example.android_bookreader2.module.BookApiModule;

import dagger.Component;

/**
 * Created by tangpeng on 2017/11/10.
 */
@Component(modules = {AppModule.class, BookApiModule.class})
public interface AppComponent {
    //说明将Context开放给其他Component使用
    Context getContext();
    //说明将BookApi开放给其他Component使用
    BookApi getReaderApi();
}
