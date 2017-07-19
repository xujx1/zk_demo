package com.zk.demo._1Api;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xujinxin on 2017/7/19.
 * 获取一个节点的数据内容
 */
public class _5GetData {

    private static ReentrantLock reentrantLock = new ReentrantLock();

    private static Condition condition_1 = reentrantLock.newCondition();
    private static Condition condition_2 = reentrantLock.newCondition();
    private static Condition condition_3 = reentrantLock.newCondition();


    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        /**
         *  建立链接
         *  @linkplain com.zk.demo._1Api._1Connection
         */
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, event -> {
            System.out.println("event status: " + event.getState());

            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState()) &&
                    Watcher.Event.EventType.None.equals(event.getType())) {

                reentrantLock.lock();
                try {
                    condition_1.signal();
                } finally {
                    reentrantLock.unlock();
                }
            }
        });

        reentrantLock.lock();
        try {
            condition_1.await();
        } finally {
            reentrantLock.unlock();
        }

        /**
         * 获取一个节点的数据内容
         *  path            指定要获取子节点的路径
         *  watcher         注册的watcher，一旦本次子节点获取之后，子节点列表发生改变，就会想客户端发送通知
         *  watch           是否需要注册watcher
         *  cb              注册一个异步回调函数
         *  ctx             用于传递上下文信息的对象
         *  stat            指定数据节点的节点状态信息
         *
         */
        byte[] bytes = zooKeeper.getData("/test", true, new Stat());
        System.out.println(new String(bytes));


        zooKeeper.getData("/test", true, (rc, path, ctx, data, stat) -> {
            String sb = ("rc=" + rc) + "\n" +
                    "path=" + path + "\n" +
                    "ctx=" + ctx + "\n" +
                    "data=" + new String(data) + "\n" +
                    "stat=" + stat;
            System.out.println(sb);

            reentrantLock.lock();
            try {
                condition_2.signal();
            } finally {
                reentrantLock.unlock();
            }
        }, new Stat());

        reentrantLock.lock();
        try {
            condition_2.await();
        } finally {
            reentrantLock.unlock();
        }

        zooKeeper.getData("/test", event -> {
            if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                System.out.println("watchedEvent type :  " + event + "  watchedEvent path " + event.getPath());
            }
        }, (rc, path, ctx, data, stat) -> {
            String sb = ("rc=" + rc) + "\n" +
                    "path=" + path + "\n" +
                    "ctx=" + ctx + "\n" +
                    "data=" + new String(data) + "\n" +
                    "stat=" + stat;
            System.out.println(sb);

            reentrantLock.lock();
            try {
                condition_3.signal();
            } finally {
                reentrantLock.unlock();
            }
        }, new Stat());

        reentrantLock.lock();
        try {
            zooKeeper.setData("/test", "".getBytes(), -1);
            condition_3.await();
        } finally {
            reentrantLock.unlock();
        }
    }
}
