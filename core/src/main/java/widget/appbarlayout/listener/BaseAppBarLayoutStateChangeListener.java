package widget.appbarlayout.listener;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Created on 2019/5/27.
 *
 * @author 郑少鹏
 * @desc AppBarLayout 状变监听
 */
public abstract class BaseAppBarLayoutStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    private State mCurrentState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    /**
     * 状变
     *
     * @param appBarLayout AppBarLayout
     * @param state        State
     */
    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);

    public enum State {
        /**
         * 展开
         */
        EXPANDED,
        /**
         * 折叠
         */
        COLLAPSED,
        /**
         * 中间
         */
        IDLE
    }
}