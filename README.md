#  📚 CoolDesktop介绍

CoolDesktop是一个Linux服务器管理软件，
CoolDesktop采用全新的操作方式，让您像操作本地系统一样，操作远程Linux，除了基本的文件复制、粘贴、上传、删除、重复名等基本操作，
还可以进行终端操作，如果对当前应用不满意，可以自行开发软件，挂载到CoolDesktop上，这和我们平常在系统上安装软件是一个道理。



# 🛫 安装

  [点击这里下载](https://github.com/houxinlin/cooldesktop/releases/download/v2.0.0/desktop-web-2.0.0.jar)

  使用scp命令或者xftp上传到您的服务器

  使用下面命令启动，默认端口为8080，可增加--server.port=xxx修改端口

```shell
java -jar desktop-web.jar --server.port=8080
```
# 🛴 构建
```shell
./gradlew desktop-web:bootJar
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

![9e7d2c3166864a87863820b0dabdc5eb_tplv-k3u1fbpfcp-watermark](https://user-images.githubusercontent.com/38684327/164463617-ef9bfb4f-bc81-4e67-887b-4b0ea99c8db1.jpg)

![2aa28977c8b7475c930b85d0286619fb_tplv-k3u1fbpfcp-watermark](https://user-images.githubusercontent.com/38684327/164464210-48f70250-bfe8-4a56-838a-9aaee23709f2.jpg)

![4a2ba0942d874805bdec637ee9c1f091_tplv-k3u1fbpfcp-watermark](https://user-images.githubusercontent.com/38684327/164464223-636f3429-63d8-43c9-a8f3-5277403c34d0.png)

![b5b07120153641459e99482b5b2cb443_tplv-k3u1fbpfcp-watermark](https://user-images.githubusercontent.com/38684327/164464233-51088e44-7b85-44df-9874-99223963eec9.png)

![c141e2dc79e340eda5bb47a94f71bc66_tplv-k3u1fbpfcp-watermark](https://user-images.githubusercontent.com/38684327/164464246-38653e45-5448-4292-a26e-666783a620e7.png)

![e34b4997b46249b1923d33ac0373fe68_tplv-k3u1fbpfcp-watermark](https://user-images.githubusercontent.com/38684327/164464265-affdf621-5ce8-4938-8d59-fb7565302053.png)


# 更新日志
**v1.1.2**
1. 修复thymeleaf加载问题

# 联系作者


![23fb4ef734561956026f0f0f8e9d88a.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/26fad3fa2cbb42d8b73f7192608abe55~tplv-k3u1fbpfcp-watermark.image?)
# cooldesktop-application-jar-manager
