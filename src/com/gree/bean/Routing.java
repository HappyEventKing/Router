package com.gree.bean;

import java.util.Arrays;

/**
 * @description:路由表bean
 * @author:
 * @time: 2021/2/4
 */
public class Routing {
    int destination;
    int[] route;

    public int getDestination() {
        return destination;
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
        return "RoutingTable{" +
                "destination=" + destination +
                ", route=" + Arrays.toString(route) +
                '}';
    }
}
