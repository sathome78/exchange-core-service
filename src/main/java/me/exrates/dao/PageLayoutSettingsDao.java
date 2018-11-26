package me.exrates.dao;

import me.exrates.model.dto.PageLayoutSettingsDto;

import java.util.Optional;

public interface PageLayoutSettingsDao {

    PageLayoutSettingsDto save(PageLayoutSettingsDto settingsDto);

    Optional<PageLayoutSettingsDto> findByUserId(Integer userId);

    boolean delete(PageLayoutSettingsDto settingsDto);

}
