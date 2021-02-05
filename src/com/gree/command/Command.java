package com.gree.command;

import com.gree.router.Router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @description:命令判断与执行
 * @author:
 * @time: 2021/2/5
 */
public class Command extends Thread {
    /**
     * @Description: 判断启动命令
     * @Param:
     * @return: boolean
     * @Author:
     * @Date: 2021/2/5
     */
    public static boolean isStartArguments(String[] args) {
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

    /**
     * @Description: N命令
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void commandN() {
        if (Router.neighbors != null) {
            for (int i = 0; i < Router.neighbors.size(); i++) {
                System.out.print(Router.neighbors.get(i).getNeighborId() + "\t");
            }
            System.out.println();
        }
    }

    /**
     * @Description: RT命令
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void commandRT() {

        System.out.println("Destination" + "\t" + "route");
        if (Router.routingTable != null) {
            for (int i = 0; i < Router.routingTable.size(); i++) {
                System.out.print(Router.routingTable.get(i).getDestination() + "\t");
                for (int j = 0; j < Router.routingTable.get(i).getRoute().length; j++) {
                    System.out.println(Router.routingTable.get(i).getRoute()[j] + "\t");
                }
            }
        }
    }

    /**
     * @Description: D命令
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void commandD(int n) {
        //TODO:待补充实现方法
    }

    /**
     * @Description: PK命令
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void commandPK(int[] n) {
        //TODO:待补充实现方法
    }

    /**
     * @Description: R命令
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void commandR(int n) {
        //TODO:待补充实现方法
    }

    /**
     * @Description: S命令
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void commandS() {
        //TODO:待补充实现方法
    }

    /**
     * @Description: 判断并执行命令
     * @Param:
     * @return: boolean
     * @Author:
     * @Date: 2021/2/5
     */
    public static boolean isCommand(String str) {
        String[] commands = str.split("\\s+");
        switch (commands.length) {
            case 0: {//空命令
                return true;
            }
            case 1: {//单命令,不代参数
                String command = commands[0];
                if (command.equals("N")) {
                    Command.commandN();
                    return true;
                } else if (command.equals("RT")) {
                    Command.commandRT();
                    return true;
                } else if (command.equals(("S"))) {
                    Command.commandS();
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
                    Command.commandD(arg);
                    return true;
                } else if (command.equals("R")) {
                    Command.commandR(arg);
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
                    Command.commandPK(args);
                    return true;
                } else {
                    return false;
                }
            }
        }

    }

    /**
     * @Description:
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public void run() {
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
                if (!(Command.isCommand(str)))//命令判断和执行
                {
                    System.out.println("命令或参数错误,请重试!!!");
                }
            }

        }
    }
}
