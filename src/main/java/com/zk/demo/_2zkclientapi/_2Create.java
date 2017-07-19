package com.zk.demo._2zkclientapi;

import com.zk.demo._2zkclientapi.domain.User;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.*;

/**
 * Created by xujinxin on 2017/7/19.
 * 创建一个数据节点
 */
public class _2Create {


    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);

        String value = zkClient.create("/node10", new User(1, "许金鑫"), CreateMode.PERSISTENT);
        System.out.println(value);
    }

}
