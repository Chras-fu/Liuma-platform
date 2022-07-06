一、项目介绍

流马测试平台是一款低代码的自动化测试工具平台，支持API/WEB/APP(APP开发中)自动化测试。演示平台地址: http://demo.liumatest.cn <br>
为更好的随时随地、自由切换地支持自动化测试，流马测试平台分为管理平台和测试引擎两个项目。本项目即为管理平台前后端代码，前端项目为vue+elementUI、后端项目为java+springboot。<br>

二、 开发环境

环境依赖: nodejs 14、java 1.8、mysql 8 <br>
IDE推荐: vue使用VSCode、java使用IDEA <br>
启动项目: <br>

前端<br>
第一步: 切换目录，cd Liuma-platform/LiuMa-frontent <br>
第二步: 安装依赖，npm install <br>
第三步: 启动项目，npm run dev <br>
启动后，浏览器打开登录页，构建成功 <br>

后端<br>
第一步: IDEA打开目录 Liuma-platform/LiuMa-backend <br>
第二步: 使用maven安装依赖 <br>
第三步: 新建数据库名: liuma <br>
第四步: 配置application.properties数据库连接 <br>
第五步: 配置阿里云邮件和七牛云存储相关信息(可以先跳过 不影响启动 但无法发送邮件和保存截图 配置见后面) <br>
第六步: 启动LiuMaApplication文件 <br>
首次启动后会创建相关数据表和基础数据，启动成功后，查看数据库liuma，所有数据表均已初始化成功 <br><br>

验证启动成功<br>
项目启动后，默认会新建两个用户: 系统管理员LMadmin/Liuma@123456、演示项目的项目管理员demo/123456 <br>
使用上述初始用户登录平台，登录成功后，即表示项目启动成功 <br>

三、第三方服务

阿里云邮件<br>
主要用于计划执行后发送邮件 可用公司邮箱替换 需自行开发 <br>
配置步骤: <br>
第一步: 注册阿里云账号 申请开通邮件服务 <br>
第二步: 获取accessKey/accessSecret 设置邮件发送人 <br>
第三步: 将以上信息填写在文件~/application.properties对应位置 <br>
注: 阿里云邮件每天有200封免费额度，超过则需要计费(价格不贵)，如果不愿意使用可对接公司自己的邮箱 <br>

七牛云存储<br>
主要用于存放WEB测试执行过程截图 公司有文件存储服务可以替换 需自行开发 <br>
配置步骤: <br>
第一步: 注册七牛云账号 开通空间存储服务 <br>
第二步: 创建存储空间bucket 获取ak/sk信息 同时获取加速域名(可以先试用测试域名 后期配置自定义域名) <br>
第三步: 将以上信息填写在文件~/application.properties对应位置 <br>
注: 七牛云存储每个用户有10G免费空间容量，超过则需要计费(价格不贵)，如果不愿意使用也可用公司自己的文件存储服务 <br>

四、发布部署

部署文档请前往流马测试官网获取，官网地址: http://www.liumatest.cn <br><br>

欢迎关注微信公众号：流马测试 <br>
![qr](https://user-images.githubusercontent.com/96771570/161195670-3868f409-ed49-431f-8650-185e3e179679.png)
