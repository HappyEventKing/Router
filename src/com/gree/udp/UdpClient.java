package com.gree.udp;

import com.gree.bean.Routing;
import net.sf.json.JSONObject;

import java.net.*;
import java.util.ArrayList;

/**
 * @description:Udp客户端
 * @author:
 * @time: 2021/2/5
 */
public class UdpClient {

    private int udpServerPort;
    private String udpServerIp;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    public UdpClient(){

    }

    public void send(ArrayList<Routing> routingTable) {
        byte buffer[] = new byte[Integer.MAX_VALUE];
        JSONObject jsonObject = new JSONObject();
        buffer = str.getBytes();
        try {
            datagramSocket = new DatagramSocket();
            datagramPacket = new DatagramPacket(buffer, 0, InetAddress.getByName(udpServerIp), udpServerPort);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
