package com.xwl.esplus.core.aop;

import com.xwl.esplus.core.annotation.EsClient;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.lang.NonNull;

/**
 * @Description:
 * @Author: hl
 * @Date: 2022/10/28 14:54
 */
public class DynamicClientAnnotationAdvisor extends AbstractPointcutAdvisor {

    private Advice advice;

    private Pointcut pointcut;

    public DynamicClientAnnotationAdvisor(
            @NonNull DynamicClientAnnotationInterceptor dynamicClientAnnotationInterceptor) {
        this.advice = dynamicClientAnnotationInterceptor;
        this.pointcut = buildPointcut();
    }
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    private Pointcut buildPointcut() {
        Pointcut cpc = new AnnotationMatchingPointcut(EsClient.class, true);
        Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(EsClient.class);
        return new ComposablePointcut(cpc).union(mpc);
    }
}
