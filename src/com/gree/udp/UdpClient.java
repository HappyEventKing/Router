package com.gree.udp;

import com.gree.bean.Routing;
import com.gree.router.Router;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;

/**
 * @description:Udp客户端
 * @author:
 * @time: 2021/2/5
 */
public class UdpClient {

    private int udpServerPort;
    private final String udpServerIp = "127.0.0.1";
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private JSONObject sendData;

    public UdpClient(int port) {
        this.udpServerPort = port;
    }

    public void sendRoutingTable() {
        if (Router.routingTable != null) {
            JSONObject sendData = new JSONObject();
            sendData.put("DataType", "RoutingTableData");//数据类别
            sendData.put("SourceRouterId", Router.routerId);//源RouterId
            JSONArray routingTable = new JSONArray();
            for (int i = 0; i < Router.routingTable.size(); i++) {
                JSONObject routingJson = new JSONObject();
                Routing routing = Router.routingTable.get(i);
                routingJson.put("Destination", routing.getDestination());//目的节点
                routingJson.put("AutoUpdateFlag", routing.isAutoUpdateFlag());//是否自动更新标志位
                int[] route = routing.getRoute();
                JSONArray routeJson = new JSONArray();
                for (int j = 0; j < route.length; j++) {
                    routeJson.put(route[i]);
                }
                routingJson.put("Route", routeJson);//路径
                routingTable.put(routingJson);
            }
            sendData.put("routingTable", routingTable);//路由表
            String sendStr = sendData.toString();
            byte buffer[] = new byte[sendStr.length()];
            try {
                buffer = sendStr.getBytes();
                datagramSocket = new DatagramSocket();
                datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(udpServerIp), udpServerPort);
                datagramSocket.send(datagramPacket);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
