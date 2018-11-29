package me.exrates.controller.ngcontroller.service.impl;

import me.exrates.controller.ngcontroller.dao.RefillPendingRequestDAO;
import me.exrates.controller.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.controller.ngcontroller.service.RefillPendingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefillPendingRequestServiceImpl implements RefillPendingRequestService {

    @Autowired
    private RefillPendingRequestDAO refillPendingRequestDAO;

    @Override
    public List<RefillPendingRequestDto> getPendingRefillRequests(long userId) {
        return refillPendingRequestDAO.getPendingRefillRequests(userId);
    }
}
