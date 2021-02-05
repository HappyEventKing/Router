package com.gree;

import com.gree.bean.RoutingTable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Router extends Thread {
    public static int routerId;//RouterID
    public static int myPort;//UDPServer端口
    public static int otherPort[];//连接其他Router的端口
    public static RoutingTable routingTable;//路由表

    //判断启动命令
    public static boolean isStartParameter(String[] args) {
        if (args.length < 2) {//判断启动参数是否正确,启动参数必须包括ID和myport;
            System.out.println("参数错误,至少需要输入两个个参数");
            return false;
        } else {
            try {
                Router.routerId = Integer.parseInt(args[0]);
                Router.myPort = Integer.parseInt(args[1]);
                if (args.length >= 3) {
                    int tempPort[] = new int[args.length - 2];
                    for (int i = 0; i < args.length - 2; i++) {
                        tempPort[i] = Integer.parseInt(args[i + 2]);
                        Router.otherPort = tempPort;
                    }
                }
            } catch (NumberFormatException e) {//捕获字符串转整型异常
                System.out.println("参数错误");
                return false;
            }
            return true;
        }
    }

    //发送路由表,实现(Each router sends out their routing table every 30 seconds.)
    public static void sendOut() {

    }

    //更新路由表,实现(Each router updates its own routing table according to the received routing table. ...)
    public static void update() {

    }

    //扫描附近节点,实现(Routers must have the ability to detect whether a neighbor is active. ...)
    public static void detect() {

    }

    //判断并执行命令
    public static boolean isCommand(String str) {
        String[] commands = str.split("\\s+");
        switch (commands.length) {
            case 0: {//空命令
                return true;
            }
            case 1: {//单命令,不代参数
                String command = commands[0];
                if (command.equals("N")) {
                    return true;
                } else if (command.equals("RT")) {
                    return true;
                } else if (command.equals(("S"))) {
                    return true;
                } else {
                    return false;
                }
            }
            case 2: {//一个命令并带一个参数
                String command = commands[0];
                int arg;
                try {
                    arg = Integer.parseInt(commands[1]);
                } catch (NumberFormatException e) {//捕获字符串转整型异常
                    return false;
                }

                if (command.equals("D")) {
                    return true;
                } else if (command.equals("R")) {
                    return true;
                } else {
                    return false;
                }
            }
            default: {//一个命令并带多个参数
                String command = commands[0];
                if (command.equals("PK")) {
                    int args[] = new int[(commands.length - 1)];
                    try {
                        for (int i = 0; i < commands.length - 1; i++) {
                            args[i] = Integer.parseInt(commands[i + 1]);
                        }
                    } catch (NumberFormatException e) {//捕获字符串转整型异常
                        return false;
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }

    }

    //启动路由进程并等待和处理输入命令
    public void run() {
        System.out.println("启动完成,RouterID为:" + Router.routerId + "," +
                "RouterUDPServerPort为:" + Router.myPort);
        while (true) {
            System.out.print("Router" + Router.routerId + ":" + Router.myPort + ">>");//输出提示信息,等待命令输入
            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            String str;
            try {
                str = buf.readLine();//获取输入命令
            } catch (IOException e) {
                System.out.println("命令错误,请重试!!!");
                continue;
            }
            if (str.length() != 0) {
                if (!(Router.isCommand(str)))//命令判断和执行
                {
                    System.out.println("命令或参数错误,请重试!!!");
                }
            }

        }

    }

}
