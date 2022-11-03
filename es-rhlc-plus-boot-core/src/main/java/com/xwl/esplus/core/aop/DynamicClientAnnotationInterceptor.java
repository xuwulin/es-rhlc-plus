package com.xwl.esplus.core.aop;

import com.xwl.esplus.core.annotation.EsClient;
import com.xwl.esplus.core.toolkit.DynamicClientContextHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * @Description:
 * @Author: hl
 * @Date: 2022/10/28 11:23
 */
public class DynamicClientAnnotationInterceptor implements MethodInterceptor {


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            DynamicClientContextHolder.push(determineClient(invocation));
            return invocation.proceed();
        } finally {
            DynamicClientContextHolder.poll();
        }
    }

    private String determineClient(MethodInvocation invocation){
        Method method = invocation.getMethod();
        EsClient ec = method.isAnnotationPresent(EsClient.class)
                ? method.getAnnotation(EsClient.class)
                : AnnotationUtils.findAnnotation(targetClass(invocation), EsClient.class);
        String key = ec.value();
        return key;
    }

    public Class<?> targetClass(MethodInvocation invocation) {
        return invocation.getMethod().getDeclaringClass();
    }
}
