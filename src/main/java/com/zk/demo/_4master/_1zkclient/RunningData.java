package com.zk.demo._4master._1zkclient;

import java.io.Serializable;

/**
 * Created by xujinxin on 2017/7/20.
 * 必须实现Serializable接口，否则会抛异常
 * org.I0Itec.zkclient.exception.ZkMarshallingError: java.io.NotSerializableException: com.zk.demo._4master._1zkclient.RunningData
 */
public class RunningData implements Serializable {

    private static final long serialVersionUID = 4260577459043203630L;

    //服务器id
    private long sid;

    //服务器名称
    private String sname;

    //当前服务器第几次尝试获取master
    private int tryCount;


    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }


    public int getTryCount() {
        return tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    @Override
    public String toString() {
        return "RunningData{" +
                "sid=" + sid +
                ", sname='" + sname + '\'' +
                ", tryCount=" + tryCount +
                '}';
    }
}
