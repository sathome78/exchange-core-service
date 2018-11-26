package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.OrderDao;
import me.exrates.service.DashboardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Log4j2
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    DashboardDao dashboardDao;

    @Autowired
    OrderDao orderDao;

    @Override
    public BigDecimal getBalanceByCurrency(int userId, int currencyId) {
        log.info("Begin 'getBalanceByCurrency' method");
        return dashboardDao.getBalanceByCurrency(userId, currencyId);
    }

}