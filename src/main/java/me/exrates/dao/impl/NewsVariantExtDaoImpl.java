package me.exrates.dao.impl;

import me.exrates.dao.NewsVariantExtDao;
import me.exrates.model.newsEntity.NewsVariant;
import org.springframework.stereotype.Repository;

@Repository
public class NewsVariantExtDaoImpl implements NewsVariantExtDao {
    
    public NewsVariant findByNewsVariantLanguage(Integer newsId, String language) {
        return null;
    }
}
