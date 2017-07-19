package com.zk.demo._3curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;


/**
 * Created by xujinxin on 2017/7/19.
 * 订阅节点的子节点变化（可以监听不存在的节点当他创建的时候接收到通知）
 */
public class _8SubscribeChildChanges {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .connectString("127.0.0.1:2181")
                        .sessionTimeoutMs(5000)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .namespace("base")
                        .build();
        curatorFramework.start();


        NodeCache cache = new NodeCache(curatorFramework, "/_3curator/create");
        cache.start();
        cache.getListenable().addListener(() -> {
            System.out.println(new String(cache.getCurrentData().getData()));
            countDownLatch.countDown();
        });

        curatorFramework.setData().forPath("/_3curator/create", "update".getBytes());
        countDownLatch.await();

    }
}
