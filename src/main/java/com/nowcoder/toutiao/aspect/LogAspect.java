package com.nowcoder.toutiao.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;


/**
 * @program: toutiao
 * @description: log
 * @author: Cheng Qun
 * @create: 2019-04-19 16:12
 */
@Aspect
@Component
public class LogAspect {
    private static final org.slf4j.Logger logger =  LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.nowcoder.toutiao.controller.IndexController.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for(Object arg : joinPoint.getArgs()){
            sb.append("arg:" + arg.toString() + "|");
        }
        logger.info("before method:" + sb.toString());
    }
    @After("execution(* com.nowcoder.toutiao.controller.IndexController.*(..))")
    public void afterMethod(JoinPoint joinPoint){
        logger.info("after method:");
    }
}
