package com.admin.common.util;

import cn.hutool.core.collection.CollUtil;
import com.admin.config.exception.BusinessException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel 相关工具类
 */
@Slf4j
public class ExcelUtils {
    /**
     * excel 转为 csv
     * @return
     */
    public static String excelToCsv(InputStream inputStream) {
/*        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }*/
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(inputStream)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (Exception e) {
            log.error("表格处理错误", e);
            throw new BusinessException(500, "表格处理错误");
        }
        if (CollUtil.isEmpty(list)) {
            return "";
        }
        // 转换为csv
        StringBuilder stringBuilder = new StringBuilder();
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取数据
        for (int i=1;i<list.size();i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }


    public static String excelToCsv(InputStream inputStream, List<String> selectedColumns) {
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            StringBuilder csvData = new StringBuilder();

            // 处理表头
            Row headerRow = sheet.getRow(0);
            List<Integer> columnIndices = new ArrayList<>();

            // 找出选定列的索引
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null && selectedColumns.contains(cell.getStringCellValue())) {
                    columnIndices.add(i);
                    csvData.append(cell.getStringCellValue()).append(",");
                }
            }
            csvData.deleteCharAt(csvData.length() - 1).append("\n");

            // 处理数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (int colIndex : columnIndices) {
                        Cell cell = row.getCell(colIndex);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    csvData.append(cell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    csvData.append(cell.getNumericCellValue());
                                    break;
                                // 其他类型处理...
                            }
                        }
                        csvData.append(",");
                    }
                    csvData.deleteCharAt(csvData.length() - 1).append("\n");
                }
            }
            return csvData.toString();
        } catch (Exception e) {
            throw new RuntimeException("Excel转CSV失败", e);
        }
    }
    public static void main(String[] args) {
        excelToCsv(null);
    }
}
