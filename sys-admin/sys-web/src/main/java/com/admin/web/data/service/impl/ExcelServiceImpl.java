package com.admin.web.data.service.impl;

import com.admin.config.minio.MinioConfig;
import com.admin.web.data.service.ExcelService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {
    
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private MinioConfig minioConfig;

    @Override
    public List<String> getColumns(String objectName) {
        try {
            // 从Minio获取文件流
            InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build());
            
            return getColumnsFromExcel(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("获取Excel列名失败", e);
        }
    }

    private List<String> getColumnsFromExcel(InputStream inputStream) {
        List<String> columns = new ArrayList<>();
        Workbook workbook = null;
        
        try {
            workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            Row headerRow = sheet.getRow(0);     // 获取第一行(表头)
            
            if (headerRow != null) {
                // 遍历所有单元格
                for (Cell cell : headerRow) {
                    if (cell != null) {
                        String cellValue = getCellValue(cell);
                        if (StringUtils.isNoneBlank(cellValue)) {
                            columns.add(cellValue);
                        }
                    }
                }
            }
            
            return columns;
        } catch (Exception e) {
            throw new RuntimeException("解析Excel列名失败", e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
            try {
                inputStream.close();
            } catch (Exception e) {
                // 忽略关闭异常
            }
        }
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}