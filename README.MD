## gRPC双向流



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

[grpc/客户端启动后，重连服务端.gif at main · MagicFollower/grpc (github.com)](https://github.com/MagicFollower/grpc/blob/main/doc/images/客户端启动后，重连服务端.gif)

② 客户端启动成功后，接受服务器keep-alive（间隔5s）

[grpc/客户端连接成功，接受keep-alive.gif at main · MagicFollower/grpc (github.com)](https://github.com/MagicFollower/grpc/blob/main/doc/images/客户端连接成功，接受keep-alive.gif)

③ 客户端启动后，服务器失去连接，重连

[grpc/客户端失败重连5次.gif at main · MagicFollower/grpc (github.com)](https://github.com/MagicFollower/grpc/blob/main/doc/images/客户端失败重连5次.gif)



