package com.xwl.esplus.core.param;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.geometry.Geometry;

import java.util.List;

/**
 * @author xwl
 * @since 2022/3/16 11:08
 */
public class EsGeoParam {
    /**
     * 字段名
     */
    private String field;
    /**
     * geoBoundingBox 左上点坐标
     */
    private GeoPoint topLeft;
    /**
     * geoBoundingBox 右下点坐标
     */
    private GeoPoint bottomRight;
    /**
     * 中心点坐标
     */
    private GeoPoint centralGeoPoint;
    /**
     * 距离 双精度类型
     */
    private Double distance;
    /**
     * 距离 单位
     */
    private DistanceUnit distanceUnit;
    /**
     * 距离 字符串类型
     */
    private String distanceStr;
    /**
     * 不规则坐标点列表
     */
    private List<GeoPoint> geoPoints;
    /**
     * 已被索引形状的索引id
     */
    private String indexedShapeId;
    /**
     * 图形
     */
    private Geometry geometry;
    /**
     * 图形关系
     */
    private ShapeRelation shapeRelation;
    /**
     * 权重值
     */
    private Float boost;
    /**
     * 是否在范围内
     */
    private boolean isIn;

    public EsGeoParam() {
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public GeoPoint getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(GeoPoint topLeft) {
        this.topLeft = topLeft;
    }

    public GeoPoint getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(GeoPoint bottomRight) {
        this.bottomRight = bottomRight;
    }

    public GeoPoint getCentralGeoPoint() {
        return centralGeoPoint;
    }

    public void setCentralGeoPoint(GeoPoint centralGeoPoint) {
        this.centralGeoPoint = centralGeoPoint;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public DistanceUnit getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(DistanceUnit distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getDistanceStr() {
        return distanceStr;
    }

    public void setDistanceStr(String distanceStr) {
        this.distanceStr = distanceStr;
    }

    public List<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(List<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
    }

    public String getIndexedShapeId() {
        return indexedShapeId;
    }

    public void setIndexedShapeId(String indexedShapeId) {
        this.indexedShapeId = indexedShapeId;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public ShapeRelation getShapeRelation() {
        return shapeRelation;
    }

    public void setShapeRelation(ShapeRelation shapeRelation) {
        this.shapeRelation = shapeRelation;
    }

    public Float getBoost() {
        return boost;
    }

    public void setBoost(Float boost) {
        this.boost = boost;
    }

    public boolean isIn() {
        return isIn;
    }

    public void setIn(boolean in) {
        isIn = in;
    }
}
