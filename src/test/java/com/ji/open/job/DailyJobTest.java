package com.ji.open.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@SpringBootTest
public class DailyJobTest {


    @Resource
    private DailyJob dailyJob;

    /**
     * 入口：触发模拟定时任务扫描执行
     */
    @Test
    public void simulateScheduledExecution() {
        log.info("🕒 开始扫描并执行定时任务...");
        invokeScheduledMethods(DailyJob.class);
    }

    /**
     * 扫描类中所有标注了 @Scheduled 的方法，并根据 cron 判断是否应执行
     */
    @SneakyThrows
    private void invokeScheduledMethods(Class<?> targetClass) {
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Scheduled.class)) {
                Scheduled scheduled = method.getAnnotation(Scheduled.class);
                String cronExpression = scheduled.cron();

                if (shouldExecute(cronExpression, method.getName())) {
                    log.info("✅ 执行方法：{}", method.getName());
                    method.invoke(dailyJob);  // 调用目标方法
                }

                log.info("----------------------------");
            }
        }
    }

    /**
     * 判断是否处于 cron 表达式允许的执行时间窗口
     */
    @SneakyThrows
    private boolean shouldExecute(String cronExpressionStr, String methodName) {
        CronExpression cronExpression = new CronExpression(cronExpressionStr);
        // 当前北京时间
        DateTime beijingNow = new DateTime();
        DateTime nowTime = DateUtil.offsetMinute(beijingNow, -5);  // 假设检查过去 5 分钟内是否应执行

        log.info("🕓 当前北京时间：{},当前时区:{}", beijingNow, TimeZone.getDefault().getID());

        // 下次执行时间
        Date nextExecutionTime = cronExpression.getNextValidTimeAfter(nowTime);

        log.info("📌 method={}, cron={}, 当前时间={}, 下次执行时间={}",
                methodName, cronExpressionStr,
                nowTime,
                DateUtil.format(nextExecutionTime, "yyyy-MM-dd HH:mm:ss"));

        // 如果下次执行时间在当前时间之后且小于某个阈值，说明该任务在此窗口内应执行
        return DateUtil.between(nowTime, nextExecutionTime, DateUnit.HOUR) < 1;
    }


}
