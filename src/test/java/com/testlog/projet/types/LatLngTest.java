package com.testlog.projet.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LatLngTest {

    @Test
    public void testDistance_withSameValues_shouldReturnZero() {
        LatLng a = new LatLng(1., 1.);
        LatLng b = new LatLng(1., 1.);
        assertEquals(0., a.distance(b));
    }

    @Test
    public void testDistance_withNonZeroLat_shouldReturnCorrectValue() {
        LatLng a = new LatLng(1., 1.);
        LatLng b = new LatLng(2., 1.);
        assertEquals(111.195, a.distance(b), 0.001);
    }

    @Test
    public void testDistance_withNonZeroLng_shouldReturnCorrectValue() {
        LatLng a = new LatLng(1., 1.);
        LatLng b = new LatLng(1., 2.);
        assertEquals(111.178, a.distance(b), 0.001);
    }

    @Test
    public void testDistance_withDifferentValues_shouldReturnCorrectValue() {
        LatLng a = new LatLng(1., 1.);
        LatLng b = new LatLng(2., 2.);
        assertEquals(157.225, a.distance(b), 0.001);
    }

    @Test
    public void testDistance_withNegativeValues_shouldReturnCorrectValue() {
        LatLng a = new LatLng(-1., 1.);
        LatLng b = new LatLng(2., -2.);
        assertEquals(471.724, a.distance(b), 0.001);
    }
}