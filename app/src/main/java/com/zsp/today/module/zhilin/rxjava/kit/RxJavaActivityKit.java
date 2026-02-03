package com.zsp.today.module.zhilin.rxjava.kit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import widget.dialog.materialalertdialog.DebugMaterialAlertDialogKit;

/**
 * Created on 2026/1/31.
 *
 * @author 郑少鹏
 * @desc 响应式异步框架页配套原件
 */
public class RxJavaActivityKit {
    /**
     * 示例一
     *
     * @param appCompatActivity 活动
     */
    public void exampleOne(AppCompatActivity appCompatActivity) {
        StringBuilder stringBuilder = new StringBuilder();
        Observable<Integer> observable = Observable.create(emitter -> {
            stringBuilder.append("[发射] emitter 1\n");
            emitter.onNext(1);
            stringBuilder.append("[发射] emitter 2\n");
            emitter.onNext(2);
            stringBuilder.append("[发射] emitter 3\n");
            emitter.onNext(3);
            stringBuilder.append("[发射完成] emitter onComplete\n");
            emitter.onComplete();
            stringBuilder.append("[完成后继续发射] emitter 4");
            emitter.onNext(4);
        });
        Observer<Integer> observer = new Observer<>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                stringBuilder.append("[订阅] onSubscribe\n");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                stringBuilder.append("[接收] onNext %s ").append(integer).append("\n");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                stringBuilder.append("[异常] onError %s ").append(e.getMessage()).append("\n");
            }

            @Override
            public void onComplete() {
                stringBuilder.append("[完成] onComplete\n");
            }
        };
        observable.subscribe(observer);
        DebugMaterialAlertDialogKit.getInstance().show(appCompatActivity, stringBuilder.toString(), true);
    }

    /**
     * 示例二
     *
     * @param appCompatActivity 活动
     */
    public void exampleTwo(AppCompatActivity appCompatActivity) {
        StringBuilder stringBuilder = new StringBuilder();
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            stringBuilder.append("[发射] emitter 1\n");
            emitter.onNext(1);
            stringBuilder.append("[发射] emitter 2\n");
            emitter.onNext(2);
            stringBuilder.append("[发射] emitter 3\n");
            emitter.onNext(3);
            stringBuilder.append("[发射完成] emitter onComplete\n");
            emitter.onComplete();
            stringBuilder.append("[完成后继续发射] emitter 4");
            emitter.onNext(4);
        }).subscribe(new Observer<>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                stringBuilder.append("[订阅] onSubscribe\n");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                stringBuilder.append("[接收] onNext %s ").append(integer).append("\n");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                stringBuilder.append("[异常] onError %s ").append(e.getMessage()).append("\n");
            }

            @Override
            public void onComplete() {
                stringBuilder.append("[完成] onComplete\n");
            }
        });
        DebugMaterialAlertDialogKit.getInstance().show(appCompatActivity, stringBuilder.toString(), true);
    }

    /**
     * 示例三
     *
     * @param appCompatActivity 活动
     */
    public void exampleThree(AppCompatActivity appCompatActivity) {
        StringBuilder stringBuilder = new StringBuilder();
        Observable<Integer> observable = Observable.create(emitter -> {
            stringBuilder.append("上游线程 %s ").append(Thread.currentThread().getName()).append("\n");
            stringBuilder.append("[发射] emitter 1\n");
            emitter.onNext(1);
        });
        Consumer<Integer> consumer = integer -> {
            stringBuilder.append("下游线程 %s ").append(Thread.currentThread().getName()).append("\n");
            stringBuilder.append("[接收] onNext %s ").append(integer);
        };
        Disposable disposable = observable.subscribe(consumer);
        DebugMaterialAlertDialogKit.getInstance().show(appCompatActivity, stringBuilder.toString(), true);
    }

    /**
     * 示例四
     *
     * @param appCompatActivity 活动
     */
    public void exampleFour(AppCompatActivity appCompatActivity) {
        StringBuilder stringBuilder = new StringBuilder();
        Observable<Integer> observable = Observable.create(emitter -> {
            stringBuilder.append("上游线程 %s ").append(Thread.currentThread().getName()).append("\n");
            stringBuilder.append("[发射] emitter 1\n");
            emitter.onNext(1);
        });
        Consumer<Integer> consumer = integer -> {
            stringBuilder.append("下游线程 %s ").append(Thread.currentThread().getName()).append("\n");
            stringBuilder.append("[接收] onNext %s ").append(integer);
        };
        Disposable disposable = observable.subscribeOn(Schedulers.newThread()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io()).subscribe(consumer);
        DebugMaterialAlertDialogKit.getInstance().show(appCompatActivity, stringBuilder.toString(), true);
    }

    /**
     * 示例五
     *
     * @param appCompatActivity 活动
     */
    public void exampleFive(AppCompatActivity appCompatActivity) {
        StringBuilder stringBuilder = new StringBuilder();
        Disposable disposable = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            stringBuilder.append("[发射] emitter 1\n");
            emitter.onNext(1);
            stringBuilder.append("[发射] emitter 2\n");
            emitter.onNext(2);
        }).map(integer -> {
            stringBuilder.append("[转化] apply %s ").append(integer).append("\n");
            return "apply " + integer;
        }).subscribe(s -> stringBuilder.append("[接收] accept %s ").append(s).append("\n"));
        DebugMaterialAlertDialogKit.getInstance().show(appCompatActivity, stringBuilder.toString(), true);
    }

    /**
     * 示例六
     *
     * @param appCompatActivity 活动
     */
    public void exampleSix(AppCompatActivity appCompatActivity) {
        StringBuilder stringBuilder = new StringBuilder();
        Disposable disposable = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            stringBuilder.append("[发射] emitter 1\n");
            emitter.onNext(1);
            stringBuilder.append("[发射] emitter 2\n");
            emitter.onNext(2);
            stringBuilder.append("[发射] emitter 3");
            emitter.onNext(3);
        }).flatMap((Function<Integer, ObservableSource<String>>) integer -> {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                list.add("apply " + integer);
            }
            return Observable.fromIterable(list).delay(10, TimeUnit.SECONDS);
        }).subscribe(s -> stringBuilder.append("[接收] accept %s ").append(s).append("\n"));
        DebugMaterialAlertDialogKit.getInstance().show(appCompatActivity, stringBuilder.toString(), true);
    }

    /**
     * 示例七
     *
     * @param appCompatActivity 活动
     */
    public void exampleSeven(AppCompatActivity appCompatActivity) {
        StringBuilder stringBuilder = new StringBuilder();
        Disposable disposable = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            stringBuilder.append("[发射] emitter 1\n");
            emitter.onNext(1);
            stringBuilder.append("[发射] emitter 2\n");
            emitter.onNext(2);
            stringBuilder.append("[发射] emitter 3");
            emitter.onNext(3);
        }).concatMap((Function<Integer, ObservableSource<String>>) integer -> {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                list.add("apply " + integer);
            }
            return Observable.fromIterable(list).delay(10, TimeUnit.SECONDS);
        }).subscribe(s -> stringBuilder.append("[接收] accept %s ").append(s).append("\n"));
        DebugMaterialAlertDialogKit.getInstance().show(appCompatActivity, stringBuilder.toString(), true);
    }
}