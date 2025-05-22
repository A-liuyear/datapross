package com.admin.web.user.entity;

import lombok.Data;

@Data
public class LoginVo {
    private String nickName;
    private String token;
    private Long userId;
}
