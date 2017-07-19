package com.zk.demo._1Api;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * Created by xujinxin on 2017/7/19.
 * 检查节点是否存在
 */
public class _7Exist {

    //一次只允许一个线程执行
    private static Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {


        /**
         *  建立链接
         *  @linkplain com.zk.demo._1Api._1Connection
         */
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, event -> {
            System.out.println("event status: " + event.getState());

            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())) {

                // 获取许可
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }
        });


        /**
         * 检查节点是否存在
         *  path            指定要获取子节点的路径
         *  watcher         注册的watcher，一旦本次子节点获取之后，子节点列表发生改变，就会想客户端发送通知
         *  watch           是否需要注册watcher
         *  cb              注册一个异步回调函数
         *  ctx             用于传递上下文信息的对象
         */
        //线程睡眠1s,保证上面的线程首先去获取许可
        Thread.sleep(1000L);
        try {
            semaphore.acquire();
            Stat stat = zooKeeper.exists("/test", true);
            System.out.println(stat != null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }

        zooKeeper.exists("/test", true, (rc, path, ctx, stat) -> {
            try {
                semaphore.acquire();
                String sb = ("rc=" + rc) + "\n" +
                        "path=" + path + "\n" +
                        "ctx=" + ctx + "\n" +
                        "stat=" + stat;
                System.out.println(sb);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }

        }, "检查节点是否存在");


        Thread.sleep(1000L);

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
}
