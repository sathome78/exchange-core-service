package me.exrates.service.impl;

import me.exrates.model.dto.NewsTopicDto;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.service.NewsVariantExtService;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class NewsVariantExtServiceImpl implements NewsVariantExtService {
    @Override
    public void deleteNewsVariant(Object id) {

    }

    @Override
    public NewsTopicDto getMaterialPageContent(NewsTypeEnum page, String toUpperCase, Locale locale) {
        return null;
    }
}
