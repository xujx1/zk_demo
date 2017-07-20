package com.zk.demo._4master._1zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xujinxin on 2017/7/20.
 * 测试类
 */
public class LeaderSelectorZkClient {

    //启动的服务器数
    private static final int CLIENT_QTY = 3;

    //zookeeper服务器的地址
    private static final String ZOOKEEPER_SERVER = "127.0.0.1:2181";

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(CLIENT_QTY);

    public static void main(String[] args) throws InterruptedException {


        for (int i = 0; i < CLIENT_QTY; i++) {

            int finalI = i;
            EXECUTOR_SERVICE.submit(() -> {
                //创建 ZkClient
                ZkClient zkClient = new ZkClient(ZOOKEEPER_SERVER, 5000, 5000, new SerializableSerializer());

                //创建 serverData
                RunningData runningData = new RunningData();
                runningData.setSid((long) finalI);
                runningData.setSname("Client_" + finalI);

                //创建服务
                WorkServer workServer = new WorkServer(runningData);
                workServer.setZkClient(zkClient);
                workServer.start();
            });
        }
        COUNT_DOWN_LATCH.await();
    }
}
