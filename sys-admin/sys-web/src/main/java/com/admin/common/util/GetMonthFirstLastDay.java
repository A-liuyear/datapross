package com.admin.common.util;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
public class GetMonthFirstLastDay {
    public static void main(String[] args) {
        System.out.println("当月第一天: " + getFirstDayOfMonth());
        System.out.println("当月最后一天: " + getLastDayOfMonth());
    }

    public static String getFirstDayOfMonth() {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return firstDay.format(formatter);
    }

    public static String getLastDayOfMonth() {
        LocalDate now = LocalDate.now();
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return lastDay.format(formatter);
    }
}    