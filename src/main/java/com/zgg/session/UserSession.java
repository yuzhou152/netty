package com.zgg.session;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

@Data
public class UserSession implements Serializable {
    /**
     * session 参数实体类
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String passport;
    private String token;
    private String name;

}
