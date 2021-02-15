package com.gree.udp;

import com.gree.bean.Neighbor;
import com.gree.bean.Routing;
import com.gree.router.Router;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * @description:
 * @author: Eventi
 * @time: 2021/2/6
 */
public class UdpData {

    private DatagramPacket datagramPacket;
    private DatagramSocket datagramSocket;
    private final String udpServerIp = "127.0.0.1";//绑定或发送udp的IP
    private int udpServerPort;//绑定或发送udp的端口
    private String type;//使用的角色(Client或Server)

    /**
     * @Description: 构造函数
     * @Param: udpSeverPort:端口,type:使用角色
     * @return:
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public UdpData(int udpServerPort, String type) {
        byte buffer[] = new byte[4096];
        this.udpServerPort = udpServerPort;
        this.type = type;
        try {
            if (this.type == "Server") {
                this.datagramSocket = new DatagramSocket(this.udpServerPort);
                this.datagramPacket = new DatagramPacket(buffer, buffer.length);
            } else if (this.type == "Client") {
                this.datagramSocket = new DatagramSocket();
                this.datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.udpServerIp), this.udpServerPort);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     * @Description: 发送路由表
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public void sendRoutingTable() {
        if (Router.routingTable != null) {
            byte buffer[] = new byte[4096];
            buffer = this.dataPack("RoutingTableData");
            try {
                this.datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.udpServerIp), this.udpServerPort);
                this.datagramSocket.send(this.datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Description: 打包发送路由表数据
     * @Param: dataType:数据类别
     * @return: byte[] 返回打包后的字节数据
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public byte[] dataPack(String dataType) {
        if (Router.routingTable != null) {
            JSONObject sendData = new JSONObject();
            sendData.put("DataType", dataType);//数据类别
            sendData.put("SourceRouterId", Router.routerId);//源RouterId
            sendData.put("SourceRouterPort", Router.myPort);//源Router服务IP端口
            JSONArray routingTable = new JSONArray();
            for (int i = 0; i < Router.routingTable.size(); i++) {
                JSONObject routingJson = new JSONObject();
                Routing routing = Router.routingTable.get(i);
                routingJson.put("Destination", routing.getDestination());//目的节点
                routingJson.put("AutoUpdateFlag", routing.isAutoUpdateFlag());//是否自动更新标志位
                int[] route = routing.getRoute();
                JSONArray routeJson = new JSONArray();
                for (int j = 0; j < route.length; j++) {
                    routeJson.put(route[j]);
                }
                routingJson.put("Route", routeJson);//路径
                routingTable.put(routingJson);
            }
            sendData.put("routingTable", routingTable);//路由表
            String sendStr = sendData.toString();
            byte sendBuffer[] = new byte[sendStr.length()];
            sendBuffer = sendStr.getBytes();
            return sendBuffer;
        }
        return null;
    }

    /**
     * @Description: 处理接收到的路由表数据
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public void dealRoutingTableData(JSONObject jsonObject) {
        ArrayList<Routing> routingTable = new ArrayList<Routing>();
        int sourceRouterId = jsonObject.getInt("SourceRouterId");//源RouterId
        int sourceRouterPort = jsonObject.getInt("SourceRouterPort");//源Router服务IP端口
        JSONArray routingTableJson = new JSONArray();
        routingTableJson = jsonObject.getJSONArray("routingTable");//路由表
        if (routingTableJson.length() != 0) {
            for (int i = 0; i < routingTableJson.length(); i++) {
                Routing routing = new Routing();
                JSONObject routingJson = new JSONObject();
                routingJson = routingTableJson.getJSONObject(i);
                routing.setDestination(routingJson.getInt("Destination"));//目的节点
                routing.setAutoUpdateFlag(routingJson.getBoolean("AutoUpdateFlag"));//是否自动更新标志位
                JSONArray routeJson = new JSONArray();
                routeJson = routingJson.getJSONArray("Route");//路径
                int route[] = new int[routeJson.length()];
                for (int j = 0; j < routeJson.length(); j++) {
                    route[j] = routeJson.getInt(j);
                }
                routing.setRoute(route);
                routingTable.add(routing);
            }

        }
        Router.updateRoutingTableFromOther(sourceRouterId, routingTable);//更新路由表
        int i = 0;
        for (i = 0; i < Router.neighbors.size(); i++) {
            if (Router.neighbors.get(i).getNeighborId() == sourceRouterId) {
                Router.neighbors.get(i).neighborLostTimeReset();//清除丢失时间
                Router.neighbors.get(i).setNeighborPort(sourceRouterPort);//更新邻居端口
                break;
            }
        }
        if (i == Router.neighbors.size())//邻居中还未包含此Router
        {
            Neighbor neighbor = new Neighbor(sourceRouterId);
            Router.neighbors.add(neighbor);//添加此Router;
            Router.neighbors.get(i).setNeighborPort(sourceRouterPort);//添加邻居端口
            Router.updateRoutingTableFromNeighbor();//更新路由表
        }
    }

    /**
     * @Description: 回复客户端发来的路由表数据(将自己路由表发送给客户端)
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public void routingTableDataResponse() {
        if (Router.routingTable != null) {
            byte buffer[] = this.dataPack("RoutingTableDataResponse");
            this.datagramPacket.setData(buffer);
            try {
                this.datagramSocket.send(this.datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Description:接收数据(此函数会阻塞)
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public void receive() {
        try {

            if (this.type == "Client") {
                this.datagramSocket.setSoTimeout(3000);
            }
            byte buffer[] = new byte[4096];
            this.datagramPacket = new DatagramPacket(buffer, buffer.length);
            this.datagramSocket.receive(this.datagramPacket);
            String result = new String(this.datagramPacket.getData(), this.datagramPacket.getOffset(), this.datagramPacket.getLength());
            JSONObject resultJson = new JSONObject();
            resultJson = JSONObject.fromString(result);
            switch (resultJson.getString("DataType")) {
                case "RoutingTableData": {
                    this.dealRoutingTableData(resultJson);
                    this.routingTableDataResponse();//回复本Router路由表
                    break;
                }
                case "RoutingTableDataResponse": {
                    this.dealRoutingTableData(resultJson);
                    break;
                }
                case "TTLData": {
                    this.dealTTLData(resultJson);
                }
                default:
            }

        } catch (SocketTimeoutException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * @Description: 发送TTL数据
    * @Param: destination:目的地,TTL:ttl数据
    * @return: void
    * @Author: Eventi
    * @Date: 2021/2/7
    */
    public void sendTTL(int destination, int TTL) {
        JSONObject sendData = new JSONObject();
        sendData.put("DataType", "TTLData");//数据类别
        sendData.put("Destination", destination);
        sendData.put("TTL", TTL);
        String sendStr = sendData.toString();
        byte sendBuffer[] = new byte[sendStr.length()];
        sendBuffer = sendStr.getBytes();
        try {
            this.datagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(this.udpServerIp), this.udpServerPort);
            this.datagramSocket.send(this.datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * @Description: 处理接收到TTL数据
    * @Param: jsonObject 传入接收到的json数据
    * @return: void
    * @Author: Eventi
    * @Date: 2021/2/7
    */
    public void dealTTLData(JSONObject jsonObject) {
        int destination = jsonObject.getInt("Destination");//目的地
        int TTL = jsonObject.getInt("TTL");//TTL
        TTL--;
        if (destination == Router.routerId) {
            System.out.println("Destination " + destination);
        } else {
            if (0 == TTL) {
                System.out.println("time exceeded " + destination);
            } else {
                int j = 0;
                for (j = 0; j < Router.routingTable.size(); j++) {
                    if (Router.routingTable.get(j).getDestination() == destination) {
                        int nextRouterID = Router.routingTable.get(j).getRoute()[0];//获取下一个节点ID
                        for (int k = 0; k < Router.neighbors.size(); k++) {
                            if (nextRouterID == Router.neighbors.get(k).getNeighborId())//查询下一个节点的端口
                            {
                                UdpClient udpClient = new UdpClient(Router.neighbors.get(k).getNeighborPort());//先发送给相邻的下一个节点
                                udpClient.sendTTL(destination, TTL);
                                System.out.println("Forward to " + destination);
                                udpClient.close();
                            }
                        }
                        break;
                    }
                }
                if (j == Router.routingTable.size()) {
                    System.out.println("Dropped " + destination);
                }
            }
        }
    }

    /** 
    * @Description: 关闭Socket
    * @Param: 
    * @return: void
    * @Author: Eventi
    * @Date: 2021/2/7
    */
    public void close()
    {
        this.datagramSocket.close();
    }
}
