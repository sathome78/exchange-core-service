package me.exrates.service;

import me.exrates.model.dto.SurveyDto;

public interface SurveyService {
    void savePollAsDoneByUser(String name);

    SurveyDto getFirstActiveSurveyByLang(String language);
}
