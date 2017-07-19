package com.zk.demo._2zkclientapi;

import org.I0Itec.zkclient.ZkClient;

/**
 * Created by xujinxin on 2017/7/19.
 * 创建一个最基本的zookeeper会话实例
 */
public class _1Connection {
    public static void main(String[] args) {
        new ZkClient("127.0.0.1:2181",5000);
        System.out.println("connection success");
    }
}
