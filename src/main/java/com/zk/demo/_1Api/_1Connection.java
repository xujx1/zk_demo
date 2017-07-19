package com.zk.demo._1Api;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * Created by xujinxin on 2017/7/19.
 * 创建一个最基本的zookeeper会话实例
 */
public class _1Connection {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException {
        
        /**
         * 创建连接
         * connectString   127.0.0.1:2181或者127.0.0.1:2181/${Znode_name}
         * sessionTimeout  心跳检测超时时间 毫秒
         * watcher         事件通知处理器
         * canBeReadOnly   是否只读，默认当失去过半的机器，就不接受客户端的事件申请
         * sessionId       会话id
         * sessionPasswd   密码
         * 客户端传入sessionId和sessionPasswd是为了复用会话
         */

        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, watchedEvent -> {
            System.out.println("received: " + watchedEvent);
            /**
             * 一旦客户端和服务器的某一个节点建立连接
             * （注意，虽然集群有多个节点，但是客户端一次连接到一个节点就行了），
             * 并完成一次version、zxid的同步，
             * 这时的客户端和服务器的连接状态就是SyncConnected
             */
            if (Watcher.Event.KeeperState.SyncConnected.equals(watchedEvent.getState())) {
                //唤醒等待线程
                countDownLatch.countDown();
            }
        });

        System.out.println(zooKeeper.getState());

        try {
            //线程等待，等待连接成功
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(zooKeeper.getState());

        long sessionId = zooKeeper.getSessionId();
        byte[] sessionPasswd = zooKeeper.getSessionPasswd();

        System.out.println("sessionId : " + sessionId);

        System.out.println("sessionPasswd" + Arrays.asList(sessionPasswd));


        Object lock = new Object();

        zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, watchedEvent -> {
            System.out.println("received: " + watchedEvent);
            /**
             * 一旦客户端和服务器的某一个节点建立连接
             * （注意，虽然集群有多个节点，但是客户端一次连接到一个节点就行了），
             * 并完成一次version、zxid的同步，
             * 这时的客户端和服务器的连接状态就是SyncConnected
             */
            if (!Watcher.Event.KeeperState.SyncConnected.equals(watchedEvent.getState())) {
                System.out.println("watchedEvent state :  " + watchedEvent.getState());
            }
            synchronized (lock) {
                lock.notify();
            }
        }, 1L, "test".getBytes());  // sessionId,sessionPasswd

        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(zooKeeper.getState());

    }
}
