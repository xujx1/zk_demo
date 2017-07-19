package com.zk.demo._3curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;


/**
 * Created by xujinxin on 2017/7/19.
 * 删除一个数据节点
 */
public class _3Delete {

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.builder()
                        .connectString("127.0.0.1:2181")
                        .sessionTimeoutMs(5000)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                        .namespace("base")
                        .build();
        curatorFramework.start();

        System.out.println(curatorFramework.delete()
                .deletingChildrenIfNeeded()
                .forPath("/_3curator/create"));

    }

}
