package com.lilincpp.github.libezftp;


import com.lilincpp.github.libezftp.exceptions.EZFtpNoInitException;
import com.lilincpp.github.libezftp.user.EZFtpUser;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.util.ArrayList;
import java.util.List;

final class EZFtpServerImpl implements IEZFtpServer {

    private FtpServer ftpServer;
    private final Object lock = new Object();
    private boolean isInit = false;

    EZFtpServerImpl(List<EZFtpUser> users, int port) {
        //配置参数
        FtpServerFactory serverFactory = new FtpServerFactory();
        //设置访问用户名和密码还有共享路径
        for (EZFtpUser user : users) {
            BaseUser baseUser = new BaseUser();
            baseUser.setName(user.getName());
            baseUser.setPassword(user.getPassword());
            baseUser.setHomeDirectory(user.getSharedPath());
            List<Authority> authorities = new ArrayList<>();
            authorities.add(user.getPermission().getAuthority());
            baseUser.setAuthorities(authorities);
            try {
                serverFactory.getUserManager().save(baseUser);
            } catch (FtpException e) {
                e.printStackTrace();
            }
        }
        //设置监听端口
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(port);
        serverFactory.addListener("default", factory.createListener());
        //创建FTP服务实例
        ftpServer = serverFactory.createServer();
        isInit = true;
    }

    private void checkInit() {
        synchronized (lock) {
            if (!isInit) {
                throw new EZFtpNoInitException("EZFtp server is no init or has been release!");
            }
        }
    }

    private void release() {
        synchronized (lock) {
            if (ftpServer != null && !ftpServer.isStopped()) {
                ftpServer.stop();
            }
            isInit = false;
        }
    }

    @Override
    public void start() {
        checkInit();
        try {
            ftpServer.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        checkInit();
        ftpServer.stop();
    }

    @Override
    public boolean isStopped() {
        return ftpServer.isStopped();
    }
}
