package com.example.android_bookreader2.module;


import com.example.android_bookreader2.api.BookApi;
import com.example.android_bookreader2.api.support.HeaderInterceptor;
import com.example.android_bookreader2.api.support.Logger;
import com.example.android_bookreader2.api.support.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by tangpeng on 2017/11/10.
 */
@Module
public class BookApiModule {

    @Provides
    public OkHttpClient provideOkHttpClient() {
        LoggingInterceptor logging = new LoggingInterceptor(new Logger());
        logging.setLevel(LoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder =new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true) // 失败重发
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(logging);
        return builder.build();
    }
    @Provides
    protected BookApi provideBookService(OkHttpClient okHttpClient) {
//        Request request = new Request.Builder()
//                .url("")
//                .build();
//        WebSocket webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
//            @Override
//            public void onOpen(WebSocket webSocket, Response response) {
//                super.onOpen(webSocket, response);
//            }
//        });
//        okHttpClient.dispatcher().executorService().shutdown();
        return BookApi.getInstance(okHttpClient);
    }
}
