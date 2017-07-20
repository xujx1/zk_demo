package com.zk.demo._4master._1zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xujinxin on 2017/7/20.
 * master选举 主工作类
 */
public class WorkServer {

    //客户端状态
    private volatile boolean running = false;

    //zk服务器
    private ZkClient zkClient;

    //zk主节点路径
    public static final String MASTER_PATH = "/master";

    //监听(用户监听主节点删除事件)
    private IZkDataListener dataListener;

    //服务器基本信息
    private RunningData serverData;

    //主节点基本信息
    private RunningData masterData;


    //创建一个线程池，它可安排在给定延迟后运行命令或者定期地执行。
    private ScheduledExecutorService delayExecutor = Executors.newScheduledThreadPool(1);

    //延迟时间5s
    private int delayTime = 5;

    //第几次释放
    private static volatile AtomicInteger atomicInteger = new AtomicInteger(1);


    public WorkServer(RunningData runningData) {
        this.serverData = runningData;
        this.dataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                //监听master临时节点的改变
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {


                if (null != masterData && masterData.getSname().equals(serverData.getSname())) {
                    //若之前master为本机,则立即抢主,否则延迟5秒抢主(防止小故障引起的抢主可能导致的网络数据风暴)
                    takeMaster();

                } else {
                    // 假设延时5s尝试获取master，正好此时master主动释放，会导致抢到master
                    delayExecutor.schedule(() -> takeMaster(), delayTime, TimeUnit.SECONDS);
                }
            }
        };
    }

    //启动
    public void start() throws RuntimeException {
        if (running) {
            throw new RuntimeException("server has startup");
        }

        running = true;

        zkClient.subscribeDataChanges(MASTER_PATH, dataListener);

        takeMaster();
    }

    private void stop() throws Exception {
        if (!running) {
            throw new RuntimeException("server has stopped");
        }

        running = false;
        delayExecutor.shutdown();

        zkClient.unsubscribeDataChanges(MASTER_PATH, dataListener);

        releaseMaster();
    }


    //抢注主节点
    private void takeMaster() {

        serverData.setTryCount(serverData.getTryCount() + 1);

        if (serverData.getTryCount() != atomicInteger.get()) {
            return;
        }

        if (!running) return;

        try {
            //注册master 临时节点

            zkClient.create(MASTER_PATH, serverData, CreateMode.EPHEMERAL);

            masterData = serverData;

            System.out.println("master is : " + masterData);

            if (masterData.getTryCount() == 10) {
                stop();
            } else {
                //测试 每5s释放一次主节点
                delayExecutor.schedule(this::releaseMaster, 1, TimeUnit.SECONDS);
            }

        } catch (ZkNodeExistsException e) {
            //主节点已经存在
            RunningData runningData = zkClient.readData(MASTER_PATH, true);

            if (null == runningData) {
                takeMaster();
            } else {
                masterData = runningData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //释放主节点
    private void releaseMaster() {
        if (checkMaster()) {
            zkClient.delete(MASTER_PATH);
            atomicInteger.incrementAndGet();
        }
    }


    //判断自己是否是主节点
    private boolean checkMaster() {
        try {
            RunningData masterData = zkClient.readData(MASTER_PATH);

            return masterData != null && masterData.getSname().equals(serverData.getSname());
        } catch (ZkNoNodeException e) {
            //节点不存在
            return false;
        } catch (ZkInterruptedException e) {
            //网络中断
            return checkMaster();
        } catch (Exception e) {
            return false;
        }
    }


    public ZkClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }
}
