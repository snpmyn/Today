package spruce.dynamics

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Choreographer
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import androidx.collection.SimpleArrayMap

/**
 * This custom handler handles the timing pulse that is shared by all active ValueAnimators.
 * This approach ensures that the setting of animation values will happen on the
 * same thread that animations start on, and that all animations will share the same times for
 * calculating their values, which makes synchronizing animations possible.
 *
 * The handler uses the Choreographer by default for doing periodic callbacks. A custom
 * AnimationFrameCallbackProvider can be set on the handler to provide timing pulse that
 * may be independent of UI frame update. This could be useful in testing.
 */
class AnimationHandler
/**
 * The constructor of the AnimationHandler with [FrameCallbackScheduler] which is handle
 * running the given Runnable on the next frame.
 *
 * @param scheduler The scheduler for this handler to run the given runnable.
 */(
    @set:VisibleForTesting @set
    /**
     * Sets the FrameCallbackScheduler for this handler.
     * Used in testing only.
     *//* synthetic access */
    :RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) @get:VisibleForTesting @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) var scheduler: FrameCallbackScheduler
) {
    /**
     * Callbacks that receives notifications for animation timing
     */
    interface AnimationFrameCallback {
        /**
         * Run animation based on the frame time.
         *
         * @param frameTime The frame start time
         */
        fun doAnimationFrame(frameTime: Long): Boolean
    }

    /**
     * A scheduler that runs the given Runnable on the next frame.
     */
    interface FrameCallbackScheduler {
        /**
         * Callbacks on new frame arrived.
         *
         * @param frameCallback The runnable of new frame should be posted
         */
        fun postFrameCallback(frameCallback: Runnable)

        /**
         * Returns whether the current thread is the same as the thread that the scheduler is
         * running on.
         *
         * @return true if the scheduler is running on the same thread as the current thread.
         */
        val isCurrentThread: Boolean
    }

    /**
     * This class is responsible for interacting with the available frame provider by either
     * registering frame callback or posting runnable, and receiving a callback for when a
     * new frame has arrived. This dispatcher class then notifies all the on-going animations of
     * the new frame, so that they can update animation values as needed.
     */
    private inner class AnimationCallbackDispatcher {
        /**
         * Notifies all the on-going animations of the new frame.
         */
        fun  /* synthetic access */dispatchAnimationFrame() {
            mCurrentFrameTime = SystemClock.uptimeMillis()
            doAnimationFrame(mCurrentFrameTime)
            if (mAnimationCallbacks.isNotEmpty()) {
                scheduler.postFrameCallback(mRunnable)
            }
        }
    }

    /**
     * Internal per-thread collections used to avoid set collisions as animations start and end
     * while being processed.
     */
    private val mDelayedCallbackStartTime = SimpleArrayMap<AnimationFrameCallback, Long>()
    /* synthetic access */ val mAnimationCallbacks = ArrayList<AnimationFrameCallback?>()
    /* synthetic access */private val mCallbackDispatcher = AnimationCallbackDispatcher()
    /* synthetic access */private val mRunnable =
        Runnable { mCallbackDispatcher.dispatchAnimationFrame() }

    /**
     * Gets the FrameCallbackScheduler in this handler.
     * Used in testing only.
     * @return The FrameCallbackScheduler in this handler
     * @hide
     */
    var mCurrentFrameTime: /* synthetic access */Long = 0
    private var mListDirty = false

    /**
     * Register to get a callback on the next frame after the delay.
     */
    fun addAnimationFrameCallback(callback: AnimationFrameCallback, delay: Long) {
        if (mAnimationCallbacks.isEmpty()) {
            scheduler.postFrameCallback(mRunnable)
        }
        if (!mAnimationCallbacks.contains(callback)) {
            mAnimationCallbacks.add(callback)
        }
        if (delay > 0) {
            mDelayedCallbackStartTime.put(callback, SystemClock.uptimeMillis() + delay)
        }
    }

    /**
     * Removes the given callback from the list, so it will no longer be called for frame related timing.
     */
    fun removeCallback(callback: AnimationFrameCallback?) {
        // 确定回调非 null
        // 可用 !! 强制转换
        mDelayedCallbackStartTime.remove(callback!!)
        val id = mAnimationCallbacks.indexOf(callback)
        if (id >= 0) {
            mAnimationCallbacks[id] = null
            mListDirty = true
        }
    }

    fun  /* synthetic access */doAnimationFrame(frameTime: Long) {
        val currentTime = SystemClock.uptimeMillis()
        for (i in mAnimationCallbacks.indices) {
            val callback = mAnimationCallbacks[i] ?: continue
            if (isCallbackDue(callback, currentTime)) {
                callback.doAnimationFrame(frameTime)
            }
        }
        cleanUpList()
    }

    /**
     * Returns whether the current thread is the same thread as the animation handler frame scheduler.
     * @return true the current thread is the same thread as the animation handler frame scheduler.
     */
    val isCurrentThread: Boolean
        get() = scheduler.isCurrentThread

    /**
     * Remove the callbacks from mDelayedCallbackStartTime once they have passed the initial delay so that they can start getting frame callbacks.
     * @return true if they have passed the initial delay or have no delay, false otherwise.
     */
    private fun isCallbackDue(callback: AnimationFrameCallback, currentTime: Long): Boolean {
        val startTime = mDelayedCallbackStartTime[callback] ?: return true
        if (startTime < currentTime) {
            mDelayedCallbackStartTime.remove(callback)
            return true
        }
        return false
    }

    private fun cleanUpList() {
        if (mListDirty) {
            for (i in mAnimationCallbacks.indices.reversed()) {
                if (mAnimationCallbacks[i] == null) {
                    mAnimationCallbacks.removeAt(i)
                }
            }
            mListDirty = false
        }
    }

    /**
     * Default provider of timing pulse that uses Choreographer for frame callbacks.
     */
    @VisibleForTesting
    internal class FrameCallbackScheduler16 : FrameCallbackScheduler {
        private val mChoreographer = Choreographer.getInstance()
        private val mLooper = Looper.myLooper()
        override fun postFrameCallback(frameCallback: Runnable) {
            mChoreographer.postFrameCallback { frameCallback.run() }
        }

        override val isCurrentThread: Boolean
            get() = Thread.currentThread() === mLooper!!.thread
    }

    /**
     * Frame provider for ICS and ICS-MR1 releases.
     * The frame callback is achieved via posting a Runnable to the main thread Handler with a delay.
     */
    @VisibleForTesting
    internal class FrameCallbackScheduler14 : FrameCallbackScheduler {
        private val mHandler = Handler(Looper.myLooper()!!)
        private var mLastFrameTime: Long = 0
        override fun postFrameCallback(frameCallback: Runnable) {
            var delay = FRAME_DELAY_MS - (SystemClock.uptimeMillis() - mLastFrameTime)
            delay = delay.coerceAtLeast(0)
            mHandler.postDelayed({
                mLastFrameTime = SystemClock.uptimeMillis()
                frameCallback.run()
            }, delay)
        }

        override val isCurrentThread: Boolean
            get() = Thread.currentThread() === mHandler.looper.thread
    }

    companion object {
        private const val FRAME_DELAY_MS: Long = 10
        private val sAnimatorHandler = ThreadLocal<AnimationHandler?>()

        @JvmStatic
        val instance: AnimationHandler?
            get() {
                if (sAnimatorHandler.get() == null) {
                    val handler = AnimationHandler(
                        FrameCallbackScheduler16()
                    )
                    sAnimatorHandler.set(handler)
                }
                return sAnimatorHandler.get()
            }
    }
}