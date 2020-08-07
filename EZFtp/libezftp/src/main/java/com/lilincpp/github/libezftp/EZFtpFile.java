package com.lilincpp.github.libezftp;

import java.io.Serializable;
import java.util.Date;

/**
 * 远端文件信息
 */
public final class EZFtpFile implements Serializable {

    public static final int TYPE_FILE = 0;
    public static final int TYPE_DIRECTORY = 1;
    public static final int TYPE_LINK = 2;

    private String name;
    private String remotePath;
    private int type;
    private long size;
    private Date modifiedDate = null;


    public EZFtpFile(String name, String remotePath, int type, long size, Date modifiedDate) {
        this.name = name;
        this.remotePath = remotePath;
        this.type = type;
        this.size = size;
        this.modifiedDate = modifiedDate;
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

    public int getType() {
        return type;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public String toString() {
        return "EZFtpFile{" +
                "name='" + name + '\'' +
                ", remotePath='" + remotePath + '\'' +
                ", type=" + type +
                ", size=" + size +
                ", modifiedDate=" + modifiedDate +
                '}';
    }
}
