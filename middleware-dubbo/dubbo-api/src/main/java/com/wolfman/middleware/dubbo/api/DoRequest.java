package com.wolfman.middleware.dubbo.api;

import java.io.Serializable;

public class DoRequest implements Serializable {

    private static final long serialVersionUID = 2131652938502867871L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DoRequest{" +
                "name='" + name + '\'' +
                '}';
    }

}
