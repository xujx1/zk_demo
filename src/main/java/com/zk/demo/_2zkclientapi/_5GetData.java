package com.zk.demo._2zkclientapi;

import com.zk.demo._2zkclientapi.domain.User;
import org.I0Itec.zkclient.ZkClient;

/**
 * Created by xujinxin on 2017/7/19.
 * 获取一个节点的数据内容
 */
public class _5GetData {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        User user = zkClient.readData("/node10");
        System.out.println(user);
    }

}
