package com.lilincpp.github.libezftp.exceptions;

/**
 * 未初始化，调用相关方法时会抛出该异常
 *
 * @author lilin
 */
public class EZNoInitException extends IllegalStateException {

    public EZNoInitException() {
        super();
    }

    public EZNoInitException(String s) {
        super(s);
    }
}
