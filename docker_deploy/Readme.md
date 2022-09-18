# docker-compose一键部署流马项目
## 安装docker + 更换镜像源
参考链接: https://www.jb51.net/article/241288.htm

## 安装docker-compose

```
# 安装
yum install -y docker-compose

# 验证
docker-compose --version
```


## 在Linux中一键部署流马项目

在Linux中下载流马的docker部署包，[下载地址](https://github.com/Chras-fu/Liuma-platform/releases)，解压后进入docker_deploy目录，只需要运行一个命令即可。

注：此时无法使用邮件发送和截图存储，如需使用请先配置/springboot/application.properties中的阿里云邮件和七牛云存储 参考[部署文档](https://docs.qq.com/doc/p/c989fa8bf467eca1a1e0fa59b32ceab017407168?&u=13c7afef0d234ebdaea1712aa89e08aa)

```
# 启动所有容器
docker-compose up -d
```


## 验证部署结果
1. Linux中验证服务启动情况

```
# 查看当前运行的docker容器
docker ps
```
预期结果如下:会生成3个容器, liuma_server + liuma_mysql + liuma_nginx 
![docker_ps](https://user-images.githubusercontent.com/96771570/177343567-4f54d35e-50c3-4f60-8ff2-085c583abbe2.png)

端口映射情况如下(前面是本机端口, 后面是容器里面的端口)

```
mysql     3400   ->  3306      
java      8100   ->  8080
nginx     90   ->  80
```


2. 外部机器访问网站
- 浏览器访问url, 正常的话会直接跳转到流马的登录界面  

```
http://实际ip:90
```
![login](https://user-images.githubusercontent.com/96771570/177343616-976d7062-2039-434f-b35f-b100cc231e49.png)

- 输入用户名和密码

```
demo
123456
```
如果正常跳转进入平台的主页, 则证明后端服务部署正常!
![首页](https://user-images.githubusercontent.com/96771570/177343668-8fa3248e-baa7-4cc1-878b-d6b62e4c5fcc.png)

## 更新维护容器的方法
假如后续需要二次开发, 那么需要自己手动重新编译前端资源和后端服务。
- 同时更新了前端服务和后端服务
1. 替换前端资源: 用通过npm run build编译的dist文件夹替换 liuma-platform/docker_deploy/nginx/data中的dits文件夹
2. 替换后端资源: 通过mvn clean package, 用target目录生成的jar包替换
liuma-platform/docker_deploy/springboot中的jar包
3. 重新编译容器服务:

依次运行下面2条命令即可(所有容器都会删掉,重新创建所有的容器)
```
# 先停止再删掉docker-compose生成的容器
docker-compose rm -s -f

# 一键后台启动所有docker-compose中的容器
docker-compose up -d
```

- 只更新单个服务
1. 只更新了前端代码, 那么只需要替换 liuma-platform/docker_deploy/nginx/data中的数据即可
2. 只更新了后端代码
比如, 只更新了java代码, 其它没有动,那么可以只重新构建java的容器即可(不过由于java容器依赖于mysql容器, 所以mysql容器也会重新创建).
```
# 重新构建单个容器(server是容器的具体名字)
docker-compose up -d --force-recreate --build server
```
总体来说, 这种模式只会重新创建2个容器, 会比上一种模式构建快一些.

