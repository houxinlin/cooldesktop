#  📚 CoolDesktop介绍

CoolDesktop是一个Linux服务器管理软件，采用全新的操作方式，让您像操作本地系统一样，操作远程Linux，所拥有的功能如下：

  0. 仿照操作系统，可同时打开多个窗口，并最大化、最小化，<b>拥有加载、卸载第三方软件能力</b>
 
  1. 一键文件复制、粘帖、剪切、删除、重命名
  
  2. 一键文件zip、tar、7z格式压缩、解压
  
  3. 开启多终端，任意Linux命令都可执行
  
  4. 多文件管理窗口、上传文件托放既可完成
  
  5. 日志文件追踪
  
  6. jar文件一键启动、停止、重启
  
  7. 文件共享，分享文件链接给朋友，立即下载
  
  8. <b>开发自定义管理软件</b>
  
  目前软件商店提供的能力：
  1. 图片查看器
  
  2. 文件编辑器
  
  4. 系统监视器(进程、端口、磁盘)
  
  5. java进程管理器，可进行热替换、GC、基础信息仪表盘、实时内存、实时线程、在线反编译出内存中的class

# 应用截图

![image](https://user-images.githubusercontent.com/38684327/175013968-4f28e931-6a09-4cbb-bb65-dd83696156b7.png)


![image](https://user-images.githubusercontent.com/38684327/175014042-52c56a47-8a5a-4fd4-8d38-7232187379f9.png)

![image](https://user-images.githubusercontent.com/38684327/175014143-de7f6484-6ab6-414c-87c5-43350c535416.png)

![image](https://user-images.githubusercontent.com/38684327/175014648-0f2413f8-d6a1-450b-8630-9e30e8c26c93.png)


# 所用语言及依赖
1. kotlin  https://kotlinlang.org/
2. Spring Boot  https://spring.io/projects/spring-boot
3. Vue   https://vuejs.org/
4. thymeleaf  https://www.thymeleaf.org/
5. xterm + jsch  https://xtermjs.org/ +  http://www.jcraft.com/jsch/
6. h2database  https://h2database.com/html/main.html
7. websocket
# 🛫 安装

  [点击这里下载](https://github.com/houxinlin/cooldesktop/releases/download/main/cooldesktop.jar)

  使用scp命令或者xftp上传到您的服务器

  使用下面命令启动，默认端口为2556，可增加--server.port=xxx修改端口



```shell
java -jar cooldesktop.jar --server.port=8080
```
## 默认密码

注意，当您第一次进入系统后，建议修改您的密码
```java
cooldesktop
```
# 🛴 源码构建
## 后端
```shell
./gradlew desktop-web:bootJar
```
构建成功后jar包所在位置位于`./desktop-web/build/libs/desktop-web.jar`
## 前端
地址: https://github.com/houxinlin/cooldesktop-web
```shell
git clone https://github.com/houxinlin/cooldesktop-web.git

npm run build

cp -r ./dist/* ${CoolDesktop_Home}/desktop-web/src/main/resources/static/
```
# 启动配置
1. 如果需要在线安装软件，需要对软件商店服务器地址进行配置，进入《设置》-《软件商店》，输入以下服务器地址，即可在线安装软件。
```shell
https://www.houxinlin.com/application
```
# 软件商店

我们提供软件商店可免费下载您所需要的应用，但它还处于开发阶段，后续，我们会依次上线以下管理软件。
1. 端口管理
2. Tomcat管理
3. Nginx管理
4. 系统监控
5. Mysql备份

敬请期待


# 官方应用
![image](https://user-images.githubusercontent.com/38684327/188296055-fbf07247-405e-4f20-be5a-4e1395fd024d.png)

![image](https://user-images.githubusercontent.com/38684327/188296061-03eded11-b90d-4caf-a4ee-07568cde3e08.png)


# 联系作者


![23fb4ef734561956026f0f0f8e9d88a.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/26fad3fa2cbb42d8b73f7192608abe55~tplv-k3u1fbpfcp-watermark.image?)


