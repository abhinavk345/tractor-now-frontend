package com.sita.enterprises.TractorNow.service;

public interface OwnerDashboardService {

    Object getDashboard(
            Long ownerId
    );

    Object getEquipment(
            Long ownerId
    );

    Object getBookings(
            Long ownerId
    );

    Object getEarnings(
            Long ownerId
    );

    Object getStatistics(
            Long ownerId
    );
}