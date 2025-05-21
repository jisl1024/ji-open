package com.ji.open.utils;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具类：节假日判断（支持从2007年至今）
 * 数据来源于国务院放假通知:https://sousuo.www.gov.cn/sousuo/search.shtml?code=17da70961a7&searchWord=%E8%8A%82%E5%81%87%E6%97%A5%E5%AE%89%E6%8E%92&dataTypeId=107&sign=97cc6312-c808-4938-8247-94046af60c6d
 *
 * @author jisl on 2025/5/15 14:59
 **/
public class HolidayUtil {

    /**
     * 保存所有假日信息（key: yyyy-MM-dd, value: holiday true/false）
     */
    private static final Map<String, Boolean> HOLIDAY_MAP = new HashMap<>();

    public static final int START_YEAR = 2007;

    /**
     * 在类加载（ClassLoader加载类到JVM）时执行一次
     * 用于对静态变量做复杂初始化操作，比如读取配置文件、加载资源等。
     * @author jisl on 2025/5/15 15:06
     **/
    static {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        int currentYear = now.getYear();
        // 加载2007年至今年份的节假日数据
        for (int year = START_YEAR; year <= currentYear; year++) {
            loadYear(year);
        }
    }

    /**
     * 加载指定年份的 holiday json 文件，如：2023.text
     */
    @SneakyThrows
    public static void loadYear(int year) {
        String fileName = String.format("holiday/%d.txt", year);
        URL resource = HolidayUtil.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new RuntimeException("未找到节假日文件: " + fileName);
        }
        File file = new File(resource.toURI());
        try (InputStream is = new FileInputStream(file)) {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> list = mapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {
            });
            for (Map<String, Object> item : list) {
//                "date"：格式为 yyyy-MM-dd
                String date = (String) item.get("date");
//                true 表示放假（节假日或连休）
//                false 表示调休上班日（即原本是周末但安排为工作）
                Boolean isHoliday = (Boolean) item.get("holiday");
                HOLIDAY_MAP.put(date, isHoliday);
            }
        }
    }

    /**
     * 判断某天是否为节假日（true）或调休（false）或普通工作日（null）
     */
    public static Boolean getHolidayStatus(DateTime date) {
        return HOLIDAY_MAP.get(date.toDateStr());
    }

    /**
     * 判断某天是否为休息日（节假日或周末，含调休为 true）
     */
    public static boolean isRestDay(DateTime date) {
        Boolean status = getHolidayStatus(date);
        if (status != null) {
            return status;
        }
        // 没有配置则默认周六日为休息日
        switch (date.dayOfWeekEnum()) {
            case SATURDAY:
            case SUNDAY:
                return true;
            default:
                return false;
        }
    }

    /**
     * 判断某天是否为工作日（节假日为 false，调休或正常工作为 true）
     */
    public static boolean isWorkDay(DateTime date) {
        return !isRestDay(date);
    }



}
