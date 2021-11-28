## ddd领域驱动设计暨各种有趣技术的研究

### Description
这是一个技术验证型的项目, 意在通过一些典型的业务场景来验证某些新技术, 新特性的应用价值, 包括但不限于DDD, 事件溯源, CQRS, 事务性消息等..

### Feature
- 一个商品管理微服务, 提供分类, 品牌, 商品详情的日常管理功能以及配套的查询服务
- 待补充...

### api
- 创建分类
  - POST
  - `/product/category`
  - request
    ```
    {
      "name": "mini pad",
      "remark": "mini pad category",
      "parentId": 0
    }
    ```

- 查询分类
  - GET
  - `/product/query/category`
  - request
    - id(long)

### Requirements
- java17 使用到了switch模式匹配功能, 请确保你的IDE开启了预览特性支持
- springboot 2.5.6 统一开发框架
- postgresql 13.3 事件存储核心库/查询核心库
- redis 6.2.6 聚合快照, 用来加速对象重放
- kafka 3.0.0 领域事件发布 
- tact-id 一个自用的分布式ID生成工具, https://github.com/tactbug/tact-id

### module
- common
  - 基础组件模块, 包括一些常用的工具类, 一些基础类信息, 以及avro相关
  - 项目的事件传输采用avro编码协议进行, avro schema需要在.schema包里手动build, build完成需要注册到AvroSchemaGenerator里, 手动执行main方法创建.avsc文件
  - 确保.avsc创建完成后手动编译common包, 编译过程中会自动生成相应的Avro类
  - 注意工具类里面IdUtil中使用到了我自己的分布式ID生成工具(玩票性质的), 你们可以换成其他的
- product
  - 商品服务相关, 领域驱动, 事件溯源, CQRS...
  - 目前只实现的category聚合的相关功能, 事件发布也仅限于categoryCreated类型
  - inbound
    - 入站适配器, 包括http请求和command命令式消息
  - outbound
    - 出站适配器, 包括基于jpa跟redis实现的repository以及用于发布领域事件的publisher
    - 注意因为目前项目使用的还是同步编程模型, 所以事件持久化后的状态判断还是使用自旋的方式, 待研究完reactor, webflux, r2dbc或者随心情引入kafka connector后可以使用响应式模型以及数据库日志追尾来更新事件状态, 这样更优雅
  - query
    - 提供领域相关的查询功能
    - 基于事件流更新的查询视图, 因此你可以随意组合自己喜欢的查询模型, 随意选择自己喜欢的查询中间件(为了方便这里就直接使用跟时间存储库一样的中间件了)
