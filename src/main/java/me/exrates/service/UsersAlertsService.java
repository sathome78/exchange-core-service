package me.exrates.service;

import me.exrates.model.dto.AlertDto;
import me.exrates.model.enums.AlertType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

public interface UsersAlertsService {

    List<AlertDto> getAllAlerts(Locale locale);

    @Transactional
    AlertDto getAlert(AlertType alertType);
}
