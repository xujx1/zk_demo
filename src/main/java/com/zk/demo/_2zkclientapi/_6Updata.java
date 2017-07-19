package com.zk.demo._2zkclientapi;

import com.zk.demo._2zkclientapi.domain.User;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xujinxin on 2017/7/19.
 * 更新节点数据
 */
public class _6Updata {

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        zkClient.writeData("/node10", new User(2,"许"));

        User user = zkClient.readData("/node10");
        System.out.println(user);
    }
}
