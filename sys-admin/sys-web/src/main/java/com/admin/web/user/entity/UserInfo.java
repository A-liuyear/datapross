package com.admin.web.user.entity;

import lombok.Data;

@Data
public class UserInfo {
    private Long  userId;
    private String userName;
    private Object[] permissions;
}
