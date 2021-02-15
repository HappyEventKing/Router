package com.gree.bean;


import java.net.InetAddress;

/**
 * @description:邻居节点及丢失时间
 * @author: Eventi
 * @time: 2021/2/5
 */
public class Neighbor {
    private int neighborId;//领居ID
    private int neighborLostTime = 0;//领居丢失时间
    private int neighborPort;//领居udp端口

    public Neighbor(int id) {
        this.neighborId = id;
        this.neighborLostTime = 0;
    }

    public int getNeighborPort() {
        return neighborPort;
    }

    public void setNeighborPort(int neighborPort) {
        this.neighborPort = neighborPort;
    }

    public void neighborLostTimeAdd() {
        this.neighborLostTime++;
    }

    public void neighborLostTimeReset() {
        this.neighborLostTime = 0;
    }

    public int getNeighborId() {
        return neighborId;
    }

    public void setNeighborId(int neighborId) {
        this.neighborId = neighborId;
    }

    public int getNeighborLostTime() {
        return neighborLostTime;
    }

    public void setNeighborLostTime(int neighborLostTime) {
        this.neighborLostTime = neighborLostTime;
    }

    @Override
    public String toString() {
        return "Neighbor{" +
                "neighborId=" + neighborId +
                ", neighborLostTime=" + neighborLostTime +
                ", neighborPort=" + neighborPort +
                '}';
    }
}
