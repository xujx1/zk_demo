package com.zk.demo._3curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;


/**
 * Created by xujinxin on 2017/7/19.
 * 订阅节点的子节点内容的变化
 */

public class _9SubscribeDataChanges {

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


        //true 表示是否同时获取变更的数据
        PathChildrenCache cache = new PathChildrenCache(curatorFramework, "/_3curator", true);
        cache.start();
        cache.getListenable().addListener((curatorFramework1, pathChildrenCacheEvent) -> {
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    System.out.println("CHILD_ADDED:" + pathChildrenCacheEvent.getData());
                    countDownLatch.countDown();
                    break;
                case CHILD_UPDATED:
                    System.out.println("CHILD_UPDATED:" + pathChildrenCacheEvent.getData());
                    countDownLatch.countDown();
                    break;
                case CHILD_REMOVED:
                    System.out.println("CHILD_REMOVED:" + pathChildrenCacheEvent.getData());
                    countDownLatch.countDown();
                    break;
                default:
                    break;
            }


        });

        curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/_3curator/test4", "init".getBytes());

        countDownLatch.await();
    }
}
