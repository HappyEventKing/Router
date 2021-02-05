package com.gree;

import com.gree.bean.RoutingTable;

import javax.swing.*;
import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @description:Router实现
 * @author:
 * @time: 2021/2/4
 */
public class Router extends Thread {
    public static int routerId;//RouterID
    public static int myPort;//UDPServer端口
    public static int otherPort[];//连接其他Router的端口
    public static RoutingTable routingTable;//路由表

    /**
     * @Description: 发送路由表, 实现(Each router sends out their routing table every 30 seconds.)
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void sendOut() {
        while (true) {
            try {
                Thread.sleep(30 * 1000);
                //TODO:待补充实现方法
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Description: 更新路由表, 实现(Each router updates its own routing table according to the received routing table. ...)
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void update() {
        while (true) {
            //TODO:待补充实现方法
        }
    }

    /**
     * @Description: 扫描附近节点, 实现(Routers must have the ability to detect whether a neighbor is active. ...)
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void detect() {
        while (true) {
            try {
                Thread.sleep(30 * 6 * 1000);
                //TODO:待补充实现方法
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * @Description: 启动路由进程并等待和处理输入命令
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public void run() {
        new Thread() {
            public void run() {
                Router.sendOut();
            }
        }.start();//启动路由发送线程
        new Thread() {
            public void run() {
                Router.update();
            }
        }.start();//启动路由更新线程
        new Thread() {
            public void run() {
                Router.detect();
            }
        }.start();//启动路由扫描线程

        System.out.println("启动完成,RouterID为:" + Router.routerId + "," +
                "RouterUDPServerPort为:" + Router.myPort);
        new Command().start();//执行命令交互线程

    }

}
