package com.admin.web.user.entity;

import lombok.Data;

@Data
public class UpdatePwdParam {
    private Long userId;
    private String oldPassword;
    private String password;
}
