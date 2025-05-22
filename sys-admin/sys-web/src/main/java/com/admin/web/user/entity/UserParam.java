package com.admin.web.user.entity;

import com.admin.common.BasePage;
import lombok.Data;

@Data
public class UserParam extends BasePage {
    private String nickName;
    private String phone;
}
