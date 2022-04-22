package com.xwl.esplus.core.toolkit;

import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.serializer.NameFilter;
import com.xwl.esplus.core.annotation.EsDocument;
import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.annotation.EsDocumentId;
import com.xwl.esplus.core.annotation.EsHighLightField;
import com.xwl.esplus.core.cache.BaseCache;
import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.enums.EsIdTypeEnum;
import com.xwl.esplus.core.metadata.DocumentFieldInfo;
import com.xwl.esplus.core.metadata.DocumentInfo;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

/**
 * 文档字段信息工具类
 *
 * @author xwl
 * @since 2022/3/16 14:43
 */
public class DocumentInfoUtils {
    /**
     * 默认主键名称
     */
    private static final String DEFAULT_ID_NAME = "id";
    /**
     * ES默认的主键名称
     */
    private static final String DEFAULT_ES_ID_COLUMN_NAME = "_id";
    /**
     * 存放文档信息
     * key: es对应的实体类
     * value: 文档信息
     */
    private static final Map<Class<?>, DocumentInfo> DOCUMENT_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取所有es对应的实体类映射的文档信息
     *
     * @return 所有es对应的实体类映射的文档信息
     */
    public static List<DocumentInfo> getDocumentInfos() {
        return new ArrayList<>(DOCUMENT_INFO_CACHE.values());
    }

    /**
     * 获取es对应的实体类映射的文档信息
     *
     * @param clazz es对应的实体类
     * @return es对应的实体类映射的文档信息
     */
    public static DocumentInfo getDocumentInfo(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        // 从缓存中获取
        // ClassUtils.getUserClass(clazz)：获取用户定义的本来的类型，大部分情况下就是类型本身，主要针对cglib做了额外的判断，获取cglib代理之后的父类；
        DocumentInfo documentInfo = DOCUMENT_INFO_CACHE.get(ClassUtils.getUserClass(clazz));
        if (documentInfo != null) {
            return documentInfo;
        }
        // 尝试获取父类缓存
        Class<?> currentClass = clazz;
        while (documentInfo == null && Object.class != currentClass) {
            // 获取父类
            currentClass = currentClass.getSuperclass();
            documentInfo = DOCUMENT_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
        }
        if (documentInfo != null) {
            DOCUMENT_INFO_CACHE.put(ClassUtils.getUserClass(clazz), documentInfo);
        }
        // 缓存中未获取到，则执行初始化
        GlobalConfig globalConfig = GlobalConfigCache.getGlobalConfig();
        return initDocumentInfo(globalConfig, clazz);
    }

    /**
     * 初始化es对应的实体类隐映射的文档信息
     *
     * @param globalConfig 全局配置
     * @param clazz        es对应的实体类
     * @return es对应的实体类隐映射的文档信息
     */
    public synchronized static DocumentInfo initDocumentInfo(GlobalConfig globalConfig, Class<?> clazz) {
        DocumentInfo documentInfo = DOCUMENT_INFO_CACHE.get(clazz);
        if (documentInfo != null) {
            return documentInfo;
        }
        // 缓存中没有获取到DocumentInfo，则初始化
        documentInfo = new DocumentInfo();
        // 初始化索引名称
        initIndexName(clazz, globalConfig, documentInfo);
        // 初始化文档字段
        initDocumentFields(clazz, globalConfig, documentInfo);
        // 放入缓存
        DOCUMENT_INFO_CACHE.put(clazz, documentInfo);
        return documentInfo;
    }

    /**
     * 初始化索引名称
     *
     * @param clazz        es对应的实体类
     * @param globalConfig 全局配置
     * @param documentInfo 文档信息
     */
    private static void initIndexName(Class<?> clazz, GlobalConfig globalConfig, DocumentInfo documentInfo) {
        // 数据库全局配置
        GlobalConfig.DocumentConfig documentConfig = globalConfig.getDocumentConfig();
        // 获取es实体类上的@EsDocument注解
        EsDocument esDocument = clazz.getAnnotation(EsDocument.class);
        // 类名驼峰转下划线
        String simpleName = StringUtils.camelToUnderline(clazz.getSimpleName());
        // 索引前缀
        String indexPrefix = documentConfig.getIndexPrefix();
        // 索引名称
        String indexName = simpleName;
        if (Objects.nonNull(esDocument)) {
            // 获取注解中设置的索引名
            if (StringUtils.isNotBlank(esDocument.value().trim())) {
                indexName = esDocument.value().trim();
            }
            if (esDocument.keepGlobalIndexPrefix() && StringUtils.isNotBlank(indexPrefix)) {
                indexName = indexPrefix + indexName;
            }
        }
        documentInfo.setIndexName(indexName);
    }

