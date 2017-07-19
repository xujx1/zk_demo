package com.zk.demo._3curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xujinxin on 2017/7/19.
 * 检查节点是否存在
 */
public class _7Exist {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    // 异步调用每次都是创建一个线程，如果系统执行的异步调用比较多，会创建比较多的线程，这里我们需要使用线程池
    private static ExecutorService es = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws Exception {

        try {
            CuratorFramework curatorFramework =
                    CuratorFrameworkFactory.builder()
                            .connectString("127.0.0.1:2181")
                            .sessionTimeoutMs(5000)
                            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                            .namespace("base")
                            .build();
            curatorFramework.start();
            System.out.println(curatorFramework.checkExists().forPath("/_3curator/create"));


            curatorFramework.checkExists().inBackground((curatorFramework1, curatorEvent) -> {
                Stat stat = curatorEvent.getStat();
                System.out.println(stat);
                System.out.println(curatorEvent.getResultCode());
                System.out.println(curatorEvent.getContext());
                countDownLatch.countDown();
            }, "exist", es).forPath("/_3curator/create");

            countDownLatch.await();

        } finally {
            es.shutdown();
        }
    }
}
