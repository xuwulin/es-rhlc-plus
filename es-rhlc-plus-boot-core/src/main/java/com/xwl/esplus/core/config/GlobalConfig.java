package com.xwl.esplus.core.config;

import com.xwl.esplus.core.constant.EsGlobalConstants;
import com.xwl.esplus.core.enums.EsFieldStrategyEnum;
import com.xwl.esplus.core.enums.EsKeyTypeEnum;
import com.xwl.esplus.core.enums.EsRefreshPolicy;

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
     * elasticsearch DSL日志输出，默认关闭
     */
    private boolean enableDsl = false;

    /**
     * 全局文档配置
     */
    public static class DocumentConfig {
        /**
         * 索引前缀
         */
        private String indexPrefix;
        /**
         * 主键类型（默认 AUTO）
         */
        private EsKeyTypeEnum keyType = EsKeyTypeEnum.AUTO;
        /**
         * 字段验证策略 (默认 NOT NULL)
         */
        private EsFieldStrategyEnum fieldStrategy = EsFieldStrategyEnum.NOT_NULL;
        /**
         * es全局日期格式，默认：yyyy-MM-dd HH:mm:ss
         */
        private String dateFormat = EsGlobalConstants.ES_GLOBAL_DEFAULT_DATE_FORMAT;
        /**
         * 是否开启下划线转驼峰，默认开启
         */
        private boolean mapUnderscoreToCamelCase = true;
        /**
         * enableTrackTotalHits default true,是否开启查询全部数据 默认开启
         */
        private boolean enableTrackTotalHits = true;
        /**
         * must convert to filter must by default, must 条件转filter 默认不转换
         */
        private boolean enableMust2Filter = false;
        /**
         * data refresh policy 数据刷新策略,默认为NONE
         */
        private EsRefreshPolicy refreshPolicy = EsRefreshPolicy.NONE;

        public DocumentConfig() {
        }

        public String getIndexPrefix() {
            return indexPrefix;
        }

        public void setIndexPrefix(String indexPrefix) {
            this.indexPrefix = indexPrefix;
        }

        public EsKeyTypeEnum getKeyType() {
            return keyType;
        }

        public void setKeyType(EsKeyTypeEnum keyType) {
            this.keyType = keyType;
        }

        public EsFieldStrategyEnum getFieldStrategy() {
            return fieldStrategy;
        }

        public void setFieldStrategy(EsFieldStrategyEnum fieldStrategy) {
            this.fieldStrategy = fieldStrategy;
        }

        public void setFieldStrategy(String strategy) {
            EsFieldStrategyEnum fieldStrategy = Enum.valueOf(EsFieldStrategyEnum.class, strategy.toUpperCase());
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

        public boolean isEnableTrackTotalHits() {
            return enableTrackTotalHits;
        }

        public void setEnableTrackTotalHits(boolean enableTrackTotalHits) {
            this.enableTrackTotalHits = enableTrackTotalHits;
        }

        public boolean isEnableMust2Filter() {
            return enableMust2Filter;
        }

        public void setEnableMust2Filter(boolean enableMust2Filter) {
            this.enableMust2Filter = enableMust2Filter;
        }

        public EsRefreshPolicy getRefreshPolicy() {
            return refreshPolicy;
        }

        public void setRefreshPolicy(EsRefreshPolicy refreshPolicy) {
            this.refreshPolicy = refreshPolicy;
        }
    }

    public GlobalConfig() {
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
