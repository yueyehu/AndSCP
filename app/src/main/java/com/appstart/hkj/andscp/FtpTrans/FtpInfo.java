package com.appstart.hkj.andscp.FtpTrans;

import java.io.Serializable;

/**
 * Created by HKJ on 2016/8/3.
 */
public class FtpInfo implements Serializable {
    private String strIp;
    private int intPort;
    private String user;
    private String password;
    public String getStrIp(){
        return strIp;
    }
    public void setStrIp(String strIp){
        this.strIp = strIp;
    }
    public int getIntPort(){
        return intPort;
    }
    public void setIntPort(int intPort){
        this.intPort=intPort;
    }
    public String getUser(){
        return user;
    }
    public void setUser(String user){
        this.user=user;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }
}
