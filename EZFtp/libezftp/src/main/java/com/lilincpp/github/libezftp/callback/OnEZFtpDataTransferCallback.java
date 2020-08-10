package com.lilincpp.github.libezftp.callback;

public interface OnEZFtpDataTransferCallback {

    int START = 1;
    int TRANSFER = 2;
    int COMPLETE = 3;
    int ERROR = 4;

    void onStateChanged(int state);

    void onTransferred(long fileSize,int transferredSize);

    void onErr(int code, String msg);
}
