package com.zk.demo._1Api;

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

    private static volatile Boolean flag = false;
    private static volatile AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        /**
         *  建立链接
         *  @linkplain com.zk.demo._1Api._1Connection
         */
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, event -> {
            System.out.println("event status: " + event.getState());

            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())
                    && Watcher.Event.EventType.None.equals(event.getType())) {
                flag = true;
            }
        });

        //在连接成功之前主线程不停的空转
        while (!flag) {

        }

        /**
         * 更新数据
         * setData
         *
         * path            指定要获取子节点的路径
         * data[]          一个字节数组，是节点更新后的初始内容
         * version         指定节点的数据版本 如果为-1 就是不指定数据版本，如果指定了就会进行cas
         * cb              注册一个异步回调函数
         * ctx             用户传递上下文信息的对象
         */
        Stat stat = zooKeeper.setData("/test", "8".getBytes(), -1);

        zooKeeper.setData("/test", "7".getBytes(), stat.getVersion(), (rc, path, ctx, stat1) -> {
            String sb = ("rc=" + rc) + "\n" +
                    "path=" + path + "\n" +
                    "ctx=" + ctx + "\n" +
                    "stat=" + stat1;
            System.out.println(sb);
            atomicBoolean.compareAndSet(false, true);
        }, "更新数据");

        while (!atomicBoolean.get()) {

        }

    }
}
