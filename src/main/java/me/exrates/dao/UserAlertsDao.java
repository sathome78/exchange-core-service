package me.exrates.dao;

import me.exrates.model.dto.AlertDto;

import java.util.List;

public interface UserAlertsDao {
    List<AlertDto> getAlerts(boolean getOnlyEnabled);

    boolean updateAlert(AlertDto alertDto);

    AlertDto getAlert(String name);
}
