package com.zk.demo._4master._2curator;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;

import java.util.Arrays;

/**
 * Created by xujinxin on 2017/7/20.
 * 选举算法
 */
public class LeaderSelectCuratorClient {

    private static final String MASTER_PATH = "/master";
    private static final String ZK_PATH = "127.0.0.1:2181";


    public static void main(String[] args) throws InterruptedException {
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .connectString(ZK_PATH)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .build();


        curatorFramework.start();

        LeaderSelector selector = new LeaderSelector(curatorFramework, MASTER_PATH, new LeaderSelectorListener() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println("成为master");
                Thread.sleep(3000);
                System.out.println("完成master操作，释放权利");
            }

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (Arrays.asList(ConnectionState.SUSPENDED, ConnectionState.LOST).contains(connectionState)) {
                    throw new CancelLeadershipException();
                }
            }
        });
        selector.autoRequeue();
        selector.start();
        Thread.sleep(Integer.MAX_VALUE);
    }


    private static void registerListener(LeaderSelectorListener listener) {
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .connectString(ZK_PATH)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .build();
        curatorFramework.start();


        LeaderSelector selector = new LeaderSelector(curatorFramework, ZK_PATH, listener);
        selector.autoRequeue();
        selector.start();
    }

}
