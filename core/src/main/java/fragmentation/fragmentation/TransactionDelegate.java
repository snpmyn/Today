package fragmentation.fragmentation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentationMagician;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import fragmentation.fragmentation.exception.AfterSaveStateTransactionWarningException;
import fragmentation.fragmentation.helper.internal.ResultRecord;
import fragmentation.fragmentation.helper.internal.TransactionRecord;
import fragmentation.fragmentation.queue.ActionQueue;
import fragmentation.fragmentation.queue.BaseAction;
import fragmentation.value.FragmentationMagic;
import timber.log.Timber;

/**
 * @decs: TransactionDelegate
 * @author: 郑少鹏
 * @date: 2019/5/20 9:55
 */
class TransactionDelegate {
    static final int DEFAULT_POP_TO_ANIM = Integer.MAX_VALUE;
    static final String FRAGMENTATION_ARG_RESULT_RECORD = "fragment_arg_result_record";
    static final String FRAGMENTATION_ARG_ROOT_STATUS = "fragmentation_arg_root_status";
    static final String FRAGMENTATION_ARG_IS_SHARED_ELEMENT = "fragmentation_arg_is_shared_element";
    static final String FRAGMENTATION_ARG_CONTAINER = "fragmentation_arg_container";
    static final String FRAGMENTATION_ARG_REPLACE = "fragmentation_arg_replace";
    static final String FRAGMENTATION_ARG_CUSTOM_ENTER_ANIM = "fragmentation_arg_custom_enter_anim";
    static final String FRAGMENTATION_ARG_CUSTOM_EXIT_ANIM = "fragmentation_arg_custom_exit_anim";
    static final String FRAGMENTATION_ARG_CUSTOM_POP_EXIT_ANIM = "fragmentation_arg_custom_pop_exit_anim";
    static final String FRAGMENTATION_STATE_SAVE_ANIMATOR = "fragmentation_state_save_animator";
    static final String FRAGMENTATION_STATE_SAVE_IS_HIDDEN = "fragmentation_state_save_status";
    static final int TYPE_ADD = 0;
    static final int TYPE_ADD_RESULT = 1;
    static final int TYPE_ADD_WITHOUT_HIDE = 2;
    static final int TYPE_ADD_RESULT_WITHOUT_HIDE = 3;
    static final int TYPE_REPLACE = 10;
    static final int TYPE_REPLACE_DO_NOT_BACK = 11;
    private static final String FRAGMENTATION_STATE_SAVE_RESULT = "fragmentation_state_save_result";
    private final ISupportActivity mSupport;
    private final FragmentActivity mActivity;
    private final Handler mHandler;
    ActionQueue mActionQueue;

    TransactionDelegate(ISupportActivity support) {
        this.mSupport = support;
        this.mActivity = (FragmentActivity) support;
        mHandler = new Handler(Looper.getMainLooper());
        mActionQueue = new ActionQueue(mHandler);
    }

    private static <T> void checkNotNull(T value) {
        if (null == value) {
            throw new NullPointerException("toFragment == null");
        }
    }

