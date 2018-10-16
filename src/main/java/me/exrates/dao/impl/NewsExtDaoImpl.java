package me.exrates.dao.impl;

import me.exrates.dao.NewsExtDao;
import me.exrates.model.newsEntity.News;
import org.springframework.stereotype.Service;

@Service
public class NewsExtDaoImpl implements NewsExtDao {

    public News findOne(Integer newsId) {
        return null;
    }

    public News save(News news) {
        return null;
    }

    @Override
    public News findByNewsTypeAndResource(Integer code, String resource) {
        return null;
    }
}
