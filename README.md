# MonitorClient
网站源码实时监控及webshell检测查杀工具，工具以C/S构架，当前程序为客户端，分布于同一局域网内的网站服务器，并实时消息传递至服务器端，服务器端项目:[MonitorServer](https://github.com/0kami/MonitorServer)
# 功能
1. 网站源码实时监控，并将文件异动发送到服务器端。
2. 监控模式分强制模式和人工模式
   - 强制模式：运行前自动备份以及自检、运行时监控并强制恢复高危行为(新增，删除，修改)所造成的影响（删除新增文件，恢复删除或修改的文件）。
   - 人工模式：监控但不作出任何处理。
3. webshell检测
    - SSDEEP 文件相似度检测，本项目采集了5000个webshell样本集，去重过滤后生成了1698个SSDEEP值。
    - 静态规则正则匹配，共采集101条规则用于匹配
    - 贝叶斯模型分类加密文件，上述方法无法检测加密后的webshell文件。这里通过NeoPi工具的几个维度(压缩比、信息墒、重合指数、最长单词)来训练数据，区分率达97%
4. webshell模块提供远程接口用于远程处理webshell。
5. 加密通信，防止第三方监听窃密。
 
# 安装

1. 运行环境如下：

| Type     | Service                | Client
|:-------- |:---------------------- |:------------------------------------ 
| System   | Windows/Linux          | None
| Language | PHP7                   | Java 8
| Database | Mysql                  | Sqlite
| NetWork  | tcpmux                 | None

2. 添加so文件

添加环境文件到java library
根据操作系统，添加environment目录下相应的环境文件到步骤一中显示的路径中去，例如64位Linux操作系统添加64-bit Linux/libjnotify.so到图1-1的路径中去即可。

3. 修改客户端配置（config/config.ini）

4. 运行程序：

```
java -jar MonitorClient-2.1.0.jar
```

# 感谢
文件实时监控及处理 [@orleven](https://github.com/orleven)<br>
NeoPi [项目](https://github.com/Neohapsis/NeoPI)<br>
SSDEEP 文本相似度


