package me.exrates.controller.ngcontroller.service;

import me.exrates.controller.ngcontroller.model.RefillPendingRequestDto;

import java.util.List;

public interface RefillPendingRequestService {
    List<RefillPendingRequestDto> getPendingRefillRequests(long userId);
}
