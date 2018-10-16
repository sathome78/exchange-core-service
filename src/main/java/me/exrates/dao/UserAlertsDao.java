package me.exrates.dao;

import me.exrates.model.dto.AlertDto;

import java.util.List;

/**
 * Created by Maks on 13.12.2017.
 */
public interface UserAlertsDao {
    List<AlertDto> getAlerts(boolean getOnlyEnabled);

    boolean updateAlert(AlertDto alertDto);

    boolean setEnable(String alertType, boolean enable);

    AlertDto getAlert(String name);
}
