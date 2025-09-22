package widget.word;

import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @decs: 舒适单词循环器
 * @author: 郑少鹏
 * @date: 2025/9/21 21:24
 * @version: v 1.0
 * <p>
 * 固定间隔更新
 * 只有一个 TextView 不断更新
 * new ComfortWordLooper(ComfortWordLooper.Mode.SINGLE_LINE, 4000, heartBoxActivityTvDate);
 * {@link ComfortWordLooper#ComfortWordLooper(Mode, int, TextView...)}
 * <p>
 * 固定间隔更新
 * 一次性更新三个 TextView
 * new ComfortWordLooper(ComfortWordLooper.Mode.THREE_LINES, 4000, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate);
 * {@link ComfortWordLooper#ComfortWordLooper(Mode, int, TextView...)}
 * <p>
 * 每 TextView 由 scheduleNext(item) 独立调度
 * 每 MultiItem 都有一独立 Runnable task
 * 调度间隔不固定
 * 在 minInterval 和 maxInterval 间随机
 * new ComfortWordLooper(2000, 4000, heartBoxActivityTvDurationSize, heartBoxActivityTvTime, heartBoxActivityTvDate);
 * {@link ComfortWordLooper#ComfortWordLooper(int, int, TextView...)}
 */
public class ComfortWordLooper {
    public enum Mode {THREE_LINES, SINGLE_LINE, MULTI_RANDOM}

    private static class MultiItem {
        TextView textView;
        List<Integer> indexPool = new ArrayList<>();
        int currentIndex = 0;
        Runnable task;

        MultiItem(TextView tv) {
            textView = tv;
            reset();
            task = null;
        }

        void reset() {
            indexPool.clear();
            for (int i = 0; i < ComfortWordProvider.size(); i++) {
                indexPool.add(i);
            }
            Collections.shuffle(indexPool);
            currentIndex = 0;
        }

        String getNext() {
            if (currentIndex >= indexPool.size()) {
                reset();
            }
            return ComfortWordProvider.getWordAt(indexPool.get(currentIndex++));
        }
    }

    private final Mode mode;
    private final TextView[] textViews;
    private final List<MultiItem> multiItems = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final int minInterval, maxInterval, interval;
    private volatile boolean areRunning = false;

    public ComfortWordLooper(Mode mode, int intervalMillis, TextView... views) {
        if (mode == Mode.MULTI_RANDOM) {
            throw new IllegalArgumentException("MULTI_RANDOM requires min/max constructor");
        }
        this.mode = mode;
        this.interval = intervalMillis;
        this.textViews = views;
        this.minInterval = this.maxInterval = 0;
        if ((mode == Mode.SINGLE_LINE) && (views.length != 1)) {
            throw new IllegalArgumentException("SINGLE_LINE requires 1 TextView");
        }
    }

    public ComfortWordLooper(int minIntervalMillis, int maxIntervalMillis, TextView... textViews) {
        if ((minIntervalMillis < 0) || (maxIntervalMillis < minIntervalMillis)) {
            throw new IllegalArgumentException("invalid interval range");
        }
        this.mode = Mode.MULTI_RANDOM;
        this.minInterval = minIntervalMillis;
        this.maxInterval = maxIntervalMillis;
        this.interval = 0;
        this.textViews = null;
        for (TextView textView : textViews) {
            if (null != textView) {
                multiItems.add(new MultiItem(textView));
            }
        }
    }

    public void start() {
        if (areRunning) {
            return;
        }
        areRunning = true;
        switch (mode) {
            case THREE_LINES:
            case SINGLE_LINE:
                handler.post(runnableSingleOrThree);
                break;
            case MULTI_RANDOM:
                for (MultiItem item : multiItems) scheduleNext(item);
                break;
        }
    }

    public void stop() {
        areRunning = false;
        handler.removeCallbacks(runnableSingleOrThree);
        for (MultiItem multiItem : multiItems) {
            if (null != multiItem.task) {
                handler.removeCallbacks(multiItem.task);
                multiItem.task = null;
            }
            if (null != multiItem.textView) {
                multiItem.textView.post(() -> {
                    multiItem.textView.clearAnimation();
                    multiItem.textView.setAlpha(1f);
                });
            }
        }
        if (null != textViews) {
            for (TextView textView : textViews) {
                if (null != textView) {
                    textView.post(() -> {
                        textView.clearAnimation();
                        textView.setAlpha(1f);
                    });
                }
            }
        }
    }

    public void restart() {
        stop();
        start();
    }

    private final Runnable runnableSingleOrThree = new Runnable() {
        @Override
        public void run() {
            if (!areRunning) {
                return;
            }
            if (mode == Mode.THREE_LINES) {
                String[] words = ComfortWordProvider.getNextThree();
                for (int i = 0; i < Math.min(textViews.length, words.length); i++) {
                    animateText(textViews[i], words[i]);
                }
            } else {
                String word = ComfortWordProvider.getNextSingle();
                animateText(textViews[0], word);
            }
            if (areRunning) {
                handler.postDelayed(this, interval);
            }
        }
    };

    private void scheduleNext(MultiItem item) {
        if (!areRunning) {
            return;
        }
        int delay = (minInterval + random.nextInt(maxInterval - minInterval + 1));
        item.task = () -> {
            if (!areRunning) {
                item.task = null;
                return;
            }
            item.task = null;
            animateText(item.textView, item.getNext());
            if (areRunning) {
                scheduleNext(item);
            }
        };
        handler.postDelayed(item.task, delay);
    }

    private void animateText(TextView textView, String word) {
        if (null == textView) {
            return;
        }
        AlphaAnimation fadeOut = new AlphaAnimation(1.0F, 0.0F);
        fadeOut.setDuration(500);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0F, 1.0F);
        fadeIn.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!areRunning) {
                    textView.post(() -> {
                        textView.clearAnimation();
                        textView.setAlpha(1.0F);
                    });
                    return;
                }
                textView.setText(word);
                if (areRunning) textView.startAnimation(fadeIn);
            }
        });
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!areRunning) {
                    textView.post(() -> {
                        textView.clearAnimation();
                        textView.setAlpha(1.0F);
                    });
                }
            }
        });
        textView.post(() -> textView.startAnimation(fadeOut));
    }
}