    /**
     * 初始化文档主键及文档字段
     *
     * @param clazz        es对应的实体类
     * @param globalConfig 全局配置
     * @param documentInfo 文档信息
     */
    public static void initDocumentFields(Class<?> clazz, GlobalConfig globalConfig, DocumentInfo documentInfo) {
        // 文档全局配置
        GlobalConfig.DocumentConfig documentConfig = globalConfig.getDocumentConfig();
        // 获取实体类的所有字段（排除标注@EsDocumentField(exist = false)注解的字段）
        List<Field> list = getAllFields(clazz);
        // 标记是否已读取到主键
        boolean isReadId = false;
        // 是否存在@EsDocumentId注解
        boolean existDocumentId = isExistDocumentId(list);

        List<DocumentFieldInfo> fieldList = new ArrayList<>();
        for (Field field : list) {
            // 主键ID初始化
            if (!isReadId) {
                if (existDocumentId) {
                    isReadId = initDocumentIdWithAnnotation(documentConfig, documentInfo, field, clazz);
                } else {
                    isReadId = initDocumentIdWithoutAnnotation(documentConfig, documentInfo, field, clazz);
                }
                if (isReadId) {
                    continue;
                }
            }
            // 有自定义注解的字段初始化
            if (initDocumentFieldWithAnnotation(documentConfig, documentInfo, fieldList, field)) {
                continue;
            }
            // 无自定义注解的字段初始化
            initDocumentFieldWithoutAnnotation(documentConfig, documentInfo, fieldList, field);
        }

        // 添加字段列表
        documentInfo.setFieldList(fieldList);
        // 添加fastjson NameFilter
        addNameFilter(documentInfo);
        // 添加fastjson ExtraProcessor
        addExtraProcessor(documentInfo);
    }

