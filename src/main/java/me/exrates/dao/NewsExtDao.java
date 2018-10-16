package me.exrates.dao;

import me.exrates.model.newsEntity.News;

public interface NewsExtDao {
    News findOne(Integer newsId);

    News save(News news);

    News findByNewsTypeAndResource(Integer code, String resource);
}
