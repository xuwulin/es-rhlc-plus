package com.xwl.esplus.core.config;

import com.xwl.esplus.core.enums.EsFieldStrategyEnum;
import com.xwl.esplus.core.enums.EsIdTypeEnum;

/**
 * 全局配置
 *
 * @author xwl
 * @since 2022/3/11 19:13
 */
public class GlobalConfig {
    /**
     * 文档配置
     */
    private DocumentConfig documentConfig;

    /**
     * elasticsearch json日志输出
     */
    private boolean logEnable = false;

    public static class DocumentConfig {
        /**
         * 主键类型（默认 AUTO）
         */
        private EsIdTypeEnum idType = EsIdTypeEnum.AUTO;
        /**
         * 索引前缀
         */
        private String indexPrefix;
        /**
         * 字段验证策略 (默认 NOT NULL)
         */
        private EsFieldStrategyEnum fieldStrategy = EsFieldStrategyEnum.NOT_NULL;
        /**
         * 存储的日期格式
         */
        private String dateFormat;

        public DocumentConfig() {
        }

        public DocumentConfig(EsIdTypeEnum idType, String indexPrefix, EsFieldStrategyEnum fieldStrategy, String dateFormat) {
            this.idType = idType;
            this.indexPrefix = indexPrefix;
            this.fieldStrategy = fieldStrategy;
            this.dateFormat = dateFormat;
        }

        public EsIdTypeEnum getIdType() {
            return idType;
        }

        public void setIdType(EsIdTypeEnum idType) {
            this.idType = idType;
        }

        public String getIndexPrefix() {
            return indexPrefix;
        }

        public void setIndexPrefix(String indexPrefix) {
            this.indexPrefix = indexPrefix;
        }

        public EsFieldStrategyEnum getFieldStrategy() {
            return fieldStrategy;
        }

        public void setFieldStrategy(EsFieldStrategyEnum fieldStrategy) {
            this.fieldStrategy = fieldStrategy;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }
    }

    public DocumentConfig getDocumentConfig() {
        return documentConfig;
    }

    public void setDocumentConfig(DocumentConfig documentConfig) {
        this.documentConfig = documentConfig;
    }

    public boolean isLogEnable() {
        return logEnable;
    }

    public void setLogEnable(boolean logEnable) {
        this.logEnable = logEnable;
    }
}
