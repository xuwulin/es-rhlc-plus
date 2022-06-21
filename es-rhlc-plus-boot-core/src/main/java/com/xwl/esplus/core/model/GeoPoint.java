package com.xwl.esplus.core.model;

/**
 * @Description:
 * @Author: hl
 * @Date: 2022/6/21 16:03
 */
public class GeoPoint {
    private double lat;
    private double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public GeoPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
