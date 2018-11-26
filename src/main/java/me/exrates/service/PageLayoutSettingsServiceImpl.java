package me.exrates.service;

import me.exrates.dao.PageLayoutSettingsDao;
import me.exrates.model.ColorScheme;
import me.exrates.model.User;
import me.exrates.model.dto.PageLayoutSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageLayoutSettingsServiceImpl implements PageLayoutSettingsService {

    public final PageLayoutSettingsDao pageLayoutSettingsDao;

    @Autowired
    public PageLayoutSettingsServiceImpl(PageLayoutSettingsDao pageLayoutSettingsDao) {
        this.pageLayoutSettingsDao = pageLayoutSettingsDao;
    }

    @Override
    public PageLayoutSettingsDto save(PageLayoutSettingsDto settingsDto) {
        return pageLayoutSettingsDao.save(settingsDto);
    }

    @Override
    public PageLayoutSettingsDto findByUser(User user) {
        return pageLayoutSettingsDao.findByUserId(user.getId()).orElse(null);
    }

    @Override
    public boolean delete(PageLayoutSettingsDto settingsDto) {
        return pageLayoutSettingsDao.delete(settingsDto);
    }

    @Override
    public ColorScheme getColorScheme(User user) {
        PageLayoutSettingsDto userSettings = findByUser(user);
        return userSettings == null ? ColorScheme.LIGHT : userSettings.getScheme();
    }

    @Override
    public boolean toggleLowColorMode(User user, boolean enabled) {
        PageLayoutSettingsDto updated;
        PageLayoutSettingsDto userSettings = findByUser(user);
        if (userSettings == null) {
            userSettings = PageLayoutSettingsDto
                    .builder()
                    .userId(user.getId())
                    .scheme(ColorScheme.LIGHT)
                    .isLowColorEnabled(enabled)
                    .build();
            updated = pageLayoutSettingsDao.save(userSettings);
        } else {
            userSettings.setLowColorEnabled(enabled);
            updated = pageLayoutSettingsDao.save(userSettings);
        }
        return updated != null;
    }
}
