package com.zk.demo._6subscribe;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.SerializableSerializer;


/**
 * Created by xujinxin on 2017/7/21.
 * 服务器
 */
public class WorkServer {


    private final String ZK_ADDRESS = "127.0.0.1:2181";
    private final String ZK_CONFIG_PATH = "/config";
    private final DBConfig dbConfig;
    private final ZkClient zkClient;

    WorkServer(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        this.zkClient = new ZkClient(ZK_ADDRESS, 5000, 5000, new SerializableSerializer());
        this.initConfig();
        this.addListener();
    }

    /**
     * 抢锁
     */


    /**
     * 初始化数据库配置
     */
    private void initConfig() {
        try {
            zkClient.createPersistent(ZK_CONFIG_PATH, dbConfig);
        } catch (ZkNodeExistsException e) {
            System.out.println("初始化数据已存在");
        }
    }

    /**
     * 获取监听配置
     */
    private void addListener() {
        zkClient.subscribeDataChanges(ZK_CONFIG_PATH, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                DBConfig dbConfig = (DBConfig) data;
                System.out.println(dbConfig);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                zkClient.createPersistent(ZK_CONFIG_PATH, dbConfig);
            }
        });
    }


    public void update(DBConfig dbConfig) {
        zkClient.writeData(ZK_CONFIG_PATH, dbConfig);
    }
}
