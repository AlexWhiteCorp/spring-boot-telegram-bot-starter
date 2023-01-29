package com.alexcorp.springspirit.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;

@Slf4j
public class ProfilingUtils {

    public static Object profiling(ProceedingJoinPoint call, String logMsg) {
        StopWatch clock = new StopWatch(call.toString());
        try {
            clock.start(call.toShortString());
            return call.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            clock.stop();
            log.info(String.format(logMsg, clock.getTotalTimeMillis()));
        }
    }

}
