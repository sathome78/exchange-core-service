package me.exrates.controller.ngcontroller.dao;


import me.exrates.controller.ngcontroller.model.UserBalancesDto;

import java.util.List;

public interface BalanceDao {
    List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId);
}
