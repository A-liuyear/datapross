package com.admin.web.user.entity;

import lombok.Data;

@Data
public class LoginParam {
    private String phone;
    private String userName;
    private String password;
    private String code;
    private String codeToken;
}
