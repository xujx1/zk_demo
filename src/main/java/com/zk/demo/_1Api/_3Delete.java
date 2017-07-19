package com.zk.demo._1Api;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xujinxin on 2017/7/19.
 * 删除一个数据节点
 */
public class _3Delete {
    private static ReentrantLock reentrantLock = new ReentrantLock();
    private static Condition condition = reentrantLock.newCondition();

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        /**
         *  建立链接
         *  @linkplain com.zk.demo._1Api._1Connection
         */
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, event -> {
            System.out.println("event status: " + event.getState());
            reentrantLock.lock();
            try {
                if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())) {
                    condition.signal();
                }
            } finally {
                reentrantLock.unlock();
            }
        });


        reentrantLock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }

        /**
         * 删除Znode
         * path     指定要删除的数据节点的路径
         * version  指定要删除的数据节点的版本号 -1 就是不指定版本号
         * cb       注册一个异步回调函数
         * ctx      用于传递上下文信息的对象
         */

        zooKeeper.delete("/path1", -1);
        System.out.println("已经删除指定的节点");

    }

}
