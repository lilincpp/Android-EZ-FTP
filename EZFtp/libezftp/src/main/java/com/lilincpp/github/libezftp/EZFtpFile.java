package com.lilincpp.github.libezftp;

import java.io.Serializable;

/**
 * 远端文件信息
 */
public final class EZFtpFile implements Serializable {
    private String name;
    private String remotePath;
    private EZFtpFileType type;
    private long size;

    public EZFtpFile(String name, String remotePath, EZFtpFileType type, long size) {
        this.name = name;
        this.remotePath = remotePath;
        this.type = type;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public EZFtpFileType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "EZFtpFile{" +
                "name='" + name + '\'' +
                ", remotePath='" + remotePath + '\'' +
                ", type=" + type +
                ", size=" + size +
                '}';
    }
}
