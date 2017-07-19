package com.zk.demo._1Api;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by xujinxin on 2017/7/19.
 * 获取节点所有的子节点
 */
public class _4GetChildren {
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    private static final Object lock_1 = new Object();
    private static CountDownLatch countDownLatch = new CountDownLatch(2);

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException, BrokenBarrierException {
        /**
         *  建立链接
         *  @linkplain com.zk.demo._1Api._1Connection
         */
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, event -> {
            System.out.println("event status: " + event.getState());

            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState()) && Watcher.Event.EventType.None.equals(event.getType())) {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }

        });

        cyclicBarrier.await();

        /**
         * 获取子节点
         * getChildren
         *
         *  path            指定要获取子节点的路径
         *  watcher         注册的watcher，一旦本次子节点获取之后，子节点列表发生改变，就会想客户端发送通知
         *  watch           是否需要注册watcher
         *  cb              注册一个异步回调函数
         *  ctx             用于传递上下文信息的对象
         *  stat            指定数据节点的节点状态信息
         */
        List<String> list = zooKeeper.getChildren("/test", true);
        list.forEach(System.out::println);


        zooKeeper.getChildren("/test", true, (rc, path, ctx, children, stat) -> {
            String sb = ("rc=" + rc) + "\n" +
                    "path=" + path + "\n" +
                    "ctx=" + ctx + "\n" +
                    "children=" + Arrays.toString(children.toArray()) + "\n" +
                    "stat=" + stat;
            System.out.println(sb);
            synchronized (lock_1) {
                lock_1.notify();
            }
        }, "获取子节点");


        synchronized (lock_1) {
            lock_1.wait();
        }


        zooKeeper.getChildren("/test", event -> {

            System.out.println("watchedEvent  " + event);

            if (event.getState().equals(Watcher.Event.KeeperState.SyncConnected)) {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    System.out.println("watchedEvent type :  " + event.getType() + "  watchedEvent path " + event.getPath());
                    countDownLatch.countDown();
                }
            }
        }, (rc, path, ctx, children, stat) -> {
            String sb = ("rc=" + rc) + "\n" +
                    "path=" + path + "\n" +
                    "ctx=" + ctx + "\n" +
                    "children=" + Arrays.toString(children.toArray()) + "\n" +
                    "stat=" + stat;
            System.out.println(sb);
            countDownLatch.countDown();
        }, "获取子节点");

        zooKeeper.create("/test/m", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        countDownLatch.await();
    }
}
