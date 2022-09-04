#  📚 CoolDesktop介绍

CoolDesktop是一个Linux服务器管理软件，
CoolDesktop采用全新的操作方式，让您像操作本地系统一样，操作远程Linux，除了基本的文件复制、粘贴、上传、删除、重复名等基本操作，
还可以进行终端操作，如果对当前应用不满意，可以自行开发软件，挂载到CoolDesktop上，这和我们平常在系统上安装软件是一个道理。

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
http://www.houxinlin.com:8082
```
# 软件商店

我们提供软件商店可免费下载您所需要的应用，但它还处于开发阶段，后续，我们会依次上线以下管理软件。
1. 端口管理
2. Tomcat管理
3. Nginx管理
4. 系统监控
5. Mysql备份

敬请期待
# 应用截图

![image](https://user-images.githubusercontent.com/38684327/175013968-4f28e931-6a09-4cbb-bb65-dd83696156b7.png)


![image](https://user-images.githubusercontent.com/38684327/175014042-52c56a47-8a5a-4fd4-8d38-7232187379f9.png)

![image](https://user-images.githubusercontent.com/38684327/175014143-de7f6484-6ab6-414c-87c5-43350c535416.png)

![image](https://user-images.githubusercontent.com/38684327/175014648-0f2413f8-d6a1-450b-8630-9e30e8c26c93.png)

# 官方应用
![image](https://user-images.githubusercontent.com/38684327/188296055-fbf07247-405e-4f20-be5a-4e1395fd024d.png)

![image](https://user-images.githubusercontent.com/38684327/188296061-03eded11-b90d-4caf-a4ee-07568cde3e08.png)


# 联系作者


![23fb4ef734561956026f0f0f8e9d88a.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/26fad3fa2cbb42d8b73f7192608abe55~tplv-k3u1fbpfcp-watermark.image?)


