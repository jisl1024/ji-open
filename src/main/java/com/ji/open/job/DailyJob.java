package com.ji.open.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ji.open.utils.HolidayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @author jishenglong on 2022/11/24 10:50
 */
@Component
@Slf4j
public class DailyJob {


//    @Resource
//    private DailyManager dailyManager;
//
//
//    @Resource
//    private CorpWeChatManager corpWeChatManager;


    @Scheduled(cron = "0 00 22 * * ?")
    public void leetCodeJob() {
//        final String text = dailyManager.leetCodeDailyAttendanceText();
//        corpWeChatManager.sendMsg(text);
    }

    @Scheduled(cron = "0 00 20 * * ?")
    public void readDailyJob() {
//        final String text = dailyManager.readDailyAttendanceText();
//        corpWeChatManager.sendMsg(text);
    }

    @Scheduled(cron = "0 00 17 * * ?")
    public void weeklyReport() {
        DateTime now = DateTime.now();
        if (HolidayUtil.isWorkDay(now) && HolidayUtil.isRestDay(DateUtil.offsetDay(now, 1))) {
//          今天工作日，明天节假日 那么在这一天发送周报提醒
//            final String text = dailyManager.weeklyReport();
//            corpWeChatManager.sendMsg(text);
        }
    }

}
