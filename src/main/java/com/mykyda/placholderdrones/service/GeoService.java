package com.mykyda.placholderdrones.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeoService {

    @Value("${placeholder-drones.delivery.origin.latitude}")
    private double ORIGIN_LATITUDE;

    @Value("${placeholder-drones.delivery.origin.longitude}")
    private double ORIGIN_LONGITUDE;

    private static final double EARTH_RADIUS_KM = 6371;

    public double calculateDistance(double targetLat, double targetLon) {

        System.out.println(ORIGIN_LATITUDE + " " + ORIGIN_LONGITUDE);

        double latDistance = Math.toRadians(targetLat - ORIGIN_LATITUDE);
        double lonDistance = Math.toRadians(targetLon - ORIGIN_LONGITUDE);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(ORIGIN_LATITUDE))
                * Math.cos(Math.toRadians(targetLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public boolean validateNewYork(double lat, double lon) {
        return lat >= 40.49 && lat <= 45.01 &&
                lon >= -79.76 && lon <= -71.85;
    }
}
