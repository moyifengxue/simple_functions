<div align="center">
    <a href="https://moyifeng.blog.csdn.net/"> <img src="https://badgen.net/badge/MYF/莫逸风BLOG/4ab8a1?icon=rss"></a>
    <a href="https://github.com/1046895947"> <img src="https://badgen.net/badge/MYF/莫逸风GitHub/4ab8a1?icon=github"></a>
</div>
## 莫逸风企业级项目解决方案
项目版本控制说明:功能遵循极简原则，在基本SpringBoot的基础上最小依赖实现功能解决方案。

为了方便学习，会对版本进行隔离，如1.1.0基于1.0.0功能开发，学习过程中可以直接拉取1.0.0代码进行跟踪学习。
#### 1.0版本初始化

**版本：**
- SpringBoot-2.7.2
- OpenJDK-17
- MybatisPlus-3.5.2

**功能：**

- 依赖SpringWeb
- 依赖lombok
- 依赖MybatisPlus
- 依赖MybatisPlusGenerator
- 实现HelloWord接口访问

```mermaid
graph LR;
A(1.0.0:初始化hello接口)-->A1;
A(1.0.0:初始化hello接口)-->A2;
A(1.0.0:初始化hello接口)-->A3;
A(1.0.0:初始化hello接口)-->A4;
A1(1.1.0:整合Redis)-->A11(1.1.1:分布式锁);
B(Main)-->A
```
