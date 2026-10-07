package com.lab.management.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(public * com.lab.management.service..*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        // VIOLATION: Code Sustainability - String concatenation for logging
        String message = "";
        message += "[" + LocalDateTime.now() + "] ";
        message += "Method: " + joinPoint.getSignature().getName();
        message += " | Class: " + joinPoint.getSignature().getDeclaringTypeName();

        System.out.println(message);  // Should use SLF4J
    }

    @After("execution(public * com.lab.management.service..*(..))")
    public void logMethodExit(JoinPoint joinPoint) {
        String message = "[" + LocalDateTime.now() + "] Method completed: " +
                        joinPoint.getSignature().getName();

        System.out.println(message);  // Should use SLF4J
    }
}
