package org.sparta.delivery.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.sparta.delivery.global.domain.service.AddressToCoords;
import org.springframework.util.StringUtils;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreLocation {

    private String address;

    // PostGIS의 Geometry 타입으로 변경
    // SRID 4326은 WGS84(위도, 경도) 좌표계를 의미합니다.
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point point;

    private double latitude; // 위도
    private double longitude; // 경도

    protected StoreLocation(String address, AddressToCoords addressToCoords) {
        this.address = address;
        if (!StringUtils.hasText(address)) return;

        double[] coords = addressToCoords.convert(address);
        latitude = coords[0];
        longitude = coords[1];

        // JTS GeometryFactory를 이용해 Point 객체 생성
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        point = factory.createPoint(new Coordinate(longitude, latitude)); // PostGIS(Point)는  (Longitude, Latitude) 순서로 저장
    }
}
