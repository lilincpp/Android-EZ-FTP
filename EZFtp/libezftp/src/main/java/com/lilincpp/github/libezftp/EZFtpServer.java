package com.lilincpp.github.libezftp;

import com.lilincpp.github.libezftp.user.EZFtpUser;

import java.util.List;

public final class EZFtpServer implements IEZFtpServer {

    private IEZFtpServer ftpServerImpl;

    private EZFtpServer(List<EZFtpUser> users) {
        ftpServerImpl = new EZFtpServerImpl(users);
    }


    @Override
    public void start() {
        ftpServerImpl.start();
    }

    @Override
    public void stop() {
        ftpServerImpl.stop();
    }

    @Override
    public boolean isStopped() {
        return ftpServerImpl.isStopped();
    }
}
