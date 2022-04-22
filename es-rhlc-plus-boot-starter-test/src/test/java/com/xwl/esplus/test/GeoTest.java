package com.xwl.esplus.test;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.document.WorkOrderDocument;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import com.xwl.esplus.test.mapper.WorkOrderDocumentMapper;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.geometry.Point;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 坐标测试
 *
 * @author xwl
 * @since 2022/4/8 14:49
 */
@SpringBootTest
public class GeoTest {
    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Resource
    private WorkOrderDocumentMapper workOrderDocumentMapper;

    @Test
    public void testGeoBoundingBox() {
        // 由左上角和右下角确定一个矩形区域
        // 左上角坐标
        GeoPoint leftTop = new GeoPoint(30.682788, 103.855492);
        // 右下角坐标
        GeoPoint bottomRight = new GeoPoint(30.643077, 104.023769);
        EsLambdaQueryWrapper<WorkOrderDocument> wrapper = Wrappers.<WorkOrderDocument>lambdaQuery()
                // 查询在此区域内的数据
                .geoBoundingBox(WorkOrderDocument::getLocation, leftTop, bottomRight);
        List<WorkOrderDocument> list = workOrderDocumentMapper.selectList(wrapper);
        System.out.println(list);
    }

    @Test
    public void testNotInGeoBoundingBox() {
        // 由左上角和右下角确定一个矩形区域
        // 左上角坐标
        GeoPoint leftTop = new GeoPoint(30.682788, 103.855492);
        // 右下角坐标
        GeoPoint bottomRight = new GeoPoint(30.643077, 104.023769);
        EsLambdaQueryWrapper<WorkOrderDocument> wrapper = Wrappers.<WorkOrderDocument>lambdaQuery()
                // 查询不在此区域内的数据
                .notInGeoBoundingBox(WorkOrderDocument::getLocation, leftTop, bottomRight);
        List<WorkOrderDocument> list = workOrderDocumentMapper.selectList(wrapper);
        System.out.println(list);
    }

    @Test
    public void testGeoDistance() {
        EsLambdaQueryWrapper<WorkOrderDocument> wrapper = Wrappers.<WorkOrderDocument>lambdaQuery();
        // 查询距离在20km以内的数据
        wrapper.geoDistance(WorkOrderDocument::getLocation, 20.0, DistanceUnit.KILOMETERS, new GeoPoint(30.682788, 103.855492));
        // 支持以下格式
//        wrapper.geoDistance(WorkOrderDocument::getLocation, 20.0, DistanceUnit.KILOMETERS, "30.682788, 103.855492");
//        wrapper.geoDistance(WorkOrderDocument::getLocation, "20.0km", "30.682788, 103.855492");

        List<WorkOrderDocument> list = workOrderDocumentMapper.selectList(wrapper);
        System.out.println(list);
    }

    @Test
    public void testGeoPolygon() {
        // 查询以给定点列表构成的不规则图形内的所有点，点数至少为3个
        EsLambdaQueryWrapper<WorkOrderDocument> wrapper = Wrappers.<WorkOrderDocument>lambdaQuery();
        List<GeoPoint> geoPoints = new ArrayList<>();
        GeoPoint geoPoint = new GeoPoint(30.682788, 103.855492);
        GeoPoint geoPoint1 = new GeoPoint(30.643077, 104.023769);
        GeoPoint geoPoint2 = new GeoPoint(30.670211,103.97124);
        geoPoints.add(geoPoint);
        geoPoints.add(geoPoint1);
        geoPoints.add(geoPoint2);
        wrapper.geoPolygon(WorkOrderDocument::getLocation, geoPoints);
        List<WorkOrderDocument> documents = workOrderDocumentMapper.selectList(wrapper);
        System.out.println(documents);
    }

    @Test
    public void testGeoShape() {
        // 查询图形,图形的字段索引类型必须为geo_shape
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        // 圆形，其中x,y为圆心坐标,r为半径.
//        Circle circle = new Circle(10, 12, 100);
        // point
        Point point = new Point(10, 12);
        // shapeRelation支持多种,如果不传则默认为within
        wrapper.geoShape(UserDocument::getGeoLocation, point, ShapeRelation.INTERSECTS);
        List<UserDocument> documents = userDocumentMapper.selectList(wrapper);
        System.out.println(documents);
    }
}
