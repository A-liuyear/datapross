package com.admin.config.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BusinessException extends  RuntimeException{
    private  Integer code;
    private String message;
}
