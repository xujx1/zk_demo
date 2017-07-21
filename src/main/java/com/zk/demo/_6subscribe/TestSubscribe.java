package com.zk.demo._6subscribe;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujinxin on 2017/7/21.
 */
public class TestSubscribe {
    public static void main(String[] args) throws InterruptedException {
        List<WorkServer> workServers = new ArrayList<>(10);

        DBConfig dbConfig = new DBConfig();

        dbConfig.setDbUrl("url");
        dbConfig.setDbUser("user");
        dbConfig.setDbPwd("pwd");

        for (int i = 0; i < 10; i++) {
            WorkServer workServer = new WorkServer(dbConfig);
            workServers.add(workServer);
        }

        for (WorkServer workServer : workServers) {
            workServer.update(new DBConfig("1", "2", "3"));
            Thread.sleep(10000);
        }
    }
}
