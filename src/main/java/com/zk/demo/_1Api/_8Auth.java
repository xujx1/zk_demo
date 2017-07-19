package com.zk.demo._1Api;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.Exchanger;

/**
 * Created by xujinxin on 2017/7/19.
 * 权限控制
 */
public class _8Auth {
    //兄弟线程互换数据
    private static Exchanger<Boolean> exchanger_1 = new Exchanger<>();
    private static Exchanger<Boolean> exchanger_2 = new Exchanger<>();

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        /**
         *  建立链接
         *  @linkplain com.zk.demo._1Api._1Connection
         */
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, event -> {
            System.out.println("event status: " + event.getState());

            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())) {
                try {
                    System.out.println(exchanger_1.exchange(true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        /**
         * scheme    权限控制模式，分为world，auth，digest，ip和super
         * auth[]    具体的权限信息
         *
         */
        if (exchanger_1.exchange(false)) {
            zooKeeper.addAuthInfo("digest", "test".getBytes());
            zooKeeper.create("/node", "1".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
        }


        /**
         *  建立链接
         *  @linkplain com.zk.demo._1Api._1Connection
         */
        zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, event -> {
            System.out.println("event status: " + event.getState());

            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())) {
                try {
                    System.out.println(exchanger_2.exchange(true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //使用无权限的客户端操作节点，返回无权限异常
        if (exchanger_2.exchange(false)) {
            zooKeeper.create("/node", "1".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
        }


    }

}
