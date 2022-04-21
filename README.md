# CoolDesktop介绍


CoolDesktop是一个Linux服务器管理软件，
CoolDesktop采用全新的操作方式，让您像操作本地系统一样，操作远程Linux，除了基本的文件复制、粘贴、上传、删除、重复名等基本操作，
还可以进行终端操作，如果对当前应用不满意，可以自行开发软件，挂载到CoolDesktop上，这和我们平常在系统上安装软件是一个道理。

注意，项目使用的部分jar并未上传到maven，所以您clone下来可能无法运行，但不久的将来会上传到maven，但不影响您下载运行

# 下载体验

  [点击这里](https://github.com/houxinlin/cooldesktop/releases/download/v1.1.2/desktop-web.jar)
# 运行 
默认端口为8080，可增加--server.port=xxx修改端口
```shell
java -jar desktop-web.jar --server.port=8080
```
# 开发应用

本系统支持加载自定义应用，如您想开发一个定时任务应用，可通过Vue+SpringBoot开发后挂载到CoolDesktop中，详细请查看 [ 开发文档 ](https://houxinlin.com/DeveloperCoolDesktop.html)

# 软件商店

我们提供软件商店可免费下载您所需要的应用，但它还处于开发阶段，后续，我们会依次上线以下管理软件。
1. 端口管理
2. Tomcat管理
3. Nginx管理
4. 系统监控
5. Mysql备份

敬请期待
# 应用截图
![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9e7d2c3166864a87863820b0dabdc5eb~tplv-k3u1fbpfcp-watermark.image?)


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3b6578cb95bd4b948fd4d8af80a51ce1~tplv-k3u1fbpfcp-watermark.image?)


![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4a2ba0942d874805bdec637ee9c1f091~tplv-k3u1fbpfcp-watermark.image?)



![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2aa28977c8b7475c930b85d0286619fb~tplv-k3u1fbpfcp-watermark.image?)





![2022-03-19 13-43-03屏幕截图.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e34b4997b46249b1923d33ac0373fe68~tplv-k3u1fbpfcp-watermark.image?)

![2022-03-19 13-47-28屏幕截图.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b5b07120153641459e99482b5b2cb443~tplv-k3u1fbpfcp-watermark.image?)

![2022-03-19 13-48-37屏幕截图.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c141e2dc79e340eda5bb47a94f71bc66~tplv-k3u1fbpfcp-watermark.image?)

# 更新日志
**v1.1.2**
1. 修复thymeleaf加载问题

# 联系作者


![23fb4ef734561956026f0f0f8e9d88a.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/26fad3fa2cbb42d8b73f7192608abe55~tplv-k3u1fbpfcp-watermark.image?)
