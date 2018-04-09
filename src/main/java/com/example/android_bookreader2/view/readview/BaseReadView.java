package com.example.android_bookreader2.view.readview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.example.android_bookreader2.bean.BookMixAToc;
import com.example.android_bookreader2.manager.SettingManager;
import com.example.android_bookreader2.manager.ThemeManager;
import com.example.android_bookreader2.utils.LogUtils;
import com.example.android_bookreader2.utils.ScreenUtils;
import com.example.android_bookreader2.utils.ToastUtils;

import java.util.List;

/**
 * Created by tangpeng on 2017/12/19.
 */

public abstract class BaseReadView extends View{
    Scroller mScroller;
    protected PageFactory pagefactory = null;
    protected OnReadStateChangeListener listener;
    protected String bookId;
    public boolean isPrepared = false;
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected Bitmap mCurPageBitmap, mNextPageBitmap;
    protected Canvas mCurrentPageCanvas, mNextPageCanvas;
    private int mCornerX = 1; // 拖拽点对应的页脚
    private int mCornerY = 1;
    private Path mPath0;
    private Path mPath1;
    public BaseReadView(Context context, String bookId, List<BookMixAToc.mixToc.Chapters> chaptersList,
                        OnReadStateChangeListener listener) {
        super(context);
        this.listener = listener;
        this.bookId = bookId;

        mScreenWidth = ScreenUtils.getScreenWidth();
        mScreenHeight = ScreenUtils.getScreenHeight();

        mCurPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        mCurrentPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);

        mScroller = new Scroller(getContext());

