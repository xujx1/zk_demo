package com.zk.demo._5lock._1zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by xujinxin on 2017/7/21.
 * 分布式锁 基础类
 * 主要用于和Zookeeper交互
 */
public class BaseLock {

    private final ZkClient zkClient;
    //基本路径：/lock
    private final String basePath;
    //锁的完整路径: /lock/lock_
    private final String path;
    //锁的子节点的路径：lock_
    private final String lockName;

    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final Condition condition = reentrantLock.newCondition();

    /*重试获取锁次数*/

    private static final int MAX_RETRY_COUNT = 10;

    public BaseLock(ZkClient zkClient, String basePath, String lockName) {
        this.zkClient = zkClient;
        this.basePath = basePath;
        this.path = basePath.concat("/").concat(lockName);
        this.lockName = lockName;
    }


    /**
     * 尝试获取锁
     */
    protected String tryLock() throws Exception {
        return this.tryLock(null, null);
    }


    protected String tryLock(Long timeOut, TimeUnit timeUnit) throws Exception {
        boolean hasTheLock = false;
        boolean isDone = false;
        int retryCount = 0;
        String ourPath = "";
        while (!isDone) {
            isDone = true;

            try {
                ourPath = createLockNode(zkClient, path);
                hasTheLock = waitToLock(timeOut, timeUnit, ourPath);
            } catch (ZkNoNodeException e) {
                if (retryCount++ < MAX_RETRY_COUNT) {
                    isDone = false;
                } else {
                    throw e;
                }
            }
        }
        return hasTheLock ? ourPath : null;
    }

    /**
     * 释放锁
     *
     * @param lockPath
     * @throws Exception
     */
    protected void releaseLock(String lockPath) throws Exception {
        zkClient.delete(lockPath);
    }

    /**
     * 等待获取锁
     *
     * @param millisToWait 最大等待时间
     * @param timeUnit     时间单位
     * @param ourPath      等待的锁的路径:/lock/lock_1
     * @return
     */

    private boolean waitToLock(Long millisToWait, TimeUnit timeUnit, String ourPath) {

        final boolean[] hasLock = new boolean[1];
        //获取所有锁节点下的排序后的子节点
        List<String> sortChildren = getSortChildren();

        //移除掉ourPath 的/lock/
        String sequenceNodeName = ourPath.replace(basePath.concat("/"), "");

        //如果获取的子节点为空或者节点不包含当前要判断的节点，就抛异常
        if (null == sortChildren || sortChildren.size() == 0 || !sortChildren.contains(sequenceNodeName)) {
            throw new ZkNoNodeException("节点没有找到 :" + sequenceNodeName);
        }

        //判断ourPath所在的节点位置
        int ourIndex = sortChildren.indexOf(sequenceNodeName);

        //如果当前锁是第一个节点就返回true，如果大于0就监听前一个节点的删除时间
        if (ourIndex == 0) {
            hasLock[0] = true;
        } else if (ourIndex > 0) {
            //获取当前节点的前一个节点
            String previousSequencePath = basePath.concat("/").concat(sortChildren.get(ourIndex - 1));

            IZkDataListener listener = new IZkDataListener() {
                @Override
                public void handleDataChange(String s, Object o) throws Exception {

                }

                @Override
                public void handleDataDeleted(String s) throws Exception {
                    hasLock[0] = true;
                    reentrantLock.lock();
                    try {
                        condition.signal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            };

            try {
                zkClient.subscribeDataChanges(previousSequencePath, listener);

                reentrantLock.lock();
                if (millisToWait != null && timeUnit != null) {
                    condition.await(millisToWait, timeUnit);
                } else {
                    condition.await();
                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                zkClient.unsubscribeDataChanges(previousSequencePath, listener);
                zkClient.delete(previousSequencePath);
                reentrantLock.unlock();
            }
        }
        return hasLock[0];
    }


    /**
     * 获取排序完的子节点
     *
     * @return
     */
    private List<String> getSortChildren() {
        List<String> sortChildNode = new ArrayList<>();
        try {
            //首先获取:lock_1,lock_0,lock_2
            List<String> list = zkClient.getChildren(basePath);
            //将获取到的锁的子节点按照顺序排序
            sortChildNode = list.stream().sorted(Comparator.comparing(this::getLockNodeNumber)).collect(Collectors.toList());
        } catch (ZkNoNodeException e) {
            //如果没有/lock父节点，就创建一个父节点，然后重新排序
            zkClient.createPersistent(basePath, true);
            getSortChildren();
        }
        return sortChildNode;
    }

    /**
     * 获取锁的number
     *
     * @param path
     * @return
     */
    private String getLockNodeNumber(String path) {
        return path.replace(lockName, "");
    }


    private String createLockNode(ZkClient client, String path) throws Exception {
        // 创建临时循序节点
        return client.createEphemeralSequential(path, null);
    }
}
