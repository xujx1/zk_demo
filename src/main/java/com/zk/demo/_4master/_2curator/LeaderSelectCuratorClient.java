package com.zk.demo._4master._2curator;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xujinxin on 2017/7/20.
 * 选举算法
 */
public class LeaderSelectCuratorClient {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final String MASTER = "/master";
    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(3);


    //测试
    public static void main(String[] args) throws InterruptedException {
        final LeaderSelectorListener listener = initListener();

        for (int i = 0; i < 3; i++) {
            EXECUTOR_SERVICE.submit(() -> registerListener(listener));
        }

        //让主线程不中断
        COUNT_DOWN_LATCH.await();
    }


    /**
     * LeaderSelectorListener可以对领导权进行控制， 在适当的时候释放领导权，这样每个节点都有可能获得领导权。
     */
    private static LeaderSelectorListener initListener() {

        return new LeaderSelectorListener() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println(Thread.currentThread().getName() + " 成为master");
            }

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (Arrays.asList(ConnectionState.SUSPENDED, ConnectionState.LOST).contains(connectionState)) {
                    throw new CancelLeadershipException();
                }
            }
        };
    }

    /**
     * 注册选举的监听事件
     */
    private static void registerListener(LeaderSelectorListener listener) {
        CuratorFramework curatorFramework = connection();

        LeaderSelector selector = new LeaderSelector(curatorFramework, MASTER, listener);
        selector.autoRequeue();
        selector.start();
    }


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

}
