package com.lilincpp.github.libezftp.callback;

public interface OnEZCallBack<E> {

    void onSuccess(E response);

    void onFail(int code, String msg);
}
