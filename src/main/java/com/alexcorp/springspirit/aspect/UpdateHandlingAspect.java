package com.alexcorp.springspirit.aspect;

import com.alexcorp.springspirit.utils.ProfilingUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UpdateHandlingAspect {

    @Pointcut("execution(public * com.alexcorp.springspirit.core.MessageHandlerContext.handle(..))")
    public void callAtMsgHandling() { }

    @Pointcut("execution(public * com.alexcorp.springspirit.core.KeyboardHandlerContext.handle(..))")
    public void callAtKeyboardHandling() { }


    @Around("callAtMsgHandling()")
    public Object msgHandlingTime(ProceedingJoinPoint call) {
        return ProfilingUtils.profiling(call, "Message handling: %dms");
    }

    @Around("callAtKeyboardHandling()")
    public Object keyboardHandlingTime(ProceedingJoinPoint call){
        return ProfilingUtils.profiling(call, "Keyboard handling: %dms");
    }

}
