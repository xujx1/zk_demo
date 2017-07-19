package com.zk.demo._2zkclientapi;

import com.zk.demo._2zkclientapi.domain.User;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;


/**
 * Created by xujinxin on 2017/7/19.
 * 订阅节点的子节点内容的变化
 */

public class _9SubscribeDataChanges {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        zkClient.subscribeDataChanges("/node3", new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println(s + ":" + o.toString());
                countDownLatch.countDown();
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println(s);
                countDownLatch.countDown();
            }
        });

        zkClient.writeData("/node3", new User(3, "update"));
        countDownLatch.await();

    }
}
