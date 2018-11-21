- Java 操作 MongoDB 的 API 介绍
- 手写实现基于 MongoDB 的 ORM 框架

本章内容详见代码



### 为什么要手写

- 结合业务场景，需要手写，解放双手。
- 我今天在课堂手写，告诉大家实现思想及原理。

- 更好地监控、同意管理和维护



JDBC：单表操作不需要写SQL，所有类都集成Support，我能更好的把控或者优化代码

不仅写了JDBC、redis、mongodb、elastiSearche、hive、hbase

——————————

推行非常便捷，统一风格给整个团队带来一种幸福感爆棚

1. 所有的API，我都统一，而且我会进行统一的培训
2. 降低团队的学习成本，只需要一个人学会，那么整个团队就都学会了

统一方法名

Page selectForPage（QueryRule）

List selectForPage（QueryRule）

T selectForPage（QueryRule）

insert（T）

update（T）

delete（T）



### 实现思路

入口类，所有dao都继承它

abstract BaseDaoSupport<T,PK> T,PK(解决强制转型的问题)

Page<T> pageNo pageSize data

查询条件 QueryRule 查询规则 相当于拼接查询条件

查询条件个性化封装 QueryRuleBulider

————————————

实体类的解析（反射）

EntityOperation



总数据量条数超过200亿，就会出现性能瓶颈

ELK实现非侵入式的监控

————————————

