    void post(final Runnable runnable) {
        mActionQueue.enqueue(new BaseAction() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    void loadRootTransaction(final FragmentManager fragmentManager, final int containerId, final ISupportFragment to, final boolean addToBackStack, final boolean allowAnimation) {
        enqueue(fragmentManager, new BaseAction(BaseAction.ACTION_LOAD) {
            @Override
            public void run() {
                bindContainerId(containerId, to);
                String toFragmentTag = to.getClass().getName();
                TransactionRecord transactionRecord = to.getSupportDelegate().mTransactionRecord;
                if (null != transactionRecord) {
                    if (null != transactionRecord.tag) {
                        toFragmentTag = transactionRecord.tag;
                    }
                }
                start(fragmentManager, null, to, toFragmentTag, !addToBackStack, null, allowAnimation, TYPE_REPLACE);
            }
        });
    }

    void loadMultipleRootTransaction(final FragmentManager fragmentManager, final int containerId, final int showPosition, final ISupportFragment... tos) {
        enqueue(fragmentManager, new BaseAction(BaseAction.ACTION_LOAD) {
            @Override
            public void run() {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                for (int i = 0; i < tos.length; i++) {
                    Fragment to = (Fragment) tos[i];
                    Bundle args = getArguments(to);
                    args.putInt(FRAGMENTATION_ARG_ROOT_STATUS, SupportFragmentDelegate.STATUS_ROOT_ANIM_DISABLE);
                    bindContainerId(containerId, tos[i]);
                    String toName = to.getClass().getName();
                    ft.add(containerId, to, toName);
                    if (i != showPosition) {
                        ft.hide(to);
                    }
                }
                supportCommit(fragmentManager, ft);
            }
        });
    }

    /**
     * Dispatch the start transaction.
     */
    void dispatchStartTransaction(final FragmentManager fragmentManager, final ISupportFragment from, final ISupportFragment to, final int requestCode, final int launchMode, final int type) {
        enqueue(fragmentManager, new BaseAction(launchMode == ISupportFragment.SINGLE_TASK ? BaseAction.ACTION_POP_MOCK : BaseAction.ACTION_NORMAL) {
            @Override
            public void run() {
                doDispatchStartTransaction(fragmentManager, from, to, requestCode, launchMode, type);
            }
        });
    }

    /**
     * Show showFragment then hide hideFragment.
     */
    void showHideFragment(final FragmentManager fragmentManager, final ISupportFragment showFragment, final ISupportFragment hideFragment) {
        enqueue(fragmentManager, new BaseAction() {
            @Override
            public void run() {
                doShowHideFragment(fragmentManager, showFragment, hideFragment);
            }
        });
    }

    /**
     * Start the target Fragment and pop itself.
     */
    void startWithPop(final FragmentManager fragmentManager, final ISupportFragment from, final ISupportFragment to) {
        enqueue(fragmentManager, new BaseAction(BaseAction.ACTION_POP_MOCK) {
            @Override
            public void run() {
                ISupportFragment top = getTopFragmentForStart(from, fragmentManager);
                if (null == top) {
                    throw new NullPointerException("There is no Fragment in the FragmentManager, maybe you need to call loadRootFragment() first!");
                }
                int containerId = top.getSupportDelegate().mContainerId;
                bindContainerId(containerId, to);
                handleAfterSaveInStateTransactionException(fragmentManager, "popTo()");
                FragmentationMagician.executePendingTransactionsAllowingStateLoss(fragmentManager);
                top.getSupportDelegate().mLockAnim = true;
                if (!FragmentationMagician.isStateSaved(fragmentManager)) {
                    mockStartWithPopAnim(SupportHelper.getTopFragment(fragmentManager), to, top.getSupportDelegate().mAnimHelper.popExitAnimation);
                }
                removeTopFragment(fragmentManager);
                FragmentationMagician.popBackStackAllowingStateLoss(fragmentManager);
                FragmentationMagician.executePendingTransactionsAllowingStateLoss(fragmentManager);
            }
        });
        dispatchStartTransaction(fragmentManager, from, to, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD);
    }

    void startWithPopTo(final FragmentManager fragmentManager, final ISupportFragment from, final ISupportFragment to, final String fragmentTag, final boolean includeTargetFragment) {
        enqueue(fragmentManager, new BaseAction(BaseAction.ACTION_POP_MOCK) {
            @Override
            public void run() {
                int flag = 0;
                if (includeTargetFragment) {
                    flag = FragmentManager.POP_BACK_STACK_INCLUSIVE;
                }
                List<Fragment> willPopFragments = SupportHelper.getWillPopFragments(fragmentManager, fragmentTag, includeTargetFragment);
                final ISupportFragment top = getTopFragmentForStart(from, fragmentManager);
                if (null == top) {
                    throw new NullPointerException("There is no Fragment in the FragmentManager, maybe you need to call loadRootFragment() first!");
                }
                int containerId = top.getSupportDelegate().mContainerId;
                bindContainerId(containerId, to);
                if (willPopFragments.isEmpty()) {
                    return;
                }
                handleAfterSaveInStateTransactionException(fragmentManager, "startWithPopTo()");
                FragmentationMagician.executePendingTransactionsAllowingStateLoss(fragmentManager);
                if (!FragmentationMagician.isStateSaved(fragmentManager)) {
                    mockStartWithPopAnim(SupportHelper.getTopFragment(fragmentManager), to, top.getSupportDelegate().mAnimHelper.popExitAnimation);
                }
                safePopTo(fragmentTag, fragmentManager, flag, willPopFragments);
            }
        });
        dispatchStartTransaction(fragmentManager, from, to, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD);
    }

    /**
     * remove
     */
    void remove(final FragmentManager fragmentManager, final Fragment fragment, final boolean showPreFragment) {
        enqueue(fragmentManager, new BaseAction(BaseAction.ACTION_POP, fragmentManager) {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).remove(fragment);
                if (showPreFragment) {
                    ISupportFragment preFragment = SupportHelper.getPreFragment(fragment);
                    if (preFragment instanceof Fragment) {
                        fragmentTransaction.show((Fragment) preFragment);
                    }
                }
                supportCommit(fragmentManager, fragmentTransaction);
            }
        });
    }

