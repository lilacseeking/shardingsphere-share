## 1.1.分库分表是什么

分库分表就是为了解决由于数据量过大而导致数据库性能降低的问题，将原来独立的数据库拆分成若干数据库组成 ，将数据大表拆分成若干数据表组成，使得单一数据库、单一数据表的数据量变小，从而达到提升数据库性能的目的。

## 1.2.分库分表的方式

分库分表包括分库和分表两个部分，在生产中通常包括：垂直分库、水平分库、垂直分表、水平分表四种方式。

### 1.2.1.垂直分表

将一个表按照字段分成多表，每个表存储其中一部分字段。

### 1.2.2.垂直分库
通过垂直分表性能得到了一定程度的提升，但是还没有达到要求，并且磁盘空间也快不够了，因为数据还是始终限制在一台服务器，库内垂直分表只解决了单一表数据量过大的问题，但没有将表分布到不同的服务器上，因此每个表还是竞争同一个物理机的CPU、内存、网络IO、磁盘。
经过思考，他把原有的SELLER_DB(卖家库)，分为了PRODUCT_DB(商品库)和STORE_DB(店铺库)，并把这两个库分散到不同服务器，如下图：

### 1.2.2.垂直分库
通过垂直分表性能得到了一定程度的提升，但是还没有达到要求，并且磁盘空间也快不够了，因为数据还是始终限制在一台服务器，库内垂直分表只解决了单一表数据量过大的问题，但没有将表分布到不同的服务器上，因此每个表还是竞争同一个物理机的CPU、内存、网络IO、磁盘。

 垂直分库是指按照业务将表进行分类，分布到不同的数据库上面，每个库可以放在不同的服务器上，它的核心理念是专库专用。

它带来的提升是：

解决业务层面的耦合，业务清晰
能对不同业务的数据进行分级管理、维护、监控、扩展等
高并发场景下，垂直分库一定程度的提升IO、数据库连接数、降低单机硬件资源的瓶颈



垂直分库通过将表按业务分类，然后分布在不同数据库，并且可以将这些数据库部署在不同服务器上，从而达到多个服务器共同分摊压力的效果，但是依然没有解决单表数据量过大的问题。

### 1.2.3.水平分库
经过垂直分库后，数据库性能问题得到一定程度的解决，但是随着业务量的增长，PRODUCT_DB(商品库)单库存储数据已经超出预估。粗略估计，目前有8w店铺，每个店铺平均150个不同规格的商品，再算上增长，那商品数量得往1500w+上预估，并且PRODUCT_DB(商品库)属于访问非常频繁的资源，单台服务器已经无法支撑。此时该如何优化？

水平分库是把同一个表的数据按一定规则拆到不同的数据库中，每个库可以放在不同的服务器上。

它带来的提升是：

解决了单库大数据，高并发的性能瓶颈。

提高了系统的稳定性及可用性。



当一个应用难以再细粒度的垂直切分，或切分后数据量行数巨大，存在单库读写、存储性能瓶颈，这时候就需要进行水平分库了，经过水平切分的优化，往往能解决单库存储量及性能瓶颈。但由于同一个表被分配在不同的数据库，需要额外进行数据操作的路由工作，因此大大提升了系统复杂度。

### 1.2.4.水平分表
按照水平分库的思路对他把PRODUCT_DB_X(商品库)内的表也可以进行水平拆分，其目的也是为解决单表数据量大的问题，如下图：

水平分表是在同一个数据库内，把同一个表的数据按一定规则拆到多个表中。

它带来的提升是：

优化单一表数据量过大而产生的性能问题

避免IO争抢并减少锁表的几率

库内的水平分表，解决了单一表数据量过大的问题，分出来的小表中只包含一部分数据，从而使得单个表的数据量变小，提高检索性能。

### 1.2.5 小结
本章介绍了分库分表的各种方式，它们分别是垂直分表、垂直分库、水平分库和水平分表：
垂直分表：可以把一个宽表的字段按访问频次、是否是大字段的原则拆分为多个表，这样既能使业务清晰，还能提升部分性能。拆分后，尽量从业务角度避免联查，否则性能方面将得不偿失。
垂直分库：可以把多个表按业务耦合松紧归类，分别存放在不同的库，这些库可以分布在不同服务器，从而使访问压力被多服务器负载，大大提升性能，同时能提高整体架构的业务清晰度，不同的业务库可根据自身情况定制优化方案。但是它需要解决跨库带来的所有复杂问题。
水平分库：可以把一个表的数据(按数据行)分到多个不同的库，每个库只有这个表的部分数据，这些库可以分布在不同服务器，从而使访问压力被多服务器负载，大大提升性能。它不仅需要解决跨库带来的所有复杂问题，还要解决数据路由的问题(数据路由问题后边介绍)。
水平分表：可以把一个表的数据(按数据行)分到多个同一个数据库的多张表中，每个表只有这个表的部分数据，这样做能小幅提升性能，它仅仅作为水平分库的一个补充优化。
一般来说，在系统设计阶段就应该根据业务耦合松紧来确定垂直分库，垂直分表方案，在数据量及访问压力不是特别大的情况，首先考虑缓存、读写分离、索引技术等方案。若数据量极大，且持续增长，再考虑水平分库水平分表方案。

## 1.3.分库分表带来的问题



分库分表能有效的缓解了单机和单库带来的性能瓶颈和压力，突破网络IO、硬件资源、连接数的瓶颈，同时也带来了一些问题。

### 1.3.1.事务一致性问题
由于分库分表把数据分布在不同库甚至不同服务器，不可避免会带来分布式事务问题。

### 1.3.2.跨节点关联查询
在没有分库前，我们检索商品时可以通过以下SQL对店铺信息进行关联查询：

