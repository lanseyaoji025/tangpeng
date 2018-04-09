package com.example.android_bookreader2.ui.activity;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android_bookreader2.R;
import com.example.android_bookreader2.base.BaseActivity;
import com.example.android_bookreader2.base.Constant;
import com.example.android_bookreader2.bean.user.TencentLoginResult;
import com.example.android_bookreader2.component.AppComponent;
import com.example.android_bookreader2.component.DaggerMainComponent;
import com.example.android_bookreader2.manager.EventManager;
import com.example.android_bookreader2.manager.SettingManager;
import com.example.android_bookreader2.service.DownloadBookService;
import com.example.android_bookreader2.ui.contract.MainContract;
import com.example.android_bookreader2.ui.fragment.CommunityFragment;
import com.example.android_bookreader2.ui.fragment.FindFragment;
import com.example.android_bookreader2.ui.fragment.RecommendFragment;
import com.example.android_bookreader2.ui.presenter.MainActivityPresenter;
import com.example.android_bookreader2.utils.LogUtils;
import com.example.android_bookreader2.utils.SharedPreferencesUtil;
import com.example.android_bookreader2.utils.ToastUtils;
import com.example.android_bookreader2.view.GenderPopupWindow;
import com.example.android_bookreader2.view.LoginPopupWindow;
import com.example.android_bookreader2.view.RVPIndicator;
import com.google.gson.Gson;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/11/6.
 */

public class MainActivity extends BaseActivity implements MainContract.View, LoginPopupWindow.LoginTypeListener{

    @Bind(R.id.indicator)
    RVPIndicator mIndicator;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Inject
    MainActivityPresenter mPresenter;

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;
    private List<Fragment> mTabContents;
    private FragmentPagerAdapter mAdapter;
    private List<String> mDatas;

    private GenderPopupWindow genderPopupWindow;
    private LoginPopupWindow popupWindow;

    public static Tencent mTencent;
    public IUiListener loginListener;
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder().appComponent(appComponent)
                .build().inject(this);
    }

    @Override
    public void initToolBar() {
//        mCommonToolbar.setLogo(R.mipmap.logo);
        setTitle("汤帅");
    }
    public void pullSyncBookShelf() {
        mPresenter.syncBookShelf();
    }
    @Override
    public void initDatas() {
        startService(new Intent(this, DownloadBookService.class));

        mTencent = Tencent.createInstance("1105670298", MainActivity.this);

        mDatas= Arrays.asList(getResources().getStringArray(R.array.home_tabs));
        mTabContents = new ArrayList<>();
        mTabContents.add(new RecommendFragment());
        mTabContents.add(new CommunityFragment());
        mTabContents.add(new FindFragment());
        mAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }

            @Override
            public int getCount() {
                return mTabContents.size();
            }
        };
    }

    @Override
    public void configViews() {
        LogUtils.d("configViews执行了");
        mIndicator.setTabItemTitles(mDatas);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mIndicator.setViewPager(mViewPager, 0);

        mPresenter.attachView(this);
        mIndicator.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!SettingManager.getInstance().isUserChooseSex()&&(genderPopupWindow == null || !genderPopupWindow.isShowing())){
                    //第一次进来让用户选择性别，根据性别作出推荐的书籍。
                    showChooseSexPopupWindow();
                }else {
                    showDialog();
                    mPresenter.syncBookShelf();
                }
            }
        },500);
    }

    private void showChooseSexPopupWindow() {
        if (genderPopupWindow == null) {
            genderPopupWindow = new GenderPopupWindow(MainActivity.this);
        }
        if (!SettingManager.getInstance().isUserChooseSex()  && !genderPopupWindow.isShowing()) {
            genderPopupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void loginSuccess() {
        ToastUtils.showSingleToast("登陆成功");
    }

    @Override
    public void syncBookShelfCompleted() {
        LogUtils.d("syncBookShelfCompleted执行");
        dismissDialog();
        EventManager.refreshCollectionList();
    }

    @Override
    public void showError() {
        ToastUtils.showSingleToast("同步异常");
        dismissDialog();
    }

    @Override
    public void complete() {
    }
    /**
     * 创建菜单布局
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction()== KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showToast(getString(R.string.exit_tips));
                return true;
            }else {
                finish(); // 退出
            }
        }else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    /**
     * 菜单布局每项的点击事件
     * */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            case R.id.action_login:
                if (popupWindow == null) {
                    popupWindow = new LoginPopupWindow(this);
                    popupWindow.setLoginTypeListener(this);
                }
                popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
                break;
            case R.id.action_my_message:
                break;
            case R.id.action_sync_bookshelf: //同步书架
                showDialog();
                mPresenter.syncBookShelf();
                break;
            case R.id.action_scan_local_book://扫描本地书籍
                ScanLocalBookActivity.startActivity(this);
                break;
            case R.id.action_wifi_book://WIFI传书
                break;
            case R.id.action_feedback://意见反馈
                break;
            case R.id.action_night_mode://夜间模式
                if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false)) {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                recreate();
                break;
            case R.id.action_settings://设置
                SettingActivity.startActivity(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示item中的图片；
     *只要按一次Menu按键，就会调用一次。所以可以在这里动态的改变menu。
     * @param view
     * @param menu
     * @return
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if(menu!=null){
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                    m.setAccessible(true);
                    m.invoke(menu,true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    public void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onLogin(ImageView view, String type) {
        if (type.equals("QQ")) {
            if (!mTencent.isSessionValid()) {
                if (loginListener == null) loginListener = new BaseUIListener();
                mTencent.login(this, "all", loginListener);
            }
        }
    }

    public class BaseUIListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            JSONObject jsonObject = (JSONObject) o;
            String json = jsonObject.toString();
            Gson gson = new Gson();
            TencentLoginResult result = gson.fromJson(json, TencentLoginResult.class);
            LogUtils.e(result.toString());
            mPresenter.login(result.openid, result.access_token, "QQ");
        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadBookService.cancel();
        stopService(new Intent(this, DownloadBookService.class));
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

}
