package com.admin.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class ResultVo<T> {
    private String msg;
    private int code;
    private T data;

}
