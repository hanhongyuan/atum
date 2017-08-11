
## 泓华微服务

GitHub:<https://github.com/BornToRain/atum.git>  


_项目基于Spring Boot 2.0.0.M3,Spring Cloud Dalston.RELEASE，各模块简要说明如下：_  

|模块|端口|说明|  
|---|---|---|  
|[wechat](wechat/README.md)|3333|微信服务模块|
|[commons](commons/README.md)|2222|公用服务模块|
|[accounts](accounts/README.md)|1111|账号服务模块|
|[monitor](monitor/README.md)|8040|应用监控、管理员模块|
|[edge](edge/README.md)|10000|微服务网关|  
|[config](config/README.md)|8888|云配置中心|  
|[registry](registry/README.md)|8761|服务注册中心|  
|[zipkin](zipkin/README.md)|9411|分布式跟踪模块|  
|...|...|  

> 项目启动顺序：  
> 1. 服务注册中心
> 2. 云配置中心、应用监控、分布式跟踪、管理员
> 3. 各个服务模块  
> 4. 服务网关  




# 开发进度

- [X] 搭建微服务架构
- [X] 服务注册
- [X] 服务发现
- [X] 客户端负载均衡
- [X] 云配置中心
- [X] 断路器
- [X] API网关
- [X] 每个服务一个数据库
- [X] 每个服务一个容器
- [X] 使用docker发布微服务
- [X] 访问令牌(JWT)
- [X] 消息驱动
- [X] 事件源
- [X] CQRS
- [ ] 日志聚合
- [X] 应用监控
- [ ] 审计日志
- [ ] 分布式跟踪
- [ ] 异常追踪
- [X] 健康监测
- [ ] UI界面模块
- [X] 公用服务模块
- [x] 微信服务模块

## 服务注册
访问 http://localhost:8761/ 账号:admin 密码:admin

![registry](/screenshots/registry.png)
## 监控
访问 http://localhost:8040/ 账号:admin 密码:admin
### 控制面板
![monitor](/screenshots/monitor1.png)
### 应用注册历史
![monitor](/screenshots/monitor2.png)
### Turbine Hystrix面板
![monitor](/screenshots/monitor3.png)
### 应用信息、健康状况、垃圾回收等详情
![monitor](/screenshots/monitor4.png)
### 计数器
![monitor](/screenshots/monitor5.png)
### 查看和修改环境变量
![monitor](/screenshots/monitor6.png)
### 管理 Logback 日志级别
![monitor](/screenshots/monitor7.png)
### 查看并使用 JMX
![monitor](/screenshots/monitor8.png)
### 查看线程
![monitor](/screenshots/monitor9.png)
### 认证历史
![monitor](/screenshots/monitor10.png)
### 查看 Http 请求轨迹
![monitor](/screenshots/monitor11.png)
### Hystrix 面板
![monitor](/screenshots/monitor12.png)
## 链路跟踪
访问 http://localhost:9411/ 默认账号 admin，密码 admin
### 控制面板
![zipkin](/screenshots/zipkin1.png)
### 链路跟踪明细
![zipkin](/screenshots/zipkin2.png)
### 服务依赖关系
![zipkin](/screenshots/zipkin3.png)