但垂直分库后[商品信息]和[店铺信息]不在一个数据库，甚至不在一台服务器，无法进行关联查询。
可将原关联查询分为两次查询，第一次查询的结果集中找出关联数据id，然后根据id发起第二次请求得到关联数据，最后将获得到的数据进行拼装。

### 1.3.3.跨节点分页、排序函数
跨节点多库进行查询时，limit分页、order by排序等问题，就变得比较复杂了。需要先在不同的分片节点中将数据进行排序并返回，然后将不同分片返回的结果集进行汇总和再次排序。
如，进行水平分库后的商品库，按ID倒序排序分页，取第一页：

以上流程是取第一页的数据，性能影响不大，但由于商品信息的分布在各数据库的数据可能是随机的，如果是取第N页，需要将所有节点前N页数据都取出来合并，再进行整体的排序，操作效率可想而知。所以请求页数越大，系统的性能也会越差。
在使用Max、Min、Sum、Count之类的函数进行计算的时候，与排序分页同理，也需要先在每个分片上执行相应的函数，然后将各个分片的结果集进行汇总和再次计算，最终将结果返回。

### 1.3.4.主键避重
在分库分表环境中，由于表中数据同时存在不同数据库中，主键值平时使用的自增长将无用武之地，某个分区数据库生成的ID无法保证全局唯一。因此需要单独设计全局主键，以避免跨库主键重复问题。

### 1.3.5.公共表
  实际的应用场景中，参数表、数据字典表等都是数据量较小，变动少，而且属于高频联合查询的依赖表。例子中地理区域表也属于此类型。
  可以将这类表在每个数据库都保存一份，所有对公共表的更新操作都同时发送到所有分库执行。
  由于分库分表之后，数据被分散在不同的数据库、服务器。因此，对数据的操作也就无法通过常规方式完成，并且它还带来了一系列的问题。好在，这些问题不是所有都需要我们在应用层面上解决，市面上有很多中间件可供我们选择，其中Sharding-JDBC使用流行度较高，我们来了解一下它。

## 2.1 Sharding-JDBC介绍

sharding-JDBC是当当网研发的开源分布式数据库中间件，从 3.0 开始Sharding-JDBC被包含在 Sharding-Sphere中，之后该项目进入进入Apache孵化器，4.0版本之后的版本为Apache版本。
ShardingSphere是一套开源的分布式数据库中间件解决方案组成的生态圈，它由Sharding-JDBC、Sharding-Proxy和Sharding-Sidecar（计划中）这3款相互独立的产品组成。 他们均提供标准化的数据分片、分布式事务和数据库治理功能，可适用于如Java同构、异构语言、容器、云原生等各种多样化的应用场景。
官方地址：https://shardingsphere.apache.org/document/current/cn/overview/
  咱们目前只需关注Sharding-JDBC，它定位为轻量级Java框架，在Java的JDBC层提供的额外服务。 它使用客户端直连数据库，以jar包形式提供服务，无需额外部署和依赖，可理解为增强版的JDBC驱动，完全兼容JDBC和各种ORM框架。
Sharding-JDBC的核心功能为数据分片和读写分离，通过Sharding-JDBC，应用可以透明的使用jdbc访问已经分库分表、读写分离的多个数据源，而不用关心数据源的数量以及数据如何分布。

![image-20210328092401683](C:\Users\lilacseeking\AppData\Roaming\Typora\typora-user-images\image-20210328092401683.png)

上图展示了Sharding-Jdbc的工作方式，使用Sharding-Jdbc前需要人工对数据库进行分库分表，在应用程序中加入Sharding-Jdbc的Jar包，应用程序通过Sharding-Jdbc操作分库分表后的数据库和数据表，由于Sharding-Jdbc是对Jdbc驱动的增强，使用Sharding-Jdbc就像使用Jdbc驱动一样，在应用程序中是无需指定具体要操作的分库和分表的。

## 2.2 Sharding-JDBC快速入门

demo地址：https://github.com/lilacseeking/shardingsphere-share.git

数据分片：

```properties

spring.shardingsphere.datasource.names=d-sharding-master-0,d-sharding-master-1

spring.shardingsphere.datasource.d-sharding-master-0.type=org.apache.commons.dbcp2.BasicDataSource
spring.shardingsphere.datasource.d-sharding-master-0.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.d-sharding-master-0.url=jdbc:mysql://localhost:3306/d-sharding-master-0
spring.shardingsphere.datasource.d-sharding-master-0.username=root
spring.shardingsphere.datasource.d-sharding-master-0.password=root

spring.shardingsphere.datasource.d-sharding-master-1.type=org.apache.commons.dbcp2.BasicDataSource
spring.shardingsphere.datasource.d-sharding-master-1.driver-class-name=com.mysql.jdbc.Driver
spring.shardingsphere.datasource.d-sharding-master-1.url=jdbc:mysql://localhost:3306/d-sharding-master-1
spring.shardingsphere.datasource.d-sharding-master-1.username=root
spring.shardingsphere.datasource.d-sharding-master-1.password=root

spring.shardingsphere.sharding.default-database-strategy.inline.sharding-column=data_id
spring.shardingsphere.sharding.default-database-strategy.inline.algorithm-expression=d-sharding-master-$->{data_id % 2}

spring.shardingsphere.sharding.tables.t_share_order_info.actual-data-nodes=d-sharding-master-$->{0..1}.t_share_order_info_$->{0..1}
spring.shardingsphere.sharding.tables.t_share_order_info.table-strategy.inline.sharding-column=table_id
spring.shardingsphere.sharding.tables.t_share_order_info.table-strategy.inline.algorithm-expression=t_share_order_info_$->{table_id % 2}

spring.shardingsphere.props.sql.show = true
```

读写分离

