package com.xwl.esplus.core.toolkit;

import com.xwl.esplus.core.annotation.EsDocument;
import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.annotation.EsDocumentId;
import com.xwl.esplus.core.annotation.EsHighLightField;
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
     * 储存反射类表信息
     */
    private static final Map<Class<?>, DocumentInfo> DOCUMENT_INFO_CACHE = new ConcurrentHashMap<>();
    /**
     * 默认主键名称
     */
    private static final String DEFAULT_ID_NAME = "id";
    /**
     * Es 默认的主键名称
     */
    private static final String DEFAULT_ES_ID_NAME = "_id";

    /**
     * 获取文档信息
     *
     * @param clazz 类
     * @return 文档信息
     */
    public static DocumentInfo getDocumentInfo(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        // ClassUtils.getUserClass(clazz)：获取用户定义的本来的类型，大部分情况下就是类型本身，主要针对cglib做了额外的判断，获取cglib代理之后的父类；
        DocumentInfo documentInfo = DOCUMENT_INFO_CACHE.get(ClassUtils.getUserClass(clazz));
        if (null != documentInfo) {
            return documentInfo;
        }
        // 尝试获取父类缓存
        Class currentClass = clazz;
        while (documentInfo == null && Object.class != currentClass) {
            // 获取父类
            currentClass = currentClass.getSuperclass();
            documentInfo = DOCUMENT_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
        }
        if (documentInfo != null) {
            DOCUMENT_INFO_CACHE.put(ClassUtils.getUserClass(clazz), documentInfo);
        }
        // 缓存中未获取到,则初始化
        GlobalConfig globalConfig = GlobalConfigCache.getGlobalConfig();
        return initDocumentInfo(globalConfig, clazz);
    }

    /**
     * 获取所有实体映射文档信息
     *
     * @return 所有实体映射文档信息
     */
    public static List<DocumentInfo> getDocumentInfos() {
        return new ArrayList<>(DOCUMENT_INFO_CACHE.values());
    }

    /**
     * 实体类反射获取文档信息，初始化
     *
     * @param globalConfig 全局配置
     * @param clazz        类
     * @return 文档对应实体信息
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
     * 初始化 文档主键,文档字段
     *
     * @param clazz        类
     * @param globalConfig 全局配置
     * @param documentInfo 实体信息
     */
    public static void initDocumentFields(Class<?> clazz, GlobalConfig globalConfig, DocumentInfo documentInfo) {
        // 文档全局配置
        GlobalConfig.DocumentConfig documentConfig = globalConfig.getDocumentConfig();
        // 获取实体类的所有字段（排除标注@EsDocumentField(exist = false)注解的字段）
        List<Field> list = getAllFields(clazz);
        // 标记是否读取到主键
        boolean isReadPK = false;
        // 是否存在 @EsDocumentId 注解
        boolean existDocumentId = isExistDocumentId(list);

        List<DocumentFieldInfo> fieldList = new ArrayList<>();
        for (Field field : list) {
            // 主键ID 初始化
            if (!isReadPK) {
                if (existDocumentId) {
                    isReadPK = initDocumentIdWithAnnotation(documentConfig, documentInfo, field, clazz);
                } else {
                    isReadPK = initDocumentIdWithoutAnnotation(documentConfig, documentInfo, field, clazz);
                }
                if (isReadPK) {
                    continue;
                }
            }

            // 有自定义注解的字段初始化
            if (initDocumentFieldWithAnnotation(documentConfig, fieldList, field, documentInfo)) {
                continue;
            }

            // 无自定义注解的字段初始化
            fieldList.add(new DocumentFieldInfo(documentConfig, field));
        }

        // 字段列表
        documentInfo.setFieldList(fieldList);
    }

    /**
     * 文档字段属性初始化
     *
     * @param documentConfig 索引配置
     * @param fieldList      字段列表
     * @param field          字段
     * @return
     */
    private static boolean initDocumentFieldWithAnnotation(GlobalConfig.DocumentConfig documentConfig,
                                                           List<DocumentFieldInfo> fieldList,
                                                           Field field,
                                                           DocumentInfo documentInfo) {
        boolean hasAnnotation = false;
        // 获取自定义注解
        EsDocumentField esDocumentField = field.getAnnotation(EsDocumentField.class);
        EsHighLightField esFieldHighLight = field.getAnnotation(EsHighLightField.class);
        if (Objects.nonNull(esDocumentField) && esDocumentField.exist()) {
            fieldList.add(new DocumentFieldInfo(documentConfig, field, field.getName(), esDocumentField));
            hasAnnotation = true;
        }
        if (Objects.nonNull(esFieldHighLight) && StringUtils.isNotBlank(esFieldHighLight.value())) {
            documentInfo.getHighlightFieldMap().putIfAbsent(esFieldHighLight.value(), field.getName());
            hasAnnotation = true;
        }
        return hasAnnotation;
    }

    /**
     * 文档主键属性初始化
     *
     * @param dbConfig     索引配置
     * @param documentInfo 文档信息
     * @param field        字段
     * @param clazz        类
     * @return 布尔值
     */
    private static boolean initDocumentIdWithAnnotation(GlobalConfig.DocumentConfig dbConfig,
                                                        DocumentInfo documentInfo,
                                                        Field field,
                                                        Class<?> clazz) {
        EsDocumentId esDocumentId = field.getAnnotation(EsDocumentId.class);
        if (esDocumentId != null) {
            if (StringUtils.isEmpty(documentInfo.getKeyColumn())) {
                // 主键策略（ 注解 > 全局 ）
                // 设置 Sequence 其他策略无效
                if (EsIdTypeEnum.NONE == esDocumentId.type()) {
                    documentInfo.setIdType(dbConfig.getIdType());
                } else {
                    documentInfo.setIdType(esDocumentId.type());
                }

                // 字段
                String column = esDocumentId.value();
                field.setAccessible(Boolean.TRUE);
                documentInfo.setClazz(field.getDeclaringClass());
                documentInfo.setKeyColumn(column);
                documentInfo.setKeyField(field);
                documentInfo.setIdClass(field.getType());
                documentInfo.setKeyProperty(field.getName());
                return true;
            } else {
                String msg = "There must be only one, Discover multiple @EsDocumentId annotation in %s";
                throw new RuntimeException(String.format(msg, clazz));
            }
        }
        documentInfo.setHasIdAnnotation(Boolean.TRUE);
        return false;
    }

    /**
     * 文档主键属性初始化
     *
     * @param documentConfig 索引配置
     * @param documentInfo   实体信息
     * @param field          字段
     * @param clazz          类
     * @return 布尔值
     */
    private static boolean initDocumentIdWithoutAnnotation(GlobalConfig.DocumentConfig documentConfig, DocumentInfo documentInfo,
                                                           Field field, Class<?> clazz) {
        String column = field.getName();
        if (DEFAULT_ID_NAME.equalsIgnoreCase(column) || DEFAULT_ES_ID_NAME.equals(column)) {
            if (StringUtils.isEmpty(documentInfo.getKeyColumn())) {
                field.setAccessible(Boolean.TRUE);
                documentInfo.setIdType(documentConfig.getIdType());
                documentInfo.setKeyColumn(DEFAULT_ES_ID_NAME);
                documentInfo.setKeyProperty(field.getName());
                documentInfo.setKeyField(field);
                documentInfo.setIdClass(field.getType());
                documentInfo.setClazz(field.getDeclaringClass());
                return true;
            } else {
                String msg = "There must be only one, Discover multiple @EsDocumentId annotation in %s";
                throw new RuntimeException(String.format(msg, clazz));
            }
        }
        documentInfo.setHasIdAnnotation(Boolean.FALSE);
        return false;
    }

    /**
     * 判断文档主键注解是否存在
     *
     * @param list 字段列表
     * @return 布尔值
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
     * 获取该类的所有属性列表
     *
     * @param clazz 类
     * @return 字段列表
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = ReflectionUtils.getFieldList(ClassUtils.getUserClass(clazz));
        if (CollectionUtils.isNotEmpty(fieldList)) {
            return fieldList.stream()
                    .filter(i -> {
                        // 过滤注解非文档字段属性
                        EsDocumentField esDocumentField = i.getAnnotation(EsDocumentField.class);
                        if (esDocumentField != null && !esDocumentField.exist()) {
                            return false;
                        }
                        return true;
                    }).collect(toList());
        }
        return fieldList;
    }

    /**
     * 初始化索引名称
     *
     * @param clazz        类
     * @param globalConfig 全局配置
     * @param documentInfo 实体信息
     */
    private static void initIndexName(Class<?> clazz, GlobalConfig globalConfig, DocumentInfo documentInfo) {
        // 数据库全局配置
        GlobalConfig.DocumentConfig documentConfig = globalConfig.getDocumentConfig();
        // 获取类上的注解@EsDocument
        EsDocument esDocument = clazz.getAnnotation(EsDocument.class);
        // 类名驼峰转下划线
        String simpleName = StringUtils.camelToUnderline(clazz.getSimpleName());
        // 索引前缀
        String indexPrefix = documentConfig.getIndexPrefix();
        // 索引名称
        String indexName = simpleName;

        if (Objects.nonNull(esDocument)) {
            // 获取注解中设置的索引名
            if (StringUtils.isNotBlank(esDocument.value())) {
                indexName = esDocument.value();
            }
            if (esDocument.keepGlobalIndexPrefix() && StringUtils.isNotBlank(indexPrefix)) {
                indexName = indexPrefix + indexName;
            }
        }
        documentInfo.setIndexName(indexName);
    }
}
