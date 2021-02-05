package com.gree;

public class Main {

    public static void main(String[] args) {
        //判断启动命令,成功后执行Router线程,否则退出
        if(Router.isStartParameter(args))
        {
            new Router().start();//启动Router
        }
    }

}

