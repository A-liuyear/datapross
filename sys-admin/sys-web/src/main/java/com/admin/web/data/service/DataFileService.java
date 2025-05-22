package com.admin.web.data.service;

import com.admin.web.data.entity.DataFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface DataFileService extends IService<DataFile> {
    //保存角色
    void saveData(DataFile sysUser);

    void editData(DataFile sysUser);

    void deleteData(Long userId);

    String uploadFile(MultipartFile file) throws Exception;
}
