package com.hek;

import com.hek.command.Command;
import com.hek.router.Router;

/**
 * @description:主类,实现主方法
 * @author: Eventi
 * @time: 2021/2/4
 */
public class Main {

    /**
    * @Description: 主方法
    * @Param:
    * @return: void
    * @Author: Eventi
    * @Date: 2021/2/15
    */
    public static void main(String[] args) {
        //判断启动命令,成功后执行Router线程,否则退出
        if(Command.isStartArguments(args))
        {
            new Router().start();//启动Router
        }
    }

}

