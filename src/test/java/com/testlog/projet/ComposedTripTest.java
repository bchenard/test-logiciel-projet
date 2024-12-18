package com.testlog.projet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class ComposedTripTest {

    @Test
    void testGetMode_withSingleTransportMode() {
        SimpleTrip trip1 = new SimpleTrip("A", "B", TransportationMode.TRAIN, 100, null, null);
        SimpleTrip trip2 = new SimpleTrip("B", "C", TransportationMode.TRAIN, 200, null, null);
        List<SimpleTrip> tripList = Arrays.asList(trip1, trip2);

        ComposedTrip composedTrip = new ComposedTrip(tripList);
        List<TransportationMode> modes = composedTrip.getMode();

        Assertions.assertEquals(2, modes.size());
        Assertions.assertEquals(TransportationMode.TRAIN, modes.get(0));
        Assertions.assertEquals(TransportationMode.TRAIN, modes.get(1));
    }

    @Test
    void testGetMode_withMultipleTransportModes() {
        SimpleTrip trip1 = new SimpleTrip("A", "B", TransportationMode.PLANE, 100, null, null);
        SimpleTrip trip2 = new SimpleTrip("B", "C", TransportationMode.TRAIN, 200, null, null);
        List<SimpleTrip> tripList = Arrays.asList(trip1, trip2);

        ComposedTrip composedTrip = new ComposedTrip(tripList);
        List<TransportationMode> modes = composedTrip.getMode();

        Assertions.assertEquals(2, modes.size());
        Assertions.assertEquals(TransportationMode.PLANE, modes.get(0));
        Assertions.assertEquals(TransportationMode.TRAIN, modes.get(1));
    }

    @Test
    void testGetMode_withNotSpecifiedTransportMode() {
        SimpleTrip trip1 = new SimpleTrip("A", "B", TransportationMode.NOT_SPECIFIED, 100, null, null);
        SimpleTrip trip2 = new SimpleTrip("B", "C", TransportationMode.TRAIN, 200, null, null);
        List<SimpleTrip> tripList = Arrays.asList(trip1, trip2);

        ComposedTrip composedTrip = new ComposedTrip(tripList);
        List<TransportationMode> modes = composedTrip.getMode();

        Assertions.assertEquals(2, modes.size());
        Assertions.assertEquals(TransportationMode.NOT_SPECIFIED, modes.get(0));
        Assertions.assertEquals(TransportationMode.TRAIN, modes.get(1));
    }
}