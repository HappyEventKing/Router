package com.gree.udp;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

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
        byte[] buffer = new byte[Integer.MAX_VALUE];
        try {
            datagramSocket = new DatagramSocket(udpServerPort);
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            this.recevie();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void recevie() {
        while (true) {
            try {
                JSONObject jsonObject = new JSONObject();
                this.datagramSocket.receive(this.datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
