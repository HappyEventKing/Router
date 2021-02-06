package com.gree.router;

import com.gree.bean.Neighbor;
import com.gree.command.Command;
import com.gree.bean.Routing;
import com.gree.udp.UdpClient;
import com.gree.udp.UdpServer;

import java.util.ArrayList;

/**
 * @description:Router实现
 * @author:
 * @time: 2021/2/4
 */
public class Router extends Thread {
    public static int routerId;//RouterID
    public static int myPort;//UDPServer端口
    public static int otherPort[];//连接其他Router的端口
    public static ArrayList<Routing> routingTable;//路由表
    public static ArrayList<Neighbor> neighbors;//领居Router
    public static UdpServer udpServer;//udpServer对象(一个Router只有一个udp服务端角色)
    public static UdpClient udpClient[];//udp客户端对象(一个Router可扮演多个udp客户端角色)
    public static int updateTimes = 0;//路由表更新次数
    public static ArrayList<Integer> refusedNode;

    /**
     * @Description: 发送路由表, 实现(Each router sends out their routing table every 30 seconds.)
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/5
     */
    public static void sendRoutingTable() {
        while (true) {
            if (Router.udpClient != null) {
                for (int i = 0; i < Router.udpClient.length; i++) {
                    Router.udpClient[i].sendRoutingTable();//给邻近Router发送路由表
                }
                try {
                    Thread.sleep(30 * 1000);//延时30s后再次发送
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Description: 更新路由表, 实现(Each router updates its own routing table according to the received routing table. ...)
     * @Param: [sourceRouterId, sourceRoutingTable]
     * @return: void
     * @Author: ManolinCoder
     * @Date: 2021/2/5
     */
    public static void updateRoutingTableFromOther(int sourceRouterId, ArrayList<Routing> sourceRoutingTable) {
        for (int i = 0; i < sourceRoutingTable.size(); i++) {
            Routing sourceRouting = sourceRoutingTable.get(i);
            int j = 0;
            for (j = 0; j < Router.routingTable.size(); j++) {
                Routing myRouting = Router.routingTable.get(j);
                if (sourceRouting.getDestination() == myRouting.getDestination()) {
                    if (((sourceRouting.getRoute().length + 1) < myRouting.getRoute().length)||(sourceRouting.isAutoUpdateFlag()==false))//如果发送来的路由比本路由路径短,则替换
                    {
                        int route[] = new int[(sourceRouting.getRoute().length + 1)];
                        route[0] = sourceRouterId;
                        for (int k = 1; k < (sourceRouting.getRoute().length + 1); k++) {
                            route[k] = sourceRouting.getRoute()[k - 1];
                        }
                        if((!Command.isRefusedPassNode(route))&&(!Command.isSpecifiedPriorityRoute(Router.routingTable.get(j))))//非拒绝节点和非特殊路径则更新
                        {
                                Router.routingTable.get(j).setRoute(route);//更新路径信息
                                Router.updateTimes++;//更新次数加1

                        }

                    }
                    break;
                }
            }
            if ((j == Router.routingTable.size()) && (sourceRouting.getDestination() != Router.routerId))//本路由无此路由信息,则添加此路由表项
            {
                Routing routing = new Routing();
                routing.setAutoUpdateFlag(sourceRouting.isAutoUpdateFlag());
                routing.setDestination(sourceRouting.getDestination());
                int route[] = new int[(sourceRouting.getRoute().length + 1)];
                route[0] = sourceRouterId;
                for (int k = 1; k < (sourceRouting.getRoute().length + 1); k++) {
                    route[k] = sourceRouting.getRoute()[k - 1];
                }
                routing.setRoute(route);
                if((!Command.isRefusedPassNode(route))&&(!Command.isSpecifiedPriorityRoute(routing)))//非拒绝节点和非特殊路径则更新
                {
                    Router.routingTable.add(routing);
                    Router.updateTimes++;//更新次数加1
                }
            }
        }

    }

    /**
     * @Description: 给Router添加领居
     * @Param: nextRouterId:领居ID
     * @return: void
     * @Author:
     * @Date: 2021/2/6
     */
    public static void addNeighborToRoutingTable(int nextRouterId) {
        Routing routing = new Routing();
        routing.setAutoUpdateFlag(true);
        routing.setDestination(nextRouterId);
        int route[] = new int[1];
        route[0] = nextRouterId;
        routing.setRoute(route);
        if((!Command.isRefusedPassNode(route))&&(!Command.isSpecifiedPriorityRoute(routing))) //非拒绝节点和非特殊路径则更新
        {
            Router.routingTable.add(routing);
            Router.updateTimes++;//更新次数加1
        }
    }

    /**
     * @Description: 根据发现的领居更新路由表
     * @Param:
     * @return: void
     * @Author:
     * @Date: 2021/2/6
     */
    public static void updateRoutingTableFromNeighbor() {

        //添加新增的领居
        for (int i = 0; i < Router.neighbors.size(); i++) {
            int nextRouterId = Router.neighbors.get(i).getNeighborId();
            int j = 0;
            for (j = 0; j < Router.routingTable.size(); j++) {
                int tableNextRouterId = Router.routingTable.get(j).getRoute()[0];
                if (nextRouterId == tableNextRouterId) {
                    break;
                }
            }
            if (j == Router.routingTable.size())//路由表中无此领居节点,则添加
            {
                Router.addNeighborToRoutingTable(nextRouterId);
            } else {
                if (Router.routingTable.get(j).getDestination() == nextRouterId) {//路由表中存在此领居节点且目的地为此领居,则覆盖
                    int route[] = new int[1];
                    route[0] = nextRouterId;
                    if((!Command.isRefusedPassNode(route))&&(!Command.isSpecifiedPriorityRoute(Router.routingTable.get(j))))//非拒绝节点和非特殊路径则更新
                    {
                        Router.routingTable.get(j).setRoute(route);
                        Router.updateTimes++;//更新次数加1
                    }
                }
                //判断此领居是否在路由表中
                int k = 0;
                for (k = 0; k < Router.routingTable.size(); k++) {
                    if (nextRouterId == Router.routingTable.get(k).getDestination()) {
                        break;
                    }
                }
                if (k == Router.routingTable.size()) {
                    Router.addNeighborToRoutingTable(nextRouterId);
                }
            }
        }
        //移除丢失的邻居
        for (int i = 0; i < Router.routingTable.size(); i++) {
            int tableNextRouterId = Router.routingTable.get(i).getRoute()[0];
            int j = 0;
            for (j = 0; j < Router.neighbors.size(); j++) {
                int nextRouterId = Router.neighbors.get(j).getNeighborId();
                if (nextRouterId == tableNextRouterId) {
                    break;
                }
            }
            if (j == Router.neighbors.size())//路由表中还存在已丢失的节点,则移除
            {
                Router.routingTable.remove(i);
                i--;
                Router.updateTimes++;//更新次数加1
            }
        }
    }

    /**
     * @Description: 扫描附近节点, 实现(Routers must have the ability to detect whether a neighbor is active. ...)
     * @Param: []
     * @return: void
     * @Author: ManolinCoder
     * @Date: 2021/2/5
     */
    public static void neighborDetect() {
        int neighborIndex = 0;
        while (true) {
            if (Router.neighbors != null) {
                neighborIndex++;
                if (neighborIndex >= Router.neighbors.size()) {
                    neighborIndex = 0;
                }
                if (Router.neighbors.size() != 0) {
                    Router.neighbors.get(neighborIndex).neighborLostTimeAdd();
                    if ((30 * 6) == Router.neighbors.get(neighborIndex).getNeighborLostTime()) {
                        Router.neighbors.remove(neighborIndex);
                        neighborIndex--;//移除超时节点
                        Router.updateRoutingTableFromNeighbor();//更新路由表
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
        //启动Udp服务端
        udpServer = new UdpServer(Router.myPort);
        udpServer.start();//启动Udp服务端
        //初始化Udp客户端,后续根据初始参数连接Udp服务
        if (Router.otherPort != null) {
            udpClient = new UdpClient[Router.otherPort.length];
            for (int i = 0; i < Router.otherPort.length; i++) {
                udpClient[i] = new UdpClient(Router.otherPort[i]);
            }
        }
        //初始化路由表
        Router.routingTable = new ArrayList<Routing>();
        //初始化领居表
        Router.neighbors = new ArrayList<Neighbor>();
        new Thread() {
            public void run() {
                Router.sendRoutingTable();
            }
        }.start();//启动路由发送线程
        new Thread() {
            public void run() {
                Router.neighborDetect();
            }
        }.start();//启动路由扫描线程

        System.out.println("启动完成,RouterID为:" + Router.routerId + "," +
                "RouterUDPServerPort为:" + Router.myPort);
        new Command().start();//执行命令交互线程

    }

}
