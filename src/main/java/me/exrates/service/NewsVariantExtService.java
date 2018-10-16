package me.exrates.service;

import me.exrates.model.dto.NewsTopicDto;
import me.exrates.model.enums.NewsTypeEnum;

import java.util.Locale;

public interface NewsVariantExtService {
    void deleteNewsVariant(Object id);

    NewsTopicDto getMaterialPageContent(NewsTypeEnum page, String toUpperCase, Locale locale);
}
