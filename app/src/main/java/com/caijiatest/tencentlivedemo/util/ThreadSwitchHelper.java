package com.caijiatest.tencentlivedemo.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * 线程切换帮助类,注意每次执行任务，都需要new一个实例,类似AsyncTask
 * Created by cai.jia on 2017/8/28 0028.
 */

public class ThreadSwitchHelper<T> {

    private static final int MSG_SUCCESS = 200;
    private static final int MSG_FAILURE = 404;
    private InternalHandler<T> handler;
    private Callback<T> callback;
    private Runnable task;

    public ThreadSwitchHelper() {
        handler = new InternalHandler<>(this);
    }

    public ThreadSwitchHelper<T> task(final Callable<T> callable) {
        return task(callable, 0);
    }

    /**
     * 当一个任务依赖别的结果时用Callable包装
     *
     * @param callable
     * @return
     */
    public ThreadSwitchHelper<T> task(final Callable<T> callable, final long delay) {
        task = new Runnable() {
            @Override
            public void run() {
                try {
                    if (delay > 0) {
                        Thread.sleep(delay);
                    }
                    T rs = callable.call();
                    handler.sendMessage(Message.obtain(handler, MSG_SUCCESS, rs));

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendMessage(Message.obtain(handler, MSG_FAILURE, e.getMessage()));
                }
            }
        };
        return this;
    }

    public void exexute() {
        execute(null);
    }

    public void execute(@Nullable Callback<T> callback) {
        this.callback = callback;
        if (task != null) {
            ThreadPoolManager.getInstance().execute(task);
        }
    }

    private void dispatchSuccessEvent(T result) {
        if (callback != null) {
            callback.onSuccess(result);
        }
    }

    private void dispatchFailureEvent(String errorMsg) {
        if (callback != null) {
            callback.onFailure(errorMsg);
        }
    }

    private static class InternalHandler<T> extends Handler {

        WeakReference<ThreadSwitchHelper> ref;

        InternalHandler(ThreadSwitchHelper helper) {
            super(Looper.getMainLooper());
            ref = new WeakReference<>(helper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS: {
                    ThreadSwitchHelper helper = ref.get();
                    if (helper != null) {
                        Object obj = msg.obj;
                        if (obj == null) {
                            helper.dispatchFailureEvent("");
                            return;
                        }

                        T result = (T) msg.obj;
                        helper.dispatchSuccessEvent(result);
                    }
                    break;
                }

                case MSG_FAILURE: {
                    ThreadSwitchHelper helper = ref.get();
                    if (helper != null) {
                        Object obj = msg.obj;
                        helper.dispatchFailureEvent(obj == null ? "" : (String) obj);
                    }
                    break;
                }
            }
        }
    }

    public interface Callback<E> {

        void onSuccess(E result);

        void onFailure(String error);
    }
}
