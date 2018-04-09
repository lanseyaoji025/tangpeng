package com.example.android_bookreader2;

import android.app.Application;
import android.content.Context;

import com.example.android_bookreader2.component.AppComponent;
import com.example.android_bookreader2.component.DaggerAppComponent;
import com.example.android_bookreader2.module.AppModule;
import com.example.android_bookreader2.module.BookApiModule;
import com.example.android_bookreader2.utils.AppUtils;
import com.example.android_bookreader2.utils.SharedPreferencesUtil;

/**
 * Created by tangpeng on 2017/11/6.
 */

public class ReaderApplication2 extends Application {

    private static ReaderApplication2 sInstance;
    private AppComponent appComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initCompoent();
        AppUtils.init(this);
        initPrefs();
    }

    private void initCompoent() {
        appComponent = DaggerAppComponent.builder()
                .bookApiModule(new BookApiModule())
                .appModule(new AppModule(this))
                .build();
    }
    public AppComponent getAppComponent() {
        return appComponent;
    }
    /**
     * 初始化SharedPreference
     */
    protected void initPrefs() {
        SharedPreferencesUtil.init(getApplicationContext(), getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

    public static ReaderApplication2 getsInstance() {
        return sInstance;
    }
}
