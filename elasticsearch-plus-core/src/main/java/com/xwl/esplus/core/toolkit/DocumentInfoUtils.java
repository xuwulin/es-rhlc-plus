package com.xwl.esplus.core.toolkit;

import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.annotation.EsDocumentId;
import com.xwl.esplus.core.annotation.IndexName;
import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.metadata.DocumentFieldInfo;
import com.xwl.esplus.core.metadata.DocumentInfo;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.enums.EsIdTypeEnum;
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
     * 获取实体映射文档信息
     *
     * @param clazz 类
     * @return 文档字段信息
     */
    public static DocumentInfo getDocumentInfo(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        DocumentInfo documentInfo = DOCUMENT_INFO_CACHE.get(ClassUtils.getUserClass(clazz));
        if (null != documentInfo) {
            return documentInfo;
        }
        // 尝试获取父类缓存
        Class currentClass = clazz;
        while (null == documentInfo && Object.class != currentClass) {
            currentClass = currentClass.getSuperclass();
            documentInfo = DOCUMENT_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
        }
        if (documentInfo != null) {
            DOCUMENT_INFO_CACHE.put(ClassUtils.getUserClass(clazz), documentInfo);
        }

        // 缓存中未获取到,则初始化
        GlobalConfig globalConfig = GlobalConfigCache.getGlobalConfig();
        return initTableInfo(globalConfig, clazz);
    }

    /**
     * 获取所有实体映射表信息
     *
     * @return 所有实体映射表信息
     */
    public static List<DocumentInfo> getTableInfos() {
        return new ArrayList<>(DOCUMENT_INFO_CACHE.values());
    }

    /**
     * 实体类反射获取表信息 初始化
     *
     * @param globalConfig 全局配置
     * @param clazz        类
     * @return 实体信息
     */
    public synchronized static DocumentInfo initTableInfo(GlobalConfig globalConfig, Class<?> clazz) {
        DocumentInfo entityInfo = DOCUMENT_INFO_CACHE.get(clazz);
        if (entityInfo != null) {
            return entityInfo;
        }

        // 没有获取到缓存信息,则初始化
        entityInfo = new DocumentInfo();
        // 初始化表名(索引名)相关
        initTableName(clazz, globalConfig, entityInfo);
        // 初始化字段相关
        initTableFields(clazz, globalConfig, entityInfo);

        // 放入缓存
        DOCUMENT_INFO_CACHE.put(clazz, entityInfo);

        return entityInfo;
    }


    /**
     * 初始化 表主键,表字段
     *
     * @param clazz        类
     * @param globalConfig 全局配置
     * @param entityInfo   实体信息
     */
    public static void initTableFields(Class<?> clazz, GlobalConfig globalConfig, DocumentInfo entityInfo) {
        // 数据库全局配置
        GlobalConfig.DocumentConfig documentConfig = globalConfig.getDocumentConfig();
        List<Field> list = getAllFields(clazz);
        // 标记是否读取到主键
        boolean isReadPK = false;
        // 是否存在 @EsDocumentId 注解
        boolean existTableId = isExistTableId(list);

        List<DocumentFieldInfo> fieldList = new ArrayList<>();
        for (Field field : list) {
            // 主键ID 初始化
            if (!isReadPK) {
                if (existTableId) {
                    isReadPK = initTableIdWithAnnotation(documentConfig, entityInfo, field, clazz);
                } else {
                    isReadPK = initTableIdWithoutAnnotation(documentConfig, entityInfo, field, clazz);
                }
                if (isReadPK) {
                    continue;
                }
            }

            // 有 @DocumentField 注解的字段初始化
            if (initTableFieldWithAnnotation(documentConfig, fieldList, field)) {
                continue;
            }

            // 无 @DocumentField 注解的字段初始化
            fieldList.add(new DocumentFieldInfo(documentConfig, field));
        }

        // 字段列表
        entityInfo.setFieldList(fieldList);

    }


    /**
     * 字段属性初始化
     *
     * @param documentConfig 索引配置
     * @param fieldList      字段列表
     * @param field          字段
     * @return
     */
    private static boolean initTableFieldWithAnnotation(GlobalConfig.DocumentConfig documentConfig,
                                                        List<DocumentFieldInfo> fieldList, Field field) {
        // 获取注解属性，自定义字段
        EsDocumentField esDocumentField = field.getAnnotation(EsDocumentField.class);
        if (null == esDocumentField) {
            return false;
        }

        if (esDocumentField.exist()) {
            fieldList.add(new DocumentFieldInfo(documentConfig, field, field.getName(), esDocumentField));
        }
        return true;
    }


    /**
     * 主键属性初始化
     *
     * @param dbConfig     索引配置
     * @param documentInfo 实体信息
     * @param field        字段
     * @param clazz        类
     * @return 布尔值
     */
    private static boolean initTableIdWithAnnotation(GlobalConfig.DocumentConfig dbConfig, DocumentInfo documentInfo,
                                                     Field field, Class<?> clazz) {
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
     * 主键属性初始化
     *
     * @param documentConfig 索引配置
     * @param documentInfo   实体信息
     * @param field          字段
     * @param clazz          类
     * @return 布尔值
     */
    private static boolean initTableIdWithoutAnnotation(GlobalConfig.DocumentConfig documentConfig, DocumentInfo documentInfo,
                                                        Field field, Class<?> clazz) {
        String column = field.getName();
        if (DEFAULT_ID_NAME.equalsIgnoreCase(column) || DEFAULT_ES_ID_NAME.equals(column)) {
            if (StringUtils.isEmpty(documentInfo.getKeyColumn())) {
                field.setAccessible(Boolean.TRUE);
                documentInfo.setIdType(documentConfig.getIdType());
                documentInfo.setKeyColumn(DEFAULT_ES_ID_NAME);
                documentInfo.setKeyProperty(field.getName());
                documentInfo.setKeyField(field);
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
     * 判断主键注解是否存在
     *
     * @param list 字段列表
     * @return 布尔值
     */
    public static boolean isExistTableId(List<Field> list) {
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
                        // 过滤注解非表字段属性
                        EsDocumentField tableField = i.getAnnotation(EsDocumentField.class);
                        return (tableField == null || tableField.exist());
                    }).collect(toList());
        }
        return fieldList;
    }

    /**
     * 初始化表(索引)名称
     *
     * @param clazz        类
     * @param globalConfig 全局配置
     * @param entityInfo   实体信息
     */
    private static void initTableName(Class<?> clazz, GlobalConfig globalConfig, DocumentInfo entityInfo) {
        // 数据库全局配置
        GlobalConfig.DocumentConfig documentConfig = globalConfig.getDocumentConfig();
        IndexName index = clazz.getAnnotation(IndexName.class);
        String tableName = clazz.getSimpleName().toLowerCase(Locale.ROOT);
        String tablePrefix = documentConfig.getTablePrefix();

        boolean tablePrefixEffect = true;
        String indexName;
        if (Objects.isNull(index)) {
            // 无注解, 直接使用类名
            indexName = tableName;
        } else {
            // 有注解,看注解中是否有指定
            if (StringUtils.isNotBlank(index.value())) {
                indexName = index.value();
                if (StringUtils.isNotBlank(tablePrefix) && !index.keepGlobalPrefix()) {
                    tablePrefixEffect = false;
                }
            } else {
                indexName = tableName;
            }
        }

        String targetIndexName = indexName;
        if (StringUtils.isNotBlank(tablePrefix) && tablePrefixEffect) {
            targetIndexName = tablePrefix + targetIndexName;
        }
        entityInfo.setIndexName(targetIndexName);
    }
}
