# 专利项目

### 1. 自动生成代码
- 读 zcy-patent-code-generator 内 READEME.md

### 2.启动
1. 修改application-dev.yml 中的mysql，redis配置
2. 执行ZcyPatentApplication.java 中的主方法启动程序
3. 访问 http://localhost:8091/zcy-patent/zcyPtArchiveAsk/getZcyPtArchiveAskById/1 看是否进入方法即可
4. jar包启动：java .jar zcy-patent.jar --spring.profiles.active=dev -Xms2048m -Xmx2048m -Xmn512m -Xss2M -XX:ParallelGCThreads=8

### 3.redis
- 支持无中心集群和单点两种模式， 只需要切换application.yml 中的redis配置即可

### 4.netty
1. 建立tcp长连接 request.controller.tcp.RestTcpController.login()
2. 调用tcp长连接 request.controller.tcp.RestTcpController
3. 启动时执行    common.netty.config.TcpActionLoader   扫描长连接相关注解及接口 -> 启动netty服务 -> 与服务端建立连接
4. 发送到接收指令调用链 
   MessageSendHandler -> TcpSendHandler -> MessageEncoder -> MessageDecoder -> MessageReceiverHandler -> TcpReceiveHandler 
5. 本地环境搭建  
  - 可建立两个端，服务端不需操作，客户端可使用application-devclient.yml中的配置
  - 再打开TcpActionLoader中连接服务端的注解
  - 先启动服务端 ， 再启动客户端即可

```sql
建表语句
CREATE TABLE `zcy_pt_archive_ask` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `archive_id` int(8) default NULL,
  `ask_type` varchar(100) default  NULL,
  `name` varchar(100) default  NULL ,
  `content` varchar(100) default  NULL,
  `create_id` int(8) default  NULL ,
  `create_name` varchar(100) default NULL,
  `create_time` timestamp default null ,
  `modify_id` int(8) default  NULL ,
  `modify_name` varchar(100) default NULL ,
  `modify_time` timestamp default null ,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='demo表';
```