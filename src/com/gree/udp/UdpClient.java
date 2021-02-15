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
 * @author: Eventi
 * @time: 2021/2/5
 */
public class UdpClient {

    private int udpServerPort;
    private UdpData udpData;

    /**
     * @Description: 构造函数
     * @Param: port:端口
     * @return:
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public UdpClient(int port) {
        this.udpServerPort = port;
        this.udpData = new UdpData(this.udpServerPort, "Client");
    }

    /**
     * @Description: 发送路由表数据(发送完后转接收)
     * @Param:
     * @return: void
     * @Author: Eventi
     * @Date: 2021/2/6
     */
    public void sendRoutingTable() {
        this.udpData.sendRoutingTable();
        this.udpData.receive();
    }

    /**
    * @Description: 发送TTL
    * @Param: destination:目的地,TTL:ttl数据
    * @return: void
    * @Author: Eventi
    * @Date: 2021/2/7
    */
    public void sendTTL(int destination, int TTL) {
        this.udpData.sendTTL(destination, TTL);
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
        this.udpData.close();
    }

}
