package com.eiou.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {
    @Before("execution(* com.eiou.spring.aop.BillingService.charge(..))")
    public void beforeCharge(JoinPoint joinPoint) {
        System.out.println("[before] " + joinPoint.getSignature().toShortString());
    }

    @AfterReturning(
            pointcut = "execution(* com.eiou.spring.aop.BillingService.charge(..))",
            returning = "result"
    )
    public void afterCharge(Object result) {
        System.out.println("[after-returning] result=" + result);
    }

    @Around("execution(* com.eiou.spring.aop.BillingService.sensitiveOperation(..))")
    public Object aroundSensitiveOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("[around-before] " + joinPoint.getSignature().toShortString());
        Object result = joinPoint.proceed();
        System.out.println("[around-after] result=" + result);
        return result;
    }
}
