package com.xwl.esplus.core.enums;

/**
 * @author xwl
 * @since 2022/3/16 11:14
 */
public enum EsBaseParamTypeEnum {
    /**
     * AND开头左括号 (
     */
    AND_LEFT_BRACKET(1),
    /**
     * AND开头右括号 )
     */
    AND_RIGHT_BRACKET(2),
    /**
     * OR开头左括号 (
     */
    OR_LEFT_BRACKET(3),
    /**
     * OR开头右括号 )
     */
    OR_RIGHT_BRACKET(4),
    /**
     * OR 左右括号都包含的情况 比如:
     * wrapper.eq(User::getName, "张三")
     * .or()
     * .eq(Document::getAge, 18);
     */
    OR_ALL(5);
    /**
     * 类型
     */
    private Integer type;

    EsBaseParamTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
