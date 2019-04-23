# bee-java 模仿 beego 框架）
    使用了netty作为底层框架，编写代码无需安装 tomcat 等等服务
    自己的做的app使用了此框架开发的后端程序，流量不大暂时没发现问题
    暂时功能很少，后续会陆续追加
##java 代码

```Java
            new HttpServer()
                .Router(new OrderController())
                .Router("/admin/list", context -> {
                    context.print("进入了管理列表");
                })
                .Router("/user/list",context->{
                    context.print("进入了用户列表");
                })
                .startServer();  
```
##go 代码
```go
        ns := beego.NewNamespace("/v1",
    		beego.NSNamespace("/user",
    			beego.NSInclude(
    				&controllers.UserController{},
    			),
    		),beego.NSNamespace("/email",
    			beego.NSInclude(
    				&controllers.EmailController{},
    			),
    		),beego.NSNamespace("/node",
    			beego.NSInclude(
    				&controllers.NodeController{},
    			),
    		),beego.NSNamespace("/update",
    			beego.NSInclude(
    				&controllers.UpdateController{},
    			),
    		),
    	)
    	beego.AddNamespace(ns)    	
```