package com.xwl.esplus.core.enums;

/**
 * 分词器枚举
 *
 * @author xwl
 * @since 2022/3/11 17:22
 */
public enum EsAnalyzerEnum {
    /**
     * （1）默认分词器，如果未指定，则使用该分词器。
     * （2）按词切分，支持多语言
     * （3）小写处理，它删除大多数标点符号、小写术语，并支持删除停止词。
     * "The 2 QUICK Brown-Foxes jumped over the lazy dog's bone."
     * 上面的句子会被拆分成：
     * [ the, 2, quick, brown, foxes, jumped, over, the, lazy, dog's, bone ]
     */
    STANDARD,
    /**
     * （1）按照非字母切分，简单分词器在遇到不是字母的字符时将文本分解为术语
     * （2）小写处理，所有条款都是小写的。
     * "The 2 QUICK Brown-Foxes jumped over the lazy dog's bone."
     * 上面的句子会被拆分成：
     * [ the, quick, brown, foxes, jumped, over, the, lazy, dog, s, bone ]
     */
    SIMPLE,
    /**
     * （1）类似于Simple Analyzer，但相比Simple Analyzer，支持删除停止字
     * （2）停用词指语气助词等修饰性词语，如the, an, 的， 这等
     * "The 2 QUICK Brown-Foxes jumped over the lazy dog's bone."
     * 上面的句子会被拆分成：
     * [ quick, brown, foxes, jumped, over, lazy, dog, s, bone ]
     */
    STOP,
    /**
     * 空白字符作为分隔符，当遇到任何空白字符，空白分词器将文本分成术语。
     * "The 2 QUICK Brown-Foxes jumped over the lazy dog's bone."
     * 上面的句子会被拆分成：
     * [ The, 2, QUICK, Brown-Foxes, jumped, over, the, lazy, dog's, bone. ]
     */
    WHITESPACE,
    /**
     * 不分词，直接将输入作为一个单词输出，它接受给定的任何文本，并输出与单个术语完全相同的文本。
     */
    KEYWORD,
    /**
     * （1）通过正则表达式自定义分隔符
     * （2）默认是\W+，即非字词的符号作为分隔符
     */
    PATTERN,
    /**
     * ElasticSearch提供许多语言特定的分析工具，如英语或法语。
     */
    LANGUAGE,
    /**
     * 拼音分词器：不能直接使用，有同音字问题，需要配置
     */
    PINYIN,
    /**
     * ik分词器：智能切分，粗粒度
     */
    IK_SMART,
    /**
     * ik分词器：最细切分，细粒度
     */
    IK_MAX_WORD;

    EsAnalyzerEnum() {
    }
}
