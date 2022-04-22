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
     * elasticsearch DSL日志输出
     */
    private boolean enableDsl = false;

    /**
     * 全局文档配置
     */
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
         * 统一设置存储的日期格式
         */
        private String dateFormat;
        /**
         * 是否开启下划线转驼峰
         */
        private boolean mapUnderscoreToCamelCase = false;

        public DocumentConfig() {
        }

        public DocumentConfig(EsIdTypeEnum idType, String indexPrefix, EsFieldStrategyEnum fieldStrategy, String dateFormat, boolean mapUnderscoreToCamelCase) {
            this.idType = idType;
            this.indexPrefix = indexPrefix;
            this.fieldStrategy = fieldStrategy;
            this.dateFormat = dateFormat;
            this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
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

        public boolean isMapUnderscoreToCamelCase() {
            return mapUnderscoreToCamelCase;
        }

        public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
            this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
        }
    }

    public DocumentConfig getDocumentConfig() {
        return documentConfig;
    }

    public void setDocumentConfig(DocumentConfig documentConfig) {
        this.documentConfig = documentConfig;
    }

    public boolean isEnableDsl() {
        return enableDsl;
    }

    public void setEnableDsl(boolean enableDsl) {
        this.enableDsl = enableDsl;
    }
}
