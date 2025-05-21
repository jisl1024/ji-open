package com.ji.open.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ji.open.utils.HolidayUtil;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HolidayUtilTest {


    @Test
    void testHolidayStatus() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        final int monthValue = now.getMonthValue();
//        如果当前时间是12月份了，那么请求后面一年的数据。（提前创建放假文件信息）
        int currentYear = monthValue == 12 ? now.getYear() + 1 : now.getYear();
        System.out.printf("startYear:%s,currentYear:%s,一共%s年%n", HolidayUtil.START_YEAR, currentYear, currentYear - HolidayUtil.START_YEAR + 1);
        // 加载2018年至今年份的节假日数据
        for (int year = HolidayUtil.START_YEAR; year <= currentYear; year++) {
            DateTime holiday = DateUtil.parse(String.format("%s-10-01", year));
            if (Objects.isNull(HolidayUtil.getHolidayStatus(holiday))) {
//                判断年放假文件是否存在，不存在那么抛异常 提醒创建
                throw new RuntimeException(String.format("当前已经12月份了，需要去创建【%s】年的放假文件", year));
            }
            final boolean restDay = HolidayUtil.isRestDay(holiday);
            assertTrue(restDay);
        }
        System.out.println(HolidayUtil.isRestDay(DateUtil.parse("2025-09-28")));
    }

}
