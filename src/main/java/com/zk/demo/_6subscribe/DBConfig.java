package com.zk.demo._6subscribe;

import java.io.Serializable;

/**
 * Created by xujinxin on 2017/7/21.
 * 数据库配置信息
 */
public class DBConfig implements Serializable {

    private static final long serialVersionUID = 2250766705698539974L;

    private String dbUrl;
    private String dbPwd;
    private String dbUser;


    DBConfig() {

    }

    DBConfig(String dbUrl, String dbPwd, String dbUser) {
        this.dbUrl = dbUrl;
        this.dbPwd = dbPwd;
        this.dbUser = dbUser;
    }


    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    @Override
    public String toString() {
        return "DBConfig{" +
                "dbUrl='" + dbUrl + '\'' +
                ", dbPwd='" + dbPwd + '\'' +
                ", dbUser='" + dbUser + '\'' +
                '}';
    }
}
