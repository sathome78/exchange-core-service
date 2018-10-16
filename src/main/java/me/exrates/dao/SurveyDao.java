package me.exrates.dao;

import me.exrates.model.dto.SurveyDto;

public interface SurveyDao {
    SurveyDto findFirstActiveByLang(String language);
}
