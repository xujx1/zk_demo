package com.zk.demo._1Api;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by xujinxin on 2017/7/19.
 * 创建一个数据节点
 */
public class _2Create {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final Object lock = new Object();

    public static void main(String[] args) throws IOException {
        /**
         *  建立链接
         *  @linkplain com.zk.demo._1Api._1Connection
         */
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, event -> {
            System.out.println("event status: " + event.getState());

            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())) {
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * 创建节点
         * path             需要创建的节点的路径地址，必须以"/"开头
         * data[]           一个字节数组，是节点创建后的初始内容
         * acl              节点的ACL策略：Ids.OPEN_ACL_UNSAFE 任何人可以对这个节点进行任何操作  CREATOR_ALL_ACL 允许所有创建操作  READ_ACL_UNSAFE 允许所有读操作
         * createMode       节点类型  PERSISTENT 持久性  PERSISTENT_SEQUENTIAL  持久顺序  EPHEMERAL 临时 EPHEMERAL_SEQUENTIAL 临时顺序
         * cb               注册一个异步回调函数，异步创建节点
         * ctx              用于传递一个对象，可以再回调方法执行的时候使用，通常是放一个上下文信息
         */

        byte[] data = "".getBytes();

        try {
            //同步创建Znode节点
            String path1 = zooKeeper.create("/node1", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("success create znode :" + path1);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

        //异步创建Znode节点

        /**
         * StringCallback  注册一个异步回调函数
         *  rc   返回码:  0 接口调用成功 -4 客户端和服务器端连接已断开  -110 制定节点已存在   -122 会话已过期
         *  path 我们需要创建的节点的完整路径
         *  ctx  上面传入的值("创建")
         *  name 服务器返回给我们已经创建的节点的真实路径,如果是顺序节点path和name是不一样的
         */
        zooKeeper.create("/node2", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, (rc, path12, ctx, name) -> {

            String sb = ("rc=" + rc) + "\n" +
                    "path=" + path12 + "\n" +
                    "ctx=" + ctx + "\n" +
                    "name=" + name;
            System.out.println(sb);

            synchronized (lock) {
                lock.notify();
            }
        }, "创建");

        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
