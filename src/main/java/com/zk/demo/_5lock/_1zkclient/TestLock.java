package com.zk.demo._5lock._1zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

/**
 * Created by xujinxin on 2017/7/21.
 * 测试分布式锁
 */
public class TestLock {


    public static void main(String[] args) {

        // 需要手动创建节点 /locker

        ZkClient zkClient1 = new ZkClient("127.0.0.1:2181", 5000, 5000, new BytesPushThroughSerializer());
        LockImpl lock1 = new LockImpl(zkClient1, "/lock");

        ZkClient zkClient2 = new ZkClient("127.0.0.1:2181", 5000, 5000, new BytesPushThroughSerializer());
        final LockImpl lock2 = new LockImpl(zkClient2, "/lock");

        try {
            lock1.getLock();
            System.out.println("Client1 is get lock!");
            Thread client2Thd = new Thread(() -> {
                try {
                    lock2.getLock();
                    System.out.println("Client2 is get lock");
                    lock2.releaseLock();
                    System.out.println("Client2 is released lock");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            client2Thd.start();

            // 5s 后lock1释放锁
            Thread.sleep(5000);
            lock1.releaseLock();
            System.out.println("Client1 is released lock");

            client2Thd.join();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
