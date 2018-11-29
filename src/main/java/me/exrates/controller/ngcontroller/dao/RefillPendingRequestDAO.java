package me.exrates.controller.ngcontroller.dao;

import me.exrates.controller.ngcontroller.model.RefillPendingRequestDto;

import java.util.List;

public interface RefillPendingRequestDAO {
    List<RefillPendingRequestDto> getPendingRefillRequests(long userId);
}
