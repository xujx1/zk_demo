package com.zk.demo._5lock._1zkclient;

import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by xujinxin on 2017/7/21.
 * 简单的 互斥锁
 */
public class LockImpl extends BaseLock implements ILock {


    /**
     * 锁名称前缀
     */
    private static final String LOCK_NAME = "lock-";

    /**
     * Zookeeper中locker节点的路径，如：/lock
     */
    private final String basePath;

    /**
     * 获取锁以后自己创建的那个顺序节点的路径
     */
    private String ourLockPath;


    public LockImpl(ZkClient client, String basePath) {
        super(client, basePath, LOCK_NAME);
        this.basePath = basePath;
    }


    @Override
    public boolean getLock() throws Exception {
        ourLockPath = super.tryLock();
        return ourLockPath == null;
    }

    @Override
    public boolean getLock(long timeOut, TimeUnit unit) throws Exception {
        ourLockPath = super.tryLock(timeOut, unit);
        return ourLockPath == null;
    }

    @Override
    public void releaseLock() throws Exception {
        releaseLock(ourLockPath);
    }
}
