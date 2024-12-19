package com.testlog.projet.criteria;

public record TripCriteria(HotelCriteria hotelCriteria, TransportCriteria transportCriteria,
                           AdditionalCriteria additionalCriteria) {
}