    /**
     * pop
     */
    void pop(final FragmentManager fragmentManager) {
        enqueue(fragmentManager, new BaseAction(BaseAction.ACTION_POP, fragmentManager) {
            @Override
            public void run() {
                handleAfterSaveInStateTransactionException(fragmentManager, "pop()");
                FragmentationMagician.popBackStackAllowingStateLoss(fragmentManager);
                removeTopFragment(fragmentManager);
            }
        });
    }

    private void removeTopFragment(FragmentManager fragmentManager) {
        try {
            // Safe popBackStack()
            ISupportFragment top = SupportHelper.getBackStackTopFragment(fragmentManager);
            if (null != top) {
                fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).remove((Fragment) top).commitAllowingStateLoss();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    void popQuiet(final FragmentManager fragmentManager, final Fragment fragment) {
        enqueue(fragmentManager, new BaseAction(BaseAction.ACTION_POP_MOCK) {
            @Override
            public void run() {
                mSupport.getSupportDelegate().mPopMultipleNoAnim = true;
                removeTopFragment(fragmentManager);
                FragmentationMagician.popBackStackAllowingStateLoss(fragmentManager, fragment.getTag(), 0);
                FragmentationMagician.popBackStackAllowingStateLoss(fragmentManager);
                FragmentationMagician.executePendingTransactionsAllowingStateLoss(fragmentManager);
                mSupport.getSupportDelegate().mPopMultipleNoAnim = false;
            }
        });
    }

    /**
     * Pop the last fragment transition from the manager's fragment pop stack.
     *
     * @param targetFragmentTag     tag
     * @param includeTargetFragment whether it includes targetFragment
     */
    void popTo(final String targetFragmentTag, final boolean includeTargetFragment, final Runnable afterPopTransactionRunnable, final FragmentManager fragmentManager, final int popAnim) {
        enqueue(fragmentManager, new BaseAction(BaseAction.ACTION_POP_MOCK) {
            @Override
            public void run() {
                doPopTo(targetFragmentTag, includeTargetFragment, fragmentManager, popAnim);
                if (null != afterPopTransactionRunnable) {
                    afterPopTransactionRunnable.run();
                }
            }
        });
    }

    void handleResultRecord(@NotNull Fragment from) {
        try {
            Bundle args = from.getArguments();
            if (null == args) {
                return;
            }
            final ResultRecord resultRecord = args.getParcelable(FRAGMENTATION_ARG_RESULT_RECORD);
            if (null == resultRecord) {
                return;
            }
            ISupportFragment targetFragment;
            targetFragment = (ISupportFragment) from.getParentFragmentManager().getFragment(from.getArguments(), FRAGMENTATION_STATE_SAVE_RESULT);
            if (null != targetFragment) {
                targetFragment.onFragmentResult(resultRecord.requestCode, resultRecord.resultCode, resultRecord.resultBundle);
            }
        } catch (IllegalStateException e) {
            // Fragment no longer exists
            Timber.e(e);
        }
    }

    /**
     * Dispatch the pop-event.
     * <p>
     * Priority of the top of the stack of Fragment.
     */
    boolean dispatchBackPressedEvent(ISupportFragment activeFragment) {
        if (null != activeFragment) {
            boolean result = activeFragment.onBackPressedSupport();
            if (result) {
                return true;
            }
            Fragment parentFragment = ((Fragment) activeFragment).getParentFragment();
            return dispatchBackPressedEvent((ISupportFragment) parentFragment);
        }
        return false;
    }

    private void enqueue(FragmentManager fragmentManager, BaseAction action) {
        if (null == fragmentManager) {
            Timber.d("FragmentManager is null, skip the action!");
            return;
        }
        mActionQueue.enqueue(action);
    }

    private void doDispatchStartTransaction(FragmentManager fragmentManager, ISupportFragment from, ISupportFragment to, int requestCode, int launchMode, int type) {
        checkNotNull(to);
        boolean flag = (((type == TYPE_ADD_RESULT) || (type == TYPE_ADD_RESULT_WITHOUT_HIDE)) && (null != from));
        if (flag) {
            if (!((Fragment) from).isAdded()) {
                Timber.d(((Fragment) from).getClass().getSimpleName(), " has not been attached yet! startForResult() converted to start()");
            } else {
                saveRequestCode(fragmentManager, (Fragment) from, (Fragment) to, requestCode);
            }
        }
        from = getTopFragmentForStart(from, fragmentManager);
        int containerId = getArguments((Fragment) to).getInt(FRAGMENTATION_ARG_CONTAINER, 0);
        if ((null == from) && (containerId == 0)) {
            Timber.d("There is no Fragment in the FragmentManager, maybe you need to call loadRootFragment()!");
            return;
        }
        if ((null != from) && (containerId == 0)) {
            bindContainerId(from.getSupportDelegate().mContainerId, to);
        }
        // process ExtraTransaction
        String toFragmentTag = to.getClass().getName();
        boolean doNotAddToBackStack = false;
        ArrayList<TransactionRecord.SharedElement> sharedElementList = null;
        TransactionRecord transactionRecord = to.getSupportDelegate().mTransactionRecord;
        if (null != transactionRecord) {
            if (null != transactionRecord.tag) {
                toFragmentTag = transactionRecord.tag;
            }
            doNotAddToBackStack = transactionRecord.doNotAddToBackStack;
            if (null != transactionRecord.sharedElementList) {
                sharedElementList = transactionRecord.sharedElementList;
            }
        }
        if (handleLaunchMode(fragmentManager, from, to, toFragmentTag, launchMode)) {
            return;
        }
        start(fragmentManager, from, to, toFragmentTag, doNotAddToBackStack, sharedElementList, false, type);
    }

    private ISupportFragment getTopFragmentForStart(ISupportFragment from, FragmentManager fragmentManager) {
        ISupportFragment top;
        if (null == from) {
            top = SupportHelper.getTopFragment(fragmentManager);
        } else {
            if (from.getSupportDelegate().mContainerId == 0) {
                Fragment fFrom = (Fragment) from;
                if ((null != fFrom.getTag()) && !fFrom.getTag().startsWith(FragmentationMagic.STRING_ANDROID_SWITCHER)) {
                    throw new IllegalStateException("Can't find container, please call loadRootFragment() first!");
                }
            }
            top = SupportHelper.getTopFragment(fragmentManager, from.getSupportDelegate().mContainerId);
        }
        return top;
    }

    private void start(@NotNull FragmentManager fragmentManager, final ISupportFragment from, ISupportFragment to, String toFragmentTag, boolean doNotAddToBackStack, ArrayList<TransactionRecord.SharedElement> sharedElementList, boolean allowRootFragmentAnim, int type) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        boolean addMode = ((type == TYPE_ADD) || (type == TYPE_ADD_RESULT) || (type == TYPE_ADD_WITHOUT_HIDE) || (type == TYPE_ADD_RESULT_WITHOUT_HIDE));
        Fragment fFrom = (Fragment) from;
        Fragment fTo = (Fragment) to;
        Bundle args = getArguments(fTo);
        args.putBoolean(FRAGMENTATION_ARG_REPLACE, !addMode);
        if (null == sharedElementList) {
            if (addMode) {
                // Replace mode forbidden animation, the replace animations exist overlapping Bug on support-v4.
                TransactionRecord record = to.getSupportDelegate().mTransactionRecord;
                if ((null != record) && (record.targetFragmentEnter != Integer.MIN_VALUE)) {
                    ft.setCustomAnimations(record.targetFragmentEnter, record.currentFragmentPopExit, record.currentFragmentPopEnter, record.targetFragmentExit);
                    args.putInt(FRAGMENTATION_ARG_CUSTOM_ENTER_ANIM, record.targetFragmentEnter);
                    args.putInt(FRAGMENTATION_ARG_CUSTOM_EXIT_ANIM, record.targetFragmentExit);
                    args.putInt(FRAGMENTATION_ARG_CUSTOM_POP_EXIT_ANIM, record.currentFragmentPopExit);
                } else {
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                }
            } else {
                args.putInt(FRAGMENTATION_ARG_ROOT_STATUS, SupportFragmentDelegate.STATUS_ROOT_ANIM_DISABLE);
            }
        } else {
            args.putBoolean(FRAGMENTATION_ARG_IS_SHARED_ELEMENT, true);
            for (TransactionRecord.SharedElement item : sharedElementList) {
                ft.addSharedElement(item.sharedElement, item.sharedName);
            }
        }
        if (null == from) {
            ft.replace(args.getInt(FRAGMENTATION_ARG_CONTAINER), fTo, toFragmentTag);
            if (!addMode) {
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                args.putInt(FRAGMENTATION_ARG_ROOT_STATUS, allowRootFragmentAnim ? SupportFragmentDelegate.STATUS_ROOT_ANIM_ENABLE : SupportFragmentDelegate.STATUS_ROOT_ANIM_DISABLE);
            }
        } else {
            if (addMode) {
                ft.add(from.getSupportDelegate().mContainerId, fTo, toFragmentTag);
                if ((type != TYPE_ADD_WITHOUT_HIDE) && (type != TYPE_ADD_RESULT_WITHOUT_HIDE)) {
                    ft.hide(fFrom);
                }
            } else {
                ft.replace(from.getSupportDelegate().mContainerId, fTo, toFragmentTag);
            }
        }
        if (!doNotAddToBackStack && (type != TYPE_REPLACE_DO_NOT_BACK)) {
            ft.addToBackStack(toFragmentTag);
        }
        supportCommit(fragmentManager, ft);
    }

    private void bindContainerId(int containerId, ISupportFragment to) {
        Bundle args = getArguments((Fragment) to);
        args.putInt(FRAGMENTATION_ARG_CONTAINER, containerId);
    }

    private @NotNull Bundle getArguments(@NotNull Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (null == bundle) {
            bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        return bundle;
    }

    private void supportCommit(FragmentManager fragmentManager, @NotNull FragmentTransaction transaction) {
        handleAfterSaveInStateTransactionException(fragmentManager, "commit()");
        transaction.commitAllowingStateLoss();
    }

    private boolean handleLaunchMode(FragmentManager fragmentManager, ISupportFragment topFragment, final ISupportFragment to, String toFragmentTag, int launchMode) {
        if (null == topFragment) {
            return false;
        }
        final ISupportFragment stackToFragment = SupportHelper.findBackStackFragment(to.getClass(), toFragmentTag, fragmentManager);
        if (null == stackToFragment) {
            return false;
        }
        if (launchMode == ISupportFragment.SINGLE_TOP) {
            if ((to == topFragment) || to.getClass().getName().equals(topFragment.getClass().getName())) {
                handleNewBundle(to, stackToFragment);
                return true;
            }
        } else if (launchMode == ISupportFragment.SINGLE_TASK) {
            doPopTo(toFragmentTag, false, fragmentManager, DEFAULT_POP_TO_ANIM);
            mHandler.post(() -> handleNewBundle(to, stackToFragment));
            return true;
        }
        return false;
    }

    private void handleNewBundle(@NotNull ISupportFragment toFragment, ISupportFragment stackToFragment) {
        Bundle argsNewBundle = toFragment.getSupportDelegate().mNewBundle;
        Bundle args = getArguments((Fragment) toFragment);
        if (args.containsKey(FRAGMENTATION_ARG_CONTAINER)) {
            args.remove(FRAGMENTATION_ARG_CONTAINER);
        }
        if (null != argsNewBundle) {
            args.putAll(argsNewBundle);
        }
        stackToFragment.onNewBundle(args);
    }

    private void doShowHideFragment(FragmentManager fragmentManager, ISupportFragment showFragment, ISupportFragment hideFragment) {
        if (showFragment == hideFragment) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction().show((Fragment) showFragment);
        if (null == hideFragment) {
            List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fragmentManager);
            for (Fragment fragment : fragmentList) {
                if ((null != fragment) && (fragment != showFragment)) {
                    ft.hide(fragment);
                }
            }
        } else {
            ft.hide((Fragment) hideFragment);
        }
        supportCommit(fragmentManager, ft);
    }

    private void doPopTo(final String targetFragmentTag, boolean includeTargetFragment, FragmentManager fragmentManager, int popAnim) {
        handleAfterSaveInStateTransactionException(fragmentManager, "popTo()");
        Fragment targetFragment = fragmentManager.findFragmentByTag(targetFragmentTag);
        if (null == targetFragment) {
            Timber.d("Pop failure! Can't find FragmentTag:" + targetFragmentTag + " in the FragmentManager's Stack.");
            return;
        }
        int flag = 0;
        if (includeTargetFragment) {
            flag = FragmentManager.POP_BACK_STACK_INCLUSIVE;
        }
        List<Fragment> willPopFragments = SupportHelper.getWillPopFragments(fragmentManager, targetFragmentTag, includeTargetFragment);
        if (willPopFragments.isEmpty()) {
            return;
        }
        Fragment top = willPopFragments.get(0);
        mockPopToAnim(top, targetFragmentTag, fragmentManager, flag, willPopFragments, popAnim);
    }

    /**
     * Save requestCode.
     */
    private void saveRequestCode(@NotNull FragmentManager fragmentManager, Fragment from, Fragment to, int requestCode) {
        Bundle bundle = getArguments(to);
        ResultRecord resultRecord = new ResultRecord();
        resultRecord.requestCode = requestCode;
        bundle.putParcelable(FRAGMENTATION_ARG_RESULT_RECORD, resultRecord);
        fragmentManager.putFragment(bundle, FRAGMENTATION_STATE_SAVE_RESULT, from);
    }

    private void mockPopToAnim(Fragment from, String targetFragmentTag, FragmentManager fragmentManager, int flag, List<Fragment> willPopFragments, int popAnim) {
        if (!(from instanceof ISupportFragment)) {
            safePopTo(targetFragmentTag, fragmentManager, flag, willPopFragments);
            return;
        }
        final ISupportFragment fromSupport = (ISupportFragment) from;
        final ViewGroup container = findContainerById(from, fromSupport.getSupportDelegate().mContainerId);
        if (null == container) {
            return;
        }
        final View fromView = from.getView();
        if (null == fromView) {
            return;
        }
        container.removeViewInLayout(fromView);
        final ViewGroup mock = addMockView(fromView, container);
        safePopTo(targetFragmentTag, fragmentManager, flag, willPopFragments);
        Animation animation;
        if (popAnim == DEFAULT_POP_TO_ANIM) {
            animation = fromSupport.getSupportDelegate().getExitAnim();
            if (null == animation) {
                animation = new Animation() {
                };
            }
        } else if (popAnim == 0) {
            animation = new Animation() {
            };
        } else {
            animation = AnimationUtils.loadAnimation(mActivity, popAnim);
        }
        fromView.startAnimation(animation);
        mHandler.postDelayed(() -> {
            try {
                mock.removeViewInLayout(fromView);
                container.removeViewInLayout(mock);
            } catch (Exception ignored) {
            }
        }, animation.getDuration());
    }

    private void safePopTo(String fragmentTag, final @NotNull FragmentManager fragmentManager, int flag, @NotNull List<Fragment> willPopFragments) {
        mSupport.getSupportDelegate().mPopMultipleNoAnim = true;
        FragmentTransaction transaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        for (Fragment fragment : willPopFragments) {
            transaction.remove(fragment);
        }
        transaction.commitAllowingStateLoss();
        FragmentationMagician.popBackStackAllowingStateLoss(fragmentManager, fragmentTag, flag);
        FragmentationMagician.executePendingTransactionsAllowingStateLoss(fragmentManager);
        mSupport.getSupportDelegate().mPopMultipleNoAnim = false;
    }

    @NonNull
    private ViewGroup addMockView(View fromView, @NotNull ViewGroup container) {
        ViewGroup mock = new ViewGroup(mActivity) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
            }
        };
        mock.addView(fromView);
        container.addView(mock);
        return mock;
    }

    private @Nullable ViewGroup findContainerById(@NotNull Fragment fragment, int containerId) {
        if (null == fragment.getView()) {
            return null;
        }
        View container;
        Fragment parentFragment = fragment.getParentFragment();
        if (null != parentFragment) {
            if (null != parentFragment.getView()) {
                container = parentFragment.getView().findViewById(containerId);
            } else {
                container = findContainerById(parentFragment, containerId);
            }
        } else {
            container = mActivity.findViewById(containerId);
        }
        if (container instanceof ViewGroup) {
            return (ViewGroup) container;
        }
        return null;
    }

    private void mockStartWithPopAnim(final @NotNull ISupportFragment from, ISupportFragment to, final Animation exitAnim) {
        final Fragment fragment = (Fragment) from;
        final ViewGroup container = findContainerById(fragment, from.getSupportDelegate().mContainerId);
        if (null == container) {
            return;
        }
        final View fromView = fragment.getView();
        if (null == fromView) {
            return;
        }
        container.removeViewInLayout(fromView);
        final ViewGroup mock = addMockView(fromView, container);
        to.getSupportDelegate().mEnterAnimListener = () -> {
            fromView.startAnimation(exitAnim);
            mHandler.postDelayed(() -> {
                try {
                    mock.removeViewInLayout(fromView);
                    container.removeViewInLayout(mock);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }, exitAnim.getDuration());
        };
    }

    private void handleAfterSaveInStateTransactionException(FragmentManager fragmentManager, String action) {
        boolean stateSaved = FragmentationMagician.isStateSaved(fragmentManager);
        if (stateSaved) {
            AfterSaveStateTransactionWarningException e = new AfterSaveStateTransactionWarningException(action);
            if (null != Fragmentation.getDefault().getExceptionHandler()) {
                Fragmentation.getDefault().getExceptionHandler().onException(e);
            }
        }
    }
}
