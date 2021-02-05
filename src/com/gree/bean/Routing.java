package com.gree.bean;

import java.util.Arrays;

/**
 * @description:路由表bean
 * @author:
 * @time: 2021/2/4
 */
public class Routing {
    int destination;//目的地
    int[] route;//路径
    boolean autoUpdateFlag = true;//此路由信息是否允许自动更新标志位

    public int getDestination() {
        return destination;
    }

    public boolean isAutoUpdateFlag() {
        return autoUpdateFlag;
    }

    public void setAutoUpdateFlag(boolean autoUpdateFlag) {
        this.autoUpdateFlag = autoUpdateFlag;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int[] getRoute() {
        return route;
    }

    public void setRoute(int[] route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Routing{" +
                "destination=" + destination +
                ", route=" + Arrays.toString(route) +
                ", autoUpdateFlag=" + autoUpdateFlag +
                '}';
    }
}
