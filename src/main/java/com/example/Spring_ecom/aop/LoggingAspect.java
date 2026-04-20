package com.example.Spring_ecom.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger("LoggingAspect");

    //return type class-name.methodname(args)

    @Before("execution(* com.example.Spring_ecom.service.ProductService.*(..))")
    public void logMethodCall(JoinPoint joinPoint){
            LOGGER.info("Method called " + joinPoint.getSignature().getName());
    }

    @After("execution(* com.example.Spring_ecom.service.ProductService.*(..))")
    public void logMethodExecuted(JoinPoint joinPoint){
        LOGGER.info("Method Executed " + joinPoint.getSignature().getName());
    }
}
