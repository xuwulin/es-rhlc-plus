package com.xwl.esplus.core.aop;

import com.xwl.esplus.core.mapper.EsBaseMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @Description: 客户端切换aop
 * @Author: hl
 * @Date: 2022/10/28 15:07
 */
@Aspect
@Component
@ConditionalOnBean(DynamicClientAnnotationAdvisor.class)
public class DynamicClientSwitchAspect {

    // 匹配EsBaseMapper中的所有方法
    @Pointcut("execution(* com.xwl.esplus.core.mapper.EsBaseMapper.*(..))")
    public void matchType() {
    }

    // 环绕执行
    @Around("matchType()")
    public Object Around(ProceedingJoinPoint joinPoint) {
        EsBaseMapper target = (EsBaseMapper) joinPoint.getTarget();
        target.setRestHighLevelClient();
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
