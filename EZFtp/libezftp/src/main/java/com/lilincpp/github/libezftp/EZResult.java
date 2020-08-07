package com.lilincpp.github.libezftp;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class EZResult {

    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAIL = 1;

    @IntDef({RESULT_SUCCESS, RESULT_FAIL})
    @Retention(RetentionPolicy.SOURCE)
    @interface RESULT_CODE {

    }

    private int code;
    private String msg;

    public EZResult(@RESULT_CODE int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
