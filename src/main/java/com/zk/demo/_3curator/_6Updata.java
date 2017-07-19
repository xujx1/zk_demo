package com.zk.demo._3curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

/**
 * Created by xujinxin on 2017/7/19.
 * 更新节点数据
 */
public class _6Updata {

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .connectString("127.0.0.1:2181")
                        .sessionTimeoutMs(5000)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .namespace("base")
                        .build();
        curatorFramework.start();
        Stat stat = curatorFramework.setData().forPath("/_3curator/create", "update".getBytes());
        System.out.println(stat.getVersion());
    }
}