        pagefactory = new PageFactory(getContext(), bookId, chaptersList);
        pagefactory.setOnReadStateChangeListener(listener);
    }

    public synchronized void init(int theme) {
        if (!isPrepared) {
            try {
                pagefactory.setBgBitmap(ThemeManager.getThemeDrawable(theme));
                // 自动跳转到上次阅读位置
                int pos[] = SettingManager.getInstance().getReadProgress(bookId);
                //根据上次阅读位置打开书籍到内存中
                int ret = pagefactory.openBook(pos[0], new int[]{pos[1], pos[2]});
                LogUtils.i("上次阅读位置：chapter=" + pos[0] + " startPos=" + pos[1] + " endPos=" + pos[2]);
                if (ret == 0) {
                    listener.onLoadChapterFailure(pos[0]);
                    return;
                }
                pagefactory.onDraw(mCurrentPageCanvas);
                //postInvalidate()在工作者线程中被调用
                postInvalidate();
            }catch (Exception e){
            }
            isPrepared = true;
        }
    }

    public void jumpToChapter(int chapter) {
        resetTouchPoint();
        pagefactory.openBook(chapter, new int[]{0, 0});
        pagefactory.onDraw(mCurrentPageCanvas);
        pagefactory.onDraw(mNextPageCanvas);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calcPoints();
        drawCurrentPageArea(canvas);
        drawNextPageAreaAndShadow(canvas);
        drawCurrentPageShadow(canvas);
        drawCurrentBackArea(canvas);
    }
    private int dx, dy;
    private long et = 0;
    private boolean cancel = false;
    private boolean center = false;
    protected PointF mTouch = new PointF();
    protected float actiondownX, actiondownY;
    protected float touch_down = 0; // 当前触摸点与按下时的点的差值
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                et = System.currentTimeMillis();
                dx = (int) e.getX();
                dy = (int) e.getY();
                mTouch.x = dx;
                mTouch.y = dy;
                actiondownX = dx;
                actiondownY = dy;
                touch_down = 0;
                pagefactory.onDraw(mCurrentPageCanvas);
                if (actiondownX >= mScreenWidth / 3 && actiondownX <= mScreenWidth * 2 / 3
                        && actiondownY >= mScreenHeight / 3 && actiondownY <= mScreenHeight * 2 / 3) {
                    center = true;
                } else {
                    center = false;
                    calcCornerXY(actiondownX, actiondownY);
                    if (actiondownX < mScreenWidth / 2) {// 从左翻
                        BookStatus status = pagefactory.prePage();
                        if (status == BookStatus.NO_PRE_PAGE) {
                            ToastUtils.showSingleToast("没有上一页啦");
                            return false;
                        } else if (status == BookStatus.LOAD_SUCCESS) {
                            abortAnimation();
                            pagefactory.onDraw(mNextPageCanvas);
                        } else {
                            return false;
                        }
                    } else if (actiondownX >= mScreenWidth / 2) {// 从右翻
                        BookStatus status = pagefactory.nextPage();
                        if (status == BookStatus.NO_NEXT_PAGE) {
                            ToastUtils.showSingleToast("没有下一页啦");
                            return false;
                        } else if (status == BookStatus.LOAD_SUCCESS) {
                            abortAnimation();
                            pagefactory.onDraw(mNextPageCanvas);
                        } else {
                            return false;
                        }
                    }
                    //隐藏状态栏
                    listener.onFlip();
                    setBitmaps(mCurPageBitmap, mNextPageBitmap);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (center)
                    break;
                int mx = (int) e.getX();
                int my = (int) e.getY();
                cancel = (actiondownX < mScreenWidth / 2 && mx < mTouch.x) || (actiondownX > mScreenWidth / 2 && mx > mTouch.x);
                mTouch.x = mx;
                mTouch.y = my;
                touch_down = mTouch.x - actiondownX;
                this.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                long t = System.currentTimeMillis();
                int ux = (int) e.getX();
                int uy = (int) e.getY();
                if (center) { // ACTION_DOWN的位置在中间，则不响应滑动事件
                    resetTouchPoint();
                    if (Math.abs(ux - actiondownX) < 5 && Math.abs(uy - actiondownY) < 5) {
                        listener.onCenterClick();
                        return false;
                    }
                    break;
                }
                if ((Math.abs(ux - dx) < 10) && (Math.abs(uy - dy) < 10)) {
                    if ((t - et < 1000)) { // 单击
                        if(this instanceof NoAimWidget) {
                            ((NoAimWidget)this).startAnimation(0);
                        }else{
                            startAnimation();
                        }
                    } else { // 长按
                        pagefactory.cancelPage();
                        restoreAnimation();
                    }
                    postInvalidate();
                    return true;
                }
                if (cancel) {
                    pagefactory.cancelPage();
                    restoreAnimation();
                    postInvalidate();
                } else {
                    startAnimation();
                    postInvalidate();
                }
                cancel = false;
                center = false;
                break;
            default:
                break;
        }
        return true;
    }
    /**
     * 复位触摸点位
     */
    protected void resetTouchPoint() {
        mTouch.x = 0.1f;
        mTouch.y = 0.1f;
        touch_down = 0;
        calcCornerXY(mTouch.x, mTouch.y);
    }
    protected abstract void drawNextPageAreaAndShadow(Canvas canvas);

    protected abstract void drawCurrentPageShadow(Canvas canvas);

    protected abstract void drawCurrentBackArea(Canvas canvas);

    protected abstract void drawCurrentPageArea(Canvas canvas);

    protected abstract void calcPoints();

    protected abstract void calcCornerXY(float x, float y);

    /**
     * 开启翻页
     */
    protected abstract void startAnimation();

    /**
     * 停止翻页动画（滑到一半调用停止的话  翻页效果会卡住 可调用#{restoreAnimation} 还原效果）
     */
    protected abstract void abortAnimation();

    /**
     * 还原翻页
     */
    protected abstract void restoreAnimation();

    protected abstract void setBitmaps(Bitmap mCurPageBitmap, Bitmap mNextPageBitmap);

    public abstract void setTheme(int theme);


    public synchronized void setTextColor(int textColor, int titleColor) {
        resetTouchPoint();
        pagefactory.setTextColor(textColor, titleColor);
        if (isPrepared) {
            pagefactory.onDraw(mCurrentPageCanvas);
            pagefactory.onDraw(mNextPageCanvas);
            postInvalidate();
        }
    }


    public void setBattery(int battery) {
        pagefactory.setBattery(battery);
        if (isPrepared) {
            pagefactory.onDraw(mCurrentPageCanvas);
            postInvalidate();
        }
    }

    public void setTime(String time) {
        pagefactory.setTime(time);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (pagefactory != null) {
            pagefactory.recycle();
        }
        if (mCurPageBitmap != null && !mCurPageBitmap.isRecycled()) {
            mCurPageBitmap.recycle();
            mCurPageBitmap = null;
            LogUtils.d("mCurPageBitmap recycle");
        }

        if (mNextPageBitmap != null && !mNextPageBitmap.isRecycled()) {
            mNextPageBitmap.recycle();
            mNextPageBitmap = null;
            LogUtils.d("mNextPageBitmap recycle");
        }
    }

    public synchronized void setFontSize(final int fontSizePx) {
        resetTouchPoint();
        pagefactory.setTextFont(fontSizePx);
        if (isPrepared) {
            pagefactory.onDraw(mCurrentPageCanvas);
            pagefactory.onDraw(mNextPageCanvas);
            //SettingManager.getInstance().saveFontSize(bookId, fontSizePx);
            SettingManager.getInstance().saveFontSize(fontSizePx);
            postInvalidate();
        }
    }


}
