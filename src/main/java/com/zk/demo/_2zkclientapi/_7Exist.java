package com.zk.demo._2zkclientapi;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * Created by xujinxin on 2017/7/19.
 * 检查节点是否存在
 */
public class _7Exist {

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        System.out.println(zkClient.exists("/node10"));
    }
}
