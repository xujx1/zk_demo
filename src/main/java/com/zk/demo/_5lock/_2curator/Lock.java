package com.zk.demo._5lock._2curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujinxin on 2017/7/21.
 * 简单的互斥锁
 */
public class Lock {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    private static final String ZK_LOCK_PATH = "/lock";

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(2);

    /**
     * 建立连接
     */
    private static CuratorFramework connection() {
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .connectString(ZK_ADDRESS)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .build();

        curatorFramework.start();
        return curatorFramework;
    }

    /**
     * 获取锁，如果获取成功，就把锁传出来
     *
     * @param curatorFramework
     * @return
     */
    private static InterProcessMutex getLock(CuratorFramework curatorFramework) {
        boolean hasLock = false;

        InterProcessMutex lock = new InterProcessMutex(curatorFramework, ZK_LOCK_PATH);

        try {
            if (lock.acquire(1000, TimeUnit.SECONDS)) {
                hasLock = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasLock ? lock : null;
    }

    /**
     * 释放锁
     *
     * @param lock
     */
    private static void release(InterProcessMutex lock) {
        try {
            lock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CuratorFramework curatorFramework = connection();

        for (int i = 0; i < 2; i++) {

            EXECUTOR_SERVICE.submit(() -> {
                InterProcessMutex interProcessMutex = getLock(curatorFramework);

                if (null != interProcessMutex) {
                    System.out.println(Thread.currentThread().getName() + " 获取锁");
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(Thread.currentThread().getName() + " 释放锁");
                    release(interProcessMutex);
                    COUNT_DOWN_LATCH.countDown();
                }
            });
        }

        COUNT_DOWN_LATCH.await();
        EXECUTOR_SERVICE.shutdown();
    }
}
