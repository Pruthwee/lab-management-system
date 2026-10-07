package com.lab.management.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {

    @Around("execution(public * com.lab.management.service..*(..))")
    public Object monitorMethodPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // VIOLATION: Code Sustainability - String concatenation for logging
        String message = "";
        message += "Method: " + joinPoint.getSignature().getName();
        message += " | Execution Time: " + executionTime + " ms";

        // Log slow methods (VIOLATION: No threshold check)
        if (executionTime > 100) {
            System.out.println("[SLOW] " + message);
        }

        return result;
    }
}
