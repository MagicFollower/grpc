## gRPC双向流（Java）



#### 一、需求分析

- 服务器端

  - 需要存在一个客户端建立连接的方法 [sayHello]
  - 维护客户端服务器双向连接
  - 维持客户端集合，客户端连接时添加，断开时移除

- 客户端

  - 失败重连
  - SpringBoot启动前连接，20次重试，连接失败则SpringBoot启动失败

  1. 1. SpringBoot启动失败后给出直观的提示

  1. SpringBoot启动后重试，不间断重试



#### 二、效果演示

① 客户端启动前连接gRPC-Server

- 5次失败后，SpringBoot启动失败

 ![](https://github.com/MagicFollower/grpc/blob/main/doc/images/%E5%AE%A2%E6%88%B7%E7%AB%AF%E5%A4%B1%E8%B4%A5%E9%87%8D%E8%BF%9E5%E6%AC%A1.gif)

② 客户端启动成功后，接受服务器keep-alive（间隔5s）

 ![](https://github.com/MagicFollower/grpc/blob/main/doc/images/%E5%AE%A2%E6%88%B7%E7%AB%AF%E8%BF%9E%E6%8E%A5%E6%88%90%E5%8A%9F%EF%BC%8C%E6%8E%A5%E5%8F%97keep-alive.gif)

③ 客户端启动后，服务器失去连接，重连

 ![](https://github.com/MagicFollower/grpc/blob/main/doc/images/%E5%AE%A2%E6%88%B7%E7%AB%AF%E5%90%AF%E5%8A%A8%E5%90%8E%EF%BC%8C%E9%87%8D%E8%BF%9E%E6%9C%8D%E5%8A%A1%E7%AB%AF.gif)



