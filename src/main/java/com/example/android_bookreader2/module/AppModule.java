package com.example.android_bookreader2.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tangpeng on 2017/11/10.
 */
@Module
public class AppModule {

    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }
}
