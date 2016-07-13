package com.example.spcat.atdemo.domain;

import java.io.Serializable;

/**
 * Created by spc on 2016/7/11.
 */
public class User implements Serializable {

    private String nickName;

    public User(String nickName, String id) {
        this.nickName = nickName;
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
}
