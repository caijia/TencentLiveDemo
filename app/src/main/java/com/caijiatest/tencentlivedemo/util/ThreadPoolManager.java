package com.caijiatest.tencentlivedemo.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cai.jia on 2017/8/28 0028.
 */

public class ThreadPoolManager {

    private static volatile ThreadPoolManager instance;
    private ExecutorService executorService;

    private ThreadPoolManager() {
        executorService = Executors.newCachedThreadPool();
    }

    public static ThreadPoolManager getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolManager.class) {
                if (instance == null) {
                    instance = new ThreadPoolManager();
                }
            }
        }
        return instance;
    }

    public void execute(Runnable task) {
        executorService.execute(task);
    }
}
