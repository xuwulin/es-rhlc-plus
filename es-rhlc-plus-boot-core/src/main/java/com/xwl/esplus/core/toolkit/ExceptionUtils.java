package com.xwl.esplus.core.toolkit;

import com.xwl.esplus.core.exception.EsPlusException;

/**
 * 异常辅助工具类
 *
 * @author xwl
 * @since 2022/3/11 15:42
 */
public class ExceptionUtils {
    private ExceptionUtils() {
    }

    /**
     * 返回一个新的异常，统一构建，方便统一处理
     *
     * @param msg 消息
     * @param t   异常信息
     * @return 返回异常
     */
    public static EsPlusException epe(String msg, Throwable t, Object... params) {
        return new EsPlusException(String.format(msg, params), t);
    }

    /**
     * 重载的方法
     *
     * @param msg 消息
     * @return 返回异常
     */
    public static EsPlusException epe(String msg, Object... params) {
        return new EsPlusException(String.format(msg, params));
    }

    /**
     * 重载的方法
     *
     * @param t 异常
     * @return 返回异常
     */
    public static EsPlusException epe(Throwable t) {
        return new EsPlusException(t);
    }

    public static void throwEpe(boolean condition, String msg, Object... params) {
        if (condition) {
            throw epe(msg, params);
        }
    }
}
