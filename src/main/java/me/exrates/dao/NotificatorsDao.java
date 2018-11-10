package me.exrates.dao;

import me.exrates.model.dto.Notificator;

import java.util.List;

public interface NotificatorsDao {
    Notificator getById(int id);

    List<Notificator> getAllNotificators();
}
