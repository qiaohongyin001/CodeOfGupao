package com.wolfman.middleware.dubbo.api;

import java.io.Serializable;

public class DoResponse implements Serializable {

    private static final long serialVersionUID = -5251118293962207024L;

    private Object data;

    private String code;

    private String memo;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "DoResponse{" +
                "data=" + data +
                ", code='" + code + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}
