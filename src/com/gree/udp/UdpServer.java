package com.gree.udp;

import com.gree.bean.Neighbor;
import com.gree.bean.Routing;
import com.gree.router.Router;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * @description:Udp服务端
 * @author:
 * @time: 2021/2/5
 */
public class UdpServer extends Thread {

    private int udpServerPort;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    public UdpServer(int port) {
        this.udpServerPort = port;
    }

    public void run() {
        byte[] buffer = new byte[4096];
        try {
            datagramSocket = new DatagramSocket(udpServerPort);
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            while (true) {
                this.recevie();//未接收到数据时阻塞在此
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void recevie() {
        while (true) {
            try {

                this.datagramSocket.receive(this.datagramPacket);
                String result = new String(this.datagramPacket.getData(), this.datagramPacket.getOffset(), this.datagramPacket.getLength());
                JSONObject resultJson = new JSONObject();
                resultJson = JSONObject.fromString(result);
                switch (resultJson.getString("DataType")) {
                    case "RoutingTableData": {
                        dealRoutingTableData(resultJson);
                        break;
                    }
                    default:
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void dealRoutingTableData(JSONObject jsonObject) {
        ArrayList<Routing> routingTable = new ArrayList<Routing>();
        int sourceRouterId = jsonObject.getInt("SourceRouterId");//源RouterId
        JSONArray routingTableJson = new JSONArray();
        routingTableJson = jsonObject.getJSONArray("routingTable");//路由表
        if (routingTableJson.length() != 0) {
            Routing routing = new Routing();
            for (int i = 0; i < routingTableJson.length(); i++) {
                JSONObject routingJson = new JSONObject();
                routingJson = routingTableJson.getJSONObject(i);
                routing.setDestination(routingJson.getInt("Destination"));//目的节点
                routing.setAutoUpdateFlag(routingJson.getBoolean("AutoUpdateFlag"));//是否自动更新标志位
                JSONArray routeJson = new JSONArray();
                routeJson = routingJson.getJSONArray("Route");//路径
                int route[] = new int[routeJson.length()];
                for (int j = 0; j < routeJson.length(); j++) {
                    route[i] = routeJson.getInt(i);
                    routeJson.put(route[i]);
                }
                routing.setRoute(route);
            }
            routingTable.add(routing);
        }
        Router.updateRoutingTableFromOther(sourceRouterId, routingTable);//更新路由表
        int i = 0;
        for (i = 0; i < Router.neighbors.size(); i++) {
            if (Router.neighbors.get(i).getNeighborId() == sourceRouterId) {
                Router.neighbors.get(i).neighborLostTimeReset();//清除丢失时间
                break;
            }
        }
        if (i == Router.neighbors.size())//邻居中还未包含此Router
        {
            Neighbor neighbor = new Neighbor(sourceRouterId);
            Router.neighbors.add(neighbor);//添加此Router;
            Router.updateRoutingTableFromNeighbor();//更新路由表
        }
    }

}
