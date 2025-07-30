package fragmentation.fragmentation.queue;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;

import fragmentation.fragmentation.ISupportFragment;
import fragmentation.fragmentation.SupportHelper;

/**
 * @decs: ActionQueue
 * The queue of perform action.
 * @author: 郑少鹏
 * @date: 2019/5/20 9:38
 */
public class ActionQueue {
    private final Queue<BaseAction> mQueue = new LinkedList<>();
    private final Handler mMainHandler;

    public ActionQueue(Handler mainHandler) {
        this.mMainHandler = mainHandler;
    }

    public void enqueue(final BaseAction baseAction) {
        if (areThrottleBack(baseAction)) {
            return;
        }
        if ((baseAction.action == BaseAction.ACTION_LOAD) && mQueue.isEmpty() && (Thread.currentThread() == Looper.getMainLooper().getThread())) {
            baseAction.run();
            return;
        }
        mMainHandler.post(() -> enqueueAction(baseAction));
    }

    private void enqueueAction(BaseAction baseAction) {
        mQueue.add(baseAction);
        if (mQueue.size() == 1) {
            handleAction();
        }
    }

    private void handleAction() {
        if (mQueue.isEmpty()) {
            return;
        }
        BaseAction baseAction = mQueue.peek();
        if (null != baseAction) {
            baseAction.run();
            executeNextAction(baseAction);
        }
    }

    private void executeNextAction(@NotNull BaseAction baseAction) {
        if (baseAction.action == BaseAction.ACTION_POP) {
            ISupportFragment top = SupportHelper.getBackStackTopFragment(baseAction.fragmentManager);
            baseAction.duration = ((null == top) ? BaseAction.DEFAULT_POP_TIME : top.getSupportDelegate().getExitAnimDuration());
        }
        mMainHandler.postDelayed(() -> {
            mQueue.poll();
            handleAction();
        }, baseAction.duration);
    }

    @Contract(pure = true)
    private boolean areThrottleBack(@NotNull BaseAction baseAction) {
        if (baseAction.action == BaseAction.ACTION_BACK) {
            BaseAction head = mQueue.peek();
            return ((null != head) && (head.action == BaseAction.ACTION_POP));
        }
        return false;
    }
}
