package com.gree;

/**
 * @description:主方法
 * @author:
 * @time: 2021/2/4
 */
public class Main {

    public static void main(String[] args) {
        //判断启动命令,成功后执行Router线程,否则退出
        if(Command.isStartArguments(args))
        {
            new Router().start();//启动Router
        }
    }

}

