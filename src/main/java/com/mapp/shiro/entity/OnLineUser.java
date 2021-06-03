package com.mapp.shiro.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OnLineUser implements Serializable {

    private Serializable sessionId;
    private String username;

}
