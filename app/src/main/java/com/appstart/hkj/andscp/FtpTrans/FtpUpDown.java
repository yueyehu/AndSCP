package com.appstart.hkj.andscp.FtpTrans;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.TimeZone;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
/**
 * Created by HKJ on 2016/8/3.
 */
public class FtpUpDown implements Serializable {
    private FTPClient ftpClient;
    private String strIp;
    private int intPort;
    private String user;
    private String password;

    /* *
     * Ftp构造函数
     */
    public FtpUpDown(String strIp, int intPort, String user, String Password) {
        this.strIp = strIp;
        this.intPort = intPort;
        this.user = user;
        this.password = Password;
        this.ftpClient = new FTPClient();
    }
    /* *
     * Ftp构造函数
     */
    public FtpUpDown() {
        this.strIp = "192.168.1.62";
        this.intPort = 21;
        this.user = "a333";
        this.password = "a631a631";
        this.ftpClient = new FTPClient();
    }
    /**
     * @return 输出登录状态
     * */
    public int ftpLogin() {
        int loginFlag = 0;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        this.ftpClient.setControlEncoding("GBK");
        this.ftpClient.configure(ftpClientConfig);
        this.ftpClient.setConnectTimeout(30*1000);
        try {
            if (this.intPort > 0) {
                this.ftpClient.connect(this.strIp, this.intPort);
            } else {
                this.ftpClient.connect(this.strIp);
            }
            // FTP服务器连接回答
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                loginFlag = 1;
            }
            else if(!this.ftpClient.login(this.user, this.password)){
                loginFlag = 2;
            }
            else{
                // 设置传输协议
                this.ftpClient.enterLocalPassiveMode();
                this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                loginFlag = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginFlag = 4;
        }
        if(loginFlag==0){
            this.ftpClient.setBufferSize(1024 * 2);
            this.ftpClient.setDataTimeout(30 * 1000);
        }
        return loginFlag;
    }

    /**
     * @退出关闭服务器链接
     * */
    public int ftpLogOut() {
        int logOutState = 0;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                boolean reuslt = this.ftpClient.logout();// 退出FTP服务器
                if (reuslt) {
                    logOutState = 0;
                    //logger.info("成功退出服务器");
                }
            } catch (IOException e) {
                logOutState = 1;
                e.printStackTrace();
                //logger.warn("退出FTP服务器异常！" + e.getMessage());
            } finally {
                try {
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (IOException e) {
                    logOutState = 2;
                    e.printStackTrace();
                    //logger.warn("关闭FTP服务器的连接异常！");
                }
            }
        }
        return logOutState;
    }

    /***
     * 上传Ftp文件
     * @param localFile 当地文件
     * @param romotUpLoadePath上传服务器路径 - 应该以/结束
     * */
    public boolean uploadFile(File localFile, String romotUpLoadePath) {
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            //String pubstr = Environment.getExternalStorageDirectory();
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            //logger.info(localFile.getName() + "开始上传.....");
            success = this.ftpClient.storeFile(localFile.getName(), inStream);
            if (success == true) {
                //logger.info(localFile.getName() + "上传成功");
                return success;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //logger.error(localFile + "未找到");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /***
     * 下载文件
     * @param remoteFileName   待下载文件名称
     * @param localDires 下载到当地那个路径下
     * @param remoteDownLoadPath remoteFileName所在的路径
     * */

    public boolean downloadFile(String remoteFileName, String localDires,
                                String remoteDownLoadPath) {
        String strFilePath = localDires + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            outStream = new BufferedOutputStream(new FileOutputStream(
                    strFilePath));
            //logger.info(remoteFileName + "开始下载....");
            success = this.ftpClient.retrieveFile(remoteFileName, outStream);
            if (success == true) {
                //logger.info(remoteFileName + "成功下载到" + strFilePath);
                return success;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // logger.error(remoteFileName + "下载失败");
        } finally {
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (success == false) {
            // logger.error(remoteFileName + "下载失败!!!");
        }
        return success;
    }

    /***
     * @上传文件夹
     * @param localDirectory
     *            当地文件夹
     * @param remoteDirectoryPath
     *            Ftp 服务器路径 以目录"/"结束
     * */
    public boolean uploadDirectory(String localDirectory,
                                   String remoteDirectoryPath) {
        File src = new File(localDirectory);
        try {
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
            this.ftpClient.makeDirectory(remoteDirectoryPath);
            // ftpClient.listDirectories();
        } catch (IOException e) {
            e.printStackTrace();
            //logger.info(remoteDirectoryPath + "目录创建失败");
        }
        File[] allFile = src.listFiles();
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
            if (!allFile[currentFile].isDirectory()) {
                String srcName = allFile[currentFile].getPath().toString();
                uploadFile(new File(srcName), remoteDirectoryPath);
            }
        }
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
            if (allFile[currentFile].isDirectory()) {
                // 递归
                uploadDirectory(allFile[currentFile].getPath().toString(),
                        remoteDirectoryPath);
            }
        }
        return true;
    }

    /***
     * @下载文件夹
     * @param localDirectoryPath本地地址
     * @param remoteDirectory 远程文件夹
     * */
    public boolean downLoadDirectory(String localDirectoryPath,String remoteDirectory) {
        try {
            String fileName = new File(remoteDirectory).getName();
            localDirectoryPath = localDirectoryPath + fileName + "//";
            new File(localDirectoryPath).mkdirs();
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (!allFile[currentFile].isDirectory()) {
                    downloadFile(allFile[currentFile].getName(),localDirectoryPath, remoteDirectory);
                }
            }
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (allFile[currentFile].isDirectory()) {
                    String strremoteDirectoryPath = remoteDirectory + "/"+ allFile[currentFile].getName();
                    downLoadDirectory(localDirectoryPath,strremoteDirectoryPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //logger.info("下载文件夹失败");
            return false;
        }
        return true;
    }
    // FtpClient的Set 和 Get 函数
    public FTPClient getFtpClient() {
        return ftpClient;
    }
    public void setFtpClient(FTPClient ftpClient) {
        if(this.ftpClient==null) this.ftpClient=new FTPClient();
        this.ftpClient = ftpClient;
    }
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
