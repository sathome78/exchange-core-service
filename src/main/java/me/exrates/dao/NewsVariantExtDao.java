package me.exrates.dao;

import me.exrates.model.newsEntity.NewsVariant;

public interface NewsVariantExtDao {
    NewsVariant findByNewsVariantLanguage(Integer newsId, String language);
}
