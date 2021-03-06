package com.hek.command;

import com.hek.bean.Routing;
import com.hek.router.Router;
import com.hek.udp.UdpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @description:命令判断与执行
 * @author: Eventi
 * @time: 2021/2/5
 */
public class Command extends Thread {
    /**
     * @Description: 判断启动命令
     * @Param:
     * @return: boolean
     * @Author: Eventi
     * @Date: 2021/2/5
     */
    public static boolean isStartArguments(String[] arguments) {
        String[] args = arguments[0].split(",");
        if (args.length < 2) {//判断启动参数是否正确,启动参数必须包括ID和myport;
            System.out.println("参数错误,至少需要输入router id与port 两个数值");
            return false;
        } else {
            try {
                Router.routerId = Integer.parseInt(args[0]);//获取id
                Router.myPort = Integer.parseInt(args[1]);//获取端口
                if (args.length >= 3) {//获取其他端口
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
     * @Author: Eventi
     * @Date: 2021/2/5
     */
    public static void commandN() {
        if (Router.neighbors != null) {
            for (int i = 0; i < Router.neighbors.size(); i++) {
                System.out.print(Router.neighbors.get(i).getNeighborId() + "\t");//打印领居节点
            }
            System.out.println();
        }
    }

    /**
     * @Description: RT命令
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/5
     */
    public static void commandRT() {

        System.out.println("Destination" + "\t" + "route");//打印表头
        if (Router.routingTable != null) {
            for (int i = 0; i < Router.routingTable.size(); i++) {
                System.out.print(Router.routingTable.get(i).getDestination() + "\t");//打印目的地
                for (int j = 0; j < Router.routingTable.get(i).getRoute().length; j++) {
                    if(j==(Router.routingTable.get(i).getRoute().length-1)) {
                        System.out.println(Router.routingTable.get(i).getRoute()[j]);//打印路径(最后一个)
                    }
                    else
                    {
                        System.out.print(Router.routingTable.get(i).getRoute()[j] + ",");//打印路径
                    }
                }
            }
        }
    }

    /**
     * @Description: D命令
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/5
     */
    public static void commandD(int n) {
        int i = 0;
        int TTL = 15;
        for (i = 0; i < Router.neighbors.size(); i++) {
            if (n == Router.neighbors.get(i).getNeighborId()) {
                UdpClient udpClient = new UdpClient(Router.neighbors.get(i).getNeighborPort());//发送给领居
                udpClient.sendTTL(n, TTL);
                System.out.println("Direct to " + n);
                break;
            }
        }
        if (i == Router.neighbors.size())//n不为领居节点
        {
            int j = 0;
            for (j = 0; j < Router.routingTable.size(); j++) {
                if (Router.routingTable.get(j).getDestination() == n) {
                    int nextRouterID = Router.routingTable.get(j).getRoute()[0];//获取下一个节点ID
                    for (int k = 0; k < Router.neighbors.size(); k++) {
                        if (nextRouterID == Router.neighbors.get(k).getNeighborId())//查询下一个节点的端口
                        {
                            UdpClient udpClient = new UdpClient(Router.neighbors.get(k).getNeighborPort());//先发送给相邻的下一个节点
                            udpClient.sendTTL(n, TTL);
                        }
                    }
                    break;
                }
            }
            if (j == Router.routingTable.size()) {//路由表中无此目的节点
                System.out.println("No route to " + n);
            }
        }

    }

    /**
     * @Description: PK命令
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/5
     */
    public static void commandPK(int[] n) {
        Routing addrouting = new Routing();
        addrouting.setDestination(n[n.length - 1]);
        addrouting.setAutoUpdateFlag(false);
        addrouting.setRoute(n);
        int j;
        for (j = 0; j < Router.routingTable.size(); j++) {
            Routing myRouting = Router.routingTable.get(j);
            if (addrouting.getDestination() == myRouting.getDestination()) {
                if (!Command.isRefusedPassNode(addrouting.getRoute()))//非拒绝节点则更新
                {
                    Router.routingTable.get(j).setRoute(addrouting.getRoute());//更新路径信息
                    Router.routingTable.get(j).setAutoUpdateFlag(addrouting.isAutoUpdateFlag());
                    Router.updateTimes++;//更新次数加1
                }
                break;
            }
        }
        if ((j == Router.routingTable.size()) && (addrouting.getDestination() != Router.routerId))//本路由无此路由信息,则添加此路由表项
        {
            if ((!Command.isRefusedPassNode(addrouting.getRoute())))//非拒绝节点和非特殊路径则更新
            {
                Router.routingTable.add(addrouting);
                Router.updateTimes++;//更新次数加1
            }
        }
    }

    /**
     * @Description: R命令
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/5
     */
    public static void commandR(int n) {
        if (Router.refusedNode == null) {
            Router.refusedNode = new ArrayList<Integer>();
        }
        Router.refusedNode.add(n);//添加拒绝更新节点
    }

    /**
     * @Description: S命令
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/5
     */
    public static void commandS() {
        System.out.println("本路由表更新次数:" + Router.updateTimes);//打印路由表更新次数
    }

    /**
     * @Description: 判断并执行命令
     * @Param: str:命令
     * @return: boolean
     * @Author: Eventi
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
     * @Description: 命令交互进程
     * @Param:
     * @return: void
     * @Author: Eventi
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

    /**
    * @Description: 判断此更新是否在拒绝更新列表中
    * @Param: route:需要判断的路由数据
    * @return: boolean
    * @Author: Eventi
    * @Date: 2021/2/7
    */
    public static boolean isRefusedPassNode(int[] route) {
        if (Router.refusedNode != null) {
            for (int i = 0; i < route.length; i++) {
                for (int j = 0; j < Router.refusedNode.size(); j++) {
                    if (route[i] == Router.refusedNode.get(j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
    * @Description: 判断是否为特殊路由
    * @Param: routing:需要判断的路由表项
    * @return: boolean
    * @Author: Eventi
    * @Date: 2021/2/7
    */
    public static boolean isSpecifiedPriorityRoute(Routing routing) {
        return (!routing.isAutoUpdateFlag());
    }
}
