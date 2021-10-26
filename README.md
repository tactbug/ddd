## ddd领域驱动设计探索

### Description
这是一个技术验证型的项目, 意在通过一些典型的业务场景来验证某些新技术, 新特性的应用价值, 包括但不限于DDD, 事件溯源, CQRS, 事务性消息, 响应式编程...

### Feature
- 一个商品管理微服务, 提供分类, 品牌, 商品详情的日常管理功能以及配套的查询服务
- 待补充...

### Requirements
- java17 使用到了switch模式匹配功能, 请确保你的IDE开启预览特性支持
- springboot 2.5.6 统一开发框架
- postgresql 13.3 事件存储核心库/查询核心库
- redis 6.2.6 聚合快照, 用来加速对象重放
- kafka 3.0.0 领域事件发布
- tact-id 一个自用的分布式ID生成工具, https://github.com/tactbug/tact-id