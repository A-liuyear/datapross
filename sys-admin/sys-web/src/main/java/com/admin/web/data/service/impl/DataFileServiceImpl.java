package com.admin.web.data.service.impl;

import com.admin.config.minio.MinioConfig;
import com.admin.config.minio.MinioUtils;
import com.admin.web.data.entity.DataFile;
import com.admin.web.data.mapper.DataFileMapper;
import com.admin.web.data.service.DataFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DataFileServiceImpl extends ServiceImpl<DataFileMapper, DataFile> implements DataFileService {

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient minioClient;

    @Override
    public void saveData(DataFile dataFile) {
        this.baseMapper.insert(dataFile);
    }

    @Override
    public void editData(DataFile dataFile) {
        this.baseMapper.updateById(dataFile);
    }

    @Override
    public void deleteData(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        // 确保存储桶存在
        if (!minioUtils.bucketExists(minioConfig.getBucketName())){
            minioUtils.makeBucket(minioConfig.getBucketName(),"");
        }
        String date = DateTime.now().toString("yyyyMMddHHmmss");
        // 上传视频块
        String objectName = date+"_"+file.getOriginalFilename();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        return objectName;
    }
}
