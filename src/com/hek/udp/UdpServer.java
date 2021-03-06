package com.hek.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @description:Udp服务端
 * @author: Eventi
 * @time: 2021/2/5
 */
public class UdpServer extends Thread {

    private int udpServerPort;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    /**
     * @Description: 构造函数
     * @Param: prot:服务端端口
     * @return:
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public UdpServer(int port) {
        this.udpServerPort = port;
    }

    /**
     * @Description: 服务端接收进程
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public void run() {
        UdpData udpData = new UdpData(this.udpServerPort, "Server");
        while (true) {
            udpData.receive();//未接收到数据时阻塞在此
        }
    }

}
