package com.zk.demo._2zkclientapi;

import org.I0Itec.zkclient.ZkClient;


/**
 * Created by xujinxin on 2017/7/19.
 * 删除一个数据节点
 */
public class _3Delete {

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        boolean e = zkClient.delete("/node7");
        System.out.println(e);
    }

}
