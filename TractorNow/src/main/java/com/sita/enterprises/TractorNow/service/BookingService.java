package com.sita.enterprises.TractorNow.service;

import com.sita.enterprises.TractorNow.dto.booking.BookingRequest;

public interface BookingService {

    Object create(
            BookingRequest request
    );

    Object getById(Long id);

    Object getFarmerBookings(
            Long farmerId
    );

    Object getOwnerBookings(
            Long ownerId
    );

    Object confirm(Long id);

    Object cancel(Long id);

    Object ongoing(Long id);

    Object complete(Long id);
}