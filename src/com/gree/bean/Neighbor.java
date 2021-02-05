package com.gree.bean;

/**
 * @description:邻居节点及丢失时间
 * @author:
 * @time: 2021/2/5
 */
public class Neighbor {
    private int neighborId;
    private int neighborLostTime = 0;

    public Neighbor(int id)
    {
        this.neighborId = id;
        this.neighborLostTime = 0;
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
                '}';
    }
}