    /**
     * 获es对应的实体类的所有字段列表
     *
     * @param clazz es对应的实体类
     * @return es对应的实体类的字段列表
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = ReflectionUtils.getFieldList(ClassUtils.getUserClass(clazz));
        if (CollectionUtils.isNotEmpty(fieldList)) {
            return fieldList.stream()
                    .filter(i -> {
                        // 过滤注解非文档字段属性
                        EsDocumentField esDocumentField = i.getAnnotation(EsDocumentField.class);
                        return esDocumentField == null || esDocumentField.exist();
                    }).collect(toList());
        }
        return fieldList;
    }

    /**
     * 判断es对应的实体类中是否存在自定义主键@EsDocumentId注解
     *
     * @param list 字段列表
     * @return true-存在@EsDocumentId注解，false-不存在@EsDocumentId注解
     */
    public static boolean isExistDocumentId(List<Field> list) {
        for (Field field : list) {
            EsDocumentId tableId = field.getAnnotation(EsDocumentId.class);
            if (tableId != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 文档主键属性初始化-有自定义注解@EsDocumentId
     *
     * @param documentConfig 全局文档配置
     * @param documentInfo   文档信息
     * @param field          字段
     * @param clazz          es对应的实体类
     * @return true-读取到id，false-未读取到id
     */
    private static boolean initDocumentIdWithAnnotation(GlobalConfig.DocumentConfig documentConfig,
                                                        DocumentInfo documentInfo,
                                                        Field field,
                                                        Class<?> clazz) {
        EsDocumentId esDocumentId = field.getAnnotation(EsDocumentId.class);
        if (esDocumentId != null) {
            documentInfo.setHasIdAnnotation(Boolean.TRUE);
            if (StringUtils.isEmpty(documentInfo.getKeyColumn())) {
                // 主键策略（ 注解 > 全局 ）
                // 设置 Sequence 其他策略无效
                if (EsIdTypeEnum.NONE == esDocumentId.type()) {
                    // 如果EsDocumentId注解中的type为NONE（默认值），则使用全局配置的主键策略（默认AUTO）
                    documentInfo.setIdType(documentConfig.getIdType());
                } else {
                    // 否则使用注解中指定的主键策略
                    documentInfo.setIdType(esDocumentId.type());
                }

                // es索引实际字段
                String column = esDocumentId.value();
                field.setAccessible(Boolean.TRUE);
                documentInfo.setClazz(field.getDeclaringClass());
                documentInfo.setIdClass(field.getType());
                documentInfo.setKeyField(field);
                documentInfo.setKeyProperty(field.getName());
                documentInfo.setKeyColumn(column);
                return true;
            } else {
                String msg = "@EsDocumentId annotation maximum one was allowed, but found more than one in %s";
                throw new RuntimeException(String.format(msg, clazz));
            }
        }
//        documentInfo.setHasIdAnnotation(Boolean.TRUE);
        return false;
    }

    /**
     * 文档主键属性初始化-无自定义注解@EsDocumentId
     *
     * @param documentConfig 全局文档配置
     * @param documentInfo   文档信息
     * @param field          字段
     * @param clazz          es对应的实体类
     * @return true-读取到id，false-未读取到id
     */
    private static boolean initDocumentIdWithoutAnnotation(GlobalConfig.DocumentConfig documentConfig,
                                                           DocumentInfo documentInfo,
                                                           Field field,
                                                           Class<?> clazz) {
        String column = field.getName();
        if (DEFAULT_ID_NAME.equalsIgnoreCase(column) || DEFAULT_ES_ID_COLUMN_NAME.equals(column)) {
            if (StringUtils.isEmpty(documentInfo.getKeyColumn())) {
                field.setAccessible(Boolean.TRUE);
                documentInfo.setClazz(field.getDeclaringClass());
                documentInfo.setIdClass(field.getType());
                documentInfo.setIdType(documentConfig.getIdType());
                documentInfo.setKeyField(field);
                documentInfo.setKeyProperty(field.getName());
                documentInfo.setKeyColumn(DEFAULT_ES_ID_COLUMN_NAME);
                return true;
            } else {
                String msg = "@EsDocumentId annotation maximum one was allowed, but found more than one in %s";
                throw new RuntimeException(String.format(msg, clazz));
            }
        }
//        documentInfo.setHasIdAnnotation(Boolean.FALSE);
        return false;
    }

    /**
     * 文档字段属性初始化-有自定义注解@EsDocumentField
     *
     * @param documentConfig 全局文档配置
     * @param documentInfo   文档信息
     * @param fieldList      字段列表
     * @param field          字段
     * @return true-有自定义注解@EsDocumentField，false-没有自定义注解@EsDocumentField
     */
    private static boolean initDocumentFieldWithAnnotation(GlobalConfig.DocumentConfig documentConfig,
                                                           DocumentInfo documentInfo,
                                                           List<DocumentFieldInfo> fieldList,
                                                           Field field) {
        boolean hasAnnotation = false;
        // 获取自定义注解@EsDocumentField
        EsDocumentField esDocumentField = field.getAnnotation(EsDocumentField.class);
        if (Objects.nonNull(esDocumentField) && esDocumentField.exist()) {
            DocumentFieldInfo documentFieldInfo = new DocumentFieldInfo(documentConfig, field, esDocumentField);
            // es中的字段名
            String mappingColumn;
            if (StringUtils.isNotBlank(esDocumentField.value().trim())) {
                // 自定义注解指定的名称优先级最高
                documentInfo.getMappingColumnMap().putIfAbsent(field.getName(), esDocumentField.value().trim());
                documentInfo.getColumnMappingMap().putIfAbsent(esDocumentField.value().trim(), field.getName());
                mappingColumn = esDocumentField.value().trim();
            } else {
                // 下划线驼峰
                mappingColumn = initMappingColumnMapAndGet(documentConfig, documentInfo, field);
            }
            documentFieldInfo.setMappingColumn(mappingColumn);
            fieldList.add(documentFieldInfo);
            hasAnnotation = true;
        }

        // 获取自定义注解@EsHighLightField（字段高亮）
        EsHighLightField esFieldHighLight = field.getAnnotation(EsHighLightField.class);
        if (Objects.nonNull(esFieldHighLight) && StringUtils.isNotBlank(esFieldHighLight.value())) {
            String customField = documentInfo.getMappingColumnMap().get(esFieldHighLight.value());
            String realHighLightField = Objects.isNull(customField) ? esFieldHighLight.value() : customField;
            if (documentConfig.isMapUnderscoreToCamelCase()) {
                realHighLightField = StringUtils.camelToUnderline(realHighLightField);
            }
            documentInfo.getHighlightFieldMap().putIfAbsent(realHighLightField, field.getName());
            hasAnnotation = true;
        }
        return hasAnnotation;
    }

    /**
     * 文档字段属性初始化-无自定义注解@EsDocumentField
     *
     * @param documentConfig 全局文档配置
     * @param documentInfo   文档信息
     * @param fieldList      字段列表
     * @param field          字段
     */
    private static void initDocumentFieldWithoutAnnotation(GlobalConfig.DocumentConfig documentConfig,
                                                           DocumentInfo documentInfo,
                                                           List<DocumentFieldInfo> fieldList,
                                                           Field field) {
        DocumentFieldInfo documentFieldInfo = new DocumentFieldInfo(documentConfig, field);
        // es中的字段名
        documentFieldInfo.setMappingColumn(field.getName());
        fieldList.add(documentFieldInfo);
        // 初始化
        initMappingColumnMapAndGet(documentConfig, documentInfo, field);
    }

    /**
     * 初始化es实体类字段与es字段映射关系Map
     *
     * @param documentConfig 全局文档配置
     * @param documentInfo   文档信息
     * @param field          字段
     * @return es中的字段名
     */
    private static String initMappingColumnMapAndGet(GlobalConfig.DocumentConfig documentConfig,
                                                     DocumentInfo documentInfo,
                                                     Field field) {
        // 自定义字段名及驼峰下划线转换
        String mappingColumn = field.getName();
        if (documentConfig.isMapUnderscoreToCamelCase()) {
            // 下划线转驼峰
            mappingColumn = StringUtils.camelToUnderline(field.getName());
        }
        documentInfo.getMappingColumnMap().putIfAbsent(field.getName(), mappingColumn);
        return mappingColumn;
    }

    /**
     * 添加fastjson字段过滤器
     *
     * @param documentInfo 文档信息
     */
    private static void addNameFilter(DocumentInfo documentInfo) {
        Map<String, String> mappingColumnMap = documentInfo.getMappingColumnMap();
        if (!mappingColumnMap.isEmpty()) {
            NameFilter nameFilter = (object, name, value) -> {
                String mappingColumn = mappingColumnMap.get(name);
                if (Objects.equals(mappingColumn, name)) {
                    return name;
                }
                return mappingColumn;
            };
            documentInfo.setSerializeFilter(nameFilter);
        }
    }

    /**
     * 预添加fastjson解析object时对非实体类字段的处理(比如自定义字段名,下划线等)
     *
     * @param documentInfo 文档信息
     */
    private static void addExtraProcessor(DocumentInfo documentInfo) {
        Map<String, String> columnMappingMap = documentInfo.getColumnMappingMap();
        ExtraProcessor extraProcessor = (object, key, value) ->
                Optional.ofNullable(columnMappingMap.get(key))
                        .flatMap(realMethodName -> Optional.ofNullable(BaseCache.getEsEntitySetterMethod(documentInfo.getClazz(), realMethodName)))
                        .ifPresent(method -> {
                            try {
                                method.invoke(object, value);
                            } catch (Throwable e) {
                                throw ExceptionUtils.epe(e);
                            }
                        });
        documentInfo.setExtraProcessor(extraProcessor);
    }
}
