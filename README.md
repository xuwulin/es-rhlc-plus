# es-rhlc-plus
## 简介

es-rhlc-plus是一个 RestHighLevelClient 的增强工具，在 RestHighLevelClient 的基础上只做增强不做改变，为简化开发、提高效率而生。

> **愿景**
>
> 愿天下没有996，没有ICU

### 特性

- **无侵入**：只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑
- **损耗小**：启动即会自动注入基本 CRUD，性能基本无损耗，直接面向对象操作
- **强大的 CRUD 操作**：内置通用 Mapper，仅仅通过少量配置即可实现单索引大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
- **支持 Lambda 形式调用**：通过 Lambda 表达式，方便的编写各类查询条件，无需再担心字段写错
- **支持主键自动生成**：支持多达 3 种主键策略，可自由配置，完美解决主键问题
- **内置分页插件**：基于 RestHighLevelClient 物理分页，开发者无需关心具体操作，写分页等同于普通 List 查询

### 代码托管

> **[Github](https://github.com/xuwulin/es-rhlc-plus)**

### 参与贡献

欢迎各路好汉一起来参与完善 es-rhlc-plus，期待你的 PR！

- 贡献代码：代码地址 [es-rhlc-plus](https://github.com/xuwulin/es-rhlc-plus)，欢迎提交 Issue 或者 Pull Requests
- 维护文档：文档地址 [es-rhlc-plus ](https://github.com/xuwulin/es-rhlc-plus)，欢迎参与翻译和修订

## 快速开始

### 安装

#### 环境

`es-rhcl-plus`基于JDK8，提供了 `lambda` 形式的调用，所以安装集成`es-rhcl-plus`要求如下：

- JDK 8+
- Spring Boot 2+
- Maven or Gradle
- Elasticsearch 7+

#### 依赖

Maven：

```xml
<dependency>
    <groupId>io.github.xuwulin</groupId>
    <artifactId>es-rhlc-plus-boot-starter</artifactId>
    <version>latest</version>
</dependency>
```

Gradle：

```gr
compile group: 'io.github.xuwulin', name: 'es-rhlc-plus-boot-starter', version: '1.0.0'
```

### 配置

application.yml

必要配置：

```yaml
es-plus:
  # es主机ip:端口，如果为集群使用英文逗号（,）隔开
  address: 139.198.107.120:9200
```

可选配置：

| 配置                                                         | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| es-plus.schema                                               | 连接模式：默认http                                           |
| es-plus.username                                             | 用户名：默认elastic                                          |
| es-plus.password                                             | 密码                                                         |
| es-plus.connect-timeout                                      | 客户端和服务器建立连接的超时时间（单位：ms），默认1000       |
| es-plus.connection-request-timeout                           | 从连接池获取连接的超时时间（单位：ms），默认500              |
| es-plus.socket-timeout                                       | 客户端从服务器读取数据（通讯）的超时时间（单位：ms），默认30000 |
| es-plus.max-connTotal                                        | 连接池中最大连接数（单位：个），默认100                      |
| es-plus.max-conn-per-route                                   | 最大路由连接数（单位：个），默认100                          |
| es-plus.global-config.enable-dsl                             | DSL日志输出，默认false关闭                                   |
| es-plus.global-config.document-config.index-prefix           | 索引前缀                                                     |
| es-plus.global-config.document-config.key-type               | 文档主键策略，默认AUTO（elasticsearch自动生成id）            |
| es-plus.global-config.document-config.field-strategy         | 字段验证策略，<br />IGNORED：忽略判断，<br />NOT_NULL：非NULL判断，<br />NOT_EMPTY：非空判断，<br />默认NOT_NULL |
| es-plus.global-config.document-config.date-format            | es全局日期格式，默认yyyy-MM-dd HH:mm:ss                      |
| es-plus.global-config.document-config.map-underscore-to-camel-case | 是否开启下划线转驼峰，默认开启                               |

### 注解

#### @EsMapperScan

- 描述：mapper扫描注解，用于扫描es实体对应的mapper接口
- 使用位置：启动类

```java
@SpringBootApplication
@EsMapperScan("com.xwl.esplus.test.mapper")
public class EsPlusApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(EsPlusApplication.class, args);
    }
}
```

#### @EsMapper

- 描述：mapper扫描注解，用于扫描es实体对应的mapper接口
- 使用位置：es实体对应的mapper接口

```java
@EsMapper
public interface UserDocumentMapper extends EsBaseMapper<UserDocument> {
}
```

> 注：如果启动类未配置@EsMapperScan，则可以再Mapper文件众使用@EsMapper注解，如果配置了@EsMapperScan，则@EsMapper无效

#### @EsDocument

- 描述：索引注解，标识实体类对应的索引
- 使用位置：实体类

```java
@EsDocument(value = "user_document", keepGlobalIndexPrefix = false)
public class UserDocument {
    private String id;
    private String nickname;
}
```

| 属性                  | 类型    | 必须指定 | 默认值 | 描述                                                         |
| :-------------------- | :------ | :------- | :----- | :----------------------------------------------------------- |
| value                 | String  | 否       | ""     | 索引名称                                                     |
| keepGlobalIndexPrefix | boolean | 否       | true   | 是否保持使用全局的 indexPrefix 的值（当全局 indexPrefix 生效时） |

#### @EsDocumentId

- 描述：文档主键注解
- 使用位置：实体类主键字段

```java
@EsDocument(value = "user_document", keepGlobalIndexPrefix = false)
public class UserDocument {
    @EsDocumentId(value = "_id", type = EsKeyTypeEnum.AUTO)
    private String id;
    private String nickname;
}
```

| 属性  | 类型   | 必须指定 | 默认值             | 描述             |
| :---- | :----- | :------- | :----------------- | :--------------- |
| value | String | 否       | "_id"              | 文档主键字段名称 |
| type  | Enum   | 否       | EsKeyTypeEnum.NONE | 文档主键策略     |

##### EsKeyTypeEnum

| 值        | 描述                                                         |
| :-------- | :----------------------------------------------------------- |
| AUTO      | es自动生成                                                   |
| NONE      | 无状态，该类型为未设置主键类型（注解里等于跟随全局，全局里约等于 INPUT） |
| UUID      | 32 位 UUID 字符串(please use `ASSIGN_UUID`)                  |
| CUSTOMIZE | 用户自定义，由用户传入                                       |

#### @EsDocumentField

- 描述：字段注解（非主键）

```java
@EsDocument(value = "user_document", keepGlobalIndexPrefix = false)
public class UserDocument {
    @EsDocumentId(value = "_id", type = EsKeyTypeEnum.AUTO)
    private String id;
     @EsDocumentField(value = "nickname")
    private String nickname;
}
```

| 属性        | 类型    | 必须指定 | 默认值                      | 描述                                                         |
| :---------- | :------ | :------- | :-------------------------- | :----------------------------------------------------------- |
| value       | String  | 否       | ""                          | 指定es字段名称，如果不指定则和实体字段保持一致               |
| exist       | boolean | 否       | true                        | 是否为文档字段，默认true-存在，false-不存在                  |
| isHighLight | boolean | 否       | false                       | 是否高亮，默认false-不高亮，true-高亮                        |
| isObj       | boolean | 否       | false                       | 是否是对象，默认默认false-不是对象，true-是对象              |
| isNested    | boolean | 否       | false                       | 是否是嵌套对象，默认false-不是嵌套对象，true-是嵌套对象。此属性与isObj属性的作用都是用于判断是否是对象，当nested属性为true时，isObj属性其实就没意义了（可以理解为nested包含了isObj） |
| strategy    | Enum    | 否       | EsFieldStrategyEnum.DEFAULT | 字段验证策略                                                 |

##### EsFieldStrategyEnum

| 值        | 描述                                                        |
| :-------- | :---------------------------------------------------------- |
| IGNORED   | 忽略判断                                                    |
| NOT_NULL  | 非 NULL 判断                                                |
| NOT_EMPTY | 非空判断(只对字符串类型字段,其他类型字段依然为非 NULL 判断) |
| DEFAULT   | 追随全局配置                                                |

### 测试

```java
@SpringBootTest
public class QueryTest {

    @Resource
    private UserDocumentMapper userDocumentMapper;
    
    @Test
    public void testSelectList() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .eq(UserDocument::getAge, 100);
        List<UserDocument> list = userDocumentMapper.list(wrapper);
        System.out.println(list);
    }
}
```

## 核心功能

#### 索引相关

```java
    /**
     * 判断索引是否存在
     *
     * @param indexName 索引名称
     * @return true-存在，false-不存在
     */
    Boolean existsIndex(String indexName);

    /**
     * 创建索引
     *
     * @param wrapper 条件
     * @return true-创建成功，false-创建失败
     */
    Boolean createIndex(EsLambdaIndexWrapper<T> wrapper);

    /**
     * 更新索引
     *
     * @param wrapper 条件
     * @return true-更新成功，false-更新失败
     */
    Boolean updateIndex(EsLambdaIndexWrapper<T> wrapper);

    /**
     * 删除索引
     *
     * @param indexName 索引名称
     * @return true-删除成功，false-删除失败
     */
    Boolean deleteIndex(String indexName);
```

##### 索引是否存在

```java
@Test
public void testExistsIndex() {
    String indexName = "user_document";
    boolean existsIndex = userDocumentMapper.existsIndex(indexName);
    System.out.println(existsIndex);
}
```

##### 创建索引

```java
@Test
public void testCreateIndex() {
    EsLambdaIndexWrapper<UserDocument> wrapper = Wrappers.lambdaIndex();
    // 自定义分词器
    String analysis = "{\"analysis\":{\"filter\":{\"py\":{\"keep_joined_full_pinyin\":true,\"none_chinese_pinyin_tokenize\":false,\"keep_original\":true,\"remove_duplicated_term\":true,\"type\":\"pinyin\",\"limit_first_letter_length\":16,\"keep_full_pinyin\":false}},\"analyzer\":{\"completion_analyzer\":{\"filter\":\"py\",\"tokenizer\":\"keyword\"},\"text_anlyzer\":{\"filter\":\"py\",\"tokenizer\":\"ik_max_word\"}}}}";
    Map<String, Object> analysisMap = JSONObject.parseObject(analysis, Map.class);

    EsIndexParam cnFirstNameParam = new EsIndexParam();
    cnFirstNameParam.setFieldName("firstName");
    cnFirstNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());
    EsIndexParam cnLastNameParam = new EsIndexParam();
    cnLastNameParam.setFieldName("lastName");
    cnLastNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());

    EsIndexParam enFirstNameParam = new EsIndexParam();
    enFirstNameParam.setFieldName("firstName");
    enFirstNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());
    EsIndexParam enLastNameParam = new EsIndexParam();
    enLastNameParam.setFieldName("lastName");
    enLastNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());

    EsIndexParam deptNameParam = new EsIndexParam();
    deptNameParam.setFieldName("allName");
    deptNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());
    deptNameParam.setIndex(true);
    deptNameParam.setIgnoreAbove(50);
    wrapper.indexName("user_document")
        .alias("hello_user")
        .settings(1, 1, analysisMap)
        .mapping(UserDocument::getId, EsFieldTypeEnum.KEYWORD)
        .mapping(UserDocument::getNickname, EsFieldTypeEnum.KEYWORD, true)
        .mapping(UserDocument::getChineseName, Arrays.asList(cnFirstNameParam, cnLastNameParam))
        .mapping(UserDocument::getEnglishName, Arrays.asList(enFirstNameParam, enLastNameParam), EsFieldTypeEnum.NESTED)
        .mapping(UserDocument::getIdNumber, EsFieldTypeEnum.KEYWORD, false, 18)
        .mapping(UserDocument::getAge, EsFieldTypeEnum.INTEGER)
        .mapping(UserDocument::getGender, EsFieldTypeEnum.KEYWORD, false)
        .mapping(UserDocument::getBirthday, "yyyy-MM-dd")
        .mapping(UserDocument::getCompanyName, EsFieldTypeEnum.TEXT, true, null, UserDocument::getAll, "text_anlyzer", EsAnalyzerEnum.IK_MAX_WORD, Arrays.asList(deptNameParam))
        .mapping(UserDocument::getCompanyAddress, EsFieldTypeEnum.TEXT, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
        .mapping(UserDocument::getCompanyLocation, EsFieldTypeEnum.GEO_POINT)
        .mapping(UserDocument::getGeoLocation, EsFieldTypeEnum.GEO_SHAPE)
        .mapping(UserDocument::getRemark, EsFieldTypeEnum.TEXT, UserDocument::getAll, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
        .mapping(UserDocument::getAll, EsFieldTypeEnum.TEXT, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
        // "format": "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd HH:mm:ss.SSS || yyyy-MM-dd || epoch_millis || strict_date_optional_time || yyyy-MM-dd'T'HH:mm:ss'+'08:00"
        .mapping(UserDocument::getHireDate, "yyyy-MM-dd")
        .mapping(UserDocument::getCreatedTime, "yyyy-MM-dd HH:mm:ss")
        .mapping(UserDocument::getUpdatedTime, "yyyy-MM-dd HH:mm:ss")
        .mapping(UserDocument::isDeleted, EsFieldTypeEnum.BOOLEAN);

    boolean isOk = userDocumentMapper.createIndex(wrapper);
    System.out.println(isOk);
}
```

##### 更新索引

```java
@Test
public void testUpdateIndex() {
    String indexName = "user_document";
    EsLambdaIndexWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaIndex()
        .indexName(indexName)
        .mapping(UserDocument::getGeoLocation, EsFieldTypeEnum.GEO_SHAPE);
    boolean isOk = userDocumentMapper.updateIndex(wrapper);
    System.out.println(isOk);
}
```

##### 删除索引

```java
@Test
public void testDeleteIndex() {
    String indexName = "user_document";
    boolean isOk = userDocumentMapper.deleteIndex(indexName);
    System.out.println(isOk);
}
```

#### CRUD接口

##### save

```java
    /**
     * 插入文档
     *
     * @param entity es索引对应的实体类
     * @return 成功条数
     */
    Integer save(T entity);

    /**
     * 批量插入文档
     *
     * @param entityList es对应的实体类列表
     * @return 成功条数
     */
    Integer saveBatch(Collection<T> entityList);
```

##### update

```java
    /**
     * 根据条件更新文档
     *
     * @param entity  es索引对应的实体类
     * @param wrapper 更新条件
     * @return 成功条数
     */
    Integer update(T entity, EsLambdaUpdateWrapper<T> wrapper);

    /**
     * 根据id更新文档
     *
     * @param entity es索引对应的实体类
     * @return 成功条数
     */
    Integer updateById(T entity);

    /**
     * 根据id批量更新文档
     *
     * @param entityList es对应的实体类列表
     * @return 成功条数
     */
    Integer updateBatchById(Collection<T> entityList);
```

##### remove

```java
	/**
     * 根据条件删除文档
     *
     * @param wrapper 删除查询条件
     * @return 成功条数
     */
    Integer remove(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 根据id删除文档
     *
     * @param id 文档主键
     * @return 成功条数
     */
    Integer removeById(Serializable id);

    /**
     * 根据id批量删除文档
     *
     * @param idList 文档主键列表
     * @return 成功条数
     */
    Integer removeByIds(Collection<? extends Serializable> idList);
```

##### search

```java
	/**
     * RestHighLevelClient原生查询
     *
     * @param searchRequest  查询请求参数
     * @param requestOptions 请求选项
     * @return SearchResponse
     */
    SearchResponse search(SearchRequest searchRequest, RequestOptions requestOptions);

    /**
     * 条件查询返回SearchResponse
     *
     * @param wrapper 条件
     * @return SearchResponse
     */
    SearchResponse search(EsLambdaQueryWrapper<T> wrapper);
```

##### count

```java
	/**
     * 获取总数
     *
     * @return 总数
     */
    Long count();

    /**
     * 获取总数
     *
     * @param wrapper 条件
     * @return 总数
     */
    Long count(EsLambdaQueryWrapper<T> wrapper);
```

##### get

```java
	/**
     * 查询一条记录
     *
     * @param wrapper 条件
     * @return 指定的返回对象
     */
    T getOne(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 根据ID查询
     *
     * @param id 主键
     * @return 指定的返回对象
     */
    T getById(Serializable id);
```

##### list

```java
    /**
     * 查询（根据ID集合批量查询）
     *
     * @param idList 主键列表
     * @return 指定的返回对象列表
     */
    List<T> listByIds(Collection<? extends Serializable> idList);

    /**
     * 条件查询
     *
     * @param wrapper 条件
     * @return 指定的返回对象列表
     */
    List<T> list(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 条件查询，返回map集合
     *
     * @param wrapper 条件
     * @return map集合
     */
    List<Map<String, Object>> listMaps(EsLambdaQueryWrapper<T> wrapper);
```

##### page

```java
    /**
     * 分页查询：不指定返回类型及分页参数
     *
     * @param wrapper 条件
     * @return 分页对象（SearchHit）
     */
    PageInfo<SearchHit> pageOriginal(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 分页查询：不指定返回类型，指定分页参数
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return 分页对象（SearchHit）
     */
    PageInfo<SearchHit> pageOriginal(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize);

    /**
     * 分页查询：指定返回类型，不指定分页参数
     *
     * @param wrapper 条件
     * @return 分页对象（指定的返回对象）
     */
    PageInfo<T> page(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 分页查询：指定返回类型及分页参数
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return 分页对象（指定的返回对象）
     */
    PageInfo<T> page(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize);

    /**
     * 分页查询：指定返回类型，不指定分页参数
     *
     * @param wrapper 条件
     * @return 分页对象（Map<String, Object>）
     */
    PageInfo<Map<String, Object>> pageMaps(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 分页查询：指定返回类型及分页参数
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return 分页对象（Map<String, Object>）
     */
    PageInfo<Map<String, Object>> pageMaps(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize);
```

#### 条件构造器

##### eq

```java
eq(R column, Object val)
eq(R column, Object val, Float boost)
eq(boolean condition, R column, Object val)
eq(boolean condition, R column, Object val, Float boost)
```

- 等于
- 例：`eq(UserDocument::getNickname, "张三疯")` --->

```json
{
    "query": {
        "bool": {
            "must": [
                {
                    "term": {
                        "nickname": {
                            "value": "张三疯"
                        }
                    }
                }
            ]
        }
    }
}
```

##### ne

```java
ne(R column, Object val)
ne(R column, Object val, Float boost)
ne(boolean condition, R column, Object val)
ne(boolean condition, R column, Object val, Float boost)
```

- 不等于
- 例：`ne(UserDocument::getNickname, "张三疯")` --->

```java
{
    "query": {
        "bool": {
            "must_not": [
                {
                    "term": {
                        "nickname": {
                            "value": "张三疯"
                        }
                    }
                }
            ]
        }
    }
}
```

##### match

```java
match(R column, Object val)
match(R column, Object val, Float boost)
match(boolean condition, R column, Object val)
match(boolean condition, R column, Object val, Float boost)
```

- 分词匹配
- 例：`match(UserDocument::getCompanyAddress, "成都市")`

```java
{
    "query": {
        "bool": {
            "adjust_pure_negative": true,
            "must": [
                {
                    "match": {
                        "company_address": {
                            "auto_generate_synonyms_phrase_query": true,
                            "query": "成都市",
                            "zero_terms_query": "NONE",
                            "fuzzy_transpositions": true,
                            "boost": 1,
                            "prefix_length": 0,
                            "operator": "OR",
                            "lenient": false,
                            "max_expansions": 50
                        }
                    }
                }
            ],
            "boost": 1
        }
    }
}
```



## 自定义分词器

默认的拼音分词器会将每个汉字单独分为拼音，而我们希望的是每个词条形成一组拼音，需要对拼音分词器做个性化定制，形成自定义分词器。

elasticsearch中分词器（analyzer）的组成包含三部分：

- character filters：在tokenizer之前对文本进行处理。例如删除字符、替换字符
- tokenizer：将文本按照一定的规则切割成词条（term）。例如keyword，就是不分词；还有ik_smart
- tokenizer filter：将tokenizer输出的词条做进一步处理。例如大小写转换、同义词处理、拼音处理等

文档分词时会依次由这三部分来处理文档：

![image-20210723210427878](https://images-1318546573.cos.ap-chengdu.myqcloud.com/typora/image-20210723210427878.png)

我们可以在创建索引库时，通过settings来配置自定义的analysis（分词器）：**仅对当前索引库有效**

声明自定义分词器的语法如下：

```json
PUT /test
{
  "settings": {
    "analysis": {
      "analyzer": { // 自定义分词器
        "my_analyzer": {  // 分词器名称
          "tokenizer": "ik_max_word", // tokenizer：先分词（指定分词器为ik_max_word）
          "filter": "py" // filter(tokenizer filter)：再把分好词的交给拼音分词器处理（不能直接使用pinyin分词器，需要配置下拼音分词器：py）
        },
        "completion_analyzer": { // 拼音自动补全时用，不分词直接转拼音
        "tokenizer": "keyword", // 不分词，词条就是一个整体
        "filter": "py" // 使用拼音分词器转成拼音
        }
      },
      "filter": { // 自定义tokenizer filter
        "py": { // 过滤器名称
          "type": "pinyin", // 过滤器类型，这里是pinyin
		  "keep_full_pinyin": false, // 是否每个汉字都生产拼音
          "keep_joined_full_pinyin": true, // 是否全拼
          "keep_original": true, // 是否保留中文
          "limit_first_letter_length": 16,
          "remove_duplicated_term": true, // 是否去重
          "none_chinese_pinyin_tokenize": false
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "my_analyzer", // 创建索引时使用自定义分词器
        "search_analyzer": "ik_smart" // 搜索时使用：ik_smart/ik_max_word分词器
      }
    }
  }
}
```





不想写了。。。参考[Mybatis-Plus](https://baomidou.com/pages/10c804/#abstractwrapper)条件构造器

### 待完善

- 自动创建索引
- 新增、更新、删除后立马刷新功能
- 查询时参数可以是一段脚本代码
- 算分函数的实现
- 子聚合
- 。。。



