package com.lilincpp.github.libezftp.exceptions;

/**
 * 未初始化，调用相关方法时会抛出该异常
 *
 * @author lilin
 */
public class EZFtpNoInitException extends IllegalStateException {

    public EZFtpNoInitException() {
        super();
    }

    public EZFtpNoInitException(String s) {
        super(s);
    }
}
