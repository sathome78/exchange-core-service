package me.exrates.controller.ngcontroller.service.impl;

import me.exrates.controller.ngcontroller.dao.BalanceDao;
import me.exrates.controller.ngcontroller.model.UserBalancesDto;
import me.exrates.controller.ngcontroller.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BalanceServiceImpl implements BalanceService {

    @Autowired
    private BalanceDao balanceDao;

    @Override
    public List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId) {

        return balanceDao.getUserBalances(tikerName, sortByCreated, page, limit,userId);
    }
}
