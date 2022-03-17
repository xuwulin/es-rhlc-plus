package com.xwl.esplus.core.enums;

/**
 * 分词器枚举
 * @author xwl
 * @since 2022/3/11 17:22
 */
public enum EsAnalyzerEnum {
    STANDARD,
    SIMPLE,
    STOP,
    WHITESPACE,
    KEYWORD,
    PATTERN,
    LANGUAGE,
    SNOWBALL,
    IK_SMART,
    IK_MAX_WORD;

    EsAnalyzerEnum() {
    }
}
