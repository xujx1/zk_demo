package com.zk.demo._2zkclientapi;

import com.zk.demo._2zkclientapi.domain.User;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;


/**
 * Created by xujinxin on 2017/7/19.
 * 订阅节点的子节点变化（可以监听不存在的节点当他创建的时候接收到通知）
 */
public class _8SubscribeChildChanges {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        zkClient.subscribeChildChanges("/test", (s, list) -> {
            System.out.println(s);
            list.forEach(System.out::println);
            countDownLatch.countDown();
        });


        zkClient.create("/test/v", new User(3, "test"), CreateMode.PERSISTENT);
        countDownLatch.await();
    }
}
