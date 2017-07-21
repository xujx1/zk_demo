package com.zk.demo._5lock._1zkclient;

import java.util.concurrent.TimeUnit;

/**
 * Created by xujinxin on 2017/7/21.
 * 分布式锁接口
 */
public interface ILock {

    /*
     * 获取锁，如果没有得到就等待
     */
    boolean getLock() throws Exception;

    /*
     * 获取锁，直到超时
     */
    boolean getLock(long timeOut, TimeUnit unit) throws Exception;

    /*
     * 释放锁
     */
    void releaseLock() throws Exception;
}
