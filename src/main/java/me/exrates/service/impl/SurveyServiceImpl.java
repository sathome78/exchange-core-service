package me.exrates.service.impl;

import me.exrates.dao.SurveyDao;
import me.exrates.dao.UserDao;
import me.exrates.model.dto.SurveyDto;
import me.exrates.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyServiceImpl implements SurveyService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SurveyDao surveyDao;

    @Override
    public void savePollAsDoneByUser(String email) {
        userDao.savePollAsDoneByUser(email);
    }

    @Override
    public SurveyDto getFirstActiveSurveyByLang(String language) {
        SurveyDto surveyDto = surveyDao.findFirstActiveByLang(language);
        if (surveyDto.getId() == null) {
            surveyDto = surveyDao.findFirstActiveByLang("en");
        }
        return surveyDto;
    }
}
