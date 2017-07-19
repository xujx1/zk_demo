package com.zk.demo._2zkclientapi;

import org.I0Itec.zkclient.ZkClient;

/**
 * Created by xujinxin on 2017/7/19.
 * 获取节点所有的子节点
 */
public class _4GetChildren {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        zkClient.getChildren("/test").forEach(System.out::println);
    }
}
